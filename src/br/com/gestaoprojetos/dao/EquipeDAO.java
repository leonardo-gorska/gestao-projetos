package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.Equipe;
import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {

    // Salva equipe e associa membros
    public void salvar(Equipe e) throws SQLException {
        String sql = "INSERT INTO equipe (nome, descricao, perfil) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getNome());
            ps.setString(2, e.getDescricao());
            ps.setString(3, e.getPerfil());
            ps.executeUpdate();

            // Pega o ID gerado da equipe
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int equipeId = rs.getInt(1);

                    // Associa membros
                    String sqlMembros = "INSERT INTO equipe_usuario (equipe_id, usuario_id) VALUES (?, ?)";
                    try (PreparedStatement psMembros = con.prepareStatement(sqlMembros)) {
                        for (Usuario u : e.getMembros()) {
                            psMembros.setInt(1, equipeId);
                            psMembros.setInt(2, u.getId());
                            psMembros.addBatch();
                        }
                        psMembros.executeBatch();
                    }
                }
            }
        }
    }

    // Lista todas as equipes
    public List<Equipe> listarTodos() throws SQLException {
        List<Equipe> lista = new ArrayList<>();
        String sql = "SELECT * FROM equipe";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipe e = new Equipe();
                e.setId(rs.getInt("id"));
                e.setNome(rs.getString("nome"));
                e.setDescricao(rs.getString("descricao"));
                e.setPerfil(rs.getString("perfil"));
                lista.add(e);
            }
        }
        return lista;
    }
}
