package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.Tarefa;
import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.model.Projeto;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO para a entidade Tarefa.
 * Responsável pelo CRUD de tarefas no banco de dados.
 */
public class TarefaDAO {

    /**
     * Lista todas as tarefas do banco de dados.
     * Preenche os campos de projeto e responsável com objetos parciais (apenas id e nome).
     */
    public List<Tarefa> listarTodos() {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT t.id, t.titulo, t.descricao, t.data_inicio_prevista, t.data_termino_prevista, t.status, " +
                "t.responsavel_id, t.projeto_id, " +
                "u.nome as usuario_nome, p.nome as projeto_nome " +
                "FROM tarefa t " +
                "LEFT JOIN usuario u ON t.responsavel_id = u.id " +
                "LEFT JOIN projeto p ON t.projeto_id = p.id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tarefa t = new Tarefa();
                t.setId(rs.getInt("id"));
                t.setTitulo(rs.getString("titulo"));
                t.setDescricao(rs.getString("descricao"));
                t.setDataInicioPrevista(rs.getObject("data_inicio_prevista", LocalDate.class));
                t.setDataTerminoPrevista(rs.getObject("data_termino_prevista", LocalDate.class));
                t.setStatus(rs.getString("status"));

                // Responsável
                int usuarioId = rs.getInt("responsavel_id");
                if (usuarioId > 0) {
                    Usuario u = new Usuario();
                    u.setId(usuarioId);
                    u.setNome(rs.getString("usuario_nome"));
                    t.setResponsavel(u);
                }

                // Projeto
                int projetoId = rs.getInt("projeto_id");
                if (projetoId > 0) {
                    Projeto p = new Projeto();
                    p.setId(projetoId);
                    p.setNome(rs.getString("projeto_nome"));
                    t.setProjeto(p);
                }

                tarefas.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tarefas;
    }

    /**
     * Salva uma nova tarefa no banco de dados.
     */
    public void salvar(Tarefa tarefa) {
        String sql = "INSERT INTO tarefa(titulo, descricao, data_inicio_prevista, data_termino_prevista, status, responsavel_id, projeto_id) VALUES(?, ?, ?, ?, ?, ?, ?)";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setObject(3, tarefa.getDataInicioPrevista());
            stmt.setObject(4, tarefa.getDataTerminoPrevista());
            stmt.setString(5, tarefa.getStatus());
            stmt.setObject(6, tarefa.getResponsavel() != null ? tarefa.getResponsavel().getId() : null);
            stmt.setObject(7, tarefa.getProjeto() != null ? tarefa.getProjeto().getId() : null);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
