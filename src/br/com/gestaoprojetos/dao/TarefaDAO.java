package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.Tarefa;
import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.model.Projeto;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    public Tarefa salvar(Tarefa tarefa) throws SQLException {
        String sql = """
            INSERT INTO tarefa (
                titulo, descricao, projeto_id, responsavel_id,
                data_inicio_prevista, data_termino_prevista,
                data_inicio, data_termino, status
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setInt(3, tarefa.getProjeto().getId());
            stmt.setObject(4, tarefa.getResponsavel() != null ? tarefa.getResponsavel().getId() : null);
            stmt.setObject(5, tarefa.getDataInicioPrevista());
            stmt.setObject(6, tarefa.getDataTerminoPrevista());
            stmt.setObject(7, tarefa.getDataInicio());
            stmt.setObject(8, tarefa.getDataTermino());
            stmt.setString(9, tarefa.getStatus());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    tarefa.setId(rs.getInt(1));
                }
            }
        }
        return tarefa;
    }

    public void atualizar(Tarefa tarefa) throws SQLException {
        String sql = """
            UPDATE tarefa
            SET titulo=?, descricao=?, projeto_id=?, responsavel_id=?,
                data_inicio_prevista=?, data_termino_prevista=?,
                data_inicio=?, data_termino=?, status=?
            WHERE id=?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setInt(3, tarefa.getProjeto().getId());
            stmt.setObject(4, tarefa.getResponsavel() != null ? tarefa.getResponsavel().getId() : null);
            stmt.setObject(5, tarefa.getDataInicioPrevista());
            stmt.setObject(6, tarefa.getDataTerminoPrevista());
            stmt.setObject(7, tarefa.getDataInicio());
            stmt.setObject(8, tarefa.getDataTermino());
            stmt.setString(9, tarefa.getStatus());
            stmt.setInt(10, tarefa.getId());

            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM tarefa WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Tarefa buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT t.id, t.titulo, t.descricao,
                   t.projeto_id, p.nome AS projeto_nome,
                   t.responsavel_id, u.nome AS responsavel_nome,
                   t.data_inicio_prevista, t.data_termino_prevista,
                   t.data_inicio, t.data_termino, t.status
            FROM tarefa t
            LEFT JOIN projeto p ON t.projeto_id = p.id
            LEFT JOIN usuario u ON t.responsavel_id = u.id
            WHERE t.id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Tarefa> result = mapearResultado(rs);
                return result.isEmpty() ? null : result.get(0);
            }
        }
    }

    public List<Tarefa> listarTodos() throws SQLException {
        String sql = """
            SELECT t.id, t.titulo, t.descricao,
                   t.projeto_id, p.nome AS projeto_nome,
                   t.responsavel_id, u.nome AS responsavel_nome,
                   t.data_inicio_prevista, t.data_termino_prevista,
                   t.data_inicio, t.data_termino, t.status
            FROM tarefa t
            LEFT JOIN projeto p ON t.projeto_id = p.id
            LEFT JOIN usuario u ON t.responsavel_id = u.id
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return mapearResultado(rs);
        }
    }

    public List<Tarefa> listarPorProjeto(int projetoId) throws SQLException {
        String sql = """
            SELECT t.id, t.titulo, t.descricao,
                   t.projeto_id, p.nome AS projeto_nome,
                   t.responsavel_id, u.nome AS responsavel_nome,
                   t.data_inicio_prevista, t.data_termino_prevista,
                   t.data_inicio, t.data_termino, t.status
            FROM tarefa t
            LEFT JOIN projeto p ON t.projeto_id = p.id
            LEFT JOIN usuario u ON t.responsavel_id = u.id
            WHERE t.projeto_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projetoId);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapearResultado(rs);
            }
        }
    }

    private List<Tarefa> mapearResultado(ResultSet rs) throws SQLException {
        List<Tarefa> tarefas = new ArrayList<>();
        while (rs.next()) {
            Tarefa t = new Tarefa();
            t.setId(rs.getInt("id"));
            t.setTitulo(rs.getString("titulo"));
            t.setDescricao(rs.getString("descricao"));

            Projeto p = new Projeto();
            p.setId(rs.getInt("projeto_id"));
            p.setNome(rs.getString("projeto_nome"));
            t.setProjeto(p);

            Usuario u = new Usuario();
            u.setId(rs.getInt("responsavel_id"));
            u.setNome(rs.getString("responsavel_nome"));
            t.setResponsavel(u);

            t.setDataInicioPrevista(rs.getObject("data_inicio_prevista", java.time.LocalDate.class));
            t.setDataTerminoPrevista(rs.getObject("data_termino_prevista", java.time.LocalDate.class));
            t.setDataInicio(rs.getObject("data_inicio", java.time.LocalDate.class));
            t.setDataTermino(rs.getObject("data_termino", java.time.LocalDate.class));
            t.setStatus(rs.getString("status"));

            tarefas.add(t);
        }
        return tarefas;
    }
}
