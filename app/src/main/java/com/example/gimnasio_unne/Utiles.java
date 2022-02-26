package com.example.gimnasio_unne;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Utiles {

    public static String obtenerHoraActual(String zonaHoraria) {
        String formato = "HH:mm:ss";
        return obtenerFechaConFormato(formato, zonaHoraria);
    }

    //para generar PDF no me permite con 2 puntos
    public static String getHoraActual(String zonaHoraria) {
        String formato = "HH-mm-ss";
        return obtenerFechaConFormato(formato, zonaHoraria);
    }

    public static String obtenerFechaActual(String zonaHoraria) {
        String formato = "yyyy-MM-dd";
        return obtenerFechaConFormato(formato, zonaHoraria);
    }

    @SuppressLint("SimpleDateFormat")
    public static String obtenerFechaConFormato(String formato, String zonaHoraria) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(formato);
        sdf.setTimeZone(TimeZone.getTimeZone(zonaHoraria));
        return sdf.format(date);
    }

    //para alta, editar cupo y alumnosPorGrupo
    public static String obtenerAnio(){
        Date date = new Date();
        SimpleDateFormat getYearFormat = new SimpleDateFormat("yyyy");
        return getYearFormat.format(date);
    }

    //para alumnosPorGrupoMes
    public static String obtenerMesActual(){
        Date date = new Date();
        SimpleDateFormat getYearFormat = new SimpleDateFormat("MM");
        return getYearFormat.format(date);
    }

}
