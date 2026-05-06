# Telecom Mediation System
A centralized mediation engine routing CSV CDRs from Upstream nodes to Downstream nodes.
# 📡 SMSC Node (SFTP) – Mediation System

## 📌 Overview

The SMSC (Short Message Service Center) node simulates a telecom network element that generates CDR (Call Detail Record) files and exposes them via **SFTP** for the mediation system to collect.

---

## ⚙️ Configuration

| Property         | Value                  | Description                         |
| ---------------- | ---------------------- | ----------------------------------- |
| Node Name        | SMSC                   | Short Message Service Center        |
| Protocol         | SFTP                   | Secure File Transfer Protocol       |
| Docker Image     | atmoz/sftp             | Lightweight SFTP server             |
| Container Name   | smsc-sftp              | Docker container name               |
| Host             | 127.0.0.1              | Localhost                           |
| Port             | 2222                   | Exposed SFTP port                   |
| Username         | smsc_node              | Login username                      |
| Password         | 12345                  | Login password                      |
| Remote Directory | /home/smsc_node/upload | CDR files location inside container |
| Local Directory  | ./smsc-node/upload     | Mounted folder on host              |


