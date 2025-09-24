package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.HistoricoTarefa;
import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoricoTarefaDAO {

    public void salvar(HistoricoTarefa h) throws SQLException {
        String sql = "INSERT INTO historico_tarefa (tarefa_id, usuario_id, acao, detalhe, data_hora) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, h.getTarefaId());
            stmt.setInt(2, h.getUsuario().getId());
            stmt.setString(3, h.getAcao());
            stmt.setString(4, h.getDetalhe());
            stmt.setTimestamp(5, Timestamp.valueOf(h.getDataHora()));

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    h.setId(keys.getInt(1));
                }
            }
        }
    }

    public List<HistoricoTarefa> listarPorTarefa(int tarefaId) throws SQLException {
        String sql = "SELECT id, tarefa_id, usuario_id, acao, detalhe, data_hora FROM historico_tarefa WHERE tarefa_id = ? ORDER BY data_hora DESC";
        List<HistoricoTarefa> lista = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tarefaId);
            try (ResultSet rs = stmt.executeQuery()) {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                while (rs.next()) {
                    HistoricoTarefa h = new HistoricoTarefa();
                    h.setId(rs.getInt("id"));
                    h.setTarefaId(rs.getInt("tarefa_id"));
                    Usuario u = usuarioDAO.buscarPorId(rs.getInt("usuario_id"));
                    h.setUsuario(u);
                    h.setAcao(rs.getString("acao"));
                    h.setDetalhe(rs.getString("detalhe"));
                    Timestamp ts = rs.getTimestamp("data_hora");
                    if (ts != null) h.setDataHora(ts.toLocalDateTime());
                    lista.add(h);
                }
            }
        }
        return lista;
    }
}
