package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.Projeto;
import br.com.gestaoprojetos.model.Tarefa;
import br.com.gestaoprojetos.model.Usuario;
import br.com.gestaoprojetos.service.ProjetoService;
import br.com.gestaoprojetos.service.RelatorioService;
import br.com.gestaoprojetos.service.TarefaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatoriosController {

    @FXML private TableView<Projeto> tableProjetos;
    @FXML private TableColumn<Projeto, String> colNome;
    @FXML private TableColumn<Projeto, String> colStatus;
    @FXML private TableColumn<Projeto, String> colRisco;
    @FXML private TableColumn<Projeto, String> colConclusao;
    @FXML private TableColumn<Projeto, String> colDesempenho;

    private final RelatorioService relatorioService = new RelatorioService();
    private final TarefaService tarefaService = new TarefaService();
    private final ProjetoService projetoService = new ProjetoService();

    private Usuario usuarioLogado;

    @FXML
    public void initialize() {
        // Inicializa colunas
        colNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));
        colRisco.setCellValueFactory(cell -> new SimpleStringProperty(calcularRisco(cell.getValue())));

        // Coluna Conclusão com percentual total do projeto
        colConclusao.setCellValueFactory(cell -> {
            try {
                double perc = projetoService.calcularAndamento(cell.getValue());
                return new SimpleStringProperty(String.format("%.0f%%", perc));
            } catch (SQLException e) {
                e.printStackTrace();
                return new SimpleStringProperty("Erro");
            }
        });

        // Coluna Desempenho
        colDesempenho.setCellValueFactory(cell -> new SimpleStringProperty(calcularDesempenho(cell.getValue())));

        // Colorir linhas conforme percentual total do projeto
        tableProjetos.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Projeto item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                    setTextFill(null);
                } else {
                    try {
                        double percProjeto = projetoService.calcularAndamento(item);
                        String bgColor;

                        if (percProjeto == 100) {
                            bgColor = "#90EE90"; // verde forte
                        } else if (percProjeto >= 75) {
                            bgColor = "#CCFFCC"; // verde claro
                        } else if (percProjeto >= 50) {
                            bgColor = "#FFFF99"; // amarelo claro
                        } else if (percProjeto >= 25) {
                            bgColor = "#FFD699"; // laranja claro
                        } else {
                            bgColor = "#FFB6B6"; // vermelho suave
                        }

                        if (isSelected()) {
                            setStyle("-fx-background-color: derive(" + bgColor + ", -20%);" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5,0,0,0);");
                        } else {
                            setStyle("-fx-background-color: " + bgColor + ";");
                        }
                        setTextFill(javafx.scene.paint.Color.BLACK);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        carregarProjetos();
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    private void carregarProjetos() {
        try {
            List<Projeto> projetos = relatorioService.listarProjetos();
            tableProjetos.getItems().setAll(projetos);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao carregar projetos: " + e.getMessage());
        }
    }

    private String calcularRisco(Projeto projeto) {
        try {
            List<Tarefa> tarefas = tarefaService.listarPorProjeto(projeto.getId());
            LocalDate hoje = LocalDate.now();

            for (Tarefa t : tarefas) {
                if (t.getDataTerminoPrevista() != null &&
                        (t.getDataTermino() == null || t.getDataTermino().isAfter(t.getDataTerminoPrevista()))) {
                    return "Em risco";
                }
            }
            return "Ok";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro";
        }
    }

    private String calcularDesempenho(Projeto projeto) {
        try {
            List<Tarefa> tarefas = tarefaService.listarPorProjeto(projeto.getId());
            Map<Usuario, double[]> desempenho = new HashMap<>();

            for (Tarefa t : tarefas) {
                Usuario u = t.getResponsavel();
                if (u == null) continue;
                desempenho.putIfAbsent(u, new double[2]);
                desempenho.get(u)[0]++; // total de tarefas

                String status = t.getStatus();
                if ("Concluída".equalsIgnoreCase(status)) {
                    desempenho.get(u)[1] += 1.0; // 100%
                } else if ("Em Andamento".equalsIgnoreCase(status)) {
                    desempenho.get(u)[1] += 0.5; // 50%
                } else {
                    desempenho.get(u)[1] += 0.0; // 0%
                }
            }

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Usuario, double[]> entry : desempenho.entrySet()) {
                Usuario u = entry.getKey();
                double total = entry.getValue()[0];
                double concluido = entry.getValue()[1];
                int percentual = total > 0 ? (int)((concluido / total) * 100) : 0;
                sb.append(u.getNome()).append(": ").append(percentual).append("%; ");
            }

            return sb.toString();

        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro";
        }
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
