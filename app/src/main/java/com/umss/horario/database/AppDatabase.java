package com.umss.horario.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Asignatura.class, AgendaEvento.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract AsignaturaDao asignaturaDao();
    public abstract AgendaDao agendaDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "umss_horario.db")
                    .allowMainThreadQueries() // simplificado para la app
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
