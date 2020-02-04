package com.example.aulafirebase;

public class Usuario {

    private String nome;
    private String nivelUsuario;

    public Usuario(){

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNivelUsuario() {
        return nivelUsuario;
    }

    public void setNivelUsuario(String nivelUsuario) {
        this.nivelUsuario = nivelUsuario;
    }

    public Usuario(String nome, String nivelUsuario) {
        this.nome = nome;
        this.nivelUsuario = nivelUsuario;
    }
}
