package com.example.aulafirebase.Model;

public class Tarefa {

    private String nomeTarefa, descTarefa;
    private int valorTarefa;


    public Tarefa(){

    }

    public Tarefa(String nomeTarefa, String descTarefa, int valorTarefa) {
        this.nomeTarefa = nomeTarefa;
        this.descTarefa = descTarefa;
        this.valorTarefa = valorTarefa;
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

    public int getValorTarefa() {
        return valorTarefa;
    }

    public void setValorTarefa(int valorTarefa) {
        this.valorTarefa = valorTarefa;
    }
}
