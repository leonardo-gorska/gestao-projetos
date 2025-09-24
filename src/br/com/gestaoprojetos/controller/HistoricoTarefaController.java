package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.HistoricoTarefa;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;

public class HistoricoTarefaController {

    @FXML
    private TableView<HistoricoTarefa> tableHistorico;

    @FXML
    private TableColumn<HistoricoTarefa, String> colUsuario;

    @FXML
    private TableColumn<HistoricoTarefa, String> colAcao;

    @FXML
    private TableColumn<HistoricoTarefa, String> colDetalhe;

    @FXML
    private TableColumn<HistoricoTarefa, String> colDataHora;

    public void initialize() {
        colUsuario.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getUsuario() != null
                        ? cell.getValue().getUsuario().getNome() : "")
        );
        colAcao.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAcao())
        );
        colDetalhe.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDetalhe())
        );
        colDataHora.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDataHora() != null
                        ? cell.getValue().getDataHora().toString() : "")
        );
    }
}
