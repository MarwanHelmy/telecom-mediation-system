#!/bin/bash
FTP_DIR="/home/msc_user/ftp/cdrs"

# Define the pool of 20 allowed phone numbers
TARGET_NUMBERS=(
    "201098765432" "201123456789" "201234567890" "201543210987" "201011223344"
    "201122334455" "201233445566" "201544556677" "201055667788" "201166778899"
    "201277889900" "201588990011" "201099001122" "201100112233" "201211223344"
    "201522334455" "201033445566" "201144556677" "201255667788" "201566778899"
)

while true; do
    TIMESTAMP=$(date +"%Y%m%d%H%M%S")
    FILENAME="MSC_CDR_${TIMESTAMP}.csv"
    
    # Pick a random index between 0 and 19 to select a number from the array
    INDEX=$((RANDOM % 20))
    CALLED_PARTY=${TARGET_NUMBERS[$INDEX]}
    
    # Default normal duration (1 to 300 seconds)
    DURATION=$((RANDOM % 300 + 1)) 
    
    # Randomly apply fraud scenarios (roughly a 20% chance to be an anomaly)
    SCENARIO=$((RANDOM % 10))
    
    case $SCENARIO in
        1)
            # Scenario A: Zero duration (dropped/free call)
            DURATION=0
            ;;
        2)
            # Scenario B: Massive duration (over 1 hour)
            # 3600 seconds = 1 hour. This generates a random time between 1 and 2 hours.
            DURATION=$((RANDOM % 3600 + 3600))
            ;;
    esac
    
    # Format: RecordType, CallingParty, CalledParty, Duration, Timestamp
    echo "1,201012345678,$CALLED_PARTY,$DURATION,$TIMESTAMP" > "$FTP_DIR/$FILENAME"
    
    # Ensure the FTP user has permissions to read the generated files
    chown msc_user:msc_user "$FTP_DIR/$FILENAME"
    
    echo "Generated $FILENAME | Called: $CALLED_PARTY | Duration: $DURATION"
    
    sleep 10
done
