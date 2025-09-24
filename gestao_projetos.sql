-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: gestao_projetos
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cargo`
--

DROP TABLE IF EXISTS `cargo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cargo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nome` (`nome`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cargo`
--

LOCK TABLES `cargo` WRITE;
/*!40000 ALTER TABLE `cargo` DISABLE KEYS */;
INSERT INTO `cargo` VALUES (1,'Administrador'),(3,'Analista'),(4,'Desenvolvedor'),(5,'Designer'),(6,'Estagiário'),(2,'Gerente');
/*!40000 ALTER TABLE `cargo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipe`
--

DROP TABLE IF EXISTS `equipe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipe` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `descricao` text,
  `gerente_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_equipe_gerente` (`gerente_id`),
  CONSTRAINT `fk_equipe_gerente` FOREIGN KEY (`gerente_id`) REFERENCES `usuario` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipe`
--

LOCK TABLES `equipe` WRITE;
/*!40000 ALTER TABLE `equipe` DISABLE KEYS */;
INSERT INTO `equipe` VALUES (4,'EquipeG','Equipe Gerente',10),(5,'EquipeGP','Equipe Gerente Projeto',2);
/*!40000 ALTER TABLE `equipe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipe_usuario`
--

DROP TABLE IF EXISTS `equipe_usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipe_usuario` (
  `equipe_id` int NOT NULL,
  `usuario_id` int NOT NULL,
  PRIMARY KEY (`equipe_id`,`usuario_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `equipe_usuario_ibfk_1` FOREIGN KEY (`equipe_id`) REFERENCES `equipe` (`id`) ON DELETE CASCADE,
  CONSTRAINT `equipe_usuario_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipe_usuario`
--

LOCK TABLES `equipe_usuario` WRITE;
/*!40000 ALTER TABLE `equipe_usuario` DISABLE KEYS */;
INSERT INTO `equipe_usuario` VALUES (5,2),(4,3),(5,3),(4,6),(4,7),(5,8),(5,9),(4,10);
/*!40000 ALTER TABLE `equipe_usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historico_tarefa`
--

DROP TABLE IF EXISTS `historico_tarefa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historico_tarefa` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tarefa_id` int NOT NULL,
  `usuario_id` int NOT NULL,
  `acao` varchar(150) NOT NULL,
  `detalhe` text,
  `data_hora` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tarefa` (`tarefa_id`),
  KEY `idx_usuario` (`usuario_id`),
  CONSTRAINT `fk_historico_tarefa_tarefa` FOREIGN KEY (`tarefa_id`) REFERENCES `tarefa` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_historico_tarefa_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historico_tarefa`
--

LOCK TABLES `historico_tarefa` WRITE;
/*!40000 ALTER TABLE `historico_tarefa` DISABLE KEYS */;
INSERT INTO `historico_tarefa` VALUES (1,1,1,'Criada','Título: Tarefa1; Status: Em andamento; Início: 2025-09-08; Término: 2025-10-03; Responsável: Colaborador Exemplo','2025-09-24 09:27:17'),(2,1,3,'Atualizada','Título: Tarefa12; Status: Em andamento; Início: 2025-09-08; Término: 2025-10-03; Responsável: Colaborador Exemplo','2025-09-24 11:00:40'),(3,2,1,'Criada','Título: Sem Colab; Status: Pendente; Início: 2025-09-08; Término: 2025-10-03; Responsável: Gerente de Projetos','2025-09-24 11:55:35'),(4,2,1,'Atualizada','Título: Sem Colab; Status: Em andamento; Início: 2025-09-08; Término: 2025-10-03; Responsável: Gerente de Projetos','2025-09-24 11:58:42'),(5,2,1,'Atualizada','Título: Sem Colab; Status: Concluída; Início: 2025-09-08; Término: 2025-10-03; Responsável: Gerente de Projetos','2025-09-24 11:59:12'),(6,1,3,'Atualizada','Título: Tarefa123; Status: Em andamento; Início: 2025-09-08; Término: 2025-10-03; Responsável: Colaborador Exemplo','2025-09-24 14:24:14'),(7,3,1,'Criada','Título: Tarefa 1 A G; Status: Pendente; Início: 2025-09-16; Término: 2025-09-26; Responsável: Usuario1','2025-09-24 23:14:08'),(8,4,1,'Criada','Título: Tarefa 2; Status: Pendente; Início: 2025-09-08; Término: 2025-10-03; Responsável: Usuario2','2025-09-24 23:14:39'),(9,4,1,'Atualizada','Título: Tarefa 2; Status: Em andamento; Início: 2025-09-08; Término: 2025-10-03; Responsável: Usuario2','2025-09-24 23:14:46'),(10,5,1,'Criada','Título: Tarefa 3 A G; Status: Concluída; Início: 2025-09-08; Término: 2025-10-02; Responsável: Colaborador Exemplo','2025-09-24 23:15:17'),(11,6,1,'Criada','Título: Tarefa 1 GP; Status: Pendente; Início: 2025-08-31; Término: 2025-09-27; Responsável: Usuario4','2025-09-24 23:16:08'),(12,7,1,'Criada','Título: Tarefa 2 GP; Status: Em andamento; Início: 2025-09-15; Término: 2025-09-26; Responsável: Usuario3','2025-09-24 23:16:29'),(13,8,1,'Criada','Título: Tarefa 3 GP; Status: Concluída; Início: 2025-09-08; Término: 2025-10-03; Responsável: Colaborador Exemplo','2025-09-24 23:16:57');
/*!40000 ALTER TABLE `historico_tarefa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logs_atividade`
--

DROP TABLE IF EXISTS `logs_atividade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logs_atividade` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `acao` varchar(255) NOT NULL,
  `entidade` varchar(50) NOT NULL,
  `data_hora` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `logs_atividade_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logs_atividade`
--

LOCK TABLES `logs_atividade` WRITE;
/*!40000 ALTER TABLE `logs_atividade` DISABLE KEYS */;
INSERT INTO `logs_atividade` VALUES (1,2,'Criou equipe: Equipe A','equipe','2025-09-24 08:07:46'),(2,3,'Criou equipe: EquipeB','equipe','2025-09-24 08:08:32'),(3,1,'Criou projeto: Projeto1','projeto','2025-09-24 09:24:41'),(4,1,'Criou projeto: Tarefa1','projeto','2025-09-24 09:26:45'),(5,1,'Criou tarefa: Tarefa1','tarefa','2025-09-24 09:27:17'),(6,1,'Criou tarefa: Tarefa1','tarefa','2025-09-24 09:27:17'),(7,3,'Atualizou tarefa: Tarefa12','tarefa','2025-09-24 11:00:40'),(8,3,'Atualizou tarefa: Tarefa12','tarefa','2025-09-24 11:00:40'),(9,2,'Criou projeto: aaa','projeto','2025-09-24 11:02:08'),(10,1,'Criou usuário: Leo','usuario','2025-09-24 11:06:44'),(11,1,'Atualizou projeto: Tarefa12','projeto','2025-09-24 11:08:10'),(12,1,'Criou equipe: Sem Colab','equipe','2025-09-24 11:53:59'),(13,1,'Criou projeto: Sem Colab','projeto','2025-09-24 11:54:24'),(14,1,'Criou tarefa: Sem Colab','tarefa','2025-09-24 11:55:35'),(15,1,'Criou tarefa: Sem Colab','tarefa','2025-09-24 11:55:35'),(16,1,'Atualizou tarefa: Sem Colab','tarefa','2025-09-24 11:58:42'),(17,1,'Atualizou tarefa: Sem Colab','tarefa','2025-09-24 11:58:42'),(18,1,'Atualizou tarefa: Sem Colab','tarefa','2025-09-24 11:59:12'),(19,1,'Atualizou tarefa: Sem Colab','tarefa','2025-09-24 11:59:12'),(20,1,'Criou usuário: Gerente2','usuario','2025-09-24 12:26:25'),(21,1,'Atualizou equipe: Equipe A','equipe','2025-09-24 13:27:37'),(22,2,'Criou projeto: Gerente','projeto','2025-09-24 13:29:16'),(23,2,'Criou projeto: Gerente2','projeto','2025-09-24 13:29:43'),(24,3,'Atualizou tarefa: Tarefa123','tarefa','2025-09-24 14:24:14'),(25,3,'Atualizou tarefa: Tarefa123','tarefa','2025-09-24 14:24:14'),(26,1,'Excluiu usuário: Gerente2','usuario','2025-09-24 23:01:56'),(27,1,'Excluiu usuário: Leo','usuario','2025-09-24 23:01:59'),(28,1,'Criou usuário: Usuario1','usuario','2025-09-24 23:02:42'),(29,1,'Criou usuário: Usuario2','usuario','2025-09-24 23:03:30'),(30,1,'Criou usuário: Usuario3','usuario','2025-09-24 23:04:23'),(31,1,'Criou usuário: Usuario4','usuario','2025-09-24 23:04:48'),(32,1,'Criou usuário: UsuarioG','usuario','2025-09-24 23:05:25'),(33,1,'Excluiu equipe: Equipe A','equipe','2025-09-24 23:05:34'),(34,1,'Excluiu equipe: EquipeB','equipe','2025-09-24 23:05:37'),(35,1,'Excluiu equipe: Sem Colab','equipe','2025-09-24 23:05:39'),(36,1,'Criou equipe: EquipeG','equipe','2025-09-24 23:06:40'),(37,1,'Criou equipe: EquipeGP','equipe','2025-09-24 23:07:18'),(38,1,'Excluiu projeto: Gerente2','projeto','2025-09-24 23:07:32'),(39,1,'Excluiu projeto: Sem Colab','projeto','2025-09-24 23:07:32'),(40,1,'Excluiu projeto: Projeto1','projeto','2025-09-24 23:07:34'),(41,1,'Excluiu projeto: Tarefa12','projeto','2025-09-24 23:07:35'),(42,1,'Excluiu projeto: aaa','projeto','2025-09-24 23:07:35'),(43,1,'Excluiu projeto: Gerente','projeto','2025-09-24 23:07:36'),(44,1,'Criou projeto: Projeto1 G','projeto','2025-09-24 23:08:26'),(45,1,'Criou projeto: Projeto2 G','projeto','2025-09-24 23:09:03'),(46,1,'Criou projeto: Projeto3 G','projeto','2025-09-24 23:10:44'),(47,1,'Atualizou projeto: Projeto3 G','projeto','2025-09-24 23:11:00'),(48,1,'Criou projeto: Projeto1 GP','projeto','2025-09-24 23:11:46'),(49,1,'Criou projeto: Projeto2 GP','projeto','2025-09-24 23:12:23'),(50,1,'Criou projeto: Projeto GGP','projeto','2025-09-24 23:13:03'),(51,1,'Criou tarefa: Tarefa 1 A G','tarefa','2025-09-24 23:14:08'),(52,1,'Criou tarefa: Tarefa 1 A G','tarefa','2025-09-24 23:14:08'),(53,1,'Criou tarefa: Tarefa 2','tarefa','2025-09-24 23:14:39'),(54,1,'Criou tarefa: Tarefa 2','tarefa','2025-09-24 23:14:39'),(55,1,'Atualizou tarefa: Tarefa 2','tarefa','2025-09-24 23:14:46'),(56,1,'Atualizou tarefa: Tarefa 2','tarefa','2025-09-24 23:14:46'),(57,1,'Criou tarefa: Tarefa 3 A G','tarefa','2025-09-24 23:15:17'),(58,1,'Criou tarefa: Tarefa 3 A G','tarefa','2025-09-24 23:15:17'),(59,1,'Criou tarefa: Tarefa 1 GP','tarefa','2025-09-24 23:16:08'),(60,1,'Criou tarefa: Tarefa 1 GP','tarefa','2025-09-24 23:16:08'),(61,1,'Criou tarefa: Tarefa 2 GP','tarefa','2025-09-24 23:16:29'),(62,1,'Criou tarefa: Tarefa 2 GP','tarefa','2025-09-24 23:16:29'),(63,1,'Criou tarefa: Tarefa 3 GP','tarefa','2025-09-24 23:16:58'),(64,1,'Criou tarefa: Tarefa 3 GP','tarefa','2025-09-24 23:16:58');
/*!40000 ALTER TABLE `logs_atividade` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `perfil`
--

DROP TABLE IF EXISTS `perfil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `perfil` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descricao` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `perfil`
--

LOCK TABLES `perfil` WRITE;
/*!40000 ALTER TABLE `perfil` DISABLE KEYS */;
INSERT INTO `perfil` VALUES (1,'Administrador'),(2,'Gerente'),(3,'Colaborador');
/*!40000 ALTER TABLE `perfil` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projeto`
--

DROP TABLE IF EXISTS `projeto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projeto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `descricao` text,
  `data_inicio` date NOT NULL,
  `data_termino_prevista` date NOT NULL,
  `data_termino_real` date DEFAULT NULL,
  `status` enum('planejado','em andamento','concluido','cancelado') NOT NULL,
  `gerente_id` int NOT NULL,
  `equipe_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `gerente_id` (`gerente_id`),
  KEY `equipe_id` (`equipe_id`),
  CONSTRAINT `projeto_ibfk_1` FOREIGN KEY (`gerente_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE,
  CONSTRAINT `projeto_ibfk_2` FOREIGN KEY (`equipe_id`) REFERENCES `equipe` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projeto`
--

LOCK TABLES `projeto` WRITE;
/*!40000 ALTER TABLE `projeto` DISABLE KEYS */;
INSERT INTO `projeto` VALUES (7,'Projeto1 G','Projeto 1 Gerente','2025-09-01','2025-09-27',NULL,'planejado',10,NULL),(8,'Projeto2 G','Projeto 2 Gerente','2025-09-01','2025-10-11',NULL,'em andamento',10,NULL),(9,'Projeto3 G','Projeto 3 Gerente','2025-09-01','2025-10-29',NULL,'concluido',10,NULL),(10,'Projeto1 GP','Projeto 1 Gerente Projeto','2025-09-02','2025-10-03',NULL,'planejado',2,NULL),(11,'Projeto2 GP','Projeto 2 Gerente Projeto','2025-09-09','2025-10-03',NULL,'em andamento',2,NULL),(12,'Projeto GGP','Projeto Gerente e Gerente Projetos','2025-09-16','2025-10-10',NULL,'em andamento',2,NULL);
/*!40000 ALTER TABLE `projeto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projeto_equipe`
--

DROP TABLE IF EXISTS `projeto_equipe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projeto_equipe` (
  `projeto_id` int NOT NULL,
  `equipe_id` int NOT NULL,
  PRIMARY KEY (`projeto_id`,`equipe_id`),
  KEY `equipe_id` (`equipe_id`),
  CONSTRAINT `projeto_equipe_ibfk_1` FOREIGN KEY (`projeto_id`) REFERENCES `projeto` (`id`) ON DELETE CASCADE,
  CONSTRAINT `projeto_equipe_ibfk_2` FOREIGN KEY (`equipe_id`) REFERENCES `equipe` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projeto_equipe`
--

LOCK TABLES `projeto_equipe` WRITE;
/*!40000 ALTER TABLE `projeto_equipe` DISABLE KEYS */;
INSERT INTO `projeto_equipe` VALUES (7,4),(8,4),(9,4),(12,4),(10,5),(11,5),(12,5);
/*!40000 ALTER TABLE `projeto_equipe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tarefa`
--

DROP TABLE IF EXISTS `tarefa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tarefa` (
  `id` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(255) NOT NULL,
  `descricao` text,
  `status` varchar(50) NOT NULL DEFAULT 'Pendente',
  `data_inicio_prevista` date DEFAULT NULL,
  `data_termino_prevista` date DEFAULT NULL,
  `responsavel_id` int DEFAULT NULL,
  `projeto_id` int DEFAULT NULL,
  `data_inicio` date DEFAULT NULL,
  `data_termino` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_responsavel` (`responsavel_id`),
  KEY `idx_projeto` (`projeto_id`),
  CONSTRAINT `fk_tarefa_projeto` FOREIGN KEY (`projeto_id`) REFERENCES `projeto` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_tarefa_responsavel` FOREIGN KEY (`responsavel_id`) REFERENCES `usuario` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tarefa`
--

LOCK TABLES `tarefa` WRITE;
/*!40000 ALTER TABLE `tarefa` DISABLE KEYS */;
INSERT INTO `tarefa` VALUES (1,'Tarefa123','Tarefa1','Em andamento',NULL,NULL,3,NULL,'2025-09-08','2025-10-03'),(2,'Sem Colab','Test','Concluída',NULL,NULL,2,NULL,'2025-09-08','2025-10-03'),(3,'Tarefa 1 A G','Tarefa 1','Pendente',NULL,NULL,6,7,'2025-09-16','2025-09-26'),(4,'Tarefa 2','Tarefa 2','Em andamento',NULL,NULL,7,7,'2025-09-08','2025-10-03'),(5,'Tarefa 3 A G','Tarefa 3','Concluída',NULL,NULL,3,7,'2025-09-08','2025-10-02'),(6,'Tarefa 1 GP','Tarefa 1','Pendente',NULL,NULL,9,10,'2025-08-31','2025-09-27'),(7,'Tarefa 2 GP','Tarefa 2','Em andamento',NULL,NULL,8,10,'2025-09-15','2025-09-26'),(8,'Tarefa 3 GP','Tarefa 3','Concluída',NULL,NULL,3,10,'2025-09-08','2025-10-03');
/*!40000 ALTER TABLE `tarefa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `login` varchar(50) NOT NULL,
  `senha` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `cpf` varchar(14) DEFAULT NULL,
  `cargo_id` int DEFAULT NULL,
  `perfil_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`),
  UNIQUE KEY `cpf` (`cpf`),
  KEY `fk_usuario_cargo` (`cargo_id`),
  KEY `fk_usuario_perfil` (`perfil_id`),
  CONSTRAINT `fk_usuario_cargo` FOREIGN KEY (`cargo_id`) REFERENCES `cargo` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_usuario_perfil` FOREIGN KEY (`perfil_id`) REFERENCES `perfil` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Administrador do Sistema','admin','1','admin@empresa.com','00000000000',1,1),(2,'Gerente de Projetos','gerente','2','gerente@empresa.com','11111111111',2,2),(3,'Colaborador Exemplo','colab','3','colab@empresa.com','22222222222',3,3),(6,'Usuario1','Usuario1','1','Usuario1@email.com','00000000001',6,3),(7,'Usuario2','Usuaruio2','1','Usuario2@email.com','00000000002',5,3),(8,'Usuario3','Usuario3','1','Usuario3@email.com','00000000003',4,3),(9,'Usuario4','Usuario4','1','Usuario4@email.com','00000000004',3,3),(10,'UsuarioG','UsuarioG','1','UsuarioG@email.com','00000000005',2,2);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-24 20:20:44
