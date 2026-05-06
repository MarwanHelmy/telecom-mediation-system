# 📡 Telecom Mediation System

A centralized mediation engine routing CSV/CDR files from **Upstream (SMSC)** nodes to **Downstream (Billing)** nodes using Docker and SFTP.

# 📥 SMSC Node - SFTP CDR Ingestion Service (UPSTREAM)

## 📌 Overview
This project implements a lightweight SMSC-like node using Docker and SFTP (SSH). It receives CDR (Call Detail Record) files, stores them, and prepares them for downstream processing in telecom mediation systems.

## ⚙️ Configuration

| Item              | Value              | Description |
|------------------|-------------------|-------------|
| Service Name     | smsc-node         | Docker SMSC SFTP Node |
| Protocol         | SFTP (SSH)        | Secure File Transfer Protocol |
| Port             | 2222              | Exposed SSH/SFTP port |
| Username         | smsc              | Login user for SFTP |
| Password         | smsc123           | Default password (dev only) |
| Incoming Folder  | /home/smsc/cdr    | Folder for uploaded CDR files |
| Archive Folder   | /home/smsc/archive| Folder for processed files |
| Base Image       | Ubuntu 22.04      | Docker base OS |
| Container Name   | smsc-node         | Docker container name |

## 🚀 Run SMSC Node

docker build -t smsc-node .

docker run -d --name smsc-node -p 2222:22 -v $(pwd)/data:/home/smsc smsc-node

docker ps

## 🔌 Connect SMSC

sftp -P 2222 smsc@localhost

Username: smsc  
Password: smsc123

## 📂 SMSC Structure

/home/smsc
├── cdr
└── archive

## 🧠 SMSC Flow

External System → SFTP → SMSC Node → /cdr → Processing → /archive → Billing Node

---

# 💳 Billing Node - SFTP CDR Processing Service (DOWNSTREAM)

## 📌 Overview
Billing Node is responsible for receiving CDR files from SMSC and simulating charging / rating logic in telecom systems.

## ⚙️ Configuration

| Item              | Value              | Description |
|------------------|-------------------|-------------|
| Service Name     | billing-node      | Docker Billing SFTP Node |
| Protocol         | SFTP (SSH)        | Secure File Transfer Protocol |
| Port             | 2223              | Exposed SSH/SFTP port |
| Username         | billing           | Login user |
| Password         | bill123           | Default password (dev only) |
| Incoming Folder  | /home/billing/incoming  | Received CDR files |
| Processed Folder | /home/billing/processed | After processing |
| Base Image       | Ubuntu 22.04      | Docker base OS |
| Container Name   | billing-node      | Docker container name |

## 🚀 Run Billing Node

docker build -t billing-node .

docker run -d --name billing-node -p 2223:22 -v $(pwd)/data:/home/billing billing-node

docker ps

## 🔌 Connect Billing Node

sftp -P 2223 billing@localhost

Username: billing  
Password: bill123

## 📂 Billing Structure

/home/billing
├── incoming
└── processed

## 🧠 Billing Flow

SMSC Node → SFTP → Billing Node → incoming → processing → processed

---

# 🔥 System Architecture

External System  
→ SFTP  
→ SMSC Node (UPSTREAM)  
→ CDR Processing Layer  
→ Billing Node (DOWNSTREAM)  
→ Charging / Rating Simulation  

---

# ⚠️ Notes

- Ports 2222 & 2223 used to avoid SSH conflicts
- Default credentials are for development only
- System designed for telecom mediation learning and simulation

---

# 🚀 Future Enhancements

- Automatic SMSC → Billing file transfer
- CDR parsing & rating engine
- Database integration (SQL Server / MySQL)
- Kafka / RabbitMQ event streaming
- Full telecom OSS/BSS simulation

---

# 👨‍💻 Author
Ziad Osama  
Telecom & Software Engineering
