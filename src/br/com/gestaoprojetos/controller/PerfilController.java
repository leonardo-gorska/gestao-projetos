package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.Perfil;
import br.com.gestaoprojetos.service.PerfilService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;

public class PerfilController {

    @FXML private TextField txtDescricao;
    @FXML private TableView<Perfil> tablePerfis;
    @FXML private TableColumn<Perfil, Integer> colId;
    @FXML private TableColumn<Perfil, String> colDescricao;

    private final PerfilService service = new PerfilService();
    private Perfil perfilSelecionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        listarPerfis();

        tablePerfis.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 0) {
                perfilSelecionado = tablePerfis.getSelectionModel().getSelectedItem();
                if (perfilSelecionado != null) {
                    txtDescricao.setText(perfilSelecionado.getDescricao());
                }
            }
        });

        // Adiciona estilo às colunas
        tablePerfis.setStyle("-fx-selection-bar: #3498db; -fx-selection-bar-non-focused: #95a5a6;");
    }

    @FXML
    private void listarPerfis() {
        try {
            tablePerfis.setItems(FXCollections.observableArrayList(service.listarTodos()));
        } catch (SQLException e) {
            mostrarAlerta("Erro ao listar perfis: " + e.getMessage());
        }
    }

    @FXML
    private void limparCampos() {
        txtDescricao.clear();
        perfilSelecionado = null;
        tablePerfis.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvarPerfil() {
        String descricao = txtDescricao.getText().trim();
        if (descricao.isEmpty()) {
            mostrarAlerta("A descrição do perfil é obrigatória.");
            return;
        }

        Perfil perfil = new Perfil();
        perfil.setDescricao(descricao);

        try {
            service.salvar(perfil);
            listarPerfis();
            limparCampos();
        } catch (SQLException e) {
            mostrarAlerta("Erro ao salvar perfil: " + e.getMessage());
        }
    }

    @FXML
    private void atualizarPerfil() {
        if (perfilSelecionado == null) {
            mostrarAlerta("Selecione um perfil para atualizar.");
            return;
        }

        String descricao = txtDescricao.getText().trim();
        if (descricao.isEmpty()) {
            mostrarAlerta("A descrição do perfil é obrigatória.");
            return;
        }

        perfilSelecionado.setDescricao(descricao);

        try {
            service.atualizar(perfilSelecionado);
            listarPerfis();
            limparCampos();
        } catch (SQLException e) {
            mostrarAlerta("Erro ao atualizar perfil: " + e.getMessage());
        }
    }

    @FXML
    private void excluirPerfil() {
        if (perfilSelecionado == null) {
            mostrarAlerta("Selecione um perfil para excluir.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação");
        confirm.setHeaderText(null);
        confirm.setContentText("Tem certeza que deseja excluir este perfil?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            service.excluir(perfilSelecionado.getId());
            listarPerfis();
            limparCampos();
        } catch (SQLException e) {
            mostrarAlerta("Erro ao excluir perfil: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
