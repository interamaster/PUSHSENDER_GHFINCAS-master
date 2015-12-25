package com.mio.jrdv.pushsenderjrdvsoft.model;

/**
 * Created by joseramondelgado on 25/12/15.
 */
public class Vecino {

    private String nombre;
    private String telefono;
    private String email;
    private String comunidad;


    public Vecino(String comunidad, String email, String nombre, String telefono) {
        this.comunidad = comunidad;
        this.email = email;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getComunidad() {
        return comunidad;
    }

    public void setComunidad(String comunidad) {
        this.comunidad = comunidad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
