# MediationX - Telecom CDR Mediation System
---

# 📌 Overview

**MediationX** is a telecom mediation platform responsible for collecting, validating, filtering, routing, and distributing CDR (Call Detail Record) files between telecom network nodes and downstream systems.

The platform simulates a real telecom mediation environment where upstream telecom nodes continuously generate CDR files and the mediation engine processes them automatically using configurable routing rules.

---

# 🏗️ System Architecture

## 🔄 Flow Diagram

<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/75cea7b4-f81b-4b69-b144-db8d3ec7ed8b" />


---

# ⚙️ Core Features

- 📥 Download CDR files from upstream telecom nodes
- 🔍 Parse and validate CDR records
- 🚫 Filter invalid CDRs (duration = 0)
- 🔀 Dynamic routing engine
- 📤 Send files to downstream systems
- 🗂️ Archive processed files
- 🌐 Web-based management dashboard
- 🐳 Dockerized telecom nodes
- 📜 Real-time CLI logs
- 🔐 FTP / SFTP / SCP support

---

# 🧠 How The Mediation Works

## 1️⃣ Upstream Nodes

The upstream telecom nodes run as Docker containers and generate CDR files continuously.

Supported node types:

- MSC
- SMSC
- PGW

Each node contains:

- Protocol
- IP Address
- Port
- Username
- Password
- Data Path
- Archive Path

Supported protocols:

- FTP
- SFTP
- SCP

---

## 2️⃣ Mediation Processing Cycle

The mediation engine continuously loops through all active upstream nodes.

For every node:

### ✔️ Connect to Node
The engine establishes a connection using the configured protocol.

### ✔️ Open Channel
Communication session is initialized.

### ✔️ Navigate to Data Directory
The engine changes to the configured CDR path.

### ✔️ Download Files
Available CDR files are downloaded locally.

### ✔️ Validate CDRs
The parser validates the duration field.

Rule:

```text
IF Duration == 0
    ➜ Ignore File
ELSE
    ➜ Route File
```

### ✔️ Apply Routing Rules
The mediation system determines target downstream systems dynamically from the database.

Example:

```text
MSC  ➜ Billing
MSC  ➜ Fraud
PGW  ➜ DWH
```

### ✔️ Upload to Downstream Nodes
Valid files are transferred automatically.

### ✔️ Archive Files
Files are archived both:

- Locally inside mediation
- Remotely on upstream node

---

# 🌐 Web Management System

The platform includes a web-based administration panel for managing:

## 📌 Node Management

- Add Nodes
- Delete Nodes
- Activate / Deactivate Nodes
- Configure Connection Parameters

## 📌 Routing Rules Management

- Create Routing Rules
- Delete Rules
- Enable / Disable Rules

## 📌 Authentication

- Admin Login System

---

# 🗄️ Database Design

<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/3665c3cd-ba53-4adb-85a4-7241774d1d04" />

## 📌 admins

Stores administrator accounts.

| Column | Type |
|---|---|
| id | Integer |
| username | VARCHAR |
| password | VARCHAR |

---

## 📌 nodes

Stores upstream and downstream telecom nodes.

| Column | Description |
|---|---|
| id | Node ID |
| name | Node Name |
| type | UPSTREAM / DOWNSTREAM |
| protocol | FTP / SFTP / SCP |
| username | Authentication Username |
| password | Authentication Password |
| ip | Node IP |
| port | Node Port |
| data_path | CDR Directory |
| archive_path | Archive Directory |
| isactive | Active Status |
| isdeleted | Soft Delete Flag |

---

## 📌 routing_rules

Defines the routing logic between nodes.

| Column | Description |
|---|---|
| source_node_id | Upstream Node |
| destination_node_id | Downstream Node |
| is_active | Rule Status |
| created_at | Creation Timestamp |

---

# 🐳 Docker Infrastructure

The project simulates telecom nodes using Docker containers.

## Included Containers

### 📥 Upstream Containers

- MSC
- SMSC
- PGW

These containers generate telecom CDR files continuously.

### 📤 Downstream Containers

- Billing
- Fraud
- DWH

These containers receive valid routed files.

---

# 📂 Project Structure

```bash
telecom-mediation-system/
│
├── Mediation-System/
│   ├── src/main/java/
│   │   ├── DB_PK/
│   │   ├── Main_PK/
│   │   ├── NODE_PK/
│   │   └── PARSER_PK/
│
├── MSZ-WEB-GUI/
│   ├── src/main/java/
│   ├── webapp/
│   └── Servlets/
│
├── upstream-node-image/
├── downstream-node-image/
├── msdb_backup.sql
└── Run_Mediation_System.sh
```

---

# 🛠️ Technologies Used

<img width="1958" height="803" alt="image" src="https://github.com/user-attachments/assets/9c82c7c1-1197-4474-af56-f7373de08bf8" />

---

# 📜 Example Mediation Logs

```bash
[MEDIATION ⚙️ ] START MEDIATION CYCLE (1)

[NODE 📡 ] [MSC]
TOTAL CDR FILES : 3

GET FILE (1) : cdr_001.txt

DOWNLOAD FILE SUCCESS ✅

VALIDATING CDR...

ROUTING FILE TO [Billing] ✅

ARCHIVING FILE ✅
```

---

# 🚀 Future Enhancements

- Kafka Integration
- REST APIs
- Real-time Monitoring Dashboard
- Kubernetes Deployment
- Multi-threaded Processing
- High Availability Support
- Prometheus & Grafana Monitoring

---

# 👨‍💻 Authors

## Ziad Osama | Marawan Helmy | Mahmoud Salah

Telecom Software Engineers passionate about:

- Telecom Systems
- Mediation Platforms
- Backend Development
- Distributed Systems
- Docker Infrastructure

---

# ⭐ Project Goal

This project was built to simulate a real-world telecom mediation environment used between telecom switches and downstream operational systems such as:

- Billing
- Fraud Detection
- Data Warehouse (DWH)

It demonstrates:

- Telecom flow understanding
- Backend engineering
- File-based mediation
- Routing systems
- Dockerized infrastructure
- Database-driven architecture

---

