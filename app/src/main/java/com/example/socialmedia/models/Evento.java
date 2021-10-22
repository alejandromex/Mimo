package com.example.socialmedia.models;

public class Evento {

    public String id;
    public String descripcion;
    public String estatus;
    public String idUser;


    public Evento() {
    }

    public Evento(String id, String descripcion, String estatus, String idUser) {
        this.id = id;
        this.descripcion = descripcion;
        this.estatus = estatus;
        this.idUser = idUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
