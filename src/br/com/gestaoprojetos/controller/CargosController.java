package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.Cargo;
import br.com.gestaoprojetos.service.CargoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;
import java.util.List;

public class CargosController {

    @FXML
    private TextField txtNome;

    @FXML
    private TableView<Cargo> tableCargos;

    @FXML
    private TableColumn<Cargo, Integer> colId;

    @FXML
    private TableColumn<Cargo, String> colNome;

    private final CargoService service = new CargoService();

    private Cargo cargoSelecionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("descricao")); // ajustar para descricao

        listarCargos();

        // Seleção de linha na tabela
        tableCargos.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 0) {
                cargoSelecionado = tableCargos.getSelectionModel().getSelectedItem();
                if (cargoSelecionado != null) {
                    txtNome.setText(cargoSelecionado.getDescricao());
                }
            }
        });
    }

    @FXML
    private void listarCargos() {
        try {
            List<Cargo> cargos = service.listarTodos();
            tableCargos.setItems(FXCollections.observableArrayList(cargos));
        } catch (SQLException e) {
            mostrarAlerta("Erro ao listar cargos: " + e.getMessage());
        }
    }

    @FXML
    private void limparCampos() {
        txtNome.clear();
        cargoSelecionado = null;
        tableCargos.getSelectionModel().clearSelection();
    }

    @FXML
    private void salvarCargo() {
        String descricao = txtNome.getText().trim();
        if (descricao.isEmpty()) {
            mostrarAlerta("O nome do cargo é obrigatório.");
            return;
        }

        Cargo cargo = new Cargo();
        cargo.setDescricao(descricao);

        try {
            service.salvar(cargo);
            listarCargos();
            limparCampos();
        } catch (SQLException e) {
            mostrarAlerta("Erro ao salvar cargo: " + e.getMessage());
        }
    }

    @FXML
    private void atualizarCargo() {
        if (cargoSelecionado == null) {
            mostrarAlerta("Selecione um cargo para atualizar.");
            return;
        }

        String descricao = txtNome.getText().trim();
        if (descricao.isEmpty()) {
            mostrarAlerta("O nome do cargo é obrigatório.");
            return;
        }

        cargoSelecionado.setDescricao(descricao);

        try {
            service.atualizar(cargoSelecionado);
            listarCargos();
            limparCampos();
        } catch (SQLException e) {
            mostrarAlerta("Erro ao atualizar cargo: " + e.getMessage());
        }
    }

    @FXML
    private void excluirCargo() {
        if (cargoSelecionado == null) {
            mostrarAlerta("Selecione um cargo para excluir.");
            return;
        }

        try {
            service.excluir(cargoSelecionado.getId());
            listarCargos();
            limparCampos();
        } catch (SQLException e) {
            mostrarAlerta("Erro ao excluir cargo: " + e.getMessage());
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
