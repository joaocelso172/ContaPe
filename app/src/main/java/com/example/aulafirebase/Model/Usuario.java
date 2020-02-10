package com.example.aulafirebase.Model;

import com.google.firebase.database.Exclude;

public class Usuario {

    private String email, nome, idUsuario;

    public Usuario(){

    }

    public String getNome() {
        return nome;
    }

    @Exclude
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Exclude
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
