package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.Cargo;
import br.com.gestaoprojetos.model.Perfil;
import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void salvar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nome, cpf, email, login, senha, cargo_id, perfil_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getLogin());
            stmt.setString(5, usuario.getSenha());

            if (usuario.getCargo() != null) {
                stmt.setInt(6, usuario.getCargo().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            if (usuario.getPerfil() != null) {
                stmt.setInt(7, usuario.getPerfil().getId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.executeUpdate();
        }
    }

    public void atualizar(Usuario usuario) throws SQLException {
        String sql = """
            UPDATE usuario
            SET nome = ?, cpf = ?, email = ?, login = ?, senha = ?, cargo_id = ?, perfil_id = ?
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getLogin());
            stmt.setString(5, usuario.getSenha());

            if (usuario.getCargo() != null) {
                stmt.setInt(6, usuario.getCargo().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            if (usuario.getPerfil() != null) {
                stmt.setInt(7, usuario.getPerfil().getId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setInt(8, usuario.getId());

            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = """
            SELECT u.id, u.nome, u.cpf, u.email, u.login, u.senha,
                   c.id AS cargo_id, c.nome AS cargo_desc,
                   p.id AS perfil_id, p.descricao AS perfil_desc
            FROM usuario u
            LEFT JOIN cargo c ON u.cargo_id = c.id
            LEFT JOIN perfil p ON u.perfil_id = p.id
        """;

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setCpf(rs.getString("cpf"));
                u.setEmail(rs.getString("email"));
                u.setLogin(rs.getString("login"));
                u.setSenha(rs.getString("senha"));

                if (rs.getInt("cargo_id") != 0) {
                    Cargo cargo = new Cargo();
                    cargo.setId(rs.getInt("cargo_id"));
                    cargo.setDescricao(rs.getString("cargo_desc"));
                    u.setCargo(cargo);
                }

                if (rs.getInt("perfil_id") != 0) {
                    Perfil perfil = new Perfil();
                    perfil.setId(rs.getInt("perfil_id"));
                    perfil.setDescricao(rs.getString("perfil_desc"));
                    u.setPerfil(perfil);
                }

                usuarios.add(u);
            }
        }
        return usuarios;
    }

    public Usuario buscarPorLoginESenha(String login, String senha) throws SQLException {
        String sql = """
            SELECT u.id, u.nome, u.cpf, u.email, u.login, u.senha,
                   c.id AS cargo_id, c.nome AS cargo_desc,
                   p.id AS perfil_id, p.descricao AS perfil_desc
            FROM usuario u
            LEFT JOIN cargo c ON u.cargo_id = c.id
            LEFT JOIN perfil p ON u.perfil_id = p.id
            WHERE u.login = ? AND u.senha = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNome(rs.getString("nome"));
                    u.setCpf(rs.getString("cpf"));
                    u.setEmail(rs.getString("email"));
                    u.setLogin(rs.getString("login"));
                    u.setSenha(rs.getString("senha"));

                    if (rs.getInt("cargo_id") != 0) {
                        Cargo cargo = new Cargo();
                        cargo.setId(rs.getInt("cargo_id"));
                        cargo.setDescricao(rs.getString("cargo_desc"));
                        u.setCargo(cargo);
                    }

                    if (rs.getInt("perfil_id") != 0) {
                        Perfil perfil = new Perfil();
                        perfil.setId(rs.getInt("perfil_id"));
                        perfil.setDescricao(rs.getString("perfil_desc"));
                        u.setPerfil(perfil);
                    }

                    return u;
                }
            }
        }
        return null;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT u.id, u.nome, u.cpf, u.email, u.login, u.senha,
                   c.id AS cargo_id, c.nome AS cargo_desc,
                   p.id AS perfil_id, p.descricao AS perfil_desc
            FROM usuario u
            LEFT JOIN cargo c ON u.cargo_id = c.id
            LEFT JOIN perfil p ON u.perfil_id = p.id
            WHERE u.id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNome(rs.getString("nome"));
                    u.setCpf(rs.getString("cpf"));
                    u.setEmail(rs.getString("email"));
                    u.setLogin(rs.getString("login"));
                    u.setSenha(rs.getString("senha"));

                    if (rs.getInt("cargo_id") != 0) {
                        Cargo cargo = new Cargo();
                        cargo.setId(rs.getInt("cargo_id"));
                        cargo.setDescricao(rs.getString("cargo_desc"));
                        u.setCargo(cargo);
                    }

                    if (rs.getInt("perfil_id") != 0) {
                        Perfil perfil = new Perfil();
                        perfil.setId(rs.getInt("perfil_id"));
                        perfil.setDescricao(rs.getString("perfil_desc"));
                        u.setPerfil(perfil);
                    }

                    return u;
                }
            }
        }
        return null;
    }

    public List<Perfil> listarPerfis() throws SQLException {
        String sql = "SELECT * FROM perfil";
        List<Perfil> perfis = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Perfil p = new Perfil();
                p.setId(rs.getInt("id"));
                p.setDescricao(rs.getString("descricao"));
                perfis.add(p);
            }
        }
        return perfis;
    }
}
