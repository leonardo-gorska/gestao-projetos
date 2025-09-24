package br.com.gestaoprojetos.model;

import java.time.LocalDateTime;

public class HistoricoTarefa {
    private int id;
    private int tarefaId;
    private Usuario usuario;
    private String acao;
    private String detalhe;
    private LocalDateTime dataHora;

    public HistoricoTarefa() {}

    public HistoricoTarefa(int tarefaId, Usuario usuario, String acao, String detalhe) {
        this.tarefaId = tarefaId;
        this.usuario = usuario;
        this.acao = acao;
        this.detalhe = detalhe;
        this.dataHora = LocalDateTime.now();
    }

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTarefaId() { return tarefaId; }
    public void setTarefaId(int tarefaId) { this.tarefaId = tarefaId; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getDetalhe() { return detalhe; }
    public void setDetalhe(String detalhe) { this.detalhe = detalhe; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
