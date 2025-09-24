package br.com.gestaoprojetos.model;

public class Cargo {
    private int id;
    private String descricao;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() {
        return descricao; // importante para exibir no ComboBox
    }
}
