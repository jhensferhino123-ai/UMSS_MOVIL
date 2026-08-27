package com.umss.horario.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.umss.horario.R;
import com.umss.horario.database.Asignatura;

import java.util.List;

public class AsignaturaAdapter extends RecyclerView.Adapter<AsignaturaAdapter.ViewHolder> {

    public interface OnEditClick { void onClick(Asignatura a); }
    public interface OnDeleteClick { void onClick(Asignatura a); }

    private List<Asignatura> datos;
    private OnEditClick editListener;
    private OnDeleteClick deleteListener;

    public AsignaturaAdapter(List<Asignatura> datos, OnEditClick edit, OnDeleteClick delete) {
        this.datos = datos;
        this.editListener = edit;
        this.deleteListener = delete;
    }

    public void setDatos(List<Asignatura> nuevos) {
        this.datos = nuevos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_asignatura, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Asignatura a = datos.get(position);
        holder.tvNombre.setText(a.nombre);
        holder.tvDias.setText(a.dias.replace(",", " · "));
        holder.tvHorario.setText(a.horaInicio + " - " + a.horaFin);
        holder.tvAulaDocente.setText("Aula " + a.aula + " | " + a.docente + " | Grupo " + a.grupo);

        holder.btnEditar.setOnClickListener(v -> editListener.onClick(a));
        holder.btnEliminar.setOnClickListener(v -> deleteListener.onClick(a));
    }

    @Override
    public int getItemCount() {
        return datos != null ? datos.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDias, tvHorario, tvAulaDocente;
        ImageButton btnEditar, btnEliminar;

        ViewHolder(View view) {
            super(view);
            tvNombre = view.findViewById(R.id.tv_asignatura_nombre);
            tvDias = view.findViewById(R.id.tv_asignatura_dias);
            tvHorario = view.findViewById(R.id.tv_asignatura_horario);
            tvAulaDocente = view.findViewById(R.id.tv_asignatura_aula);
            btnEditar = view.findViewById(R.id.btn_editar_asignatura);
            btnEliminar = view.findViewById(R.id.btn_eliminar_asignatura);
        }
    }
}
