#!/bin/bash

set -e

echo "Starting SMSC SFTP Node..."

# Ensure permissions (IMPORTANT for chroot SFTP)
chown root:root /home/smsc
chmod 755 /home/smsc


mkdir -p /home/smsc/cdrs /home/smsc/archive
chown smsc:smsc /home/smsc/cdrs /home/smsc/archive

# Start the SMS generator script in the background
/usr/local/bin/sms-generator.sh &

# Start SSH daemon in the foreground
/usr/sbin/sshd -D
