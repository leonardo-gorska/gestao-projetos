package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.Perfil;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerfilDAO {

    public void salvar(Perfil perfil) throws SQLException {
        String sql = "INSERT INTO perfil (descricao) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, perfil.getDescricao());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    perfil.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Perfil perfil) throws SQLException {
        String sql = "UPDATE perfil SET descricao=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, perfil.getDescricao());
            stmt.setInt(2, perfil.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM perfil WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Perfil> listarTodos() throws SQLException {
        List<Perfil> perfis = new ArrayList<>();
        String sql = "SELECT id, descricao FROM perfil";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Perfil p = new Perfil();
                p.setId(rs.getInt("id"));
                p.setDescricao(rs.getString("descricao"));
                perfis.add(p);
            }
        }
        return perfis;
    }

    public Perfil buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, descricao FROM perfil WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Perfil p = new Perfil();
                    p.setId(rs.getInt("id"));
                    p.setDescricao(rs.getString("descricao"));
                    return p;
                }
            }
        }
        return null;
    }
}
