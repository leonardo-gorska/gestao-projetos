package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.Projeto;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO {

    public void salvar(Projeto p) throws SQLException {
        String sql = "INSERT INTO projeto (nome, descricao, data_inicio, data_termino, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescricao());
            ps.setDate(3, p.getDataInicio() != null ? Date.valueOf(p.getDataInicio()) : null);
            ps.setDate(4, p.getDataTermino() != null ? Date.valueOf(p.getDataTermino()) : null);
            ps.setString(5, p.getStatus());

            ps.executeUpdate();
        }
    }

    public List<Projeto> listarTodos() throws SQLException {
        List<Projeto> lista = new ArrayList<>();
        String sql = "SELECT * FROM projeto";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Projeto p = new Projeto();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                Date inicio = rs.getDate("data_inicio");
                Date termino = rs.getDate("data_termino");
                p.setDataInicio(inicio != null ? inicio.toLocalDate() : null);
                p.setDataTermino(termino != null ? termino.toLocalDate() : null);
                p.setStatus(rs.getString("status"));
                lista.add(p);
            }
        }
        return lista;
    }
}
