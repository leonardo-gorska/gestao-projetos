package br.com.gestaoprojetos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Carrega FontAwesome 7 Free
        Font.loadFont(getClass().getResourceAsStream("/fonts/fontawesome/fa-solid-900.otf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/fonts/fontawesome/fa-regular-400.otf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/fonts/fontawesome/fa-brands-400.otf"), 10);


        // Carrega a tela de login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Gestão de Projetos - Login");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
