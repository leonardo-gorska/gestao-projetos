package br.com.gestaoprojetos.dao;

import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.model.Cargo;
import br.com.gestaoprojetos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /**
     * Autentica usuário pelo login e senha
     */
    public Usuario autenticar(String login, String senha) {
        Usuario usuario = null;
        String sql = "SELECT u.id, u.nome, u.login, u.perfil, u.email, u.cpf, c.id as cargo_id, c.nome as cargo_nome " +
                "FROM usuario u " +
                "LEFT JOIN cargo c ON u.cargo_id = c.id " +
                "WHERE u.login = ? AND u.senha = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setLogin(rs.getString("login"));
                usuario.setPerfil(rs.getString("perfil"));
                usuario.setEmail(rs.getString("email"));
                usuario.setCpf(rs.getString("cpf"));

                int cargoId = rs.getInt("cargo_id");
                String cargoNome = rs.getString("cargo_nome");
                if (cargoId > 0) {
                    Cargo cargo = new Cargo();
                    cargo.setId(cargoId);
                    cargo.setNome(cargoNome);
                    usuario.setCargo(cargo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * Lista todos os usuários
     */
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.id, u.nome, u.login, u.perfil, u.email, u.cpf, c.id as cargo_id, c.nome as cargo_nome " +
                "FROM usuario u LEFT JOIN cargo c ON u.cargo_id = c.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setLogin(rs.getString("login"));
                u.setPerfil(rs.getString("perfil"));
                u.setEmail(rs.getString("email"));
                u.setCpf(rs.getString("cpf"));

                int cargoId = rs.getInt("cargo_id");
                String cargoNome = rs.getString("cargo_nome");
                if (cargoId > 0) {
                    Cargo cargo = new Cargo();
                    cargo.setId(cargoId);
                    cargo.setNome(cargoNome);
                    u.setCargo(cargo);
                }

                usuarios.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Salva um novo usuário no banco
     */
    public void salvar(Usuario usuario) {
        String sql = "INSERT INTO usuario(nome, login, senha, email, cpf, cargo_id, perfil) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getCpf());
            stmt.setInt(6, usuario.getCargo() != null ? usuario.getCargo().getId() : 0);
            stmt.setString(7, usuario.getPerfil());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
