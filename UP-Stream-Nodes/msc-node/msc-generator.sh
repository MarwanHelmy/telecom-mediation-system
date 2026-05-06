#!/bin/bash
FTP_DIR="/home/msc_user/ftp/cdrs"

while true; do
    TIMESTAMP=$(date +"%Y%m%d%H%M%S")
    FILENAME="MSC_CDR_${TIMESTAMP}.csv"
    
    # Generate random duration between 0 and 299
    DURATION=$((RANDOM % 300)) 
    
    # Randomly generate normal numbers or short codes to test Java filtering
    if [ $((RANDOM % 5)) -eq 0 ]; then
        CALLED_PARTY="999"  # Short code
    else
        CALLED_PARTY="2010$((RANDOM % 90000000 + 10000000))" # Normal Egyptian number format
    fi
    
    # Randomly force a zero duration to test Java filtering
    if [ $((RANDOM % 5)) -eq 0 ]; then
        DURATION=0
    fi
    
    # Format: RecordType, CallingParty, CalledParty, Duration, Timestamp
    echo "1,201012345678,$CALLED_PARTY,$DURATION,$TIMESTAMP" > "$FTP_DIR/$FILENAME"
    
    # Ensure the FTP user has permissions to read the generated files
    chown msc_user:msc_user "$FTP_DIR/$FILENAME"
    
    echo "Generated $FILENAME | Called: $CALLED_PARTY | Duration: $DURATION"
    
    sleep 10
done

