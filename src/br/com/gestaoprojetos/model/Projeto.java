package br.com.gestaoprojetos.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Projeto {

    private int id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataTermino;
    private String status;

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataTermino() { return dataTermino; }
    public void setDataTermino(LocalDate dataTermino) { this.dataTermino = dataTermino; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Conversão para String para exibir no TableView
    public String getDataInicioString() {
        if (dataInicio == null) return "";
        return dataInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getDataTerminoString() {
        if (dataTermino == null) return "";
        return dataTermino.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
