#!/bin/bash

# Start the CDR generator script in the background
/usr/local/bin/pgw-generator.sh &

# Start the FTP server in the foreground
/usr/sbin/vsftpd /etc/vsftpd/vsftpd.conf
