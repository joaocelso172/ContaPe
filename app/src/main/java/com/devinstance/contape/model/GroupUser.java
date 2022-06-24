package com.devinstance.contape.model;

public class GroupUser {

    private String nomeUsuario, emailUsuario;

    private Boolean adm, aparecerFeed;

    public Boolean getAparecerFeed() {
        return aparecerFeed;
    }

    public void setAparecerFeed(Boolean aparecerFeed) {
        this.aparecerFeed = aparecerFeed;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public Boolean getAdm() {
        return adm;
    }

    public void setAdm(Boolean adm) {
        this.adm = adm;
    }
}
