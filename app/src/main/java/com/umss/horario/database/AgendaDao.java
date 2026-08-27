package com.umss.horario.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AgendaDao {

    @Insert
    long insertar(AgendaEvento evento);

    @Update
    void actualizar(AgendaEvento evento);

    @Delete
    void eliminar(AgendaEvento evento);

    @Query("SELECT * FROM agenda ORDER BY fecha ASC")
    List<AgendaEvento> obtenerTodos();

    @Query("SELECT * FROM agenda WHERE fecha = :fecha ORDER BY tipo ASC")
    List<AgendaEvento> obtenerPorFecha(String fecha);

    @Query("SELECT DISTINCT fecha FROM agenda")
    List<String> obtenerFechasConEventos();

    @Query("SELECT * FROM agenda WHERE id = :id")
    AgendaEvento obtenerPorId(int id);
}
