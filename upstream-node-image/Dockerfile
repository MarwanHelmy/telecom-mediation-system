# =========================================================
# Dockerfile
# =========================================================

FROM ubuntu:22.04

RUN apt-get update && apt-get install -y \
    vsftpd \
    openssh-server \
    bash \
    && rm -rf /var/lib/apt/lists/*

# =========================================================
# COPY FILES
# =========================================================

COPY entrypoint.sh /entrypoint.sh

COPY cdr-generator.sh /cdr-generator.sh

COPY templates /templates

# =========================================================
# PERMISSIONS
# =========================================================

RUN chmod +x /entrypoint.sh

RUN chmod +x /cdr-generator.sh

# =========================================================
# PASSIVE FTP PORTS
# =========================================================

EXPOSE 21100-21110

ENTRYPOINT ["/entrypoint.sh"]
