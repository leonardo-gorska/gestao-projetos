# 📘 Sistema de Gestão de Projetos e Equipes

## 📌 Descrição do Projeto

O sistema tem como objetivo auxiliar na gestão de projetos e equipes, permitindo o cadastro e manutenção de usuários, projetos, tarefas, equipes e a alocação entre eles.  
Além disso, o sistema contará com relatórios de acompanhamento e dashboards, oferecendo uma visão clara do andamento dos projetos e da performance das equipes.

O sistema terá diferentes perfis de acesso (Administrador, Gerente e Colaborador), com permissões distintas de acordo com o papel de cada usuário.  
A interface será construída em Java (Swing/JavaFX) conectada a um banco de dados MySQL.

---

## ⚙️ Tecnologias Utilizadas

- Java 17+  
- JavaFX (interface gráfica)  
- MySQL (persistência de dados)  
- Maven (gerenciamento de dependências)  
- SceneBuilder (prototipação de telas em FXML)  

---

## ✅ Requisitos Iniciais (Explícitos)

### Cadastro de Usuários
- Nome completo, CPF, e-mail, cargo, login, senha.  
- Perfis: administrador, gerente, colaborador.  

### Cadastro de Projetos
- Nome, descrição, datas de início e término previstas, status (planejado, em andamento, concluído, cancelado).  
- Cada projeto deve ter um gerente responsável.  

### Cadastro de Equipes
- Nome da equipe, descrição, membros vinculados.  
- Uma equipe pode atuar em vários projetos.  

### Alocação de Equipes a Projetos
- Projetos podem ter várias equipes.  
- Equipes podem estar em mais de um projeto.  

### Cadastro de Tarefas
- Título, descrição, projeto vinculado, responsável, status (pendente, em execução, concluída).  
- Datas de início e fim (previstas e reais).  
- Cada tarefa pertence a um único projeto.  

### Relatórios e Dashboards
- Andamento dos projetos.  
- Desempenho dos colaboradores.  
- Projetos com risco de atraso.  

### Autenticação
- Tela de login com validação no banco de dados.  

### Interface Visual
- Protótipos antes da codificação.  
- Navegação simples e intuitiva.  

---

## 🤔 Requisitos Implícitos (a serem definidos pelo time)

- Vinculação entre tarefas, projetos e colaboradores.  
- Usuário pode estar em mais de uma equipe?  
- O que acontece com tarefas se o projeto for cancelado?  
- Histórico de alterações de status das tarefas.  
- Controle de permissões baseado no perfil.  
- Relacionamentos no banco de dados.  
- Organização dos pacotes Java.  
- Logs de acesso e auditoria.  
- Tratamento de campos obrigatórios e validações.  

---

## 📂 Estrutura Atual do Projeto

C:.
| .gitignore
| estrutura.txt
| gestao-projetos.iml
| pom.xml
| README.txt
| README.txt
|
+---.idea
+---.mvn
+---src
| +---br/com/gestaoprojetos
| | | Main.java
| | +---controller
| | +---dao
| | +---model
| | +---service
| | +---util
| | ---view
| +---lib
| +---main/java/org/example
| +---main/resources/fxml
| ---test/java
---target

📌 Estrutura organizada em camadas (**MVC + DAO + util**), com telas em FXML e controllers vinculados.  

---

📂 Banco de Dados

O projeto inclui o arquivo `database.sql` na raiz, que contém:

- Estrutura de tabelas (`usuario`, `usuarios`, `projeto`, `tarefa`, `equipe`, `cargo`, `logs_atividade`, tabelas de relacionamento, etc.)
- Dados iniciais para testes (usuários, cargos, tarefas, etc.)
- Chaves primárias e estrangeiras para relacionamentos corretos

Para restaurar o banco de dados, basta executar o script no MySQL Workbench ou via terminal.

> O arquivo `database.sql` está localizado na raiz do projeto.

---

## 🔎 Situação Atual do Projeto

### ✅ O que já foi feito
- Estrutura inicial do **projeto em Java** criada (pacotes controller, dao, model, service, util e view).  
- **Banco de dados MySQL** modelado e exportado (dump completo disponível).  
- Tabelas principais implementadas:  
  - `usuario` / `usuarios` (duas versões em uso no momento).  
  - `cargo`, `projeto`, `tarefa`, `equipe`, `equipe_usuario`, `projeto_equipe`, `logs_atividade`.  
- **Inserts iniciais de teste** já criados (usuários, cargos, tarefas).  
- Conexão com o banco de dados estabelecida.  
- Início da implementação da camada DAO.  
- Estrutura MVC em andamento.  

### 🚧 O que falta
- Resolver a duplicidade entre as tabelas `usuario` e `usuarios` (unificar e ajustar FKs).  
- Finalizar todos os **CRUDs de DAO** (Usuário, Projeto, Tarefa, Equipe).  
- Criar camada de **Serviços (Service)** para encapsular regras de negócio.  
- Implementar telas JavaFX (FXML) com navegação funcional.  
- Validar **regras de perfil** (Admin, Gerente, Colaborador).  
- Adicionar relatórios/dashboards (gráficos de progresso e desempenho).  
- Implementar sistema de **login com autenticação real** no banco.  
- Criar logs de auditoria mais completos.  
- Escrever testes unitários e de integração.  
- Documentar manual de uso e execução.  

---

## 🚀 Próximos Passos

### Banco de Dados
- Finalizar modelo relacional no MySQL.  
- Criar chaves estrangeiras consistentes.  
- Popular com mais dados de teste.  

### Camada de Persistência (DAO)
- Ajustar e validar todas as classes DAO (CRUD completo).  
- Garantir conexões seguras com DBConnection.  

### Camada de Serviço
- Criar regras de negócio intermediárias entre DAO e Controller.  
- Validar regras de perfis (admin, gerente, colaborador).  

### Interface (JavaFX + FXML)
- Finalizar protótipos de telas.  
- Ajustar navegação entre telas.  
- Implementar validações visuais (campos obrigatórios, erros).  

### Relatórios e Dashboards
- Criar telas de resumo de projetos e tarefas.  
- Adicionar gráficos simples (progressos, status, riscos).  

### Testes
- Testes unitários dos DAOs e serviços.  
- Testes de integração (fluxos principais: login, cadastro, alocação).  

### Documentação
- Atualizar este README a cada nova etapa.  
- Criar manual simples de uso (instruções de execução).  

---

## 👥 Equipe e Perfis

- **Administrador:** acesso total ao sistema.  
- **Gerente de Projeto:** gerencia projetos, equipes e tarefas.  
- **Colaborador:** executa tarefas atribuídas.  

---

## 📌 Observações

- O projeto será evolutivo, podendo receber novos requisitos.  
- Este README servirá como guia central de organização e acompanhamento.