package br.com.gestaoprojetos.service;

import br.com.gestaoprojetos.dao.PerfilDAO;
import br.com.gestaoprojetos.model.Perfil;

import java.sql.SQLException;
import java.util.List;

public class PerfilService {
    private final PerfilDAO perfilDAO = new PerfilDAO();

    public void salvar(Perfil perfil) throws SQLException {
        perfilDAO.salvar(perfil);
    }

    public List<Perfil> listarTodos() throws SQLException {
        return perfilDAO.listarTodos();
    }

    public void atualizar(Perfil perfil) throws SQLException {
        perfilDAO.atualizar(perfil);
    }

    public void excluir(int id) throws SQLException {
        perfilDAO.excluir(id);
    }
}
