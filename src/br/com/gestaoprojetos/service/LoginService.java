package br.com.gestaoprojetos.service;

import br.com.gestaoprojetos.model.Usuario;

import java.sql.SQLException;

public class LoginService {

    private final UsuarioService usuarioService = new UsuarioService();

    // Método que o LoginController espera
    public Usuario autenticar(String login, String senha) throws SQLException {
        return usuarioService.autenticar(login, senha);
    }

    // Opcional: mantém o método antigo caso alguma outra parte use
    public Usuario buscarPorLoginESenha(String login, String senha) throws SQLException {
        return usuarioService.autenticar(login, senha);
    }
}
