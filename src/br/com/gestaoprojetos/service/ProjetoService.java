package br.com.gestaoprojetos.service;

import br.com.gestaoprojetos.dao.ProjetoDAO;
import br.com.gestaoprojetos.dao.TarefaDAO;
import br.com.gestaoprojetos.model.Projeto;
import br.com.gestaoprojetos.model.Equipe;
import br.com.gestaoprojetos.model.Tarefa;
import br.com.gestaoprojetos.model.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjetoService {

    private final ProjetoDAO projetoDAO = new ProjetoDAO();
    private final TarefaDAO tarefaDAO = new TarefaDAO();

    public void salvar(Projeto projeto) throws SQLException {
        projetoDAO.salvar(projeto);
    }

    public void atualizar(Projeto projeto) throws SQLException {
        projetoDAO.atualizar(projeto);
    }

    public void excluir(int id) throws SQLException {
        projetoDAO.excluir(id);
    }

    public Projeto buscarPorId(int id) throws SQLException {
        return projetoDAO.buscarPorId(id);
    }

    public List<Projeto> listarTodos() throws SQLException {
        return projetoDAO.listarTodos();
    }

    public void adicionarEquipe(Projeto projeto, Equipe equipe) throws SQLException {
        List<Equipe> equipes = projeto.getEquipes();
        if (equipes != null && !equipes.contains(equipe)) {
            equipes.add(equipe);
            projetoDAO.atualizar(projeto);
        }
    }

    public void removerEquipe(Projeto projeto, Equipe equipe) throws SQLException {
        List<Equipe> equipes = projeto.getEquipes();
        if (equipes != null && equipes.contains(equipe)) {
            equipes.remove(equipe);
            projetoDAO.atualizar(projeto);
        }
    }

    // --- NOVOS MÉTODOS PARA DASHBOARD / RELATÓRIOS ---

    /** Calcula o andamento do projeto considerando status: Concluída=100%, Em Andamento=50%, Pendente=0% */
    public double calcularAndamento(Projeto projeto) throws SQLException {
        List<Tarefa> tarefas = tarefaDAO.listarPorProjeto(projeto.getId());
        if (tarefas.isEmpty()) return 0;

        double totalPontuacao = 0;
        for (Tarefa t : tarefas) {
            String status = t.getStatus();
            if ("Concluída".equalsIgnoreCase(status)) {
                totalPontuacao += 1.0; // 100%
            } else if ("Em Andamento".equalsIgnoreCase(status)) {
                totalPontuacao += 0.5; // 50%
            } else { // Pendente ou outros
                totalPontuacao += 0.0; // 0%
            }
        }

        return (totalPontuacao / tarefas.size()) * 100;
    }

    /** Retorna true se o projeto estiver em risco de atraso */
    public boolean estaEmRisco(Projeto projeto) throws SQLException {
        if (projeto.getDataTerminoPrevista() == null) return false;

        double andamento = calcularAndamento(projeto);
        LocalDate hoje = LocalDate.now();
        long diasTotais = java.time.temporal.ChronoUnit.DAYS.between(projeto.getDataInicio(), projeto.getDataTerminoPrevista());
        long diasPassados = java.time.temporal.ChronoUnit.DAYS.between(projeto.getDataInicio(), hoje);

        // Regra simples: se progresso < proporção de dias passados, está em risco
        return diasTotais > 0 && andamento < ((diasPassados * 100.0) / diasTotais);
    }

    /** Cancela um projeto: define status e marca tarefas pendentes como "Cancelada" */
    public void cancelarProjeto(Projeto projeto) throws SQLException {
        projeto.setStatus("Cancelado");
        projetoDAO.atualizar(projeto);

        List<Tarefa> tarefas = tarefaDAO.listarPorProjeto(projeto.getId());
        for (Tarefa t : tarefas) {
            if (!"Concluída".equalsIgnoreCase(t.getStatus())) {
                t.setStatus("Cancelada");
                tarefaDAO.atualizar(t);
            }
        }
    }

    /** Lista todos os projetos atualmente em risco de atraso */
    public List<Projeto> listarProjetosEmRisco() throws SQLException {
        List<Projeto> todos = listarTodos();
        List<Projeto> emRisco = new ArrayList<>();
        for (Projeto p : todos) {
            if (estaEmRisco(p)) emRisco.add(p);
        }
        return emRisco;
    }

}
