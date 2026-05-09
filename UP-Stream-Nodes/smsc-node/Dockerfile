FROM ubuntu:22.04

RUN apt-get update && apt-get install -y \
    openssh-server \
    && rm -rf /var/lib/apt/lists/*

# Create SMSC user
RUN useradd -m -d /home/smsc -s /bin/bash smsc && \
    echo "smsc:smsc123" | chpasswd

# Configure SSH for SFTP only (chrooted)
RUN mkdir /var/run/sshd && \
    sed -i 's/#Subsystem sftp/Subsystem sftp internal-sftp/' /etc/ssh/sshd_config && \
    echo "\nMatch User smsc" >> /etc/ssh/sshd_config && \
    echo "    ChrootDirectory /home/smsc" >> /etc/ssh/sshd_config && \
    echo "    ForceCommand internal-sftp" >> /etc/ssh/sshd_config && \
    echo "    PasswordAuthentication yes" >> /etc/ssh/sshd_config && \
    echo "    PermitTunnel no" >> /etc/ssh/sshd_config && \
    echo "    AllowAgentForwarding no" >> /etc/ssh/sshd_config && \
    echo "    AllowTcpForwarding no" >> /etc/ssh/sshd_config && \
    echo "    X11Forwarding no" >> /etc/ssh/sshd_config

# Copy scripts
COPY sms-generator.sh /usr/local/bin/sms-generator.sh
COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /usr/local/bin/sms-generator.sh /entrypoint.sh

EXPOSE 22

ENTRYPOINT ["/entrypoint.sh"]
