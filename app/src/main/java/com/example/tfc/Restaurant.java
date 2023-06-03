package com.example.tfc;

public class Restaurant {

    private String nombre;
    private String type;
    private String description;
    private String tlf;
    private String urlMenu;

    public Restaurant(String nombre, String type, String description, String tlf, String urlMenu) {
        this.nombre = nombre;
        this.type=type;
        this.description=description;
        this.tlf=tlf;
        this.urlMenu=urlMenu;
    }

    public String getNombre() {
        return nombre;
    }

    public String getType() {
        return type;
    }

    public String getUrlMenu() {
        return urlMenu;
    }

    public String getDescription() {
        return description;
    }

    public String getTlf() {
        return tlf;
    }
}
