FROM alpine:latest

# Install FTP server and bash
RUN apk add --no-cache vsftpd bash

# Create the FTP user and set password
RUN adduser -h /home/pgw_user -s /bin/false -D pgw_user && \
    echo "pgw_user:pass789" | chpasswd

# Configure the FTP Server
RUN echo "local_enable=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "write_enable=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "local_umask=022" >> /etc/vsftpd/vsftpd.conf && \
    echo "chroot_local_user=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "allow_writeable_chroot=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "seccomp_sandbox=NO" >> /etc/vsftpd/vsftpd.conf && \
    echo "pasv_enable=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "listen_port=2323" >> /etc/vsftpd/vsftpd.conf && \
    echo "pasv_min_port=21011" >> /etc/vsftpd/vsftpd.conf && \
    echo "pasv_max_port=21021" >> /etc/vsftpd/vsftpd.conf && \
    echo "pasv_address=172.30.0.50" >> /etc/vsftpd/vsftpd.conf

# Setup the FTP directory
RUN mkdir -p /home/pgw_user/ftp/cdrs && \
    chown -R pgw_user:pgw_user /home/pgw_user/ftp

# Copy scripts and make them executable
COPY pgw-generator.sh /usr/local/bin/pgw-generator.sh
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/pgw-generator.sh /usr/local/bin/entrypoint.sh

# Expose FTP ports
EXPOSE 2323 21011-21021

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
