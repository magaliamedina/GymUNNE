package com.example.gimnasio_unne.model;

public class Reservas {
    private String id_reserva, fecha_reserva, estudiante_nya, grupo, horarios_inicio_fin;

    public Reservas(){   }

    public Reservas(String id_reserva, String fecha_reserva, String estudiante_nya, String grupo, String horarios_inicio_fin) {
        this.id_reserva = id_reserva;
        this.fecha_reserva = fecha_reserva;
        this.estudiante_nya = estudiante_nya;
        this.grupo = grupo;
        this.horarios_inicio_fin = horarios_inicio_fin;
    }

    public String getId_reserva() {
        return id_reserva;
    }

    public void setId_reserva(String id_reserva) {
        this.id_reserva = id_reserva;
    }

    public String getFecha_reserva() {
        return fecha_reserva;
    }

    public void setFecha_reserva(String fecha_reserva) {
        this.fecha_reserva = fecha_reserva;
    }

    public String getEstudiante_nya() {
        return estudiante_nya;
    }

    public void setEstudiante_nya(String estudiante_nya) {
        this.estudiante_nya = estudiante_nya;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getHorarios_inicio_fin() {
        return horarios_inicio_fin;
    }

    public void setHorarios_inicio_fin(String horarios_inicio_fin) {
        this.horarios_inicio_fin = horarios_inicio_fin;
    }
}
