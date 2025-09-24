package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.service.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField txtLogin;
    @FXML private PasswordField txtSenha;

    private final LoginService loginService = new LoginService();

    @FXML
    private void login(ActionEvent event) {
        try {
            Usuario usuario = loginService.autenticar(txtLogin.getText(), txtSenha.getText());
            if (usuario != null) {
                abrirDashboard(usuario);
            } else {
                mostrarAlerta("Login ou senha inválidos!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            mostrarAlerta("Erro ao acessar o banco: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void abrirDashboard(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) txtLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Erro ao abrir dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Mensagem");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
