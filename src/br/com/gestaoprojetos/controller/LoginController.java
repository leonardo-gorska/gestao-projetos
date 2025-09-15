package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.dao.UsuarioDAO;
import br.com.gestaoprojetos.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtLogin;

    @FXML
    private PasswordField txtSenha;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Método chamado ao clicar no botão de login
    @FXML
    private void login(ActionEvent event) {
        String login = txtLogin.getText();
        String senha = txtSenha.getText();

        try {
            // Tenta autenticar o usuário no banco
            Usuario usuario = usuarioDAO.autenticar(login, senha);

            if (usuario != null) {
                // Login correto
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login");
                alert.setHeaderText(null);
                alert.setContentText("Login realizado com sucesso!");
                alert.showAndWait();

                // Carrega o Dashboard.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
                Scene scene = new Scene(loader.load());

                // Cria uma nova janela
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Dashboard");
                stage.show();

                // Fecha a tela de login
                txtLogin.getScene().getWindow().hide();

            } else {
                // Login incorreto
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login");
                alert.setHeaderText(null);
                alert.setContentText("Login ou senha incorretos!");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao abrir o Dashboard!");
            alert.showAndWait();
        }
    }
}
