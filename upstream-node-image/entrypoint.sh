#!/bin/bash

echo "====================================="
echo "STARTING TELECOM NODE"
echo "====================================="

echo "NODE NAME : $NODE_NAME"
echo "PROTOCOL  : $PROTOCOL"

#########################################
# CREATE USER
#########################################

useradd -m -d /home/$USERNAME -s /bin/bash $USERNAME

echo "$USERNAME:$PASSWORD" | chpasswd


#########################################
# CREATE DIRECTORIES
#########################################

mkdir -p $SOURCE_DIR
mkdir -p $ARCHIVE_DIR

chown -R $USERNAME:$USERNAME /home/$USERNAME

chmod -R 775 /home/$USERNAME

chmod 775 $SOURCE_DIR
chmod 775 $ARCHIVE_DIR

#########################################
# START CDR GENERATOR
#########################################

export CDR_DIR=$SOURCE_DIR

/cdr-generator.sh &

#########################################
# FTP
#########################################

if [ "$PROTOCOL" = "FTP" ]; then

    echo "STARTING FTP SERVER"

    mkdir -p /var/run/vsftpd/empty

    cp /templates/vsftpd.conf.template \
       /etc/vsftpd.conf

    sed -i "s/NODE_PORT/$NODE_PORT/g" \
    /etc/vsftpd.conf

    sed -i "s/NODE_IP/127.0.0.1/g" \
    /etc/vsftpd.conf

    sed -i "s/pasv_min_port=21100/pasv_min_port=$PASV_MIN/g" \
    /etc/vsftpd.conf

    sed -i "s/pasv_max_port=21110/pasv_max_port=$PASV_MAX/g" \
    /etc/vsftpd.conf

    echo "====================================="
    echo "VSFTPD CONFIG"
    echo "====================================="

    cat /etc/vsftpd.conf

    /usr/sbin/vsftpd /etc/vsftpd.conf

#########################################
# SFTP
#########################################

elif [ "$PROTOCOL" = "SFTP" ]; then

    echo "STARTING SFTP SERVER"

    mkdir -p /var/run/sshd

    cp /templates/sshd_config.template \
       /etc/ssh/sshd_config

    sed -i "s/NODE_PORT/$NODE_PORT/g" \
    /etc/ssh/sshd_config

    ssh-keygen -A

    echo "====================================="
    echo "SSHD CONFIG"
    echo "====================================="

    cat /etc/ssh/sshd_config

    /usr/sbin/sshd -D -e

else

    echo "INVALID PROTOCOL"

    exit 1

fi
