package br.com.gestaoprojetos.service;

import br.com.gestaoprojetos.dao.ProjetoDAO;
import br.com.gestaoprojetos.model.Projeto;

import java.sql.SQLException;
import java.util.List;

public class RelatorioService {

    private final ProjetoDAO projetoDAO = new ProjetoDAO();

    public List<Projeto> listarProjetos() throws SQLException {
        return projetoDAO.listarTodos();
    }
}
