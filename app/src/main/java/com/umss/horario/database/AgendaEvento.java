package com.umss.horario.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "agenda")
public class AgendaEvento {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String fecha;      // "2026-09-15"
    public String tipo;       // "Examen", "Tarea", "Proyecto", etc.
    public String materia;
    public String detalle;
    public int diasAviso;     // días antes para notificar

    public AgendaEvento() {}

    public AgendaEvento(String fecha, String tipo, String materia,
                        String detalle, int diasAviso) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.materia = materia;
        this.detalle = detalle;
        this.diasAviso = diasAviso;
    }
}
