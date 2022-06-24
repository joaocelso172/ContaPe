package com.devinstance.contape.model;

public class SharedTransaction {
    String desc, valor;

    public SharedTransaction() {
    }

    public SharedTransaction(String desc, String valor) {
        this.desc = desc;
        this.valor = valor;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
