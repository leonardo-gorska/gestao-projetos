package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.Equipe;
import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.model.LogAtividade;
import br.com.gestaoprojetos.service.EquipeService;
import br.com.gestaoprojetos.service.UsuarioService;
import br.com.gestaoprojetos.service.LogAtividadeService;
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
import javafx.scene.control.TableRow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EquipesController implements UsuarioRecebeController {

    @FXML private TextField txtNome;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<Usuario> comboGerente;

    @FXML private ListView<Usuario> listDisponiveis;
    @FXML private ListView<Usuario> listMembros;

    @FXML private Button btnAdicionar;
    @FXML private Button btnRemover;
    @FXML private Button btnSalvar;
    @FXML private Button btnLimpar;

    @FXML private TextField txtBuscaNome;

    @FXML private TableView<Equipe> tableEquipes;
    @FXML private TableColumn<Equipe, Integer> colId;
    @FXML private TableColumn<Equipe, String> colNome;
    @FXML private TableColumn<Equipe, String> colDescricao;
    @FXML private TableColumn<Equipe, String> colGerente;
    @FXML private TableColumn<Equipe, String> colMembros;
    @FXML private TableColumn<Equipe, HBox> colAcao;
    @FXML private TableColumn<Equipe, String> colAndamento;

    private final UsuarioService usuarioService = new UsuarioService();
    private final EquipeService equipeService = new EquipeService();
    private final LogAtividadeService logAtividadeService = new LogAtividadeService();

    private ObservableList<Usuario> usuariosDisponiveis = FXCollections.observableArrayList();
    private ObservableList<Usuario> membrosEquipe = FXCollections.observableArrayList();

    private Usuario usuarioLogado;
    private Equipe equipeAtual;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        atualizarPermissoes(); // habilita botões e tabela corretamente após login
    }

    @FXML
    public void initialize() {
        // Estilos de botões
        btnSalvar.getStyleClass().add("primary");
        btnLimpar.getStyleClass().add("danger");
        btnAdicionar.getStyleClass().add("neutral");
        btnRemover.getStyleClass().add("neutral");

        // Colunas da tabela
        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        colDescricao.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescricao()));
        colGerente.setCellValueFactory(cell -> {
            Usuario gerente = cell.getValue().getGerente();
            return new SimpleStringProperty(gerente != null ? gerente.getNome() : "");
        });
        colMembros.setCellValueFactory(cell -> {
            List<Usuario> membros = cell.getValue().getMembros();
            if (membros == null || membros.isEmpty()) return new SimpleStringProperty("");
            String nomes = membros.stream()
                    .map(Usuario::getNome)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(nomes);
        });
        colAndamento.setCellValueFactory(cell -> new SimpleStringProperty(calcularAndamento(cell.getValue())));

        tableEquipes.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Equipe item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    String perc = calcularAndamento(item);
                    if (perc.equals("100%")) {
                        setStyle("-fx-background-color: #90EE90;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        colAcao.setCellValueFactory(cell -> {
            Equipe equipe = cell.getValue();
            HBox box = new HBox(5);
            if (usuarioLogado != null && (usuarioLogado.isAdmin() || usuarioLogado.podeEditarEquipe())) {
                Button btnEditar = new Button("Editar");
                btnEditar.setTooltip(new Tooltip("Editar equipe"));
                btnEditar.getStyleClass().add("button");
                btnEditar.setOnAction(e -> carregarEquipeParaEdicao(equipe));

                Button btnExcluir = new Button("Excluir");
                btnExcluir.setTooltip(new Tooltip("Excluir equipe"));
                btnExcluir.getStyleClass().add("button");
                btnExcluir.setOnAction(e -> excluirEquipe(equipe));

                box.getChildren().addAll(btnEditar, btnExcluir);
            }
            return new SimpleObjectProperty<>(box);
        });

        listDisponiveis.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listMembros.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        carregarUsuarios();
        listarEquipes();

        btnAdicionar.setOnAction(e -> adicionarMembros());
        btnRemover.setOnAction(e -> removerMembros());
        btnSalvar.setOnAction(this::salvarEquipe);
        btnLimpar.setOnAction(this::limparCampos);

        txtBuscaNome.textProperty().addListener((obs, oldVal, newVal) -> buscarEquipe());
    }

    // Atualiza botões e tabela conforme perfil do usuário
    private void atualizarPermissoes() {
        boolean podeEditar = usuarioLogado != null && (usuarioLogado.isAdmin() || usuarioLogado.podeEditarEquipe());
        btnAdicionar.setDisable(!podeEditar);
        btnRemover.setDisable(!podeEditar);
        btnSalvar.setDisable(!podeEditar);
        btnLimpar.setDisable(!podeEditar);
        tableEquipes.refresh(); // força atualização das colunas de ação
    }

    private void carregarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.listarTodos();
            comboGerente.setItems(FXCollections.observableArrayList(
                    usuarios.stream().filter(Usuario::isGerente).toList()
            ));
            usuariosDisponiveis.setAll(usuarios);
            membrosEquipe.clear();
            atualizarListViews();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", "Não foi possível carregar usuários: " + e.getMessage());
        }
    }

    private void adicionarMembros() {
        List<Usuario> selecionados = new ArrayList<>(listDisponiveis.getSelectionModel().getSelectedItems());
        for (Usuario u : selecionados) {
            if (!membrosEquipe.contains(u)) membrosEquipe.add(u);
        }
        usuariosDisponiveis.removeAll(selecionados);
        atualizarListViews();
    }

    private void removerMembros() {
        List<Usuario> selecionados = new ArrayList<>(listMembros.getSelectionModel().getSelectedItems());
        for (Usuario u : selecionados) {
            if (!usuariosDisponiveis.contains(u)) usuariosDisponiveis.add(u);
        }
        membrosEquipe.removeAll(selecionados);
        atualizarListViews();
    }

    private void atualizarListViews() {
        listDisponiveis.setItems(FXCollections.observableArrayList(usuariosDisponiveis));
        listMembros.setItems(FXCollections.observableArrayList(membrosEquipe));
    }

    private void salvarEquipe(ActionEvent event) {
        if (usuarioLogado == null || !(usuarioLogado.isAdmin() || usuarioLogado.podeEditarEquipe())) {
            mostrarErro("Permissão", "Você não tem permissão para salvar ou editar equipes!");
            return;
        }

        boolean criando = (equipeAtual == null);
        if (criando) equipeAtual = new Equipe();

        equipeAtual.setNome(txtNome.getText().trim());
        equipeAtual.setDescricao(txtDescricao.getText().trim());
        equipeAtual.setGerente(comboGerente.getValue());
        equipeAtual.setMembros(new ArrayList<>(membrosEquipe));

        if (Validador.isVazio(equipeAtual.getNome())) {
            mostrarErro("Validação", "Nome da equipe é obrigatório.");
            return;
        }

        try {
            if (criando) {
                equipeService.salvar(equipeAtual);
                logAtividadeService.salvar(new LogAtividade(usuarioLogado, "Criou equipe: " + equipeAtual.getNome(), "equipe"));
                mostrarInfo("Equipe criada com sucesso!");
            } else {
                equipeService.atualizar(equipeAtual);
                logAtividadeService.salvar(new LogAtividade(usuarioLogado, "Atualizou equipe: " + equipeAtual.getNome(), "equipe"));
                mostrarInfo("Equipe atualizada com sucesso!");
            }
            listarEquipes();
            limparCampos(null);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro ao salvar/atualizar equipe", e.getMessage());
        }
    }

    private void limparCampos(ActionEvent event) {
        txtNome.clear();
        txtDescricao.clear();
        comboGerente.getSelectionModel().clearSelection();
        membrosEquipe.clear();
        carregarUsuarios();
        equipeAtual = null;
    }

    private void buscarEquipe() {
        String filtro = txtBuscaNome.getText();
        if (filtro == null || filtro.isBlank()) {
            listarEquipes();
            return;
        }
        try {
            List<Equipe> equipes = equipeService.buscarPorNome(filtro);
            tableEquipes.setItems(FXCollections.observableArrayList(equipes));
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", "Não foi possível buscar equipes: " + e.getMessage());
        }
    }

    private void listarEquipes() {
        try {
            List<Equipe> equipes = equipeService.listarTodos();
            tableEquipes.setItems(FXCollections.observableArrayList(equipes));
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro("Erro", "Não foi possível listar equipes: " + e.getMessage());
        }
    }

    public void carregarEquipeParaEdicao(Equipe equipe) {
        if (usuarioLogado == null || !(usuarioLogado.isAdmin() || usuarioLogado.podeEditarEquipe())) return;

        equipeAtual = equipe;
        txtNome.setText(equipe.getNome());
        txtDescricao.setText(equipe.getDescricao());
        comboGerente.setValue(equipe.getGerente());

        membrosEquipe.setAll(equipe.getMembros());
        usuariosDisponiveis.clear();
        try {
            List<Usuario> todos = usuarioService.listarTodos();
            for (Usuario u : todos) {
                if (!membrosEquipe.contains(u)) usuariosDisponiveis.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        atualizarListViews();
    }

    private void excluirEquipe(Equipe equipe) {
        if (usuarioLogado == null || !(usuarioLogado.isAdmin() || usuarioLogado.podeEditarEquipe())) {
            mostrarErro("Permissão", "Você não tem permissão para excluir equipes!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação");
        confirm.setHeaderText(null);
        confirm.setContentText("Deseja realmente excluir a equipe: " + equipe.getNome() + "?");

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    equipeService.excluir(equipe.getId());
                    logAtividadeService.salvar(new LogAtividade(usuarioLogado, "Excluiu equipe: " + equipe.getNome(), "equipe"));
                    mostrarInfo("Equipe excluída com sucesso!");
                    listarEquipes();
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarErro("Erro", "Não foi possível excluir equipe: " + e.getMessage());
                }
            }
        });
    }

    private String calcularAndamento(Equipe equipe) {
        if (equipe.getMembros() == null || equipe.getMembros().isEmpty()) return "0%";
        long ativos = equipe.getMembros().stream().filter(Usuario::isAtivo).count();
        return (ativos * 100 / equipe.getMembros().size()) + "%";
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
