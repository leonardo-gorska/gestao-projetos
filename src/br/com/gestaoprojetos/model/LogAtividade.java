package br.com.gestaoprojetos.model;

import java.time.LocalDateTime;

public class LogAtividade {
    private int id;
    private int usuarioId;
    private String acao;
    private String entidade;
    private LocalDateTime dataHora;

    public LogAtividade() {}

    public LogAtividade(int usuarioId, String acao, String entidade) {
        this.usuarioId = usuarioId;
        this.acao = acao;
        this.entidade = entidade;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getEntidade() { return entidade; }
    public void setEntidade(String entidade) { this.entidade = entidade; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
