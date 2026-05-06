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

