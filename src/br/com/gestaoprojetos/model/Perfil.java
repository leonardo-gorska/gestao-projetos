package br.com.gestaoprojetos.model;

public class Perfil {
    private int id;
    private String descricao;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    // --- Métodos de permissão para o usuário ---
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(descricao);
    }

    public boolean isGerente() {
        return "GERENTE".equalsIgnoreCase(descricao);
    }

    @Override
    public String toString() {
        return descricao != null ? descricao : "";
    }
}
