package br.com.gestaoprojetos.model;

import java.util.ArrayList;
import java.util.List;
import br.com.gestaoprojetos.model.Usuario;

public class Equipe {
    private int id;
    private String nome;
    private String descricao;
    private String perfil;
    private List<Usuario> membros = new ArrayList<>(); // lista de usuários da equipe

    public Equipe() {}

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public List<Usuario> getMembros() { return membros; }
    public void setMembros(List<Usuario> membros) { this.membros = membros; }
}
