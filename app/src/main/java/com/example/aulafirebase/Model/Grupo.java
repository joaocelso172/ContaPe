package com.example.aulafirebase.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Grupo implements Serializable {
    private String nomeGrupo, descGrupo, grupoId, grupoOwner;
    private Double despesaGrupo = 0.0, receitaGrupo = 0.0;

    public Double getDespesaGrupo() {
        return despesaGrupo;
    }

    public void setDespesaGrupo(Double despesaGrupo) {
        this.despesaGrupo = despesaGrupo;
    }

    public Double getReceitaGrupo() {
        return receitaGrupo;
    }

    public void setReceitaGrupo(Double receitaGrupo) {
        this.receitaGrupo = receitaGrupo;
    }

    @Exclude
    public String getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(String grupoId) {
        this.grupoId = grupoId;
    }

    public String getGrupoOwner() {
        return grupoOwner;
    }

    public void setGrupoOwner(String grupoOwner) {
        this.grupoOwner = grupoOwner;
    }

    public Grupo() {
    }

    public Grupo(String nomeGrupo, String descGrupo, Double despesaGrupo, Double receitaGrupo, String grupoId, String grupoOwner) {
        this.nomeGrupo = nomeGrupo;
        this.descGrupo = descGrupo;
        this.despesaGrupo = despesaGrupo;
        this.receitaGrupo = receitaGrupo;
        this.grupoId = grupoId;
        this.grupoOwner = grupoOwner;
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
