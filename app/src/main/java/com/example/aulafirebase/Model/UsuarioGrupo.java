package com.example.aulafirebase.Model;

public class UsuarioGrupo {
    private String grupoIdUsuario, nomeGrupo;
    private Boolean receberMovFeed;

    public String getGrupoIdUsuario() {
        return grupoIdUsuario;
    }

    public Boolean getReceberMovFeed() {
        return receberMovFeed;
    }

    public void setReceberMovFeed(Boolean receberMovFeed) {
        this.receberMovFeed = receberMovFeed;
    }

    public void setGrupoIdUsuario(String grupoIdUsuario) {
        this.grupoIdUsuario = grupoIdUsuario;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }
}
