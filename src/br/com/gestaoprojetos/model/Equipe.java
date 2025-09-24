package br.com.gestaoprojetos.model;

import java.util.List;

public class Equipe {
    private int id;
    private String nome;
    private String descricao;
    private Usuario gerente;
    private List<Usuario> membros;
    private boolean concluida; // ⚡ novo campo

    public Equipe() {}

    public Equipe(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Usuario getGerente() { return gerente; }
    public void setGerente(Usuario gerente) { this.gerente = gerente; }

    public List<Usuario> getMembros() { return membros; }
    public void setMembros(List<Usuario> membros) { this.membros = membros; }

    // alias para compatibilidade com TarefasController
    public List<Usuario> getUsuarios() {
        return membros;
    }

    // novo getter e setter
    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }

    @Override
    public String toString() {
        return nome != null ? nome : "";
    }
}
