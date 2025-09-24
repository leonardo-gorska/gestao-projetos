package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.Cargo;
import br.com.gestaoprojetos.model.Perfil;
import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.model.LogAtividade;
import br.com.gestaoprojetos.service.CargoService;
import br.com.gestaoprojetos.service.LogAtividadeService;
import br.com.gestaoprojetos.service.UsuarioService;
import br.com.gestaoprojetos.util.Validador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UsuarioEditarController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtEmail;
    @FXML private TextField txtLogin;
    @FXML private PasswordField txtSenha;
    @FXML private ComboBox<Cargo> comboCargo;
    @FXML private ComboBox<Perfil> comboPerfil;

    private final UsuarioService usuarioService = new UsuarioService();
    private final CargoService cargoService = new CargoService();
    private final LogAtividadeService logAtividadeService = new LogAtividadeService();

    private Usuario usuario;
    private Usuario usuarioLogado;
    private UsuariosController usuariosController;

    @FXML
    public void initialize() {
        carregarCargos();
        carregarPerfis();
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        carregarCamposUsuario();
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void setUsuariosController(UsuariosController usuariosController) {
        this.usuariosController = usuariosController;
    }

    private void carregarCamposUsuario() {
        if (usuario != null) {
            txtNome.setText(usuario.getNome());
            txtCpf.setText(usuario.getCpf());
            txtEmail.setText(usuario.getEmail());
            txtLogin.setText(usuario.getLogin());
            txtSenha.setText(usuario.getSenha());
            comboCargo.setValue(usuario.getCargo());
            comboPerfil.setValue(usuario.getPerfil());
        }
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
    private void salvarUsuario(ActionEvent event) {
        if (!temPermissaoAdmin()) return;

        String nome = txtNome.getText();
        String cpf = txtCpf.getText();
        String email = txtEmail.getText();
        String login = txtLogin.getText();
        String senha = txtSenha.getText();

        if (Validador.isVazio(nome)) { mostrarErro("Validação", "Nome é obrigatório."); return; }
        if (!Validador.isCPFValido(cpf)) { mostrarErro("Validação", "CPF inválido (11 dígitos)."); return; }
        if (!Validador.isEmailValido(email)) { mostrarErro("Validação", "E-mail inválido."); return; }
        if (Validador.isVazio(login) || Validador.isVazio(senha)) { mostrarErro("Validação", "Login e senha são obrigatórios."); return; }

        try {
            usuario.setNome(nome);
            usuario.setCpf(cpf.replaceAll("\\D", ""));
            usuario.setEmail(email);
            usuario.setLogin(login);
            usuario.setSenha(senha);
            usuario.setCargo(comboCargo.getValue());
            usuario.setPerfil(comboPerfil.getValue());

            usuarioService.atualizar(usuario);
            logAtividadeService.salvar(new LogAtividade(usuarioLogado, "Editou usuário: " + usuario.getNome(), "usuario"));

            mostrarInfo("Sucesso", "Usuário atualizado com sucesso!");
            atualizarTabelaPrincipal();
            fecharPopup();
        } catch (SQLException e) {
            tratarErroBanco(e);
        }
    }

    @FXML
    private void excluirUsuario(ActionEvent event) {
        if (!temPermissaoAdmin()) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar exclusão");
        confirm.setHeaderText(null);
        confirm.setContentText("Tem certeza que deseja excluir o usuário " + usuario.getNome() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    usuarioService.excluir(usuario.getId());
                    logAtividadeService.salvar(new LogAtividade(usuarioLogado, "Excluiu usuário: " + usuario.getNome(), "usuario"));

                    mostrarInfo("Sucesso", "Usuário excluído com sucesso!");
                    atualizarTabelaPrincipal();
                    fecharPopup();
                } catch (SQLException e) {
                    tratarErroBanco(e);
                }
            }
        });
    }

    private boolean temPermissaoAdmin() {
        if (usuarioLogado == null || !usuarioLogado.isAdmin()) {
            mostrarErro("Permissão negada", "Apenas administradores podem realizar esta ação.");
            return false;
        }
        return true;
    }

    private void atualizarTabelaPrincipal() {
        if (usuariosController != null) {
            usuariosController.listarUsuariosNaTabela();
        }
    }

    private void fecharPopup() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    // 🔹 NOVO MÉTODO: abrir popup com CSS padronizado
    public void abrirPopup(Usuario usuario, Usuario usuarioLogado, UsuariosController usuariosController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UsuarioEditar.fxml"));
            Parent root = loader.load();

            UsuarioEditarController controller = loader.getController();
            controller.setUsuario(usuario);
            controller.setUsuarioLogado(usuarioLogado);
            controller.setUsuariosController(usuariosController);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Usuário: " + usuario.getNome());

            Scene scene = new Scene(root);
            // 🔹 aplica CSS do projeto
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarErro("Erro", "Não foi possível abrir o popup de edição.");
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

    private void tratarErroBanco(SQLException e) {
        if (e.getErrorCode() == 1062) {
            mostrarErro("Dado duplicado", "Já existe um usuário com este dado único (login, email ou CPF).");
        } else {
            mostrarErro("Erro no banco de dados", e.getMessage());
        }
        e.printStackTrace();
    }
}
