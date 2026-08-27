package com.umss.horario.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.umss.horario.R;
import com.umss.horario.database.Asignatura;
import com.umss.horario.utils.TimeUtils;

import java.util.List;

public class ClaseAdapter extends RecyclerView.Adapter<ClaseAdapter.ViewHolder> {

    public interface OnClaseClick {
        void onClick(Asignatura asignatura);
    }

    private List<Asignatura> datos;
    private OnClaseClick listener;

    public ClaseAdapter(List<Asignatura> datos, OnClaseClick listener) {
        this.datos = datos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Asignatura clase = datos.get(position);
        holder.tvNombre.setText(clase.nombre);
        holder.tvHorario.setText(clase.horaInicio + " - " + clase.horaFin);
        holder.tvAula.setText("Aula " + clase.aula);
        holder.tvDocente.setText(clase.docente);

        // Resaltar si es la clase actual
        String horaActual = TimeUtils.getHoraActual();
        int ahora = TimeUtils.horaAMinutos(horaActual);
        int inicio = TimeUtils.horaAMinutos(clase.horaInicio);
        int fin = TimeUtils.horaAMinutos(clase.horaFin);

        if (ahora >= inicio && ahora < fin) {
            holder.itemView.setBackgroundResource(R.drawable.bg_clase_activa);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_clase_normal);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(clase));
    }

    @Override
    public int getItemCount() {
        return datos != null ? datos.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvHorario, tvAula, tvDocente;

        ViewHolder(View view) {
            super(view);
            tvNombre = view.findViewById(R.id.tv_nombre_clase);
            tvHorario = view.findViewById(R.id.tv_horario_clase);
            tvAula = view.findViewById(R.id.tv_aula_clase);
            tvDocente = view.findViewById(R.id.tv_docente_clase);
        }
    }
}
