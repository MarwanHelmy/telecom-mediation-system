#!/bin/bash

set -e

echo "Starting SMSC SFTP Node..."

# Ensure permissions (IMPORTANT for chroot SFTP)
chown root:root /home/smsc
chmod 755 /home/smsc

mkdir -p /home/smsc/cdr /home/smsc/archive
chown smsc:smsc /home/smsc/cdr /home/smsc/archive

/usr/sbin/sshd -D
