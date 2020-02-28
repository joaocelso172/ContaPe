package com.example.aulafirebase.Model;

import com.google.firebase.database.Exclude;

public class ResumoMensal {

    private Double receitaMensal, despesaMensal, saldoMensal;
    private String ano, mes, anoMes;

    @Exclude
    public String getAno() {
        return ano;
    }

    public Double getSaldoMensal() {
        return saldoMensal;
    }

    public void setSaldoMensal(Double saldoMensal) {
        this.saldoMensal = saldoMensal;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    @Exclude
    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Double getReceitaMensal() {
        return receitaMensal;
    }

    public void setReceitaMensal(Double receitaMensal) {
        this.receitaMensal = receitaMensal;
    }

    public Double getDespesaMensal() {
        return despesaMensal;
    }

    public void setDespesaMensal(Double despesaMensal) {
        this.despesaMensal = despesaMensal;
    }

    @Exclude
    public String getAnoMes() {
        return anoMes;
    }

    public void setAnoMes(String anoMes) {
        this.anoMes = anoMes;
    }

    public ResumoMensal() {
    }


    public ResumoMensal(Double receitaMensal, Double despesaMensal, Double saldoMensal, String ano, String mes) {
        this.ano = ano;
        this.mes = mes;
        this.receitaMensal = receitaMensal;
        this.despesaMensal = despesaMensal;
        this.saldoMensal = saldoMensal;
    }

    public ResumoMensal(Double receitaMensal, Double despesaMensal, Double saldoMensal, String anoMes) {
        this.anoMes = anoMes;
        this.receitaMensal = receitaMensal;
        this.despesaMensal = despesaMensal;
        this.saldoMensal = saldoMensal;
    }
}
