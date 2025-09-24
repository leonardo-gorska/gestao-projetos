package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.*;
import br.com.gestaoprojetos.service.*;
import br.com.gestaoprojetos.util.Validador;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.controlsfx.control.CheckComboBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ProjetosController implements UsuarioRecebeController {

    private final ProjetoService projetoService = new ProjetoService();
    private final EquipeService equipeService = new EquipeService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final LogAtividadeService logAtividadeService = new LogAtividadeService();
    private final TarefaService tarefaService = new TarefaService();

    private Usuario usuarioLogado;
    private Projeto projetoAtual = null;
    private Projeto projetoSelecionado = null;

    @FXML private TextField txtNome;
    @FXML private TextArea txtDescricao;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpTermino;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private CheckComboBox<Equipe> cmbEquipe;
    @FXML private ComboBox<Usuario> cmbGerente;

    @FXML private TableView<Projeto> tableProjetos;
    @FXML private TableColumn<Projeto, Integer> colId;
    @FXML private TableColumn<Projeto, String> colNome;
    @FXML private TableColumn<Projeto, String> colDescricao;
    @FXML private TableColumn<Projeto, LocalDate> colInicio;
    @FXML private TableColumn<Projeto, LocalDate> colTermino;
    @FXML private TableColumn<Projeto, String> colStatus;
    @FXML private TableColumn<Projeto, String> colEquipe;
    @FXML private TableColumn<Projeto, String> colGerente;
    @FXML private TableColumn<Projeto, String> colAndamento;
    @FXML private TableColumn<Projeto, HBox> colAcao;
    @FXML private ComboBox<String> cmbFiltroStatus;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    @FXML
    public void initialize() {
        // Configura colunas
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colDescricao.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescricao()));
        colInicio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDataInicio()));
        colTermino.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDataTerminoPrevista()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        colEquipe.setCellValueFactory(cellData -> {
            Projeto p = cellData.getValue();
            if (p.getEquipes() != null && !p.getEquipes().isEmpty()) {
                String nomes = p.getEquipes().stream()
                        .map(Equipe::getNome)
                        .collect(Collectors.joining(", "));
                return new SimpleStringProperty(nomes);
            }
            return new SimpleStringProperty("");
        });
        colGerente.setCellValueFactory(cellData -> {
            Usuario g = cellData.getValue().getGerente();
            return new SimpleStringProperty(g != null ? g.getNome() : "");
        });
        colAndamento.setCellValueFactory(cellData -> new SimpleStringProperty(calcularAndamento(cellData.getValue())));

        // Coluna de ações com permissões
        colAcao.setCellValueFactory(cellData -> {
            Projeto projeto = cellData.getValue();
            HBox box = new HBox(5);
            if (usuarioLogado != null && usuarioPodeEditarProjeto(projeto)) {
                Button btnEditar = new Button("Editar");
                btnEditar.setTooltip(new Tooltip("Editar projeto"));
                btnEditar.setOnAction(e -> carregarProjetoParaEdicao(projeto));

                Button btnExcluir = new Button("Excluir");
                btnExcluir.setTooltip(new Tooltip("Excluir projeto"));
                btnExcluir.setOnAction(e -> excluirProjeto(projeto));

                box.getChildren().addAll(btnEditar, btnExcluir);
            }
            return new SimpleObjectProperty<>(box);
        });

        // Itens do status
        cmbStatus.getItems().addAll("Planejado", "Em Andamento", "Concluído");
        cmbEquipe.setTitle("Selecione equipes");
        cmbGerente.setPromptText("Selecione o gerente");
        cmbFiltroStatus.getItems().addAll("", "Planejado", "Em Andamento", "Concluído");
        cmbFiltroStatus.setValue("");

        // Carrega dados
        carregarEquipes();
        carregarGerentes();
        listarProjetos();

        // Listener CheckComboBox
        cmbEquipe.getCheckModel().getCheckedItems().addListener((ListChangeListener<Equipe>) change -> {
            List<String> nomes = cmbEquipe.getCheckModel().getCheckedItems()
                    .stream()
                    .map(Equipe::getNome)
                    .collect(Collectors.toList());
            cmbEquipe.setTitle(String.join(", ", nomes));
        });

        // Resize
        tableProjetos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Colorir linhas por status
        tableProjetos.setRowFactory(tv -> new TableRow<Projeto>() {
            @Override
            protected void updateItem(Projeto projeto, boolean empty) {
                super.updateItem(projeto, empty);
                if (projeto == null || empty) {
                    setStyle("");
                    setTextFill(null);
                } else {
                    String status = projeto.getStatus() != null ? projeto.getStatus().toLowerCase() : "";
                    String bgColor;
                    switch (status) {
                        case "planejado" -> bgColor = "#FFCCCC";
                        case "em andamento" -> bgColor = "#FFFFCC";
                        case "concluído" -> bgColor = "#CCFFCC";
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

        // Filtro de status
        cmbFiltroStatus.setOnAction(e -> aplicarFiltro());
    }

    private boolean usuarioPodeEditarProjeto(Projeto projeto) {
        if (usuarioLogado == null) return false;
        if (usuarioLogado.isAdmin()) return true;
        return usuarioLogado.isGerente() && projeto.getGerente() != null &&
                projeto.getGerente().getId() == usuarioLogado.getId();
    }

    @FXML
    private void salvarProjeto(ActionEvent event) {
        if (usuarioLogado == null || (!usuarioLogado.isAdmin() && !usuarioLogado.isGerente())) {
            mostrarErro("Permissão", "Apenas administradores e gerentes podem salvar projetos!");
            return;
        }

        boolean criando = (projetoAtual == null);
        if (criando) projetoAtual = new Projeto();

        projetoAtual.setNome(txtNome.getText().trim());
        projetoAtual.setDescricao(txtDescricao.getText().trim());
        projetoAtual.setDataInicio(dpInicio.getValue());
        projetoAtual.setDataTerminoPrevista(dpTermino.getValue());
        projetoAtual.setStatus(cmbStatus.getValue());
        projetoAtual.setEquipes(cmbEquipe.getCheckModel().getCheckedItems());
        projetoAtual.setGerente(cmbGerente.getValue());

        if (Validador.isVazio(projetoAtual.getNome())) { mostrarErro("Validação", "Nome do projeto é obrigatório."); return; }
        if (Validador.isVazio(projetoAtual.getDescricao())) { mostrarErro("Validação", "Descrição do projeto é obrigatória."); return; }
        if (!Validador.isPeriodoValido(projetoAtual.getDataInicio(), projetoAtual.getDataTerminoPrevista())) {
            mostrarErro("Validação", "Datas inconsistentes."); return;
        }

        try {
            if (criando) {
                projetoService.salvar(projetoAtual);
                logAtividadeService.salvar(new LogAtividade(usuarioLogado, "Criou projeto: " + projetoAtual.getNome(), "projeto"));
                mostrarInfo("Projeto criado com sucesso!");
            } else {
                projetoService.atualizar(projetoAtual);
                logAtividadeService.salvar(new LogAtividade(usuarioLogado, "Atualizou projeto: " + projetoAtual.getNome(), "projeto"));
                mostrarInfo("Projeto atualizado com sucesso!");
            }
            listarProjetos();
            limparCampos(null);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro ao salvar/atualizar projeto", e.getMessage());
        }
    }

    private void carregarProjetoParaEdicao(Projeto projeto) {
        projetoAtual = projeto;
        txtNome.setText(projeto.getNome());
        txtDescricao.setText(projeto.getDescricao());
        dpInicio.setValue(projeto.getDataInicio());
        dpTermino.setValue(projeto.getDataTerminoPrevista());
        cmbStatus.setValue(projeto.getStatus());

        cmbEquipe.getCheckModel().clearChecks();
        if (projeto.getEquipes() != null)
            projeto.getEquipes().forEach(e -> cmbEquipe.getCheckModel().check(e));

        cmbGerente.setValue(projeto.getGerente());
    }

    @FXML
    private void limparCampos(ActionEvent event) {
        txtNome.clear();
        txtDescricao.clear();
        dpInicio.setValue(null);
        dpTermino.setValue(null);
        cmbStatus.getSelectionModel().clearSelection();
        cmbEquipe.getCheckModel().clearChecks();
        cmbGerente.getSelectionModel().clearSelection();
        projetoAtual = null;
    }

    @FXML
    private void listarProjetos() {
        try {
            List<Projeto> projetos = projetoService.listarTodos();
            if (usuarioLogado != null && !usuarioLogado.isAdmin() && !usuarioLogado.isGerente()) {
                projetos = projetos.stream()
                        .filter(p -> p.getEquipes() != null &&
                                p.getEquipes().stream()
                                        .flatMap(e -> e.getUsuarios().stream())
                                        .anyMatch(u -> u.getId() == usuarioLogado.getId()))
                        .collect(Collectors.toList());
            }
            tableProjetos.setItems(FXCollections.observableArrayList(projetos));
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", e.getMessage());
        }
    }

    private void carregarEquipes() {
        try {
            List<Equipe> equipes = equipeService.listarTodos();
            cmbEquipe.getItems().setAll(equipes);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", e.getMessage());
        }
    }

    private void carregarGerentes() {
        try {
            cmbGerente.getItems().setAll(usuarioService.listarTodos());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", e.getMessage());
        }
    }

    private void excluirProjeto(Projeto projeto) {
        if (usuarioLogado == null || (!usuarioLogado.isAdmin() && !usuarioLogado.isGerente())) {
            mostrarErro("Permissão", "Apenas administradores e gerentes podem excluir projetos!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação");
        confirm.setHeaderText(null);
        confirm.setContentText("Deseja realmente excluir o projeto " + projeto.getNome() + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                projetoService.excluir(projeto.getId());
                logAtividadeService.salvar(new LogAtividade(usuarioLogado, "Excluiu projeto: " + projeto.getNome(), "projeto"));
                listarProjetos();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarErro("Erro ao excluir projeto", e.getMessage());
            }
        }
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

    private String calcularAndamento(Projeto projeto) {
        try {
            List<Tarefa> tarefas = tarefaService.listarPorProjeto(projeto.getId());
            if (tarefas == null || tarefas.isEmpty()) return "0%";
            long concluidas = tarefas.stream().filter(t -> "concluída".equalsIgnoreCase(t.getStatus())).count();
            double perc = ((double) concluidas / tarefas.size()) * 100;
            return String.format("%.0f%%", perc);
        } catch (SQLException e) {
            e.printStackTrace();
            return "0%";
        }
    }

    @FXML
    private void aplicarFiltro() {
        String statusFiltro = cmbFiltroStatus.getValue();
        if (statusFiltro == null || statusFiltro.isEmpty()) {
            listarProjetos();
            return;
        }

        try {
            ObservableList<Projeto> todos = FXCollections.observableArrayList(projetoService.listarTodos());
            if (usuarioLogado != null && !usuarioLogado.isAdmin() && !usuarioLogado.isGerente()) {
                todos = todos.stream()
                        .filter(p -> p.getEquipes() != null &&
                                p.getEquipes().stream()
                                        .flatMap(e -> e.getUsuarios().stream())
                                        .anyMatch(u -> u.getId() == usuarioLogado.getId()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
            }

            ObservableList<Projeto> filtrados = todos.stream()
                    .filter(p -> p.getStatus().equals(statusFiltro))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            tableProjetos.setItems(filtrados);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", e.getMessage());
        }
    }

    @FXML
    private void selecionarProjeto() {
        projetoSelecionado = tableProjetos.getSelectionModel().getSelectedItem();
        Stage stage = (Stage) tableProjetos.getScene().getWindow();
        stage.close();
    }

    public Projeto getProjetoSelecionado() {
        return projetoSelecionado;
    }
}
