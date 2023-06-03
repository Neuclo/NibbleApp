package com.example.tfc;

public class Reserva {
    private String id;
    private String fecha;
    private String nombreRestaurante;

    public Reserva(String id,String fecha,String nombreRestaurante) {
        this.id=id;
        this.fecha = fecha;
        this.nombreRestaurante=nombreRestaurante;
    }

    public String getFecha() {
        return fecha;
    }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public String getId() {
        return id;
    }

}
