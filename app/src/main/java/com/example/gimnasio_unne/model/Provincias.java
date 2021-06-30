package com.example.gimnasio_unne.model;

public class Provincias {
    String id, provincia;

    public Provincias() {}

    public Provincias(String id, String provincia) {
        this.id = id;
        this.provincia = provincia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    @Override
    public String toString() {return provincia;}

}
