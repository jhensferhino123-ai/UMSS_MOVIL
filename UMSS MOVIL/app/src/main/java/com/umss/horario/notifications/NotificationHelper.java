package com.umss.horario.notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.umss.horario.database.AgendaEvento;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationHelper {

    public static final String CANAL_ID = "umss_horario_canal";
    public static final String CANAL_NOMBRE = "UMSS Horario";

    public static void crearCanal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CANAL_ID,
                    CANAL_NOMBRE,
                    NotificationManager.IMPORTANCE_HIGH
            );
            canal.setDescription("Notificaciones de agenda universitaria");
            canal.enableVibration(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(canal);
            }
        }
    }

    public static void programarNotificacion(Context context, AgendaEvento evento) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date fechaEvento = sdf.parse(evento.fecha);
            if (fechaEvento == null) return;

            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaEvento);
            cal.add(Calendar.DAY_OF_YEAR, -evento.diasAviso);
            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            long tiempoNotificacion = cal.getTimeInMillis();
            if (tiempoNotificacion <= System.currentTimeMillis()) return;

            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("titulo", evento.tipo + ": " + evento.materia);
            intent.putExtra("mensaje", evento.detalle + " — en " + evento.diasAviso + " día(s)");
            intent.putExtra("id", evento.id);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    evento.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP, tiempoNotificacion, pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, tiempoNotificacion, pendingIntent);
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, tiempoNotificacion, pendingIntent);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
