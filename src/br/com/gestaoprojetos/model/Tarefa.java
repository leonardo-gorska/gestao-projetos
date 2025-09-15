package br.com.gestaoprojetos.model;

import java.time.LocalDate;

/**
 * Classe modelo Tarefa.
 * Representa uma tarefa vinculada a um projeto e a um usuário responsável.
 */
public class Tarefa {

    private int id;
    private String titulo;
    private String descricao;
    private String status;
    private LocalDate dataInicioPrevista;
    private LocalDate dataTerminoPrevista;
    private Usuario responsavel;
    private Projeto projeto;

    public Tarefa() {}

    // ---------- Getters e Setters ----------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDataInicioPrevista() { return dataInicioPrevista; }
    public void setDataInicioPrevista(LocalDate dataInicioPrevista) { this.dataInicioPrevista = dataInicioPrevista; }

    public LocalDate getDataTerminoPrevista() { return dataTerminoPrevista; }
    public void setDataTerminoPrevista(LocalDate dataTerminoPrevista) { this.dataTerminoPrevista = dataTerminoPrevista; }

    public Usuario getResponsavel() { return responsavel; }
    public void setResponsavel(Usuario responsavel) { this.responsavel = responsavel; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
}
