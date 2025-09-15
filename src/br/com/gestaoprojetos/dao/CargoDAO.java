package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.Cargo;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CargoDAO {

    public List<Cargo> listarTodos() {
        List<Cargo> cargos = new ArrayList<>();
        String sql = "SELECT * FROM cargo"; // tabela cargo deve existir no BD

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cargo cargo = new Cargo();
                cargo.setId(rs.getInt("id"));
                cargo.setNome(rs.getString("nome"));
                cargos.add(cargo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cargos;
    }
}
