#!/bin/bash

set -e

echo "Starting DWH SFTP Node..."

chown root:root /home/dwh
chmod 755 /home/dwh

mkdir -p /home/dwh/archive /home/dwh/incoming /home/dwh/processed
chown dwh:dwh /home/dwh/archive /home/dwh/incoming /home/dwh/processed

/usr/sbin/sshd -D