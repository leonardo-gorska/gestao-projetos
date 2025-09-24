package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.Equipe;
import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {

    public void salvar(Equipe equipe) throws SQLException {
        String sql = "INSERT INTO equipe (nome, descricao, gerente_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, equipe.getNome());
            stmt.setString(2, equipe.getDescricao());
            stmt.setObject(3, equipe.getGerente() != null ? equipe.getGerente().getId() : null);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    equipe.setId(rs.getInt(1));
                }
            }

            salvarMembros(equipe, conn);
        }
    }

    public void atualizar(Equipe equipe) throws SQLException {
        String sql = "UPDATE equipe SET nome = ?, descricao = ?, gerente_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, equipe.getNome());
            stmt.setString(2, equipe.getDescricao());
            stmt.setObject(3, equipe.getGerente() != null ? equipe.getGerente().getId() : null);
            stmt.setInt(4, equipe.getId());
            stmt.executeUpdate();

            // Atualiza membros
            String sqlDelete = "DELETE FROM equipe_usuario WHERE equipe_id = ?";
            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete)) {
                stmtDelete.setInt(1, equipe.getId());
                stmtDelete.executeUpdate();
            }

            salvarMembros(equipe, conn);
        }
    }

    public void excluir(int id) throws SQLException {
        String sqlDeleteMembros = "DELETE FROM equipe_usuario WHERE equipe_id = ?";
        String sqlEquipe = "DELETE FROM equipe WHERE id=?";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteMembros)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlEquipe)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        }
    }

    public List<Equipe> listarTodos() throws SQLException {
        return listarPorNome("%"); // chama método genérico
    }

    // Novo método: buscar por nome
    public List<Equipe> buscarPorNome(String nome) throws SQLException {
        return listarPorNome("%" + nome + "%");
    }

    // --- NOVO MÉTODO: buscar por projeto ---
    public List<Equipe> buscarPorProjeto(int projetoId) throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        String sql = "SELECT e.*, u.nome AS gerente_nome " +
                "FROM equipe e " +
                "LEFT JOIN usuario u ON e.gerente_id = u.id " +
                "JOIN equipe_projeto ep ON e.id = ep.equipe_id " +
                "WHERE ep.projeto_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projetoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Equipe e = new Equipe();
                    e.setId(rs.getInt("id"));
                    e.setNome(rs.getString("nome"));
                    e.setDescricao(rs.getString("descricao"));

                    int gerenteId = rs.getInt("gerente_id");
                    if (!rs.wasNull()) {
                        Usuario gerente = new Usuario();
                        gerente.setId(gerenteId);
                        gerente.setNome(rs.getString("gerente_nome"));
                        e.setGerente(gerente);
                    }

                    // Carregar membros
                    String sqlMembros = "SELECT u.id, u.nome FROM usuario u " +
                            "JOIN equipe_usuario eu ON u.id = eu.usuario_id " +
                            "WHERE eu.equipe_id = ?";
                    try (PreparedStatement stmtMembro = conn.prepareStatement(sqlMembros)) {
                        stmtMembro.setInt(1, e.getId());
                        try (ResultSet rsMembro = stmtMembro.executeQuery()) {
                            List<Usuario> membros = new ArrayList<>();
                            while (rsMembro.next()) {
                                Usuario u = new Usuario();
                                u.setId(rsMembro.getInt("id"));
                                u.setNome(rsMembro.getString("nome"));
                                membros.add(u);
                            }
                            e.setMembros(membros);
                        }
                    }

                    equipes.add(e);
                }
            }
        }

        return equipes;
    }

    // Método auxiliar para listar por filtro de nome
    private List<Equipe> listarPorNome(String filtro) throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        String sql = "SELECT e.*, u.nome AS gerente_nome FROM equipe e LEFT JOIN usuario u ON e.gerente_id = u.id WHERE e.nome LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filtro);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Equipe e = new Equipe();
                    e.setId(rs.getInt("id"));
                    e.setNome(rs.getString("nome"));
                    e.setDescricao(rs.getString("descricao"));

                    int gerenteId = rs.getInt("gerente_id");
                    if (!rs.wasNull()) {
                        Usuario gerente = new Usuario();
                        gerente.setId(gerenteId);
                        gerente.setNome(rs.getString("gerente_nome"));
                        e.setGerente(gerente);
                    }

                    // Carregar membros
                    String sqlMembros = "SELECT u.id, u.nome FROM usuario u " +
                            "JOIN equipe_usuario eu ON u.id = eu.usuario_id " +
                            "WHERE eu.equipe_id = ?";
                    try (PreparedStatement stmtMembro = conn.prepareStatement(sqlMembros)) {
                        stmtMembro.setInt(1, e.getId());
                        try (ResultSet rsMembro = stmtMembro.executeQuery()) {
                            List<Usuario> membros = new ArrayList<>();
                            while (rsMembro.next()) {
                                Usuario u = new Usuario();
                                u.setId(rsMembro.getInt("id"));
                                u.setNome(rsMembro.getString("nome"));
                                membros.add(u);
                            }
                            e.setMembros(membros);
                        }
                    }

                    equipes.add(e);
                }
            }
        }
        return equipes;
    }

    private void salvarMembros(Equipe equipe, Connection conn) throws SQLException {
        if (equipe.getMembros() != null && !equipe.getMembros().isEmpty()) {
            String sqlMembros = "INSERT INTO equipe_usuario (equipe_id, usuario_id) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlMembros)) {
                for (Usuario u : equipe.getMembros()) {
                    stmt.setInt(1, equipe.getId());
                    stmt.setInt(2, u.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }
}
