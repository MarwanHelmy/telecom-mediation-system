FROM ubuntu:22.04

RUN apt-get update && apt-get install -y \
    openssh-server \
    bash \
    && rm -rf /var/lib/apt/lists/*

# Create user for SFTP
RUN useradd -m -d /home/dwh -s /bin/bash dwh && \
    echo "dwh:dwh123" | chpasswd

# Create directories
RUN mkdir -p /home/dwh/archive /home/dwh/incoming /home/dwh/processed && \
    chown -R dwh:dwh /home/dwh

# SSH setup
RUN mkdir /var/run/sshd

COPY sshd_config /etc/ssh/sshd_config
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 22

CMD ["/entrypoint.sh"]