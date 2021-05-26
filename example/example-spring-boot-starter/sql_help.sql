-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 192.168.1.184    Database: sql_help
-- ------------------------------------------------------
-- Server version	5.6.47

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `clbum`
--

DROP TABLE IF EXISTS `clbum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clbum` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `school_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='班级';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clbum`
--

LOCK TABLES `clbum` WRITE;
/*!40000 ALTER TABLE `clbum` DISABLE KEYS */;
INSERT INTO `clbum` VALUES (1,'一年级1班',1),(2,'一年级2班',1),(3,'二年级1班',2),(4,'二年级2班',2);
/*!40000 ALTER TABLE `clbum` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `school`
--

DROP TABLE IF EXISTS `school`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `school` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `school`
--

LOCK TABLES `school` WRITE;
/*!40000 ALTER TABLE `school` DISABLE KEYS */;
INSERT INTO `school` VALUES (1,'第一大学'),(2,'第二大学');
/*!40000 ALTER TABLE `school` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `student` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `school_id` int(11) DEFAULT NULL,
  `clbum_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (1,'小明',2,3),(2,'小红',2,3),(3,'小E',2,4),(4,'jj',1,1),(5,'aa',1,1),(6,'bb',1,1);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `aa` datetime DEFAULT NULL,
  `dd` text COLLATE utf8_bin,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=150 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test`
--

LOCK TABLES `test` WRITE;
/*!40000 ALTER TABLE `test` DISABLE KEYS */;
INSERT INTO `test` VALUES (1,'aaaa',NULL,NULL),(2,'aaaa',NULL,NULL),(3,'aaaa',NULL,NULL),(6,'aaaa',NULL,NULL),(7,'aaaa',NULL,NULL),(8,'aaaa',NULL,NULL),(9,'aaaa',NULL,NULL),(10,'aaaa',NULL,NULL),(13,'aaaa',NULL,NULL),(14,'aaaa',NULL,NULL),(15,'aaaa',NULL,NULL),(16,'aaaa',NULL,NULL),(17,'aaaa',NULL,NULL),(18,'aaaa',NULL,NULL),(19,'aaaa',NULL,NULL),(20,'aaaa',NULL,NULL),(21,'aaaa',NULL,NULL),(22,'aaaa',NULL,NULL),(23,'aaaa',NULL,NULL),(24,'aaaa',NULL,NULL),(25,'aaaa',NULL,NULL),(26,'aaaa',NULL,NULL),(27,'aaaa',NULL,NULL),(28,'aaaa',NULL,NULL),(29,'aaaa',NULL,NULL),(30,'aaaa',NULL,NULL),(31,'aaaa',NULL,NULL),(32,'aaaa',NULL,NULL),(33,'aaaa',NULL,NULL),(34,'aaaa',NULL,NULL),(35,'aaaa',NULL,NULL),(36,'aaaa',NULL,NULL),(37,'aaaa',NULL,NULL),(39,'aaaa',NULL,NULL),(40,'aaaa',NULL,NULL),(41,'aaaa',NULL,NULL),(42,'aaaa',NULL,NULL),(43,'aaaa',NULL,NULL),(44,'aaaa',NULL,NULL),(45,'aaaa',NULL,NULL),(46,'aaaa',NULL,NULL),(47,'aaaa',NULL,NULL),(48,'aaaa',NULL,NULL),(49,'aaaa',NULL,NULL),(50,'aaaa',NULL,NULL),(51,'aaaa',NULL,NULL),(52,'aaaa',NULL,NULL),(53,'aaaa',NULL,NULL),(54,'aaaa',NULL,NULL),(55,'aaaa',NULL,NULL),(56,'aaaa',NULL,NULL),(57,'aaaa',NULL,NULL),(58,'aaaa',NULL,NULL),(59,'aaaa',NULL,NULL),(60,'aaaa',NULL,NULL),(61,'aaaa',NULL,NULL),(62,'aaaa',NULL,NULL),(63,'aaaa',NULL,NULL),(64,'aaaa',NULL,NULL),(65,'aaaa',NULL,NULL),(66,'aaaa',NULL,NULL),(67,'aaaa',NULL,NULL),(68,'aaaa',NULL,NULL),(69,'aaaa',NULL,NULL),(70,'aaaa',NULL,NULL),(71,'aaaa',NULL,NULL),(72,'aaaa',NULL,NULL),(73,'aaaa',NULL,NULL),(74,'aaaa',NULL,NULL),(75,'aaaa',NULL,NULL),(76,'aaaa',NULL,NULL),(77,'aaaa',NULL,NULL),(78,'aaaa',NULL,NULL),(79,'aaaa',NULL,NULL),(80,'aaaa',NULL,NULL),(81,'aaaa',NULL,NULL),(82,'aaaa',NULL,NULL),(83,'aaaa',NULL,NULL),(84,'aaaa',NULL,NULL),(85,'aaaa',NULL,NULL),(86,'aaaa',NULL,NULL),(87,'aaaa',NULL,NULL),(88,'aaaa',NULL,NULL),(89,'aaaa',NULL,NULL),(90,'aaaa',NULL,NULL),(91,'aaaa',NULL,NULL),(92,'aaaa',NULL,NULL),(93,'aaaa',NULL,NULL),(94,'aaaa',NULL,NULL),(95,'aaaa',NULL,NULL),(96,'aaaa',NULL,NULL),(97,'aaaa',NULL,NULL),(98,'aaaa',NULL,NULL),(99,'aaaa',NULL,NULL),(100,'aaaa',NULL,NULL),(101,'aaaa',NULL,NULL),(102,'aaaa',NULL,NULL),(103,'aaaa',NULL,NULL),(104,'aaaa',NULL,NULL),(105,'aaaa',NULL,NULL),(106,'aaaa',NULL,NULL),(107,'aaaa',NULL,NULL),(108,'aaaa',NULL,NULL),(109,'aaaa',NULL,NULL),(110,'aaaa',NULL,NULL),(111,'aaaa',NULL,NULL),(112,'aaaa',NULL,NULL),(113,'aaaa',NULL,NULL),(114,'aaaa',NULL,NULL),(115,'aaaa',NULL,NULL),(116,'aaaa',NULL,NULL),(117,'aaaa',NULL,NULL),(118,'aaaa',NULL,NULL),(119,'aaaa',NULL,NULL),(120,'aaaa',NULL,NULL),(121,'aaaa',NULL,NULL),(122,'aaaa',NULL,NULL),(123,'aaaa',NULL,NULL),(124,'aaaa',NULL,NULL),(125,'aaaa',NULL,NULL),(126,'aaaa',NULL,NULL),(127,'aaaa',NULL,NULL),(128,'aaaa',NULL,NULL),(129,'aaaa',NULL,NULL),(130,'aaaa',NULL,NULL),(131,'aaaa',NULL,NULL),(132,'aaaa',NULL,NULL),(133,'aaaa',NULL,NULL),(134,'aaaa',NULL,NULL),(135,'aaaa',NULL,NULL),(136,'aaaa',NULL,NULL),(137,'aaaa',NULL,NULL),(138,'aaaa',NULL,NULL),(139,'aaaa',NULL,NULL),(140,'aaaa',NULL,NULL),(141,'aaaa',NULL,NULL),(142,'aaaa',NULL,NULL),(143,'aaaa',NULL,NULL),(144,'aaaa',NULL,NULL),(145,'aaaa',NULL,NULL),(146,'aaaa',NULL,NULL),(147,'aaaa',NULL,NULL),(148,'aaaa',NULL,NULL),(149,'aaaa',NULL,NULL);
/*!40000 ALTER TABLE `test` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test2`
--

DROP TABLE IF EXISTS `test2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test2` (
  `id` varchar(225) COLLATE utf8_bin NOT NULL,
  `name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `aa` datetime DEFAULT NULL,
  `dd` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test2`
--

LOCK TABLES `test2` WRITE;
/*!40000 ALTER TABLE `test2` DISABLE KEYS */;
INSERT INTO `test2` VALUES ('34789263484784640','aaaa',NULL,NULL),('34789295558627328','aaaa',NULL,NULL),('34856534940979200','aaaa',NULL,NULL),('35147724647174144','aaaa',NULL,NULL),('35147867534528512','aaaa',NULL,NULL),('35268091109838848','aaaa',NULL,NULL),('35502732425170944','aaaa',NULL,NULL),('35502758115282944','aaaa',NULL,NULL),('35503085543624704','aaaa',NULL,NULL),('35526467471413248','aaaa',NULL,NULL),('35556450063486976','aaaa',NULL,NULL),('35559782471569408','aaaa',NULL,NULL),('35559859915198464','aaaa',NULL,NULL),('35560501043924992','aaaa',NULL,NULL),('35560767591944192','aaaa',NULL,NULL),('35561340311572480','aaaa',NULL,NULL),('35563105199198208','aaaa',NULL,NULL),('35563699683069952','aaaa',NULL,NULL),('35611338621456384','aaaa',NULL,NULL),('35612176601452544','aaaa',NULL,NULL),('35612320294113280','aaaa',NULL,NULL),('35871170033225728','aaaa',NULL,NULL),('35920857549377536','aaaa',NULL,NULL),('402809817857ebcd017857ebcd480000','aaaa',NULL,NULL),('402809817857ebfa017857ebfa5d0000','aaaa',NULL,NULL),('40992745535442944','aaaa',NULL,NULL),('41017298412244992','aaaa',NULL,NULL);
/*!40000 ALTER TABLE `test2` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-05-26 12:05:49
