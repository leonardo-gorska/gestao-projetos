package br.com.gestaoprojetos.model;

import java.time.LocalDateTime;

public class LogAtividade {
    private int id;
    private Usuario usuario;
    private String acao;
    private String entidade; // ex: "tarefa", "projeto", "usuario"
    private LocalDateTime dataHora;

    public LogAtividade() {}

    public LogAtividade(Usuario usuario, String acao, String entidade) {
        this.usuario = usuario;
        this.acao = acao;
        this.entidade = entidade;
        this.dataHora = LocalDateTime.now();
    }

    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getEntidade() { return entidade; }
    public void setEntidade(String entidade) { this.entidade = entidade; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
