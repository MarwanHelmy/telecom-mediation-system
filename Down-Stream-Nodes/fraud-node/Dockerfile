FROM alpine:latest

# Install FTP server
RUN apk add --no-cache vsftpd

# Create the FTP user and set password
RUN adduser -h /home/fraud_user -s /bin/false -D fraud_user && \
    echo "fraud_user:pass456" | chpasswd

# Configure the FTP Server
RUN echo "local_enable=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "write_enable=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "local_umask=022" >> /etc/vsftpd/vsftpd.conf && \
    echo "chroot_local_user=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "allow_writeable_chroot=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "seccomp_sandbox=NO" >> /etc/vsftpd/vsftpd.conf && \
    echo "pasv_enable=YES" >> /etc/vsftpd/vsftpd.conf && \
    echo "pasv_min_port=21120" >> /etc/vsftpd/vsftpd.conf && \
    echo "pasv_max_port=21130" >> /etc/vsftpd/vsftpd.conf && \
    echo "listen_port=21" >> /etc/vsftpd/vsftpd.conf

# Setup the directory where Java will upload the files
RUN mkdir -p /home/fraud_user/ftp/received_cdrs && \
    chown -R fraud_user:fraud_user /home/fraud_user/ftp

# Expose ports
EXPOSE 21 21120-21130

# Start the FTP server in the foreground
CMD ["/usr/sbin/vsftpd", "/etc/vsftpd/vsftpd.conf"]
