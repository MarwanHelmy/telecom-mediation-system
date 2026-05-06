#!/bin/bash

# Start the CDR generator script in the background
/usr/local/bin/msc-generator.sh &

# Start the FTP server in the foreground
/usr/sbin/vsftpd /etc/vsftpd/vsftpd.conf

