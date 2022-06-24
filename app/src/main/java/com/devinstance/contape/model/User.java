package com.devinstance.contape.model;

import com.google.firebase.database.Exclude;

public class User {

    private String email, nome = "Sem nome";
    private boolean isCadastrado;
    private Double despesaTotal = 0.00;
    private Double receitaTotal = 0.00;
    private Double saldoDisponivel = 0.00;
    //Definirá o valor que alertará o usuario
    private Double valorAlerta = 200.00;

    public Double getValorAlerta() {
        return valorAlerta;
    }

    public void setValorAlerta(Double valorAlerta) {
        this.valorAlerta = valorAlerta;
    }

    public double getDespesaTotal() {
        return despesaTotal;
    }

    public void setDespesaTotal(Double despesaTotal) {
        this.despesaTotal = despesaTotal;
    }

    public double getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(Double receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public double getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public void setSaldoDisponivel(Double saldoDisponivel) {
        this.saldoDisponivel = saldoDisponivel;
    }

    @Exclude
    public boolean isCadastrado() {
        return isCadastrado;
    }

    public void setCadastrado(boolean cadastrado) {
        isCadastrado = cadastrado;
    }

    public User(){

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Exclude
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
