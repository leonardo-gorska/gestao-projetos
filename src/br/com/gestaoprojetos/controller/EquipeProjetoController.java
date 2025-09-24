package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.Equipe;
import br.com.gestaoprojetos.model.Projeto;
import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.service.EquipeService;
import br.com.gestaoprojetos.service.LogAtividadeService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class EquipeProjetoController {

    @FXML private TableView<Equipe> tableEquipes;
    @FXML private TableColumn<Equipe, Integer> colId;
    @FXML private TableColumn<Equipe, String> colNome;
    @FXML private TableColumn<Equipe, String> colDescricao;
    @FXML private TableColumn<Equipe, String> colGerente;
    @FXML private TableColumn<Equipe, String> colMembros;
    @FXML private TableColumn<Equipe, HBox> colAcao;

    private Projeto projeto;
    private Usuario usuarioLogado;

    private final EquipeService equipeService = new EquipeService();
    private final LogAtividadeService logService = new LogAtividadeService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        colDescricao.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescricao()));
        colGerente.setCellValueFactory(cell -> {
            if (cell.getValue().getGerente() != null)
                return new SimpleStringProperty(cell.getValue().getGerente().getNome());
            return new SimpleStringProperty("");
        });
        colMembros.setCellValueFactory(cell -> {
            if (cell.getValue().getMembros() == null || cell.getValue().getMembros().isEmpty())
                return new SimpleStringProperty("");
            return new SimpleStringProperty(String.join(", ",
                    cell.getValue().getMembros().stream().map(Usuario::getNome).toList()));
        });

        colAcao.setCellValueFactory(cell -> {
            Equipe equipe = cell.getValue();
            HBox box = new HBox(5);

            if (usuarioLogado != null && usuarioLogado.podeEditarEquipe()) {
                Button btnEditar = new Button("Editar");
                btnEditar.setOnAction(e -> abrirPopupEdicao(equipe));

                Button btnExcluir = new Button("Excluir");
                btnExcluir.setOnAction(e -> excluirEquipe(equipe));

                box.getChildren().addAll(btnEditar, btnExcluir);
            }

            return new SimpleObjectProperty<>(box);
        });
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
        listarEquipesDoProjeto();
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    private void listarEquipesDoProjeto() {
        if (projeto == null) return;
        try {
            List<Equipe> equipes = equipeService.buscarPorProjeto(projeto.getId());
            tableEquipes.setItems(FXCollections.observableArrayList(equipes));
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", "Não foi possível listar equipes do projeto: " + e.getMessage());
        }
    }

    private void abrirPopupEdicao(Equipe equipe) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Equipes.fxml"));
            Stage stage = new Stage();
            stage.setScene(new javafx.scene.Scene(loader.load()));
            EquipesController controller = loader.getController();
            controller.setUsuario(usuarioLogado);

            if (equipe != null) {
                controller.carregarEquipeParaEdicao(equipe);
            }

            stage.showAndWait();
            listarEquipesDoProjeto();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarErro("Erro", "Não foi possível abrir o popup de edição: " + ex.getMessage());
        }
    }

    private void excluirEquipe(Equipe equipe) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação");
        confirm.setHeaderText(null);
        confirm.setContentText("Deseja realmente excluir a equipe: " + equipe.getNome() + "?");

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    equipeService.excluir(equipe.getId());
                    logService.salvar(new br.com.gestaoprojetos.model.LogAtividade(usuarioLogado,
                            "Excluiu equipe: " + equipe.getNome(), "equipe"));
                    mostrarInfo("Equipe excluída com sucesso!");
                    listarEquipesDoProjeto();
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarErro("Erro", "Não foi possível excluir equipe: " + e.getMessage());
                }
            }
        });
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
