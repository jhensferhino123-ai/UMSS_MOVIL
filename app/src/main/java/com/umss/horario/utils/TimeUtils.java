package com.umss.horario.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static final String[] DIAS_SEMANA = {"Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab"};
    public static final String[] DIAS_CORTOS = {"Lun", "Mar", "Mie", "Jue", "Vie"};

    /**
     * Retorna el día de la semana actual abreviado (Lun, Mar, etc.)
     */
    public static String getDiaActual() {
        Calendar cal = Calendar.getInstance();
        int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
        return DIAS_SEMANA[diaSemana - 1];
    }

    /**
     * Retorna hora actual en formato HH:mm
     */
    public static String getHoraActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Convierte hora "HH:mm" a minutos desde medianoche
     */
    public static int horaAMinutos(String hora) {
        String[] partes = hora.split(":");
        return Integer.parseInt(partes[0]) * 60 + Integer.parseInt(partes[1]);
    }

    /**
     * Calcula minutos restantes entre horaActual y horaFin
     */
    public static int minutosRestantes(String horaFin) {
        int ahora = horaAMinutos(getHoraActual());
        int fin = horaAMinutos(horaFin);
        return fin - ahora;
    }

    /**
     * Formatea minutos a "1h 30min" o "45 min"
     */
    public static String formatearDuracion(int minutos) {
        if (minutos <= 0) return "Finalizada";
        if (minutos >= 60) {
            int h = minutos / 60;
            int m = minutos % 60;
            return m > 0 ? h + "h " + m + "min" : h + "h";
        }
        return minutos + " min";
    }

    /**
     * Retorna la fecha actual en formato yyyy-MM-dd
     */
    public static String getFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Retorna fecha de hoy + días en formato yyyy-MM-dd
     */
    public static String getFechaEnDias(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, dias);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(cal.getTime());
    }
}
