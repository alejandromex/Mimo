package com.example.socialmedia.models;

public class Credito {

    public String id;
    public String idUser;
    public String tipo;
    public String institucion;
    public int cantidad;
    public int tarjeta;

    public Credito() {
    }

    public Credito(String id, String idUser, String tipo, String institucion, int cantidad, int tarjeta) {
        this.id = id;
        this.idUser = idUser;
        this.tipo = tipo;
        this.institucion = institucion;
        this.cantidad = cantidad;
        this.tarjeta = tarjeta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(int tarjeta) {
        this.tarjeta = tarjeta;
    }
}
