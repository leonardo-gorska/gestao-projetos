package br.com.gestaoprojetos.service;

import br.com.gestaoprojetos.dao.EquipeDAO;
import br.com.gestaoprojetos.model.Equipe;

import java.sql.SQLException;
import java.util.List;

public class EquipeService {

    private final EquipeDAO equipeDAO = new EquipeDAO();

    public void salvar(Equipe equipe) throws SQLException {
        equipeDAO.salvar(equipe);
    }

    public void atualizar(Equipe equipe) throws SQLException {
        equipeDAO.atualizar(equipe);
    }

    public void excluir(int id) throws SQLException {
        equipeDAO.excluir(id);
    }

    public List<Equipe> listarTodos() throws SQLException {
        return equipeDAO.listarTodos();
    }

    // Busca por nome
    public List<Equipe> buscarPorNome(String nome) throws SQLException {
        return equipeDAO.buscarPorNome(nome);
    }

    // **NOVO** método para buscar por projeto
    public List<Equipe> buscarPorProjeto(int projetoId) throws SQLException {
        return equipeDAO.buscarPorProjeto(projetoId);
    }
}
