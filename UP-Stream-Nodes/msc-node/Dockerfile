FROM alpine:latest

# Install FTP server and bash
RUN apk add --no-cache vsftpd bash

# Create the FTP user and set password
RUN adduser -h /home/msc_user -s /bin/false -D msc_user && \
    echo "msc_user:pass123" | chpasswd

# Configure the FTP Server
RUN echo "local_enable=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "write_enable=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "local_umask=022" >> /etc/vsftpd/vsftpd.conf && \
    echo "chroot_local_user=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "allow_writeable_chroot=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "seccomp_sandbox=NO" >> /etc/vsftpd/vsftpd.conf && \
    echo "pasv_enable=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "pasv_min_port=21100" >> /etc/vsftpd/vsftpd.conf && \
    echo "pasv_max_port=21110" >> /etc/vsftpd/vsftpd.conf && \
    echo "listen_port=21" >> /etc/vsftpd/vsftpd.conf && \
    echo "pasv_address=127.0.0.1" >> /etc/vsftpd/vsftpd.conf

# Setup the FTP directory
RUN mkdir -p /home/msc_user/ftp/cdrs && \
    chown -R msc_user:msc_user /home/msc_user/ftp

# Copy scripts and make them executable
COPY msc-generator.sh /usr/local/bin/msc-generator.sh
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/msc-generator.sh /usr/local/bin/entrypoint.sh

# Expose FTP ports
EXPOSE 21 21100-21110

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]

