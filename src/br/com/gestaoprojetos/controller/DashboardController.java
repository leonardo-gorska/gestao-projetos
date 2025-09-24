package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.Projeto;
import br.com.gestaoprojetos.model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    private Usuario usuarioLogado;

    @FXML
    private Button btnLogout; // Botão de logout

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    @FXML
    public void initialize() {
        // Configura ação do botão de logout
        if (btnLogout != null) {
            btnLogout.setOnAction(e -> logout());
        }
    }

    @FXML
    private void abrirUsuarios() {
        if (!usuarioLogado.isAdmin()) {
            mostrarErro("Permissão", "Você não tem permissão para acessar Usuários.");
            return;
        }
        abrirTela("/fxml/Usuarios.fxml", "Usuários");
    }

    @FXML
    private void abrirEquipes() {
        if (!usuarioLogado.isAdmin() && !usuarioLogado.isGerente()) {
            mostrarErro("Permissão", "Você não tem permissão para acessar Equipes.");
            return;
        }
        abrirTela("/fxml/Equipes.fxml", "Equipes");
    }

    @FXML
    private void abrirProjetos() {
        if (!usuarioLogado.isAdmin() && !usuarioLogado.isGerente()) {
            mostrarErro("Permissão", "Você não tem permissão para acessar Projetos.");
            return;
        }
        abrirTela("/fxml/Projetos.fxml", "Projetos");
    }

    @FXML
    private void abrirTarefas() {
        Projeto projetoSelecionado = selecionarProjeto();
        if (projetoSelecionado == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Tarefas.fxml"));
            Parent root = loader.load();
            TarefasController controller = loader.getController();

            controller.setUsuario(usuarioLogado);
            controller.setProjeto(projetoSelecionado);

            Stage stage = new Stage();
            stage.setTitle("Tarefas - " + projetoSelecionado.getNome());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarErro("Erro ao abrir Tarefas", e.getMessage());
        }
    }

    @FXML
    private void abrirRelatorios() {
        if (!usuarioLogado.isAdmin() && !usuarioLogado.isGerente()) {
            mostrarErro("Permissão", "Você não tem permissão para acessar Relatórios.");
            return;
        }
        abrirTela("/fxml/Relatorios.fxml", "Relatórios");
    }

    private void abrirTela(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object controller = loader.getController();
            try {
                controller.getClass().getMethod("setUsuario", Usuario.class).invoke(controller, usuarioLogado);
            } catch (NoSuchMethodException ignored) {}
            Stage stage = new Stage();
            stage.setTitle(titulo);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao abrir tela", e.getMessage());
        }
    }

    private Projeto selecionarProjeto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Projetos.fxml"));
            Parent root = loader.load();
            ProjetosController controller = loader.getController();
            controller.setUsuario(usuarioLogado);

            Stage stage = new Stage();
            stage.setTitle("Selecione um projeto");
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();

            Projeto projetoSelecionado = controller.getProjetoSelecionado();
            if (projetoSelecionado == null) {
                mostrarInfo("Nenhum projeto selecionado.");
            }
            return projetoSelecionado;

        } catch (IOException e) {
            e.printStackTrace();
            mostrarErro("Erro ao selecionar projeto", e.getMessage());
            return null;
        }
    }

    // ===========================
    // NOVO MÉTODO DE LOGOUT
    // ===========================
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

            // Fecha a janela atual (Dashboard)
            btnLogout.getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarErro("Erro ao fazer logout", e.getMessage());
        }
    }

    private void mostrarErro(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
