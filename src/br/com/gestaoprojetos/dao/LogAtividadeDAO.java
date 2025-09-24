package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.LogAtividade;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.*;

public class LogAtividadeDAO {

    public void salvar(LogAtividade log) throws SQLException {
        String sql = "INSERT INTO logs_atividade (usuario_id, acao, entidade, data_hora) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, log.getUsuario().getId());
            stmt.setString(2, log.getAcao());
            stmt.setString(3, log.getEntidade());
            stmt.setTimestamp(4, Timestamp.valueOf(log.getDataHora()));

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) log.setId(keys.getInt(1));
            }
        }
    }
}
