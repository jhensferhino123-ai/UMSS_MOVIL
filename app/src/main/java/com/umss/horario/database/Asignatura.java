package com.umss.horario.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "asignaturas")
public class Asignatura {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public String dias;        // "Lun,Mie,Vie"
    public String horaInicio;  // "08:00"
    public String horaFin;     // "10:00"
    public String aula;
    public String docente;
    public String grupo;

    public Asignatura() {}

    public Asignatura(String nombre, String dias, String horaInicio,
                      String horaFin, String aula, String docente, String grupo) {
        this.nombre = nombre;
        this.dias = dias;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.aula = aula;
        this.docente = docente;
        this.grupo = grupo;
    }
}
