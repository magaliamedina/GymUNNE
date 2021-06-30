package com.example.gimnasio_unne.model;

public class Grupos {

    private String id, prof, horario, cupototal, descripcion,estado;

    public Grupos() {}

    public Grupos(String id, String prof, String horario, String cupototal, String descripcion,String estado) {
        this.id = id;
        this.prof = prof;
        this.horario = horario;
        this.cupototal = cupototal;
        this.descripcion= descripcion;
        this.estado=estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getCupototal() {
        return cupototal;
    }

    public void setCupototal(String cupototal) {
        this.cupototal = cupototal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String toString() {
        return descripcion + " " + horario;
    }



}
