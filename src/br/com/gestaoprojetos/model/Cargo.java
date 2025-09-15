package br.com.gestaoprojetos.model;

public class Cargo {
    private int id;
    private String nome;

    public Cargo() { }

    public Cargo(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome; // útil para exibir no ComboBox
    }
}
