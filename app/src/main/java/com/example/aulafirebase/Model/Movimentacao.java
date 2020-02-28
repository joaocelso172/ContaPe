package com.example.aulafirebase.Model;

public class Movimentacao {

    private String descTarefa, dataTarefa, tipo, categoria;
    private Double valor;
    private String ID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getDescTarefa() {
        return descTarefa;
    }

    public void setDescTarefa(String descTarefa) {
        this.descTarefa = descTarefa;
    }

}
