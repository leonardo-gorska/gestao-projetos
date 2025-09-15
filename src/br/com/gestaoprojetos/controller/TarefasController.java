package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.dao.TarefaDAO;
import br.com.gestaoprojetos.dao.ProjetoDAO;
import br.com.gestaoprojetos.dao.UsuarioDAO;
import br.com.gestaoprojetos.model.Tarefa;
import br.com.gestaoprojetos.model.Projeto;
import br.com.gestaoprojetos.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller para a tela de cadastro e listagem de tarefas.
 * Responsável por manipular os campos, validar entradas, salvar no banco e listar as tarefas.
 */
public class TarefasController {

    // ================== DAOs ==================
    private final TarefaDAO tarefaDAO = new TarefaDAO();
    private final ProjetoDAO projetoDAO = new ProjetoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // ================== Campos FXML ==================
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<Projeto> cbProjeto;
    @FXML private ComboBox<Usuario> cbResponsavel;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpTermino;

    // TableView e colunas
    @FXML private TableView<Tarefa> tableTarefas;
    @FXML private TableColumn<Tarefa, String> colTitulo;
    @FXML private TableColumn<Tarefa, String> colProjeto;
    @FXML private TableColumn<Tarefa, String> colResponsavel;
    @FXML private TableColumn<Tarefa, LocalDate> colDataInicio;
    @FXML private TableColumn<Tarefa, LocalDate> colDataTermino; // Nova coluna
    @FXML private TableColumn<Tarefa, String> colStatus;

    // Formatter para exibir datas no formato brasileiro
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Inicializa a tela ao abrir.
     */
    @FXML
    public void initialize() {
        try {
            // Preenche ComboBoxes com dados do banco
            cbProjeto.getItems().addAll(projetoDAO.listarTodos());
            cbResponsavel.getItems().addAll(usuarioDAO.listarTodos());

            // Configura colunas da TableView
            colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));

            // Coluna Projeto: exibe "Sem projeto" se não houver
            colProjeto.setCellValueFactory(cellData -> {
                Projeto p = cellData.getValue().getProjeto();
                return new javafx.beans.property.SimpleStringProperty(
                        p != null ? p.getNome() : "Sem projeto"
                );
            });

            // Coluna Responsável: exibe "Sem responsável" se não houver
            colResponsavel.setCellValueFactory(cellData -> {
                Usuario u = cellData.getValue().getResponsavel();
                return new javafx.beans.property.SimpleStringProperty(
                        u != null ? u.getNome() : "Sem responsável"
                );
            });

            // Colunas de datas com formatação amigável
            colDataInicio.setCellValueFactory(new PropertyValueFactory<>("dataInicioPrevista"));
            colDataInicio.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.format(dateFormatter));
                }
            });

            colDataTermino.setCellValueFactory(new PropertyValueFactory<>("dataTerminoPrevista"));
            colDataTermino.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.format(dateFormatter));
                }
            });

            // Coluna de status
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            // Atualiza lista de tarefas
            atualizarLista();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar projetos ou usuários: " + e.getMessage());
        }
    }

    /**
     * Salva uma nova tarefa no banco de dados.
     * Inclui validação dos campos obrigatórios.
     */
    @FXML
    public void salvarTarefa() {
        try {
            // Valida campos obrigatórios
            if (txtTitulo.getText().isEmpty() || cbProjeto.getValue() == null) {
                mostrarErro("Título e Projeto são obrigatórios.");
                return;
            }

            // Cria objeto tarefa e preenche com dados do formulário
            Tarefa tarefa = new Tarefa();
            tarefa.setTitulo(txtTitulo.getText());
            tarefa.setDescricao(txtDescricao.getText());
            tarefa.setProjeto(cbProjeto.getValue());
            tarefa.setResponsavel(cbResponsavel.getValue()); // pode ser null
            tarefa.setDataInicioPrevista(dpInicio.getValue()); // pode ser null
            tarefa.setDataTerminoPrevista(dpTermino.getValue()); // pode ser null
            tarefa.setStatus("Pendente"); // Status inicial fixo

            // Salva no banco
            tarefaDAO.salvar(tarefa);

            // Atualiza lista e limpa formulário
            atualizarLista();
            limparCampos();

            // Mensagem de sucesso
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Tarefa salva com sucesso!");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao salvar tarefa: " + e.getMessage());
        }
    }

    /**
     * Atualiza a TableView com as tarefas cadastradas no banco.
     */
    private void atualizarLista() {
        try {
            tableTarefas.getItems().clear();
            List<Tarefa> tarefas = tarefaDAO.listarTodos();
            tableTarefas.getItems().addAll(tarefas);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar lista de tarefas: " + e.getMessage());
        }
    }

    /**
     * Limpa os campos do formulário.
     */
    private void limparCampos() {
        txtTitulo.clear();
        txtDescricao.clear();
        cbProjeto.getSelectionModel().clearSelection();
        cbResponsavel.getSelectionModel().clearSelection();
        dpInicio.setValue(null);
        dpTermino.setValue(null);
    }

    /**
     * Mostra um alert de erro com a mensagem fornecida.
     */
    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
