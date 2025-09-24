package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.*;
import br.com.gestaoprojetos.service.*;
import br.com.gestaoprojetos.util.Validador;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class TarefasController implements UsuarioRecebeController {

    private final TarefaService tarefaService = new TarefaService();
    private final LogAtividadeService logAtividadeService = new LogAtividadeService();
    private final ProjetoService projetoService = new ProjetoService();

    private Usuario usuarioLogado;
    private Tarefa tarefaAtual = null;
    private Projeto projetoAtual = null;

    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescricao;
    @FXML private TableView<Tarefa> tableTarefas;
    @FXML private TableColumn<Tarefa, Integer> colId;
    @FXML private TableColumn<Tarefa, String> colTitulo;
    @FXML private TableColumn<Tarefa, String> colDescricao;
    @FXML private TableColumn<Tarefa, String> colStatus;
    @FXML private TableColumn<Tarefa, HBox> colAcao;
    @FXML private ComboBox<Usuario> cbResponsavel;
    @FXML private ComboBox<String> cbStatus;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpTermino;
    @FXML private ComboBox<Projeto> cbProjeto;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void setProjeto(Projeto projeto) {
        this.projetoAtual = projeto;

        cbProjeto.getItems().clear();
        cbProjeto.getItems().add(projetoAtual);
        cbProjeto.getSelectionModel().select(projetoAtual);
        cbProjeto.setDisable(true);

        atualizarResponsaveis();
        limparCampos(null);
        listarTarefas();
    }

    @FXML
    public void initialize() {
        // Configura colunas
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colTitulo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitulo()));
        colDescricao.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescricao()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // Coluna de ações com permissões
        colAcao.setCellValueFactory(cellData -> {
            Tarefa tarefa = cellData.getValue();
            HBox box = new HBox(5);
            if (usuarioLogado != null && usuarioPodeEditarTarefa(tarefa)) {
                Button btnEditar = new Button("Editar");
                btnEditar.setTooltip(new Tooltip("Editar tarefa"));
                btnEditar.setOnAction(e -> carregarTarefaParaEdicao(tarefa));

                Button btnExcluir = new Button("Excluir");
                btnExcluir.setTooltip(new Tooltip("Excluir tarefa"));
                btnExcluir.setOnAction(e -> excluirTarefa(tarefa));

                box.getChildren().addAll(btnEditar, btnExcluir);
            }
            return new SimpleObjectProperty<>(box);
        });

        // Colorir linhas por status igual à tabela de projetos
        tableTarefas.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Tarefa tarefa, boolean empty) {
                super.updateItem(tarefa, empty);

                if (tarefa == null || empty) {
                    setStyle("");
                    setTextFill(null);
                } else {
                    String status = tarefa.getStatus() != null ? tarefa.getStatus().toLowerCase() : "";
                    String bgColor;
                    switch (status) {
                        case "pendente" -> bgColor = "#FFCCCC";
                        case "em andamento" -> bgColor = "#FFFFCC";
                        case "concluída" -> bgColor = "#CCFFCC";
                        default -> bgColor = "transparent";
                    }

                    if (isSelected()) {
                        setStyle("-fx-background-color: derive(" + bgColor + ", -20%);" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5,0,0,0);");
                        setTextFill(javafx.scene.paint.Color.BLACK);
                    } else {
                        setStyle("-fx-background-color: " + bgColor + ";");
                        setTextFill(javafx.scene.paint.Color.BLACK);
                    }
                }
            }
        });

        cbProjeto.setOnAction(event -> {
            projetoAtual = cbProjeto.getValue();
            atualizarResponsaveis();
            listarTarefas();
        });

        cbStatus.getItems().addAll("Pendente", "Em andamento", "Concluída");
        cbStatus.getSelectionModel().selectFirst();
    }

    private void atualizarResponsaveis() {
        cbResponsavel.getItems().clear();

        if (projetoAtual == null) return;

        try {
            projetoAtual = projetoService.buscarPorId(projetoAtual.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar projeto", e.getMessage());
            return;
        }

        if (projetoAtual.getEquipes() != null) {
            for (Equipe equipe : projetoAtual.getEquipes()) {
                if (equipe.getMembros() != null) {
                    for (Usuario membro : equipe.getMembros()) {
                        if (!cbResponsavel.getItems().contains(membro)) {
                            cbResponsavel.getItems().add(membro);
                        }
                    }
                }
            }
        }

        if (!cbResponsavel.getItems().isEmpty()) {
            cbResponsavel.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void salvarTarefa(ActionEvent event) {
        if (usuarioLogado == null || projetoAtual == null) {
            mostrarErro("Permissão", "Você não tem permissão ou projeto não selecionado!");
            return;
        }

        boolean criando = (tarefaAtual == null);
        if (criando) tarefaAtual = new Tarefa();

        tarefaAtual.setTitulo(txtTitulo.getText().trim());
        tarefaAtual.setDescricao(txtDescricao.getText().trim());
        tarefaAtual.setProjeto(projetoAtual);
        tarefaAtual.setResponsavel(cbResponsavel.getValue());
        tarefaAtual.setStatus(cbStatus.getValue());
        tarefaAtual.setDataInicio(dpInicio.getValue());
        tarefaAtual.setDataTermino(dpTermino.getValue());

        if (Validador.isVazio(tarefaAtual.getTitulo())) {
            mostrarErro("Validação", "Título da tarefa é obrigatório.");
            return;
        }

        try {
            if (criando) {
                tarefaService.salvar(tarefaAtual, usuarioLogado);
                logAtividadeService.salvar(new LogAtividade(usuarioLogado, "Criou tarefa: " + tarefaAtual.getTitulo(), "tarefa"));
                mostrarInfo("Tarefa criada com sucesso!");
            } else {
                tarefaService.atualizar(tarefaAtual, usuarioLogado);
                logAtividadeService.salvar(new LogAtividade(usuarioLogado, "Atualizou tarefa: " + tarefaAtual.getTitulo(), "tarefa"));
                mostrarInfo("Tarefa atualizada com sucesso!");
            }

            listarTarefas();
            limparCampos(null);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro ao salvar/atualizar tarefa", e.getMessage());
        }
    }

    private void carregarTarefaParaEdicao(Tarefa tarefa) {
        tarefaAtual = tarefa;
        txtTitulo.setText(tarefa.getTitulo());
        txtDescricao.setText(tarefa.getDescricao());
        cbResponsavel.setValue(tarefa.getResponsavel());
        cbStatus.setValue(tarefa.getStatus());
        dpInicio.setValue(tarefa.getDataInicio());
        dpTermino.setValue(tarefa.getDataTermino());
    }

    @FXML
    private void limparCampos(ActionEvent event) {
        txtTitulo.clear();
        txtDescricao.clear();
        cbResponsavel.getSelectionModel().clearSelection();
        cbStatus.getSelectionModel().selectFirst();
        dpInicio.setValue(null);
        dpTermino.setValue(null);
        tarefaAtual = null;
    }

    private void listarTarefas() {
        if (projetoAtual == null) return;

        try {
            List<Tarefa> tarefas = tarefaService.listarPorProjeto(projetoAtual.getId());

            if (usuarioLogado != null && !usuarioLogado.isAdmin() && !usuarioLogado.isGerente()) {
                tarefas = tarefas.stream()
                        .filter(t -> t.getResponsavel() != null && t.getResponsavel().getId() == usuarioLogado.getId())
                        .collect(Collectors.toList());
            }

            tableTarefas.setItems(FXCollections.observableArrayList(tarefas));
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", e.getMessage());
        }
    }

    private void excluirTarefa(Tarefa tarefa) {
        if (usuarioLogado == null || (!usuarioLogado.isAdmin() && !usuarioLogado.isGerente() && (tarefa.getResponsavel() == null || tarefa.getResponsavel().getId() != usuarioLogado.getId()))) {
            mostrarErro("Permissão", "Você não tem permissão para excluir esta tarefa!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação");
        confirm.setHeaderText(null);
        confirm.setContentText("Deseja realmente excluir a tarefa " + tarefa.getTitulo() + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                tarefaService.excluir(tarefa.getId(), usuarioLogado);
                listarTarefas();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarErro("Erro ao excluir tarefa", e.getMessage());
            }
        }
    }

    private boolean usuarioPodeEditarTarefa(Tarefa tarefa) {
        if (usuarioLogado == null) return false;
        return usuarioLogado.isGerente() || usuarioLogado.isAdmin() ||
                (tarefa.getResponsavel() != null && tarefa.getResponsavel().getId() == usuarioLogado.getId());
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
