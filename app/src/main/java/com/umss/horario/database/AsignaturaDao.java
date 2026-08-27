package com.umss.horario.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AsignaturaDao {

    @Insert
    long insertar(Asignatura asignatura);

    @Update
    void actualizar(Asignatura asignatura);

    @Delete
    void eliminar(Asignatura asignatura);

    @Query("SELECT * FROM asignaturas ORDER BY horaInicio ASC")
    List<Asignatura> obtenerTodas();

    @Query("SELECT * FROM asignaturas WHERE dias LIKE '%' || :dia || '%' ORDER BY horaInicio ASC")
    List<Asignatura> obtenerPorDia(String dia);

    @Query("SELECT * FROM asignaturas WHERE id = :id")
    Asignatura obtenerPorId(int id);
}
