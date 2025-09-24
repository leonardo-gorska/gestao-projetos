package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.Cargo;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CargoDAO {

    // --- Salvar novo cargo ---
    public void salvar(Cargo cargo) throws SQLException {
        String sql = "INSERT INTO cargo (nome) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cargo.getDescricao());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cargo.setId(rs.getInt(1));
                }
            }
        }
    }

    // --- Listar todos os cargos ---
    public List<Cargo> listarTodos() throws SQLException {
        List<Cargo> cargos = new ArrayList<>();
        String sql = "SELECT id, nome FROM cargo";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cargos.add(mapearCargo(rs));
            }
        }
        return cargos;
    }

    // --- Buscar cargo por ID ---
    public Cargo buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome FROM cargo WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCargo(rs);
                }
            }
        }
        return null;
    }

    // --- Atualizar cargo ---
    public void atualizar(Cargo cargo) throws SQLException {
        String sql = "UPDATE cargo SET nome=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cargo.getDescricao());
            stmt.setInt(2, cargo.getId());
            stmt.executeUpdate();
        }
    }

    // --- Excluir cargo ---
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM cargo WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // --- Método auxiliar ---
    private Cargo mapearCargo(ResultSet rs) throws SQLException {
        Cargo c = new Cargo();
        c.setId(rs.getInt("id"));
        c.setDescricao(rs.getString("nome"));
        return c;
    }
}
