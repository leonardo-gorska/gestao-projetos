package br.com.gestaoprojetos.service;

import br.com.gestaoprojetos.dao.HistoricoTarefaDAO;
import br.com.gestaoprojetos.model.HistoricoTarefa;

import java.sql.SQLException;
import java.util.List;

public class HistoricoTarefaService {
    private final HistoricoTarefaDAO dao = new HistoricoTarefaDAO();

    public void salvar(HistoricoTarefa historico) throws SQLException {
        dao.salvar(historico);
    }

    public List<HistoricoTarefa> listarPorTarefa(int tarefaId) throws SQLException {
        return dao.listarPorTarefa(tarefaId);
    }
}
