package br.com.gestaoprojetos.service;

import br.com.gestaoprojetos.dao.TarefaDAO;
import br.com.gestaoprojetos.model.HistoricoTarefa;
import br.com.gestaoprojetos.model.LogAtividade;
import br.com.gestaoprojetos.model.Tarefa;
import br.com.gestaoprojetos.model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class TarefaService {

    private final TarefaDAO tarefaDAO = new TarefaDAO();
    private final HistoricoTarefaService historicoService = new HistoricoTarefaService();
    private final LogAtividadeService logService = new LogAtividadeService();

    /**
     * Salva uma nova tarefa e registra histórico e log.
     * @param tarefa Tarefa a ser salva
     * @param usuario Usuário que executa a ação
     * @throws SQLException
     */
    public void salvar(Tarefa tarefa, Usuario usuario) throws SQLException {
        if (tarefa == null) throw new IllegalArgumentException("Tarefa não pode ser nula");
        if (usuario == null) throw new IllegalArgumentException("Usuário não pode ser nulo");

        // Salva a tarefa no DAO
        tarefaDAO.salvar(tarefa);

        // Registra histórico
        String detalhe = gerarDetalheTarefa(tarefa);
        HistoricoTarefa historico = new HistoricoTarefa(tarefa.getId(), usuario, "Criada", detalhe);
        historicoService.salvar(historico);

        // Registra log de atividade
        LogAtividade log = new LogAtividade(usuario, "Criou tarefa: " + tarefa.getTitulo(), "tarefa");
        logService.salvar(log);
    }

    /**
     * Atualiza uma tarefa existente e registra histórico e log.
     * @param tarefa Tarefa a ser atualizada
     * @param usuario Usuário que executa a ação
     * @throws SQLException
     */
    public void atualizar(Tarefa tarefa, Usuario usuario) throws SQLException {
        if (tarefa == null) throw new IllegalArgumentException("Tarefa não pode ser nula");
        if (usuario == null) throw new IllegalArgumentException("Usuário não pode ser nulo");

        tarefaDAO.atualizar(tarefa);

        // Histórico
        String detalhe = gerarDetalheTarefa(tarefa);
        HistoricoTarefa historico = new HistoricoTarefa(tarefa.getId(), usuario, "Atualizada", detalhe);
        historicoService.salvar(historico);

        // Log
        LogAtividade log = new LogAtividade(usuario, "Atualizou tarefa: " + tarefa.getTitulo(), "tarefa");
        logService.salvar(log);
    }

    /**
     * Exclui uma tarefa pelo ID e registra log
     * @param id ID da tarefa
     * @param usuario Usuário que executa a ação
     * @throws SQLException
     */
    public void excluir(int id, Usuario usuario) throws SQLException {
        if (usuario == null) throw new IllegalArgumentException("Usuário não pode ser nulo");

        Tarefa t = tarefaDAO.buscarPorId(id);
        if (t != null) {
            tarefaDAO.excluir(id);

            // Histórico
            HistoricoTarefa historico = new HistoricoTarefa(id, usuario, "Excluída", gerarDetalheTarefa(t));
            historicoService.salvar(historico);

            // Log
            LogAtividade log = new LogAtividade(usuario, "Excluiu tarefa: " + t.getTitulo(), "tarefa");
            logService.salvar(log);
        }
    }

    /**
     * Busca uma tarefa pelo ID
     * @param id ID da tarefa
     * @return Tarefa encontrada ou null
     * @throws SQLException
     */
    public Tarefa buscarPorId(int id) throws SQLException {
        return tarefaDAO.buscarPorId(id);
    }

    /**
     * Lista todas as tarefas
     * @return Lista de tarefas
     * @throws SQLException
     */
    public List<Tarefa> listarTodos() throws SQLException {
        return tarefaDAO.listarTodos();
    }

    /**
     * Lista todas as tarefas de um projeto específico
     * @param projetoId ID do projeto
     * @return Lista de tarefas do projeto
     * @throws SQLException
     */
    public List<Tarefa> listarPorProjeto(int projetoId) throws SQLException {
        return tarefaDAO.listarPorProjeto(projetoId);
    }

    /**
     * Gera detalhes da tarefa para histórico
     * @param t Tarefa
     * @return String com detalhes
     */
    private String gerarDetalheTarefa(Tarefa t) {
        if (t == null) return "Tarefa nula";

        return String.format(
                "Título: %s; Status: %s; Início: %s; Término: %s; Responsável: %s",
                t.getTitulo(),
                t.getStatus() != null ? t.getStatus() : "N/A",
                t.getDataInicio() != null ? t.getDataInicio() : "N/A",
                t.getDataTermino() != null ? t.getDataTermino() : "N/A",
                t.getResponsavel() != null ? t.getResponsavel().getNome() : "N/A"
        );
    }
}
