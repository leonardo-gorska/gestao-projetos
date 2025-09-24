package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.HistoricoProjeto;
import br.com.gestaoprojetos.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoricoProjetoController {

    @FXML private TableView<HistoricoProjeto> tableHistorico;
    @FXML private TableColumn<HistoricoProjeto, Integer> colProjetoId;
    @FXML private TableColumn<HistoricoProjeto, String> colUsuario;
    @FXML private TableColumn<HistoricoProjeto, String> colAcao;
    @FXML private TableColumn<HistoricoProjeto, String> colDetalhe;
    @FXML private TableColumn<HistoricoProjeto, String> colDataHora;

    private ObservableList<HistoricoProjeto> listaHistorico = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colProjetoId.setCellValueFactory(new PropertyValueFactory<>("projetoId"));
        colUsuario.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getUsuario() != null ? cell.getValue().getUsuario().getNome() : ""
                )
        );

        colDataHora.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getDataHora() != null ? cell.getValue().getDataHora().toString() : ""
                )
        );

        tableHistorico.setItems(listaHistorico);
    }

    @FXML
    private void limparCampos() {
        tableHistorico.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvarHistorico() {
        // exemplo: criar histórico dummy
        HistoricoProjeto h = new HistoricoProjeto(1, new Usuario(1, "Admin"), "SALVAR", "Exemplo de ação");
        listaHistorico.add(h);
    }

    @FXML
    private void atualizarHistorico() {
        HistoricoProjeto selecionado = tableHistorico.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            selecionado.setDetalhe(selecionado.getDetalhe() + " (Atualizado)");
            tableHistorico.refresh();
        }
    }

    @FXML
    private void excluirHistorico() {
        HistoricoProjeto selecionado = tableHistorico.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            listaHistorico.remove(selecionado);
        }
    }

    @FXML
    private void listarHistorico() {
        tableHistorico.setItems(listaHistorico);
    }
}
