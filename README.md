# 📡 Telecom Mediation System
# Telecom Mediation System - Docker Network Setup Guide

## Overview
The project contains:

| Node         | Purpose                   | IP          |
| ------------ | ------------------------- | ----------- |
| MSC Node     | Generates CDRs using FTP  | 172.30.0.10 |
| SMSC Node    | SFTP upstream node        | 172.30.0.30 |
| Billing Node | Downstream billing system | 172.30.0.40 |

All containers communicate through a shared Docker bridge network:

```bash
telecom-network
```

---

# Project Structure

```text
telecom-mediation-system/
├── UP-Stream-Nodes/
│   ├── msc-node/
│   │   ├── Dockerfile
│   │   ├── docker-compose.yml
│   │   ├── entrypoint.sh
│   │   └── msc-generator.sh
│   │
│   └── smsc-node/
│       ├── Dockerfile
│       ├── docker-compose.yml
│       ├── entrypoint.sh
│       ├── sshd_config
│       └── data/
│
└── DOWN-Stream-Nodes/
    └── billing-system/
        ├── Dockerfile
        ├── docker-compose.yml
        ├── entrypoint.sh
        ├── sshd_config
        └── data/
```
Create Shared Docker Network

Create the telecom shared network:

```bash
docker network create \
--driver bridge \
--subnet=172.30.0.0/16 \
telecom-network
```

Verify:

```bash
docker network ls
```

Inspect network:

```bash
docker network inspect telecom-network
```

---

MSC Node Configuration

## File: `UP-Stream-Nodes/msc-node/docker-compose.yml`

```yaml
services:
  msc_node:
    build: .
    container_name: msc_node

    ports:
      - "21:21"
      - "21100-21110:21100-21110"

    networks:
      telecom-network:
        ipv4_address: 172.30.0.10

networks:
  telecom-network:
    external: true
```

---
 SMSC Node Configuration

## File: `UP-Stream-Nodes/smsc-node/docker-compose.yml`

```yaml
services:
  smsc-node:
    build: .
    container_name: smsc-node

    ports:
      - "2222:22"

    volumes:
      - ./data:/home/smsc

    restart: always

    networks:
      telecom-network:
        ipv4_address: 172.30.0.30

networks:
  telecom-network:
    external: true
```

---

Billing Node Configuration

## File: `DOWN-Stream-Nodes/billing-system/docker-compose.yml`

```yaml
services:
  billing-node:
    build: .
    container_name: billing-node

    ports:
      - "2223:22"

    volumes:
      - ./data:/home/billing

    restart: always

    networks:
      telecom-network:
        ipv4_address: 172.30.0.40

networks:
  telecom-network:
    external: true
```

---
 Build and Start Containers

## Start MSC Node

```bash
cd telecom-mediation-system/UP-Stream-Nodes/msc-node

docker compose up -d --build
```

---

## Start SMSC Node

```bash
cd ../smsc-node

docker compose up -d --build
```

---

## Start Billing Node

```bash
cd ../../DOWN-Stream-Nodes/billing-system

docker compose up -d --build
```

---
