package com.example.aulafirebase;

public class Tarefa {

    private String nomeTarefa, descTarefa, pessoaAtribuida;
    private int prioridadeTarefa;


    public Tarefa(){

    }

    public Tarefa(String nomeTarefa, String descTarefa, String pessoaAtribuida, int prioridadeTarefa) {
        this.nomeTarefa = nomeTarefa;
        this.descTarefa = descTarefa;
        this.pessoaAtribuida = pessoaAtribuida;
        this.prioridadeTarefa = prioridadeTarefa;
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

    public String getPessoaAtribuida() {
        return pessoaAtribuida;
    }

    public void setPessoaAtribuida(String pessoaAtribuida) {
        this.pessoaAtribuida = pessoaAtribuida;
    }

    public int getPrioridadeTarefa() {
        return prioridadeTarefa;
    }

    public void setPrioridadeTarefa(int prioridadeTarefa) {
        this.prioridadeTarefa = prioridadeTarefa;
    }
}
