package br.com.gestaoprojetos.service;

import br.com.gestaoprojetos.dao.CargoDAO;
import br.com.gestaoprojetos.model.Cargo;

import java.sql.SQLException;
import java.util.List;

public class CargoService {

    private final CargoDAO cargoDAO = new CargoDAO();

    public void salvar(Cargo cargo) throws SQLException {
        cargoDAO.salvar(cargo);
    }

    public List<Cargo> listarTodos() throws SQLException {
        return cargoDAO.listarTodos();
    }

    public void atualizar(Cargo cargo) throws SQLException {
        cargoDAO.atualizar(cargo);
    }

    public void excluir(int id) throws SQLException {
        cargoDAO.excluir(id);
    }

    public Cargo buscarPorId(int id) throws SQLException {
        return cargoDAO.buscarPorId(id);
    }
}
