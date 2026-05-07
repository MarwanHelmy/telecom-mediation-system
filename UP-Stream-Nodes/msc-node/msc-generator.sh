#!/bin/bash
FTP_DIR="/home/msc_user/ftp/cdrs"

while true; do
    TIMESTAMP=$(date +"%Y%m%d%H%M%S")
    FILENAME="MSC_CDR_${TIMESTAMP}.csv"
    
    # Default normal values
    DURATION=$((RANDOM % 300 + 1)) # Normal call: 1 to 300 seconds
    CALLED_PARTY="2010$((RANDOM % 90000000 + 10000000))" # Normal number format
    
    # Randomly apply non-normal scenarios (10% chance for each scenario)
    SCENARIO=$((RANDOM % 10))
    
    case $SCENARIO in
        0)
            # Scenario A: Short code
            CALLED_PARTY="999"
            ;;
        1)
            # Scenario B: Zero duration (dropped/free call)
            DURATION=0
            ;;
        2)
            # Scenario C: Massive duration (Fraud! 2 to 3 hours)
            # 7200 seconds = 2 hours. This generates between 7200 and 10800 seconds.
            DURATION=$((RANDOM % 3600 + 7200))
            ;;
    esac
    
    # Format: RecordType, CallingParty, CalledParty, Duration, Timestamp
    echo "1,201012345678,$CALLED_PARTY,$DURATION,$TIMESTAMP" > "$FTP_DIR/$FILENAME"
    
    # Ensure the FTP user has permissions to read the generated files
    chown msc_user:msc_user "$FTP_DIR/$FILENAME"
    
    echo "Generated $FILENAME | Called: $CALLED_PARTY | Duration: $DURATION"
    
    sleep 10
done
