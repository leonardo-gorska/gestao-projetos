package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.dao.EquipeDAO;
import br.com.gestaoprojetos.dao.UsuarioDAO;
import br.com.gestaoprojetos.model.Equipe;
import br.com.gestaoprojetos.model.Usuario;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class EquipesController {

    @FXML private TextField txtNome;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<String> comboPerfil;
    @FXML private ListView<Usuario> lvUsuarios;
    @FXML private TableView<Equipe> tableEquipes;
    @FXML private TableColumn<Equipe, String> colNome;
    @FXML private TableColumn<Equipe, String> colDescricao;
    @FXML private TableColumn<Equipe, String> colPerfil;

    private EquipeDAO equipeDAO = new EquipeDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        try {
            comboPerfil.getItems().addAll("Administrador", "Gerente", "Colaborador");

            List<Usuario> usuarios = usuarioDAO.listarTodos();
            lvUsuarios.setItems(FXCollections.observableArrayList(usuarios));
            lvUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            colNome.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nome"));
            colDescricao.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("descricao"));
            colPerfil.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("perfil"));

            tableEquipes.setItems(FXCollections.observableArrayList(equipeDAO.listarTodos()));

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro ao inicializar equipes: " + e.getMessage());
        }
    }

    @FXML
    public void salvarEquipe() {
        try {
            Equipe e = new Equipe();
            e.setNome(txtNome.getText());
            e.setDescricao(txtDescricao.getText());
            e.setPerfil(comboPerfil.getValue());

            List<Usuario> selecionados = lvUsuarios.getSelectionModel().getSelectedItems();
            e.setMembros(selecionados);

            equipeDAO.salvar(e);
            tableEquipes.setItems(FXCollections.observableArrayList(equipeDAO.listarTodos()));
            limparCampos();

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Erro ao salvar equipe: " + ex.getMessage());
        }
    }

    @FXML
    public void limparCampos() {
        txtNome.clear();
        txtDescricao.clear();
        comboPerfil.getSelectionModel().clearSelection();
        lvUsuarios.getSelectionModel().clearSelection();
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}
