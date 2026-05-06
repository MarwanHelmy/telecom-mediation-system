FROM ubuntu:22.04

RUN apt-get update && apt-get install -y \
    openssh-server bash \
    && rm -rf /var/lib/apt/lists/*

RUN useradd -m -d /home/billing -s /bin/bash billing && \
    echo "billing:bill123" | chpasswd

RUN mkdir -p /home/billing/incoming /home/billing/processed && \
    chown -R billing:billing /home/billing

RUN mkdir /var/run/sshd

COPY sshd_config /etc/ssh/sshd_config
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 22

CMD ["/entrypoint.sh"]
