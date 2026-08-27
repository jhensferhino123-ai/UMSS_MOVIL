package com.umss.horario.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.umss.horario.database.AgendaDao;
import com.umss.horario.database.AgendaEvento;
import com.umss.horario.database.AppDatabase;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Reprogramar todas las notificaciones al reiniciar
            AppDatabase db = AppDatabase.getInstance(context);
            AgendaDao agendaDao = db.agendaDao();
            List<AgendaEvento> eventos = agendaDao.obtenerTodos();

            for (AgendaEvento evento : eventos) {
                NotificationHelper.programarNotificacion(context, evento);
            }
        }
    }
}
