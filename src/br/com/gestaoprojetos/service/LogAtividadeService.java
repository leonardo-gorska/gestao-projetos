package br.com.gestaoprojetos.service;

import br.com.gestaoprojetos.dao.LogAtividadeDAO;
import br.com.gestaoprojetos.model.LogAtividade;

import java.sql.SQLException;

public class LogAtividadeService {
    private final LogAtividadeDAO dao = new LogAtividadeDAO();

    public void salvar(LogAtividade log) throws SQLException {
        dao.salvar(log);
    }
}
