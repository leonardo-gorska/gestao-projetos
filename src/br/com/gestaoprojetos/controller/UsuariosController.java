package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.Cargo;
import br.com.gestaoprojetos.model.Perfil;
import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.model.LogAtividade;
import br.com.gestaoprojetos.service.CargoService;
import br.com.gestaoprojetos.service.LogAtividadeService;
import br.com.gestaoprojetos.service.UsuarioService;
import br.com.gestaoprojetos.util.Validador;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class UsuariosController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtEmail;
    @FXML private TextField txtLogin;
    @FXML private TextField txtSenha;
    @FXML private ComboBox<Cargo> comboCargo;
    @FXML private ComboBox<Perfil> comboPerfil;
    @FXML private TableView<Usuario> tabelaUsuarios;

    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colCpf;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colLogin;
    @FXML private TableColumn<Usuario, String> colPerfil;
    @FXML private TableColumn<Usuario, String> colCargo;
    @FXML private TableColumn<Usuario, HBox> colAcao;

    private final UsuarioService usuarioService = new UsuarioService();
    private final CargoService cargoService = new CargoService();
    private final LogAtividadeService logAtividadeService = new LogAtividadeService();

    private Usuario usuarioLogado;

    @FXML
    public void initialize() {
        tabelaUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        carregarCargos();
        carregarPerfis();
        configurarTabela();
        listarUsuariosNaTabela();
    }

    // 🔹 Método para setar o usuário logado
    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    private void configurarTabela() {
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colCpf.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCpf()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colLogin.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLogin()));
        colPerfil.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPerfil() != null ? cellData.getValue().getPerfil().getDescricao() : ""));
        colCargo.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCargo() != null ? cellData.getValue().getCargo().getDescricao() : ""));

        colAcao.setCellValueFactory(cellData -> {
            Usuario usuario = cellData.getValue();
            HBox box = new HBox(5);

            if (usuarioLogado != null && usuarioLogado.isAdmin()) {
                Button btnEditar = new Button("Editar");
                btnEditar.setOnAction(e -> abrirPopupEditar(usuario));

                Button btnExcluir = new Button("Excluir");
                btnExcluir.setOnAction(e -> excluirUsuario(usuario));

                box.getChildren().addAll(btnEditar, btnExcluir);
            }

            return new SimpleObjectProperty<>(box);
        });
    }

    private void carregarCargos() {
        try {
            List<Cargo> cargos = cargoService.listarTodos();
            comboCargo.getItems().setAll(cargos);
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar cargos", e.getMessage());
        }
    }

    private void carregarPerfis() {
        try {
            List<Perfil> perfis = usuarioService.listarPerfis();
            comboPerfil.getItems().setAll(perfis);
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar perfis", e.getMessage());
        }
    }

    @FXML
    public void salvarUsuario(ActionEvent event) {
        if (usuarioLogado == null || !usuarioLogado.isAdmin()) {
            mostrarErro("Permissão negada", "Apenas administradores podem salvar usuários.");
            return;
        }

        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().trim();
        String email = txtEmail.getText().trim();
        String login = txtLogin.getText().trim();
        String senha = txtSenha.getText().trim();

        if (Validador.isVazio(nome)) { mostrarErro("Validação", "Nome é obrigatório."); return; }
        if (!Validador.isCPFValido(cpf)) { mostrarErro("Validação", "CPF inválido (11 dígitos)."); return; }
        if (!Validador.isEmailValido(email)) { mostrarErro("Validação", "E-mail inválido."); return; }
        if (Validador.isVazio(login) || Validador.isVazio(senha)) { mostrarErro("Validação", "Login e senha são obrigatórios."); return; }

        try {
            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setCpf(cpf.replaceAll("\\D", ""));
            usuario.setEmail(email);
            usuario.setLogin(login);
            usuario.setSenha(senha);
            usuario.setCargo(comboCargo.getValue());
            usuario.setPerfil(comboPerfil.getValue());

            usuarioService.salvar(usuario);
            logAtividadeService.salvar(new LogAtividade(usuarioLogado, "Criou usuário: " + usuario.getNome(), "usuario"));

            mostrarInfo("Sucesso", "Usuário salvo com sucesso!");
            listarUsuariosNaTabela();
            limparCampos();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                mostrarErro("Dado duplicado", "Já existe um usuário com este dado único (login, email ou CPF).");
            } else {
                mostrarErro("Erro ao salvar usuário", e.getMessage());
            }
            e.printStackTrace();
        }
    }

    @FXML
    public void listarUsuariosNaTabela() {
        try {
            List<Usuario> usuarios = usuarioService.listarTodos();
            tabelaUsuarios.getItems().setAll(usuarios);
        } catch (SQLException e) {
            mostrarErro("Erro ao listar usuários", e.getMessage());
        }
    }

    @FXML
    private void limparCampos() {
        txtNome.clear();
        txtCpf.clear();
        txtEmail.clear();
        txtLogin.clear();
        txtSenha.clear();
        comboCargo.getSelectionModel().clearSelection();
        comboPerfil.getSelectionModel().clearSelection();
    }

    @FXML
    public void abrirPerfis() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Perfil.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            URL cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = new Stage();
            stage.setTitle("Perfis");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarErro("Erro", "Não foi possível abrir a tela de perfis.");
        }
    }

    public void abrirPopupEditar(Usuario usuario) {
        if (usuarioLogado == null || !usuarioLogado.isAdmin()) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UsuarioEditar.fxml"));
            Parent root = loader.load();

            UsuarioEditarController controller = loader.getController();
            controller.setUsuario(usuario);
            controller.setUsuarioLogado(usuarioLogado);
            controller.setUsuariosController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Usuário: " + usuario.getNome());

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarErro("Erro", "Não foi possível abrir o popup de edição.");
        }
    }

    public void excluirUsuario(Usuario usuario) {
        if (usuarioLogado == null || !usuarioLogado.isAdmin()) {
            mostrarErro("Permissão negada", "Apenas administradores podem excluir usuários.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação");
        confirm.setHeaderText(null);
        confirm.setContentText("Deseja realmente excluir o usuário " + usuario.getNome() + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                usuarioService.excluir(usuario.getId());
                logAtividadeService.salvar(new LogAtividade(usuarioLogado, "Excluiu usuário: " + usuario.getNome(), "usuario"));
                mostrarInfo("Sucesso", "Usuário excluído com sucesso!");
                listarUsuariosNaTabela();
            } catch (SQLException e) {
                mostrarErro("Erro ao excluir usuário", e.getMessage());
            }
        }
    }

    private void mostrarInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
