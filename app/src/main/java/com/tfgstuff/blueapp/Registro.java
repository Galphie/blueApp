package com.tfgstuff.blueapp;

public class Registro {

    private Datos datos;
    private String fecha;

    public Registro(Datos datos, String fecha) {
        this.datos = datos;
        this.fecha = fecha;
    }

    public Registro() {
    }

    public Datos getDatos() {
        return datos;
    }

    public void setDatos(Datos datos) {
        this.datos = datos;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
