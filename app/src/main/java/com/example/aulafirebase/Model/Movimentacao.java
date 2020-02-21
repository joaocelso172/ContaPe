package com.example.aulafirebase.Model;

public class Movimentacao {

    private String nomeTarefa, descTarefa, dataTarefa, tipo, categoria;
    private Double valor;

    public Movimentacao(String nomeTarefa, String descTarefa, String dataTarefa, String tipo, String categoria, Double valor) {
        this.nomeTarefa = nomeTarefa;
        this.descTarefa = descTarefa;
        this.dataTarefa = dataTarefa;
        this.tipo = tipo;
        this.categoria = categoria;
        this.valor = valor;
    }

    public String getDataTarefa() {
        return dataTarefa;
    }

    public void setDataTarefa(String dataTarefa) {
        this.dataTarefa = dataTarefa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Movimentacao(){

    }

    public Movimentacao(String nomeTarefa, String descTarefa) {
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
