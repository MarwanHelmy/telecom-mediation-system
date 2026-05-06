# Telecom Mediation System
A centralized mediation engine routing CSV CDRs from Upstream nodes to Downstream nodes.


# 📡 SMSC Node - SFTP CDR Ingestion Service

## 📌 Overview
This project implements a lightweight SMSC-like node using Docker and SFTP (SSH). It is designed to receive CDR (Call Detail Record) files and store them for processing and archiving in telecom mediation systems. This node simulates a real telecom message ingestion / mediation entry point.

## ⚙️ Configuration

| Item              | Value              | Description |
|------------------|-------------------|-------------|
| Service Name     | smsc-node         | Docker SMSC SFTP Node |
| Protocol         | SFTP (SSH)        | Secure File Transfer Protocol |
| Port             | 2222              | Exposed SSH/SFTP port |
| Username         | smsc              | Login user for SFTP |
| Password         | smsc123           | Default password (change in production) |
| Incoming Folder  | /home/smsc/cdr    | Folder for uploaded CDR files |
| Archive Folder   | /home/smsc/archive| Folder for processed files |
| Base Image       | Ubuntu 22.04      | Docker base OS |
| Container Name   | smsc-node         | Docker container name |

## 🚀 How to Build & Run

### 1. Build Docker Image
docker build -t smsc-node .

### 2. Run Container
docker run -d --name smsc-node -p 2222:22 -v $(pwd)/data:/home/smsc smsc-node

### 3. Check Running Container
docker ps

## 🔌 Connect via SFTP
sftp -P 2222 smsc@localhost

## 🔑 Credentials
Username: smsc  
Password: smsc123

## 📂 Directory Structure
/home/smsc
├── cdr        # Incoming CDR files
└── archive    # Archived processed files

## 📤 Example Usage

Connect:
sftp -P 2222 smsc@localhost

Upload file:
sftp> cd cdr
sftp> put test.cdr

List files:
sftp> ls -l

## 🧠 System Architecture
External System → (SFTP) → Docker SMSC Node → /cdr → Processing Layer → /archive

# 💳 Billing Node - SFTP CDR Processing Service

## 📌 Overview
This project implements a lightweight **Billing Node using Docker and SFTP (SSH)**.  
It acts as a **downstream telecom system** that receives CDR (Call Detail Record) files from the SMSC node and stores them for processing and charging simulation.

This node represents the **billing / rating layer in telecom mediation architecture**.

---

## ⚙️ Configuration

| Item              | Value                | Description |
|------------------|---------------------|-------------|
| Service Name     | billing-node        | Docker Billing SFTP Node |
| Protocol         | SFTP (SSH)          | Secure File Transfer Protocol |
| Port             | 2223                | Exposed SSH/SFTP port |
| Username         | billing             | Login user for SFTP |
| Password         | bill123             | Default password (change in production) |
| Incoming Folder  | /home/billing/incoming  | Received CDR files |
| Processed Folder | /home/billing/processed | After processing storage |
| Base Image       | Ubuntu 22.04        | Docker base OS |
| Container Name   | billing-node        | Docker container name |

---

## 🚀 How to Build & Run

### 🔨 1. Build Docker Image
```bash
docker build -t billing-node .

▶️ 2. Run Container
docker run -d \
  --name billing-node \
  -p 2223:22 \
  -v $(pwd)/data:/home/billing \
  billing-node
  🔍 3. Check Running Container
docker ps
🔌 Connect via SFTP
sftp -P 2223 billing@localhost
🔑 Credentials
Username: billing
Password: bill123
📂 Directory Structure

Inside container:

/home/billing
├── incoming/   # Received CDR files from SMSC
└── processed/  # After billing processing
📤 Example Usage
Connect to Billing Node
sftp -P 2223 billing@localhost
Upload CDR file (from SMSC simulation)
sftp> cd incoming
sftp> put test.cdr
List files
sftp> ls -l
🧠 System Role in Telecom Architecture

This node simulates the billing and rating engine layer in telecom systems:

SMSC Node (UPSTREAM)
        ↓ CDR via SFTP
Billing Node (DOWNSTREAM)
        ↓
incoming/ → processing → processed/



