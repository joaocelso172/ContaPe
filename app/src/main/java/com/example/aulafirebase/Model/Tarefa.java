package com.example.aulafirebase.Model;

public class Tarefa {

    private String nomeTarefa, descTarefa;


    public Tarefa(){

    }

    public Tarefa(String nomeTarefa, String descTarefa) {
        this.nomeTarefa = nomeTarefa;
        this.descTarefa = descTarefa;
    }

    public String getNomeTarefa() {
        return nomeTarefa;
    }

    public void setNomeTarefa(String nomeTarefa) {
        this.nomeTarefa = nomeTarefa;
    }

    public String getDescTarefa() {
        return descTarefa;
    }

    public void setDescTarefa(String descTarefa) {
        this.descTarefa = descTarefa;
    }

}
