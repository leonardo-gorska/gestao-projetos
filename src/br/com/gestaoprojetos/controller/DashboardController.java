package br.com.gestaoprojetos.controller;

import br.com.gestaoprojetos.model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controlador do Dashboard principal do sistema.
 * Responsável por abrir as telas de CRUD, aplicar permissões por perfil e gerenciar o logout.
 */
public class DashboardController {

    // Label de boas-vindas
    @FXML private Label lblBoasVindas;

    // Botões do dashboard
    @FXML private Button btnUsuarios, btnProjetos, btnEquipes, btnTarefas, btnRelatorios;

    // Usuário logado atualmente
    private Usuario usuarioLogado;

    /**
     * Define o usuário logado e aplica as permissões de acordo com seu perfil.
     * @param u Usuário que fez login
     */
    public void setUsuarioLogado(Usuario u){
        this.usuarioLogado = u;
        lblBoasVindas.setText("Olá, " + u.getNome() + "!");
        aplicarPermissoes();
    }

    /**
     * Habilita ou desabilita os botões do dashboard de acordo com o perfil do usuário.
     * Perfis: Administrador, Gerente, Colaborador
     */
    private void aplicarPermissoes(){
        String perfil = usuarioLogado.getPerfil();

        switch(perfil){
            case "Administrador":
                btnUsuarios.setVisible(true);
                btnProjetos.setVisible(true);
                btnEquipes.setVisible(true);
                btnTarefas.setVisible(true);
                btnRelatorios.setVisible(true);
                break;

            case "Gerente":
                btnUsuarios.setVisible(false);
                btnProjetos.setVisible(true);
                btnEquipes.setVisible(true);
                btnTarefas.setVisible(true);
                btnRelatorios.setVisible(true);
                break;

            case "Colaborador":
                btnUsuarios.setVisible(false);
                btnProjetos.setVisible(false);
                btnEquipes.setVisible(false);
                btnTarefas.setVisible(true);
                btnRelatorios.setVisible(false);
                break;

            default:
                // Caso o perfil seja inválido, esconde todos os botões
                btnUsuarios.setVisible(false);
                btnProjetos.setVisible(false);
                btnEquipes.setVisible(false);
                btnTarefas.setVisible(false);
                btnRelatorios.setVisible(false);
                break;
        }
    }

    // ---------- Métodos para abrir telas de CRUD ----------

    @FXML private void abrirUsuarios() { abrirTela("/fxml/Usuarios.fxml", "Gestão de Usuários"); }
    @FXML private void abrirProjetos() { abrirTela("/fxml/Projetos.fxml", "Gestão de Projetos"); }
    @FXML private void abrirEquipes() { abrirTela("/fxml/Equipes.fxml", "Gestão de Equipes"); }
    @FXML private void abrirTarefas() { abrirTela("/fxml/Tarefas.fxml", "Gestão de Tarefas"); }

    /**
     * Abre a tela de relatórios
     */
    @FXML private void abrirRelatorios() {
        abrirTela("/fxml/Relatorios.fxml", "Relatórios do Sistema");
    }

    /**
     * Método genérico para abrir qualquer tela FXML.
     * Garante que o carregamento do FXML seja feito de forma segura.
     * @param fxml Caminho do arquivo FXML dentro de resources
     * @param titulo Título da janela
     */
    private void abrirTela(String fxml, String titulo){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e){
            // Mostra no console caso o FXML não seja encontrado ou ocorra outro erro
            e.printStackTrace();
        }
    }

    // ---------- Logout ----------

    /**
     * Fecha o dashboard atual e abre a tela de login.
     */
    @FXML private void logout() {
        try {
            // Fecha o stage atual
            Stage stage = (Stage) lblBoasVindas.getScene().getWindow();
            stage.close();

            // Carrega o login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
