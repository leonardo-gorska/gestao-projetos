package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.Projeto;
import br.com.gestaoprojetos.model.Equipe;
import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO {

    // --- Salvar ---
    public Projeto salvar(Projeto projeto) throws SQLException {
        String sql = """
            INSERT INTO projeto (
                nome, descricao, data_inicio,
                data_termino_prevista, data_termino_real,
                status, gerente_id
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setObject(3, projeto.getDataInicio());
            stmt.setObject(4, projeto.getDataTerminoPrevista());
            stmt.setObject(5, projeto.getDataTerminoReal());
            stmt.setString(6, projeto.getStatus());
            stmt.setObject(7, projeto.getGerente() != null ? projeto.getGerente().getId() : null);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    projeto.setId(rs.getInt(1));
                }
            }

            // --- Inserir associações com equipes ---
            if (projeto.getEquipes() != null && !projeto.getEquipes().isEmpty()) {
                String sqlAssoc = "INSERT INTO projeto_equipe (projeto_id, equipe_id) VALUES (?, ?)";
                try (PreparedStatement stmtAssoc = conn.prepareStatement(sqlAssoc)) {
                    for (Equipe e : projeto.getEquipes()) {
                        stmtAssoc.setInt(1, projeto.getId());
                        stmtAssoc.setInt(2, e.getId());
                        stmtAssoc.addBatch();
                    }
                    stmtAssoc.executeBatch();
                }
            }
        }
        return projeto;
    }

    // --- Atualizar ---
    public void atualizar(Projeto projeto) throws SQLException {
        String sql = """
            UPDATE projeto
            SET nome=?, descricao=?, data_inicio=?,
                data_termino_prevista=?, data_termino_real=?,
                status=?, gerente_id=?
            WHERE id=?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setObject(3, projeto.getDataInicio());
            stmt.setObject(4, projeto.getDataTerminoPrevista());
            stmt.setObject(5, projeto.getDataTerminoReal());
            stmt.setString(6, projeto.getStatus());
            stmt.setObject(7, projeto.getGerente() != null ? projeto.getGerente().getId() : null);
            stmt.setInt(8, projeto.getId());

            stmt.executeUpdate();

            // --- Atualizar associações com equipes ---
            String sqlDel = "DELETE FROM projeto_equipe WHERE projeto_id=?";
            try (PreparedStatement stmtDel = conn.prepareStatement(sqlDel)) {
                stmtDel.setInt(1, projeto.getId());
                stmtDel.executeUpdate();
            }

            if (projeto.getEquipes() != null && !projeto.getEquipes().isEmpty()) {
                String sqlAssoc = "INSERT INTO projeto_equipe (projeto_id, equipe_id) VALUES (?, ?)";
                try (PreparedStatement stmtAssoc = conn.prepareStatement(sqlAssoc)) {
                    for (Equipe e : projeto.getEquipes()) {
                        stmtAssoc.setInt(1, projeto.getId());
                        stmtAssoc.setInt(2, e.getId());
                        stmtAssoc.addBatch();
                    }
                    stmtAssoc.executeBatch();
                }
            }
        }
    }

    // --- Excluir ---
    public void excluir(int id) throws SQLException {
        String sqlDelAssoc = "DELETE FROM projeto_equipe WHERE projeto_id=?";
        String sqlDelProj = "DELETE FROM projeto WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmtAssoc = conn.prepareStatement(sqlDelAssoc);
             PreparedStatement stmtProj = conn.prepareStatement(sqlDelProj)) {

            stmtAssoc.setInt(1, id);
            stmtAssoc.executeUpdate();

            stmtProj.setInt(1, id);
            stmtProj.executeUpdate();
        }
    }

    // --- Buscar por ID ---
    public Projeto buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT p.id, p.nome, p.descricao,
                   p.data_inicio, p.data_termino_prevista,
                   p.data_termino_real, p.status,
                   p.gerente_id
            FROM projeto p
            WHERE p.id=?
            """;

        Projeto p = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    p = new Projeto();
                    p.setId(rs.getInt("id"));
                    p.setNome(rs.getString("nome"));
                    p.setDescricao(rs.getString("descricao"));
                    p.setDataInicio(rs.getObject("data_inicio", java.time.LocalDate.class));
                    p.setDataTerminoPrevista(rs.getObject("data_termino_prevista", java.time.LocalDate.class));
                    p.setDataTerminoReal(rs.getObject("data_termino_real", java.time.LocalDate.class));
                    p.setStatus(rs.getString("status"));
                }
            }

            if (p != null) {
                // --- Buscar equipes do projeto ---
                String sqlEquipes = """
                    SELECT e.id, e.nome, e.descricao
                    FROM equipe e
                    INNER JOIN projeto_equipe pe ON e.id = pe.equipe_id
                    WHERE pe.projeto_id = ?
                    """;

                List<Equipe> equipes = new ArrayList<>();
                try (PreparedStatement stmtEq = conn.prepareStatement(sqlEquipes)) {
                    stmtEq.setInt(1, id);
                    try (ResultSet rsEq = stmtEq.executeQuery()) {
                        while (rsEq.next()) {
                            Equipe e = new Equipe();
                            e.setId(rsEq.getInt("id"));
                            e.setNome(rsEq.getString("nome"));
                            e.setDescricao(rsEq.getString("descricao"));

                            // 🔹 Carregar membros da equipe
                            carregarMembrosEquipe(conn, e);

                            equipes.add(e);
                        }
                    }
                }
                p.setEquipes(equipes);
            }
        }
        return p;
    }

    // --- Listar todos ---
    public List<Projeto> listarTodos() throws SQLException {
        String sql = """
            SELECT p.id, p.nome, p.descricao,
                   p.data_inicio, p.data_termino_prevista,
                   p.data_termino_real, p.status,
                   p.gerente_id
            FROM projeto p
            """;

        List<Projeto> projetos = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Projeto p = new Projeto();
                int projId = rs.getInt("id");
                p.setId(projId);
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                p.setDataInicio(rs.getObject("data_inicio", java.time.LocalDate.class));
                p.setDataTerminoPrevista(rs.getObject("data_termino_prevista", java.time.LocalDate.class));
                p.setDataTerminoReal(rs.getObject("data_termino_real", java.time.LocalDate.class));
                p.setStatus(rs.getString("status"));

                // --- Buscar equipes ---
                String sqlEquipes = """
                    SELECT e.id, e.nome, e.descricao
                    FROM equipe e
                    INNER JOIN projeto_equipe pe ON e.id = pe.equipe_id
                    WHERE pe.projeto_id = ?
                    """;

                List<Equipe> equipes = new ArrayList<>();
                try (PreparedStatement stmtEq = conn.prepareStatement(sqlEquipes)) {
                    stmtEq.setInt(1, projId);
                    try (ResultSet rsEq = stmtEq.executeQuery()) {
                        while (rsEq.next()) {
                            Equipe e = new Equipe();
                            e.setId(rsEq.getInt("id"));
                            e.setNome(rsEq.getString("nome"));
                            e.setDescricao(rsEq.getString("descricao"));

                            // 🔹 Carregar membros da equipe
                            carregarMembrosEquipe(conn, e);

                            equipes.add(e);
                        }
                    }
                }
                p.setEquipes(equipes);

                projetos.add(p);
            }
        }
        return projetos;
    }

    // --- Carregar membros de uma equipe ---
    private void carregarMembrosEquipe(Connection conn, Equipe equipe) throws SQLException {
        String sqlMembros = """
            SELECT u.id, u.nome, u.email
            FROM usuario u
            INNER JOIN equipe_usuario eu ON u.id = eu.usuario_id
            WHERE eu.equipe_id = ?
            """;

        List<Usuario> membros = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sqlMembros)) {
            stmt.setInt(1, equipe.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    membros.add(u);
                }
            }
        }
        equipe.setMembros(membros);
    }
}
