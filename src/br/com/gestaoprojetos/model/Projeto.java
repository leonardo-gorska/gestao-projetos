package br.com.gestaoprojetos.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Projeto {
    private int id;
    private String nome;

    // 🔹 Agora o projeto pode ter várias equipes
    private List<Equipe> equipes = new ArrayList<>();

    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataTerminoPrevista; // novo campo
    private LocalDate dataTerminoReal;     // novo campo
    private String status;
    private Usuario gerente;               // novo campo

    public Projeto() {}

    public Projeto(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    // --- Getters e Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    // 🔹 Trabalhando com lista de equipes
    public List<Equipe> getEquipes() {
        return equipes;
    }

    @Override
    public String toString() {
        return nome != null ? nome : "Projeto sem nome";
    }

    public void setEquipes(List<Equipe> equipes) {
        this.equipes = equipes;
    }

    public void addEquipe(Equipe equipe) {
        if (equipe != null && !this.equipes.contains(equipe)) {
            this.equipes.add(equipe);
        }
    }

    public void removeEquipe(Equipe equipe) {
        this.equipes.remove(equipe);
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataTerminoPrevista() { return dataTerminoPrevista; }
    public void setDataTerminoPrevista(LocalDate dataTerminoPrevista) { this.dataTerminoPrevista = dataTerminoPrevista; }

    public LocalDate getDataTerminoReal() { return dataTerminoReal; }
    public void setDataTerminoReal(LocalDate dataTerminoReal) { this.dataTerminoReal = dataTerminoReal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Usuario getGerente() { return gerente; }
    public void setGerente(Usuario gerente) { this.gerente = gerente; }
}
