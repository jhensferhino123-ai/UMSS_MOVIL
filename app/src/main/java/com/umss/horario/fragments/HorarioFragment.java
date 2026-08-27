package com.umss.horario.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.umss.horario.R;
import com.umss.horario.adapters.ClaseAdapter;
import com.umss.horario.database.AgendaDao;
import com.umss.horario.database.AgendaEvento;
import com.umss.horario.database.AppDatabase;
import com.umss.horario.database.Asignatura;
import com.umss.horario.database.AsignaturaDao;
import com.umss.horario.databinding.FragmentHorarioBinding;
import com.umss.horario.utils.TimeUtils;

import java.util.List;

public class HorarioFragment extends Fragment {

    private FragmentHorarioBinding binding;
    private AsignaturaDao asignaturaDao;
    private AgendaDao agendaDao;
    private String diaSeleccionado;
    private Handler handler = new Handler();
    private Runnable actualizarTiempo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHorarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        asignaturaDao = db.asignaturaDao();
        agendaDao = db.agendaDao();

        diaSeleccionado = TimeUtils.getDiaActual();

        // Si hoy es fin de semana, mostrar Lun por defecto
        if (diaSeleccionado.equals("Dom") || diaSeleccionado.equals("Sab")) {
            diaSeleccionado = "Lun";
        }

        configurarBotonesDias();
        configurarCampana();
        actualizarHorario();
        iniciarActualizacionAutomatica();
    }

    private void configurarBotonesDias() {
        binding.btnLun.setOnClickListener(v -> seleccionarDia("Lun"));
        binding.btnMar.setOnClickListener(v -> seleccionarDia("Mar"));
        binding.btnMie.setOnClickListener(v -> seleccionarDia("Mie"));
        binding.btnJue.setOnClickListener(v -> seleccionarDia("Jue"));
        binding.btnVie.setOnClickListener(v -> seleccionarDia("Vie"));

        resaltarBotonDia(diaSeleccionado);
    }

    private void seleccionarDia(String dia) {
        diaSeleccionado = dia;
        resaltarBotonDia(dia);
        actualizarHorario();
    }

    private void resaltarBotonDia(String dia) {
        binding.btnLun.setSelected(dia.equals("Lun"));
        binding.btnMar.setSelected(dia.equals("Mar"));
        binding.btnMie.setSelected(dia.equals("Mie"));
        binding.btnJue.setSelected(dia.equals("Jue"));
        binding.btnVie.setSelected(dia.equals("Vie"));
    }

    private void actualizarHorario() {
        List<Asignatura> clases = asignaturaDao.obtenerPorDia(diaSeleccionado);
        String diaHoy = TimeUtils.getDiaActual();
        String horaActual = TimeUtils.getHoraActual();

        Asignatura claseActual = null;
        Asignatura claseActualTmp = null;
        Asignatura siguienteClase = null;

        // Solo calcular clase actual/siguiente si es el día de hoy
        if (diaSeleccionado.equals(diaHoy)) {
            for (Asignatura a : clases) {
                int inicio = TimeUtils.horaAMinutos(a.horaInicio);
                int fin = TimeUtils.horaAMinutos(a.horaFin);
                int ahora = TimeUtils.horaAMinutos(horaActual);

                if (ahora >= inicio && ahora < fin) {
                    claseActualTmp = a;
                } else if (ahora < inicio && siguienteClase == null) {
                    siguienteClase = a;
                }
            }
            claseActual = claseActualTmp;
        }

        // Mostrar clase actual
        if (claseActual != null) {
            binding.cardClaseActual.setVisibility(View.VISIBLE);
            binding.tvNombreClaseActual.setText(claseActual.nombre);
            binding.tvAulaActual.setText("Aula: " + claseActual.aula + " | " + claseActual.docente);
            int minRestantes = TimeUtils.minutosRestantes(claseActual.horaFin);
            binding.tvTiempoRestante.setText("⏱ " + TimeUtils.formatearDuracion(minRestantes) + " restantes");
            binding.tvHorarioActual.setText(claseActual.horaInicio + " - " + claseActual.horaFin);
        } else {
            binding.cardClaseActual.setVisibility(View.GONE);
        }

        // Mostrar siguiente clase
        if (siguienteClase != null) {
            binding.cardSiguienteClase.setVisibility(View.VISIBLE);
            binding.tvNombreSiguiente.setText(siguienteClase.nombre);
            binding.tvHorarioSiguiente.setText(siguienteClase.horaInicio + " - " + siguienteClase.horaFin);
            binding.tvAulaSiguiente.setText("Aula: " + siguienteClase.aula);
        } else {
            binding.cardSiguienteClase.setVisibility(View.GONE);
        }

        // Mostrar mensaje si no hay clases
        if (clases.isEmpty()) {
            binding.tvSinClases.setVisibility(View.VISIBLE);
        } else {
            binding.tvSinClases.setVisibility(View.GONE);
        }

        // Configurar RecyclerView con todas las clases del día
        ClaseAdapter adapter = new ClaseAdapter(clases, asignatura -> mostrarDetallesDia(asignatura));
        binding.recyclerClases.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerClases.setAdapter(adapter);
    }

    private void mostrarDetallesDia(Asignatura asignatura) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(asignatura.nombre);

        String detalle = "🕐 " + asignatura.horaInicio + " - " + asignatura.horaFin + "\n" +
                "🏫 Aula: " + asignatura.aula + "\n" +
                "👨‍🏫 Docente: " + asignatura.docente + "\n" +
                "👥 Grupo: " + asignatura.grupo + "\n" +
                "📅 Días: " + asignatura.dias;

        builder.setMessage(detalle);
        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    private void configurarCampana() {
        binding.btnCampana.setOnClickListener(v -> mostrarAvisosDelDia());
    }

    private void mostrarAvisosDelDia() {
        String fechaHoy = TimeUtils.getFechaActual();
        List<AgendaEvento> eventos = agendaDao.obtenerPorFecha(fechaHoy);

        // También verificar avisos próximos
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("🔔 Avisos del día");

        if (eventos.isEmpty()) {
            builder.setMessage("No hay eventos ni avisos para hoy.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (AgendaEvento e : eventos) {
                sb.append("📌 ").append(e.tipo).append(" - ").append(e.materia).append("\n");
                sb.append("   ").append(e.detalle).append("\n\n");
            }
            builder.setMessage(sb.toString().trim());
        }

        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    private void iniciarActualizacionAutomatica() {
        actualizarTiempo = new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    actualizarHorario();
                    handler.postDelayed(this, 60000); // cada minuto
                }
            }
        };
        handler.postDelayed(actualizarTiempo, 60000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(actualizarTiempo);
        binding = null;
    }
}
