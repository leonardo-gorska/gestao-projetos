package br.com.gestaoprojetos.service;

import br.com.gestaoprojetos.dao.TarefaDAO;
import br.com.gestaoprojetos.dao.UsuarioDAO;
import br.com.gestaoprojetos.model.Perfil;
import br.com.gestaoprojetos.model.Tarefa;
import br.com.gestaoprojetos.model.Usuario;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final TarefaDAO tarefaDAO = new TarefaDAO();

    public void salvar(Usuario usuario) throws SQLException {
        usuarioDAO.salvar(usuario);
    }

    public void atualizar(Usuario usuario) throws SQLException {
        usuarioDAO.atualizar(usuario);
    }

    public void excluir(int id) throws SQLException {
        usuarioDAO.excluir(id);
    }

    public List<Usuario> listarTodos() throws SQLException {
        return usuarioDAO.listarTodos();
    }

    public Usuario buscarPorLoginESenha(String login, String senha) throws SQLException {
        return usuarioDAO.buscarPorLoginESenha(login, senha);
    }

    public Usuario buscarPorId(int id) throws SQLException {
        return usuarioDAO.buscarPorId(id);
    }

    public Usuario autenticar(String login, String senha) throws SQLException {
        return usuarioDAO.buscarPorLoginESenha(login, senha);
    }

    public List<Perfil> listarPerfis() throws SQLException {
        return usuarioDAO.listarPerfis();
    }

    // --- NOVOS MÉTODOS PARA DASHBOARD / RELATÓRIOS ---

    /** Calcula o desempenho de um colaborador em % de tarefas concluídas */
    public double calcularDesempenho(Usuario usuario) throws SQLException {
        List<Tarefa> tarefas = tarefaDAO.listarTodos();
        List<Tarefa> minhas = tarefas.stream()
                .filter(t -> t.getResponsavel() != null && t.getResponsavel().getId() == usuario.getId())
                .toList();

        if (minhas.isEmpty()) return 0;

        long concluídas = minhas.stream()
                .filter(t -> "Concluída".equalsIgnoreCase(t.getStatus()))
                .count();

        return (concluídas * 100.0) / minhas.size();
    }

    /** Retorna mapa de desempenho de todos colaboradores (usuario -> % concluído) */
    public Map<Usuario, Double> desempenhoTodosColaboradores() throws SQLException {
        List<Usuario> todos = listarTodos();
        Map<Usuario, Double> desempenho = new HashMap<>();
        for (Usuario u : todos) {
            desempenho.put(u, calcularDesempenho(u));
        }
        return desempenho;
    }

}
