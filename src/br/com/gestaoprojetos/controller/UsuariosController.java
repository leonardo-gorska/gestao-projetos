package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.dao.CargoDAO;
import br.com.gestaoprojetos.dao.UsuarioDAO;
import br.com.gestaoprojetos.model.Cargo;
import br.com.gestaoprojetos.model.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class UsuariosController {

    // TableView e suas colunas
    @FXML
    private TableView<Usuario> tabelaUsuarios;
    @FXML
    private TableColumn<Usuario, String> colNome;
    @FXML
    private TableColumn<Usuario, String> colEmail;
    @FXML
    private TableColumn<Usuario, String> colPerfil;

    // Campos de texto
    @FXML
    private TextField txtNome, txtLogin, txtEmail, txtCpf, txtSenha;

    // ComboBoxes
    @FXML
    private ComboBox<Cargo> comboCargo; // deve bater com fx:id do FXML
    @FXML
    private ComboBox<String> comboPerfil;

    private UsuarioDAO usuarioDAO;
    private CargoDAO cargoDAO;

    public UsuariosController() {
        usuarioDAO = new UsuarioDAO();
        cargoDAO = new CargoDAO();
    }

    /**
     * Carrega todos os usuários do banco e atualiza a tabela
     */
    public void carregarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        tabelaUsuarios.setItems(FXCollections.observableArrayList(usuarios));
    }

    /**
     * Salva um usuário novo ou atualizado
     */
    @FXML
    public void salvarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome(txtNome.getText());
        usuario.setLogin(txtLogin.getText());
        usuario.setEmail(txtEmail.getText());
        usuario.setCpf(txtCpf.getText());
        usuario.setSenha(txtSenha.getText());

        // Define cargo e perfil
        usuario.setCargo(comboCargo.getValue());
        usuario.setPerfil(comboPerfil.getValue());

        usuarioDAO.salvar(usuario);
        carregarUsuarios(); // Atualiza tabela após salvar
    }

    /**
     * Carrega todos os cargos no ComboBox
     */
    public void carregarCargos() {
        List<Cargo> cargos = cargoDAO.listarTodos();
        comboCargo.setItems(FXCollections.observableArrayList(cargos));
    }

    /**
     * Carrega os perfis disponíveis
     */
    public void carregarPerfis() {
        comboPerfil.setItems(FXCollections.observableArrayList("Admin", "User"));
    }

    /**
     * Método chamado automaticamente pelo JavaFX ao inicializar o FXML
     */
    @FXML
    public void initialize() {
        carregarUsuarios();
        carregarCargos();
        carregarPerfis();
    }
}
