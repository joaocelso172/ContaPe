package com.example.aulafirebase.Model;

public class Movimentacao {

    private String descTarefa, dataTarefa, tipo, categoria, atribuicao;
    private Double valor;
    private int parcelaTotal, parcelaAtual;
    private String ID;
    private String tipoFaturamento;
    private Boolean ultimaContaRecorrente;

    public Boolean isUltimaContaRecorrente() {
        return ultimaContaRecorrente;
    }

    public void setUltimaContaRecorrente(Boolean ultimaContaRecorrente) {
        this.ultimaContaRecorrente = ultimaContaRecorrente;
    }

    public String getTipoFaturamento() {
        return tipoFaturamento;
    }

    public void setTipoFaturamento(String tipoFaturamento) {
        this.tipoFaturamento = tipoFaturamento;
    }

    public int getParcelaTotal() {
        return parcelaTotal;
    }

    public void setParcelaTotal(int parcelaTotal) {
        this.parcelaTotal = parcelaTotal;
    }

    public int getParcelaAtual() {
        return parcelaAtual;
    }

    public void setParcelaAtual(int parcelaAtual) {
        this.parcelaAtual = parcelaAtual;
    }

    public String getAtribuicao() {
        return atribuicao;
    }

    public void setAtribuicao(String atribuicao) {
        this.atribuicao = atribuicao;
    }

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
