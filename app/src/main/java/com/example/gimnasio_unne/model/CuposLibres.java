package com.example.gimnasio_unne.model;

public class CuposLibres {
    private String id_cupolibre, grupo_descripcion, profesor_nombreYapellido, cupolibre_total,
            horarios_inicio_fin, grupo_id, fecha_reserva, estado, mes, anio;

    public CuposLibres(String id_cupolibre, String grupo_descripcion, String nombreYapellido, String cupolibre_total,
                       String horarios_inicio_fin, String grupo_id, String fecha_reserva, String estado) {
        this.id_cupolibre=id_cupolibre;
        this.grupo_descripcion = grupo_descripcion;
        this.profesor_nombreYapellido = nombreYapellido;
        this.cupolibre_total = cupolibre_total;
        this.horarios_inicio_fin = horarios_inicio_fin;
        this.grupo_id= grupo_id;
        this.fecha_reserva=fecha_reserva;
        this.estado=estado;
    }

    public CuposLibres(String id_cupolibre, String grupo_descripcion, String nombreYapellido, String cupolibre_total,
                       String horarios_inicio_fin, String grupo_id, String fecha_reserva, String estado, String mes,
                       String anio) {
        this.id_cupolibre=id_cupolibre;
        this.grupo_descripcion = grupo_descripcion;
        this.profesor_nombreYapellido = nombreYapellido;
        this.cupolibre_total = cupolibre_total;
        this.horarios_inicio_fin = horarios_inicio_fin;
        this.grupo_id= grupo_id;
        this.fecha_reserva=fecha_reserva;
        this.estado=estado;
        this.mes=mes;
        this.anio=anio;
    }

    public String getId_cupolibre() {
        return id_cupolibre;
    }

    public void setId_cupolibre(String id_cupolibre) {
        this.id_cupolibre = id_cupolibre;
    }

    public String getGrupo_id() {
        return grupo_id;
    }

    public void setGrupo_id(String grupo_id) {
        this.grupo_id = grupo_id;
    }

    public String getProfesor_nombreYapellido() {
        return profesor_nombreYapellido;
    }

    public void setProfesor_nombreYapellido(String profesor_nombreYapellido) {
        this.profesor_nombreYapellido = profesor_nombreYapellido;
    }

    public String getGrupo_descripcion() {
        return grupo_descripcion;
    }

    public void setGrupo_descripcion(String grupo_descripcion) {
        this.grupo_descripcion = grupo_descripcion;
    }

    public String getCupolibre_total() {
        return cupolibre_total;
    }

    public void setCupolibre_total(String cupolibre_total) {
        this.cupolibre_total = cupolibre_total;
    }

    public String getHorarios_inicio_fin() {
        return horarios_inicio_fin;
    }

    public void setHorarios_inicio_fin(String horarios_inicio_fin) {
        this.horarios_inicio_fin = horarios_inicio_fin;
    }

    public String getFecha_reserva() {
        return fecha_reserva;
    }

    public void setFecha_reserva(String fecha_reserva) {
        this.fecha_reserva = fecha_reserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }
}
