package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.dao.ProjetoDAO;
import br.com.gestaoprojetos.model.Projeto;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class ProjetosController {

    @FXML private TextField txtNome;
    @FXML private TextArea txtDescricao;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpTermino;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private TableView<Projeto> tableProjetos;
    @FXML private TableColumn<Projeto, Integer> colId;
    @FXML private TableColumn<Projeto, String> colNome;
    @FXML private TableColumn<Projeto, String> colDescricao;
    @FXML private TableColumn<Projeto, String> colInicio;
    @FXML private TableColumn<Projeto, String> colTermino;
    @FXML private TableColumn<Projeto, String> colStatus;

    private ProjetoDAO projetoDAO = new ProjetoDAO();

    @FXML
    public void initialize() {
        // Combo status
        cmbStatus.getItems().addAll("Planejado", "Em Andamento", "Concluído", "Cancelado");

        // Configura colunas
        colId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nome"));
        colDescricao.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("descricao"));
        colInicio.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dataInicioString"));
        colTermino.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dataTerminoString"));
        colStatus.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));

        listarProjetos();
    }

    @FXML
    public void salvarProjeto() {
        try {
            Projeto p = new Projeto();
            p.setNome(txtNome.getText());
            p.setDescricao(txtDescricao.getText());
            p.setDataInicio(dpInicio.getValue());
            p.setDataTermino(dpTermino.getValue());
            p.setStatus(cmbStatus.getValue());

            projetoDAO.salvar(p);

            listarProjetos();
            limparCampos();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro ao salvar projeto: " + e.getMessage());
        }
    }

    @FXML
    public void listarProjetos() {
        try {
            List<Projeto> projetos = projetoDAO.listarTodos();
            tableProjetos.setItems(FXCollections.observableArrayList(projetos));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro ao carregar projetos: " + e.getMessage());
        }
    }

    @FXML
    public void limparCampos() {
        txtNome.clear();
        txtDescricao.clear();
        dpInicio.setValue(null);
        dpTermino.setValue(null);
        cmbStatus.getSelectionModel().clearSelection();
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
