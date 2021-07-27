package com.example.gimnasio_unne.model;

public class Personas {

    private String id, dni, apellido, nombres, sexo, fechaNac, localidad, provincia, estado, estadoCivil, usuarioId, email,
            password, lu;

    public Personas () {}

    public Personas(String id, String apellido, String nombres, String dni){
        this.id=id;
        this.apellido = apellido;
        this.nombres = nombres;
        this.dni=dni;
    }

    public Personas(String id, String dni, String apellido, String nombres, String sexo, String fechaNac,
                    String localidad, String provincia, String estado, String estadoCivil, String usuarioId,
                    String email, String pass) {
        this.id = id;
        this.dni = dni;
        this.apellido = apellido;
        this.nombres = nombres;
        this.sexo = sexo;
        this.fechaNac = fechaNac;
        this.localidad = localidad;
        this.provincia = provincia;
        this.estado = estado;
        this.estadoCivil = estadoCivil;
        this.usuarioId = usuarioId;
        this.email = email;
        this.password=pass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLu() {
        return lu;
    }

    public void setLu(String lu) {
        this.lu = lu;
    }

    public String toString() {
        return nombres+ " " +apellido;
    }
}
