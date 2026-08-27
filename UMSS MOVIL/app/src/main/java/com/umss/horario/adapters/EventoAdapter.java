package com.umss.horario.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.umss.horario.R;
import com.umss.horario.database.AgendaEvento;

import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.ViewHolder> {

    public interface OnEditClick { void onClick(AgendaEvento e); }
    public interface OnDeleteClick { void onClick(AgendaEvento e); }

    private List<AgendaEvento> datos;
    private OnEditClick editListener;
    private OnDeleteClick deleteListener;

    public EventoAdapter(List<AgendaEvento> datos, OnEditClick edit, OnDeleteClick delete) {
        this.datos = datos;
        this.editListener = edit;
        this.deleteListener = delete;
    }

    public void setDatos(List<AgendaEvento> nuevos) {
        this.datos = nuevos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AgendaEvento e = datos.get(position);

        String emoji = getEmojiTipo(e.tipo);
        holder.tvTipo.setText(emoji + " " + e.tipo);
        holder.tvMateria.setText(e.materia);
        holder.tvDetalle.setText(e.detalle);
        holder.tvAviso.setText("🔔 Aviso " + e.diasAviso + " día(s) antes");

        holder.btnEditar.setOnClickListener(v -> editListener.onClick(e));
        holder.btnEliminar.setOnClickListener(v -> deleteListener.onClick(e));
    }

    private String getEmojiTipo(String tipo) {
        switch (tipo) {
            case "Examen": return "📝";
            case "Tarea": return "📚";
            case "Proyecto": return "🔬";
            case "Presentación": return "🎤";
            case "Entrega": return "📤";
            default: return "📌";
        }
    }

    @Override
    public int getItemCount() {
        return datos != null ? datos.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipo, tvMateria, tvDetalle, tvAviso;
        ImageButton btnEditar, btnEliminar;

        ViewHolder(View view) {
            super(view);
            tvTipo = view.findViewById(R.id.tv_evento_tipo);
            tvMateria = view.findViewById(R.id.tv_evento_materia);
            tvDetalle = view.findViewById(R.id.tv_evento_detalle);
            tvAviso = view.findViewById(R.id.tv_evento_aviso);
            btnEditar = view.findViewById(R.id.btn_editar_evento);
            btnEliminar = view.findViewById(R.id.btn_eliminar_evento);
        }
    }
}
