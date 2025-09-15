package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.dao.TarefaDAO;
import br.com.gestaoprojetos.model.Tarefa;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class RelatoriosController {

    private TarefaDAO tarefaDAO = new TarefaDAO();

    @FXML
    private ListView<String> listaRelatorio;

    // Método para gerar relatório
    public void gerarRelatorio() {
        // Aqui não precisamos de try-catch para SQLException, pois tarefaDAO já trata internamente
        List<Tarefa> tarefas = tarefaDAO.listarTodos();

        listaRelatorio.getItems().clear();
        for (Tarefa t : tarefas) {
            String status = t.getStatus();
            String dataTermino = t.getDataTerminoPrevista() != null ? t.getDataTerminoPrevista().toString() : "Sem data";
            listaRelatorio.getItems().add(
                    "Tarefa: " + t.getDescricao() + " | Status: " + status + " | Termino previsto: " + dataTermino
            );
        }
    }
}
