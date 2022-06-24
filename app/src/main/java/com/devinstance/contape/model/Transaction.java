package com.devinstance.contape.model;

import java.io.Serializable;

public class Transaction implements Serializable {

    private String descTarefa, dataTarefa, tipo, categoria, atribuicao;
    private Double valor, valorTotalParcelas;
    private int parcelaTotal, parcelaAtual;
    private String ID;
    private String tipoFaturamento, nomeGrupo, idGrupoVinculo;
    private Boolean ultimaContaRecorrente, aparecerFeed, inverso; //o booleano inverso representa que aparecerá como movimentação contrária a registrada no grupo quando esse valor for verdadeiro

    public Boolean getInverso() {
        return inverso;
    }

    public void setInverso(Boolean inverso) {
        this.inverso = inverso;
    }

    public String getIdGrupoVinculo() {
        return idGrupoVinculo;
    }

    public void setIdGrupoVinculo(String idGrupoVinculo) {
        this.idGrupoVinculo = idGrupoVinculo;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public Boolean getAparecerFeed() {
        return aparecerFeed;
    }

    public void setAparecerFeed(Boolean aparecerFeed) {
        this.aparecerFeed = aparecerFeed;
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

    public Transaction(){

    }

    public String getDescTarefa() {
        return descTarefa;
    }

    public void setDescTarefa(String descTarefa) {
        this.descTarefa = descTarefa;
    }

}
