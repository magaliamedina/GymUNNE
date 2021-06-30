package com.example.gimnasio_unne.model;

public class Horarios {
    private String id, hora_inicio, hora_fin, estado;

    public Horarios() {}

    public Horarios(String id, String hora_inicio, String hora_fin, String estado) {
        this.id =id;
        this.hora_inicio=hora_inicio;
        this.hora_fin=hora_fin;
        this.estado=estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHoraInicio() {
        return hora_inicio;
    }

    public void setHoraInicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getHoraFin() {
        return hora_fin;
    }

    public void setHoraFin(String hora_fin) {
        this.hora_fin = hora_fin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "De " + hora_inicio + " a " + hora_fin;
    }
}
