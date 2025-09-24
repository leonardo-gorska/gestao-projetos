package br.com.gestaoprojetos.service;

import br.com.gestaoprojetos.model.HistoricoProjeto;
import java.util.ArrayList;
import java.util.List;

public class HistoricoProjetoService {

    private static List<HistoricoProjeto> historicos = new ArrayList<>();

    public void salvar(HistoricoProjeto historico) {
        historicos.add(historico);
    }

    public List<HistoricoProjeto> listarTodos() {
        return new ArrayList<>(historicos);
    }

    // Futuramente pode adicionar métodos de filtro por projeto, usuário, data, etc.
}
