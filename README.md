# Gestao de Projetos e Equipes

Sistema desktop para gerenciamento de projetos e equipes desenvolvido em Java com interface Swing.

## Stack

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-007396?style=flat&logo=java&logoColor=white)

## Funcionalidades

- Cadastro e gerenciamento de projetos
- Alocacao de membros em equipes
- Acompanhamento de status e prazos
- Persistencia em banco MySQL via JDBC

## Estrutura

`
src/br/com/gestaoprojetos/
  dao/         # Data Access Objects (JDBC)
  model/       # Entidades do dominio
  util/        # Conexao com banco de dados
  view/        # Interface grafica (Swing)
`

## Configuracao

1. Criar banco MySQL `gestao_projetos`
2. Configurar credenciais via variavel de ambiente ou editar `DBConnection.java`
3. Compilar e executar a classe principal

## Autor

**Leonardo Gorska** - [GitHub](https://github.com/leonardo-gorska) - [LinkedIn](https://linkedin.com/in/leonardo-gorska)
