#!/bin/bash

echo "Starting Billing Node..."

chown root:root /home/billing
chmod 755 /home/billing

mkdir -p /home/billing/incoming /home/billing/processed
chown billing:billing /home/billing/incoming /home/billing/processed

/usr/sbin/sshd -D
