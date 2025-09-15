package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.LogAtividade;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class LogAtividadeDAO {

    public void registrar(LogAtividade log) {
        String sql = "INSERT INTO logs_atividade (usuario_id, acao, entidade) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, log.getUsuarioId());
            ps.setString(2, log.getAcao());
            ps.setString(3, log.getEntidade());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
