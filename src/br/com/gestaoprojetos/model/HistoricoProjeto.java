package br.com.gestaoprojetos.model;

import java.time.LocalDateTime;

public class HistoricoProjeto {
    private int id;
    private int projetoId;
    private Usuario usuario;
    private String acao;
    private String detalhe;
    private LocalDateTime dataHora;

    public HistoricoProjeto() {}

    public HistoricoProjeto(int projetoId, Usuario usuario, String acao, String detalhe) {
        this.projetoId = projetoId;
        this.usuario = usuario;
        this.acao = acao;
        this.detalhe = detalhe;
        this.dataHora = LocalDateTime.now();
    }

    // getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProjetoId() { return projetoId; }
    public void setProjetoId(int projetoId) { this.projetoId = projetoId; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getDetalhe() { return detalhe; }
    public void setDetalhe(String detalhe) { this.detalhe = detalhe; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
