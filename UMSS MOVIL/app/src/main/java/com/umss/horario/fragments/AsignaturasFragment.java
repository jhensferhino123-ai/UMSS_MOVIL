package com.umss.horario.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.umss.horario.R;
import com.umss.horario.adapters.AsignaturaAdapter;
import com.umss.horario.database.AppDatabase;
import com.umss.horario.database.Asignatura;
import com.umss.horario.database.AsignaturaDao;
import com.umss.horario.databinding.FragmentAsignaturasBinding;
import com.umss.horario.databinding.DialogAsignaturaBinding;

import java.util.ArrayList;
import java.util.List;

public class AsignaturasFragment extends Fragment {

    private FragmentAsignaturasBinding binding;
    private AsignaturaDao asignaturaDao;
    private AsignaturaAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAsignaturasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        asignaturaDao = AppDatabase.getInstance(requireContext()).asignaturaDao();

        adapter = new AsignaturaAdapter(new ArrayList<>(),
                asignatura -> mostrarDialogoEditar(asignatura),
                asignatura -> confirmarEliminar(asignatura));

        binding.recyclerAsignaturas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerAsignaturas.setAdapter(adapter);

        binding.fabAgregarAsignatura.setOnClickListener(v -> mostrarDialogoAgregar());

        cargarAsignaturas();
    }

    private void cargarAsignaturas() {
        List<Asignatura> lista = asignaturaDao.obtenerTodas();
        adapter.setDatos(lista);

        if (lista.isEmpty()) {
            binding.tvSinAsignaturas.setVisibility(View.VISIBLE);
        } else {
            binding.tvSinAsignaturas.setVisibility(View.GONE);
        }
    }

    private void mostrarDialogoAgregar() {
        mostrarDialogoForm(null);
    }

    private void mostrarDialogoEditar(Asignatura asignatura) {
        mostrarDialogoForm(asignatura);
    }

    private void mostrarDialogoForm(Asignatura asignaturaEditar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogAsignaturaBinding dialogBinding = DialogAsignaturaBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        builder.setTitle(asignaturaEditar == null ? "Nueva Asignatura" : "Editar Asignatura");

        // Si editamos, pre-llenar datos
        if (asignaturaEditar != null) {
            dialogBinding.etNombre.setText(asignaturaEditar.nombre);
            dialogBinding.etHoraInicio.setText(asignaturaEditar.horaInicio);
            dialogBinding.etHoraFin.setText(asignaturaEditar.horaFin);
            dialogBinding.etAula.setText(asignaturaEditar.aula);
            dialogBinding.etDocente.setText(asignaturaEditar.docente);
            dialogBinding.etGrupo.setText(asignaturaEditar.grupo);

            // Marcar días
            String dias = asignaturaEditar.dias;
            dialogBinding.cbLun.setChecked(dias.contains("Lun"));
            dialogBinding.cbMar.setChecked(dias.contains("Mar"));
            dialogBinding.cbMie.setChecked(dias.contains("Mie"));
            dialogBinding.cbJue.setChecked(dias.contains("Jue"));
            dialogBinding.cbVie.setChecked(dias.contains("Vie"));
        }

        AlertDialog dialog = builder.create();

        dialogBinding.btnGuardar.setOnClickListener(v -> {
            String nombre = dialogBinding.etNombre.getText().toString().trim();
            String horaInicio = dialogBinding.etHoraInicio.getText().toString().trim();
            String horaFin = dialogBinding.etHoraFin.getText().toString().trim();
            String aula = dialogBinding.etAula.getText().toString().trim();
            String docente = dialogBinding.etDocente.getText().toString().trim();
            String grupo = dialogBinding.etGrupo.getText().toString().trim();

            // Construir string de días
            List<String> diasSeleccionados = new ArrayList<>();
            if (dialogBinding.cbLun.isChecked()) diasSeleccionados.add("Lun");
            if (dialogBinding.cbMar.isChecked()) diasSeleccionados.add("Mar");
            if (dialogBinding.cbMie.isChecked()) diasSeleccionados.add("Mie");
            if (dialogBinding.cbJue.isChecked()) diasSeleccionados.add("Jue");
            if (dialogBinding.cbVie.isChecked()) diasSeleccionados.add("Vie");

            if (nombre.isEmpty() || diasSeleccionados.isEmpty() ||
                    horaInicio.isEmpty() || horaFin.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            String dias = String.join(",", diasSeleccionados);

            if (asignaturaEditar == null) {
                Asignatura nueva = new Asignatura(nombre, dias, horaInicio, horaFin, aula, docente, grupo);
                asignaturaDao.insertar(nueva);
                Toast.makeText(requireContext(), "Asignatura guardada ✓", Toast.LENGTH_SHORT).show();
            } else {
                asignaturaEditar.nombre = nombre;
                asignaturaEditar.dias = dias;
                asignaturaEditar.horaInicio = horaInicio;
                asignaturaEditar.horaFin = horaFin;
                asignaturaEditar.aula = aula;
                asignaturaEditar.docente = docente;
                asignaturaEditar.grupo = grupo;
                asignaturaDao.actualizar(asignaturaEditar);
                Toast.makeText(requireContext(), "Asignatura actualizada ✓", Toast.LENGTH_SHORT).show();
            }

            cargarAsignaturas();
            dialog.dismiss();
        });

        dialogBinding.btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void confirmarEliminar(Asignatura asignatura) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar asignatura")
                .setMessage("¿Eliminar \"" + asignatura.nombre + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    asignaturaDao.eliminar(asignatura);
                    cargarAsignaturas();
                    Toast.makeText(requireContext(), "Eliminada", Toast.LENGTH_SHORT).show();
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
