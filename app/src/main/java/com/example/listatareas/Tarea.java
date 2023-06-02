package com.example.listatareas;

public class Tarea {
    String id, titulo;

    public Tarea(String id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

}
