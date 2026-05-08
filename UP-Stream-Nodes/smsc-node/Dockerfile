FROM ubuntu:22.04

RUN apt-get update && apt-get install -y \
    openssh-server \
    bash \
    && rm -rf /var/lib/apt/lists/*

# Create user for SFTP
RUN useradd -m -d /home/smsc -s /bin/bash smsc && \
    echo "smsc:smsc123" | chpasswd

# Create directories
RUN mkdir -p /home/smsc/cdr /home/smsc/archive && \
    chown -R smsc:smsc /home/smsc

# SSH setup
RUN mkdir /var/run/sshd

COPY sshd_config /etc/ssh/sshd_config
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 22

CMD ["/entrypoint.sh"]
