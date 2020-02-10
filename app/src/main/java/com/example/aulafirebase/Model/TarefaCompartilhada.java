package com.example.aulafirebase.Model;

public class TarefaCompartilhada {
    String desc, valor;

    public TarefaCompartilhada() {
    }

    public TarefaCompartilhada(String desc, String valor) {
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
