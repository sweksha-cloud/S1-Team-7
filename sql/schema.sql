-- MySQL dump 10.13  Distrib 8.0.45, for macos15 (arm64)
--
-- Host: 127.0.0.1    Database: team7
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `Bookings`
--

DROP TABLE IF EXISTS `Bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Bookings` (
  `Booking_ID` int NOT NULL,
  `Ride_ID` int NOT NULL,
  `Booking_Timestamp` datetime DEFAULT NULL,
  `Status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Booking_ID`),
  KEY `fk_bookings_ride` (`Ride_ID`),
  CONSTRAINT `fk_bookings_ride` FOREIGN KEY (`Ride_ID`) REFERENCES `Rides` (`Ride_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Bookings`
--

LOCK TABLES `Bookings` WRITE;
/*!40000 ALTER TABLE `Bookings` DISABLE KEYS */;
INSERT INTO `Bookings` VALUES (1,1,'2026-03-17 07:05:24','confirmed'),(2,2,'2026-03-17 07:05:24','pending'),(3,3,'2026-03-17 07:05:24','confirmed'),(4,4,'2026-03-17 07:05:24','cancelled'),(5,5,'2026-03-17 07:05:24','confirmed'),(6,6,'2026-03-17 07:05:24','pending'),(7,7,'2026-03-17 07:05:24','confirmed'),(8,8,'2026-03-17 07:05:24','confirmed'),(9,9,'2026-03-17 07:05:24','pending'),(10,10,'2026-03-17 07:05:24','confirmed'),(11,12,'2026-05-04 00:27:18','cancelled'),(12,11,'2026-05-04 01:26:28','declined'),(13,11,'2026-05-04 01:26:45','cancelled'),(14,11,'2026-05-04 01:43:51','cancelled'),(15,14,'2026-05-04 19:59:09','accepted');
/*!40000 ALTER TABLE `Bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Drivers`
--

DROP TABLE IF EXISTS `Drivers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Drivers` (
  `User_ID` int NOT NULL,
  `License_Number` varchar(50) DEFAULT NULL,
  `Verification_Status` varchar(20) DEFAULT NULL,
  `Driver_Rating` decimal(2,1) DEFAULT NULL,
  PRIMARY KEY (`User_ID`),
  CONSTRAINT `drivers_ibfk_1` FOREIGN KEY (`User_ID`) REFERENCES `Users` (`User_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Drivers`
--

LOCK TABLES `Drivers` WRITE;
/*!40000 ALTER TABLE `Drivers` DISABLE KEYS */;
INSERT INTO `Drivers` VALUES (1,'LIC6','pending',4.0),(2,'LIC1','verified',4.8),(3,'LIC7','verified',4.3),(4,'LIC2','verified',4.5),(5,'LIC8','verified',4.6),(6,'LIC3','pending',4.2),(7,'LIC9','pending',4.1),(8,'LIC4','verified',4.9),(9,'LIC10','verified',4.4),(10,'LIC5','verified',4.7),(13,'D1234567','pending',0.0),(14,'D1234567','pending',0.0),(15,'D1234567','pending',0.0),(16,'231waewewqwqewqeq','pending',0.0),(17,'231waewewqwqewqeq','pending',0.0),(18,'231waewewqwqewqeq','pending',0.0),(19,'231waewewqwqewqeq','pending',0.0),(21,'dawdwadwadad','pending',0.0),(22,'dawdwadwadwadwada','pending',0.0),(24,'D1234567','pending',0.0),(25,'D1231231','pending',0.0),(26,'D69420','pending',0.0);
/*!40000 ALTER TABLE `Drivers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Logs`
--

DROP TABLE IF EXISTS `Logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Logs` (
  `Log_ID` int NOT NULL,
  `Event_Type` varchar(50) DEFAULT NULL,
  `Event_Timestamp` datetime DEFAULT NULL,
  `Details` text,
  PRIMARY KEY (`Log_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Logs`
--

LOCK TABLES `Logs` WRITE;
/*!40000 ALTER TABLE `Logs` DISABLE KEYS */;
INSERT INTO `Logs` VALUES (1,'login','2026-03-17 07:05:24','ok'),(2,'logout','2026-03-17 07:05:24','ok'),(3,'book','2026-03-17 07:05:24','ok'),(4,'cancel','2026-03-17 07:05:24','ok'),(5,'update','2026-03-17 07:05:24','ok'),(6,'login','2026-03-17 07:05:24','ok'),(7,'logout','2026-03-17 07:05:24','ok'),(8,'book','2026-03-17 07:05:24','ok'),(9,'cancel','2026-03-17 07:05:24','ok'),(10,'update','2026-03-17 07:05:24','ok');
/*!40000 ALTER TABLE `Logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Notifications`
--

DROP TABLE IF EXISTS `Notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Notifications` (
  `Notification_ID` int NOT NULL,
  `Content` text,
  `Timestamp` datetime DEFAULT NULL,
  `Read_Status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Notification_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Notifications`
--

LOCK TABLES `Notifications` WRITE;
/*!40000 ALTER TABLE `Notifications` DISABLE KEYS */;
INSERT INTO `Notifications` VALUES (1,'Msg1','2026-03-17 07:05:24','unread'),(2,'Msg2','2026-03-17 07:05:24','read'),(3,'Msg3','2026-03-17 07:05:24','unread'),(4,'Msg4','2026-03-17 07:05:24','read'),(5,'Msg5','2026-03-17 07:05:24','unread'),(6,'Msg6','2026-03-17 07:05:24','read'),(7,'Msg7','2026-03-17 07:05:24','unread'),(8,'Msg8','2026-03-17 07:05:24','read'),(9,'Msg9','2026-03-17 07:05:24','unread'),(10,'Msg10','2026-03-17 07:05:24','read');
/*!40000 ALTER TABLE `Notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Passengers`
--

DROP TABLE IF EXISTS `Passengers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Passengers` (
  `User_ID` int NOT NULL,
  `Total_Rides_Taken` int DEFAULT NULL,
  PRIMARY KEY (`User_ID`),
  CONSTRAINT `passengers_ibfk_1` FOREIGN KEY (`User_ID`) REFERENCES `Users` (`User_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Passengers`
--

LOCK TABLES `Passengers` WRITE;
/*!40000 ALTER TABLE `Passengers` DISABLE KEYS */;
INSERT INTO `Passengers` VALUES (1,5),(2,0),(3,2),(4,0),(5,7),(6,0),(7,1),(8,0),(9,4),(10,0),(11,0),(12,0),(13,0),(14,0),(15,0),(16,0),(17,0),(18,0),(19,0),(20,0),(21,0),(22,0),(23,0),(25,0),(26,0),(27,0);
/*!40000 ALTER TABLE `Passengers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Reviews`
--

DROP TABLE IF EXISTS `Reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Reviews` (
  `Review_ID` int NOT NULL,
  `Rating_Stars` int DEFAULT NULL,
  `Comments` text,
  `Review_Date` datetime DEFAULT NULL,
  PRIMARY KEY (`Review_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Reviews`
--

LOCK TABLES `Reviews` WRITE;
/*!40000 ALTER TABLE `Reviews` DISABLE KEYS */;
INSERT INTO `Reviews` VALUES (1,5,'Great','2026-01-01 00:00:00'),(2,4,'Good','2026-01-02 00:00:00'),(3,3,'Okay','2026-01-03 00:00:00'),(4,2,'Bad','2026-01-04 00:00:00'),(5,1,'Terrible','2026-01-05 00:00:00'),(6,5,'Nice','2026-01-06 00:00:00'),(7,4,'Cool','2026-01-07 00:00:00'),(8,3,'Meh','2026-01-08 00:00:00'),(9,5,'Excellent','2026-01-09 00:00:00'),(10,4,'Solid','2026-01-10 00:00:00');
/*!40000 ALTER TABLE `Reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Rides`
--

DROP TABLE IF EXISTS `Rides`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Rides` (
  `Ride_ID` int NOT NULL,
  `Origin` varchar(100) DEFAULT NULL,
  `Destination` varchar(100) DEFAULT NULL,
  `Departure_Date` datetime DEFAULT NULL,
  `Seats_Left` int DEFAULT NULL,
  `Status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Ride_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Rides`
--

LOCK TABLES `Rides` WRITE;
/*!40000 ALTER TABLE `Rides` DISABLE KEYS */;
INSERT INTO `Rides` VALUES (1,'SJ','SF','2026-01-01 10:00:00',3,'completed'),(2,'SJ','LA','2026-01-02 12:00:00',2,'completed'),(3,'SF','SJ','2026-01-03 09:00:00',4,'completed'),(4,'LA','SJ','2026-01-04 08:00:00',1,'completed'),(5,'SJ','SD','2026-01-05 07:00:00',3,'completed'),(6,'SD','SJ','2026-01-06 06:00:00',2,'completed'),(7,'SJ','NY','2026-01-07 05:00:00',5,'completed'),(8,'NY','SJ','2026-01-08 04:00:00',4,'completed'),(9,'SJ','LV','2026-01-09 03:00:00',2,'completed'),(10,'LV','SJ','2026-05-10 06:00:00',5,'open'),(11,'King','Diridon','2026-05-04 03:09:00',5,'completed'),(12,'King','Diridon','2026-05-04 13:28:00',5,'completed'),(13,'King','Diri','2026-05-04 19:58:00',3,'completed'),(14,'King','Diridonn','2026-05-04 20:00:00',2,'completed');
/*!40000 ALTER TABLE `Rides` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Saved_Routes`
--

DROP TABLE IF EXISTS `Saved_Routes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Saved_Routes` (
  `Route_ID` int NOT NULL,
  `Start_Location` varchar(100) DEFAULT NULL,
  `End_Location` varchar(100) DEFAULT NULL,
  `Frequency` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`Route_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Saved_Routes`
--

LOCK TABLES `Saved_Routes` WRITE;
/*!40000 ALTER TABLE `Saved_Routes` DISABLE KEYS */;
INSERT INTO `Saved_Routes` VALUES (1,'SJ','SF','daily'),(2,'SJ','LA','weekly'),(3,'SF','SJ','daily'),(4,'LA','SJ','weekly'),(5,'SJ','SD','monthly'),(6,'SD','SJ','monthly'),(7,'SJ','NY','yearly'),(8,'NY','SJ','yearly'),(9,'SJ','LV','daily'),(10,'LV','SJ','daily');
/*!40000 ALTER TABLE `Saved_Routes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Users` (
  `User_ID` int NOT NULL AUTO_INCREMENT,
  `SJSU_ID` varchar(20) DEFAULT NULL,
  `First_Name` varchar(50) DEFAULT NULL,
  `Last_Name` varchar(50) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `Gender` varchar(10) DEFAULT NULL,
  `Password_Hash` varchar(255) DEFAULT NULL,
  `Account_Status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`User_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
INSERT INTO `Users` VALUES (1,'S001','John','Doe','john@email.com','M','h1','active'),(2,'S002','Jane','Smith','jane@email.com','F','h2','active'),(3,'S003','Mike','Lee','mike@email.com','M','h3','active'),(4,'S004','Sara','Kim','sara@email.com','F','h4','active'),(5,'S005','Alex','Ng','alex@email.com','M','h5','active'),(6,'S006','Lily','Chen','lily@email.com','F','h6','active'),(7,'S007','Tom','Brown','tom@email.com','M','h7','active'),(8,'S008','Emma','Davis','emma@email.com','F','h8','active'),(9,'S009','Ryan','Park','ryan@email.com','M','h9','active'),(10,'S010','Nina','Lopez','nina@email.com','F','h10','active'),(23,'312331231','Passenger','Passenger','passenger@sjsu.edu','male','Passenger','active'),(24,'312312312','Driver','Driver','driver@sjsu.edu','non-binary','Passenger','active'),(25,'312312312','DriverAndPassenger','DriverAndPassenger','driverandpassenger@sjsu.edu','female','DriverAndPassenger','active'),(26,'019007729','Brian','Ou','brian.ou@sjsu.edu','male','$2a$12$BIPnUOZW.EfycrNVRMeOfe8f7z.29FHST8rUpcC/QrpO9l.VE.Nva','active'),(27,'829103892','hashed','test','hashtest@sjsu.edu','female','$2a$12$321TIGo4o3zCyaDSasarH.aJ8zzuifDD/1g9KH7qLOHdUujUCMV/G','active');
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Vehicles`
--

DROP TABLE IF EXISTS `Vehicles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Vehicles` (
  `Vehicle_ID` int NOT NULL AUTO_INCREMENT,
  `License_Plate` varchar(20) DEFAULT NULL,
  `Make` varchar(50) DEFAULT NULL,
  `Model` varchar(50) DEFAULT NULL,
  `Color` varchar(20) DEFAULT NULL,
  `Total_Seats` int DEFAULT NULL,
  `Insurance_Num` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`Vehicle_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Vehicles`
--

LOCK TABLES `Vehicles` WRITE;
/*!40000 ALTER TABLE `Vehicles` DISABLE KEYS */;
INSERT INTO `Vehicles` VALUES (1,'A1','Toyota','Camry','White',5,'I1'),(2,'A2','Honda','Civic','Black',5,'I2'),(3,'A3','Tesla','M3','Red',5,'I3'),(4,'A4','Ford','Focus','Blue',5,'I4'),(5,'A5','BMW','X5','Gray',5,'I5'),(6,'A6','Audi','A4','White',5,'I6'),(7,'A7','Nissan','Altima','Black',5,'I7'),(8,'A8','Hyundai','Elantra','Silver',5,'I8'),(9,'A9','Kia','Soul','Green',5,'I9'),(10,'A10','Chevy','Malibu','Blue',5,'I10'),(11,'D1234567','Hyundai','IONIQ 6','Grey',5,NULL),(13,'6HAY8293','Hyundai','IONIQ 6','Transmission Blue',5,'');
/*!40000 ALTER TABLE `Vehicles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-04 21:02:38
