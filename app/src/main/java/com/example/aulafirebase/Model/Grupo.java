package com.example.aulafirebase.Model;

public class Grupo {
    private String nomeGrupo, descGrupo;

    public Grupo() {
    }

    public Grupo(String nomeGrupo, String descGrupo) {
        this.nomeGrupo = nomeGrupo;
        this.descGrupo = descGrupo;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public String getDescGrupo() {
        return descGrupo;
    }

    public void setDescGrupo(String descGrupo) {
        this.descGrupo = descGrupo;
    }
}
