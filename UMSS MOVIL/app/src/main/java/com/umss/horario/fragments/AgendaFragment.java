package com.umss.horario.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.umss.horario.R;
import com.umss.horario.adapters.EventoAdapter;
import com.umss.horario.database.AgendaDao;
import com.umss.horario.database.AgendaEvento;
import com.umss.horario.database.AppDatabase;
import com.umss.horario.database.AsignaturaDao;
import com.umss.horario.database.Asignatura;
import com.umss.horario.databinding.FragmentAgendaBinding;
import com.umss.horario.databinding.DialogEventoBinding;
import com.umss.horario.notifications.NotificationHelper;
import com.umss.horario.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AgendaFragment extends Fragment {

    private FragmentAgendaBinding binding;
    private AgendaDao agendaDao;
    private AsignaturaDao asignaturaDao;
    private EventoAdapter adapter;
    private String fechaSeleccionada;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAgendaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        agendaDao = db.agendaDao();
        asignaturaDao = db.asignaturaDao();

        fechaSeleccionada = TimeUtils.getFechaActual();

        adapter = new EventoAdapter(new ArrayList<>(),
                evento -> mostrarDialogoEditar(evento),
                evento -> confirmarEliminar(evento));

        binding.recyclerEventos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerEventos.setAdapter(adapter);

        // Configurar calendario
        configurarCalendario();

        binding.fabAgregarEvento.setOnClickListener(v -> mostrarDialogoAgregar());

        cargarEventos(fechaSeleccionada);
    }

    private void configurarCalendario() {
        // Marcar el día actual
        binding.calendarView.setDate(System.currentTimeMillis(), false, true);

        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            fechaSeleccionada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            cargarEventos(fechaSeleccionada);
        });

        // Indicador de dias con eventos
        actualizarIndicadoresCalendario();
    }

    private void actualizarIndicadoresCalendario() {
        // El CalendarView básico de Android no soporta puntos nativamente,
        // pero mostramos el contador en el título
        List<String> fechas = agendaDao.obtenerFechasConEventos();
        binding.tvFechasConEventos.setText("📅 " + fechas.size() + " días con eventos");
    }

    private void cargarEventos(String fecha) {
        List<AgendaEvento> eventos = agendaDao.obtenerPorFecha(fecha);
        adapter.setDatos(eventos);

        // Mostrar fecha seleccionada
        binding.tvFechaSeleccionada.setText("Eventos del " + formatearFecha(fecha));

        if (eventos.isEmpty()) {
            binding.tvSinEventos.setVisibility(View.VISIBLE);
        } else {
            binding.tvSinEventos.setVisibility(View.GONE);
        }
    }

    private String formatearFecha(String fecha) {
        // "2026-09-15" → "15/09/2026"
        String[] partes = fecha.split("-");
        if (partes.length == 3) {
            return partes[2] + "/" + partes[1] + "/" + partes[0];
        }
        return fecha;
    }

    private void mostrarDialogoAgregar() {
        mostrarDialogoForm(null);
    }

    private void mostrarDialogoEditar(AgendaEvento evento) {
        mostrarDialogoForm(evento);
    }

    private void mostrarDialogoForm(AgendaEvento eventoEditar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogEventoBinding dialogBinding = DialogEventoBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        builder.setTitle(eventoEditar == null ? "Nuevo Evento" : "Editar Evento");

        // Configurar spinner de tipos
        String[] tipos = {"Examen", "Tarea", "Proyecto", "Presentación", "Entrega", "Otro"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerTipo.setAdapter(tipoAdapter);

        // Configurar spinner de materias
        List<Asignatura> asignaturas = asignaturaDao.obtenerTodas();
        List<String> nombresMaterias = new ArrayList<>();
        nombresMaterias.add("Sin materia");
        for (Asignatura a : asignaturas) {
            nombresMaterias.add(a.nombre);
        }
        ArrayAdapter<String> materiaAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombresMaterias);
        materiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerMateria.setAdapter(materiaAdapter);

        // Fecha por defecto
        String[] fechaRef = {fechaSeleccionada};
        dialogBinding.btnFecha.setText(formatearFecha(fechaSeleccionada));

        dialogBinding.btnFecha.setOnClickListener(v -> {
            String[] partes = fechaRef[0].split("-");
            int year = Integer.parseInt(partes[0]);
            int month = Integer.parseInt(partes[1]) - 1;
            int day = Integer.parseInt(partes[2]);

            new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
                fechaRef[0] = String.format("%04d-%02d-%02d", y, m + 1, d);
                dialogBinding.btnFecha.setText(formatearFecha(fechaRef[0]));
            }, year, month, day).show();
        });

        // Si editamos, pre-llenar
        if (eventoEditar != null) {
            fechaRef[0] = eventoEditar.fecha;
            dialogBinding.btnFecha.setText(formatearFecha(eventoEditar.fecha));
            dialogBinding.etDetalle.setText(eventoEditar.detalle);
            dialogBinding.etDiasAviso.setText(String.valueOf(eventoEditar.diasAviso));

            // Seleccionar tipo
            for (int i = 0; i < tipos.length; i++) {
                if (tipos[i].equals(eventoEditar.tipo)) {
                    dialogBinding.spinnerTipo.setSelection(i);
                    break;
                }
            }

            // Seleccionar materia
            for (int i = 0; i < nombresMaterias.size(); i++) {
                if (nombresMaterias.get(i).equals(eventoEditar.materia)) {
                    dialogBinding.spinnerMateria.setSelection(i);
                    break;
                }
            }
        }

        AlertDialog dialog = builder.create();

        dialogBinding.btnGuardar.setOnClickListener(v -> {
            String tipo = dialogBinding.spinnerTipo.getSelectedItem().toString();
            String materia = dialogBinding.spinnerMateria.getSelectedItem().toString();
            String detalle = dialogBinding.etDetalle.getText().toString().trim();
            String diasAvisoStr = dialogBinding.etDiasAviso.getText().toString().trim();
            int diasAviso = diasAvisoStr.isEmpty() ? 1 : Integer.parseInt(diasAvisoStr);

            if (detalle.isEmpty()) {
                Toast.makeText(requireContext(), "Escribe un detalle", Toast.LENGTH_SHORT).show();
                return;
            }

            AgendaEvento evento;
            if (eventoEditar == null) {
                evento = new AgendaEvento(fechaRef[0], tipo, materia, detalle, diasAviso);
                agendaDao.insertar(evento);
                Toast.makeText(requireContext(), "Evento guardado ✓", Toast.LENGTH_SHORT).show();
            } else {
                eventoEditar.fecha = fechaRef[0];
                eventoEditar.tipo = tipo;
                eventoEditar.materia = materia;
                eventoEditar.detalle = detalle;
                eventoEditar.diasAviso = diasAviso;
                agendaDao.actualizar(eventoEditar);
                evento = eventoEditar;
                Toast.makeText(requireContext(), "Evento actualizado ✓", Toast.LENGTH_SHORT).show();
            }

            // Programar notificación
            NotificationHelper.programarNotificacion(requireContext(), evento);

            cargarEventos(fechaSeleccionada);
            actualizarIndicadoresCalendario();
            dialog.dismiss();
        });

        dialogBinding.btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void confirmarEliminar(AgendaEvento evento) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar evento")
                .setMessage("¿Eliminar \"" + evento.tipo + " - " + evento.materia + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    agendaDao.eliminar(evento);
                    cargarEventos(fechaSeleccionada);
                    actualizarIndicadoresCalendario();
                    Toast.makeText(requireContext(), "Eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
