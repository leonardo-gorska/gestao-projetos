package br.com.gestaoprojetos.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public abstract class GenericDAO<T> {

    protected Connection connection;
    private Class<T> type;

    public GenericDAO(Class<T> type) {
        this.type = type;
        try {
            // Dados do Banco
            String url = "jdbc:mysql://localhost:3306/gestao_projetos";
            String user = "root";
            String password = "1234";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao conectar com o banco de dados");
        }
    }

    // Métodos abstratos que cada DAO implementa
    public abstract void salvar(T objeto) throws SQLException;

    public abstract List<T> listarTodos() throws SQLException;
}
