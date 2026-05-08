#!/bin/bash
FTP_DIR="/home/pgw_user/ftp/cdrs"

# Define the pool of 20 allowed MSISDN (phone numbers)
MSISDNS=(
    "201098765432" "201123456789" "201234567890" "201543210987" "201011223344"
    "201122334455" "201233445566" "201544556677" "201055667788" "201166778899"
    "201277889900" "201588990011" "201099001122" "201100112233" "201211223344"
    "201522334455" "201033445566" "201144556677" "201255667788" "201566778899"
)

# Define APNs (Access Point Names)
APNS=(
    "internet" "mms" "wap" "ims" "voip"
)

while true; do
    TIMESTAMP=$(date +"%Y%m%d%H%M%S")
    FILENAME="PGW_CDR_${TIMESTAMP}.csv"
    
    # Pick a random MSISDN from the pool
    MSISDN_INDEX=$((RANDOM % 20))
    MSISDN=${MSISDNS[$MSISDN_INDEX]}
    
    # Pick a random APN
    APN_INDEX=$((RANDOM % 5))
    APN=${APNS[$APN_INDEX]}
    
    # Generate random session duration (1 to 3600 seconds)
    DURATION=$((RANDOM % 3600 + 1))
    
    # Generate random data volumes (in MB)
    # Normal: 1-500 MB
    UPLINK=$((RANDOM % 500 + 1))
    DOWNLINK=$((RANDOM % 500 + 1))
    
    # Randomly apply fraud/anomaly scenarios (roughly a 15% chance)
    SCENARIO=$((RANDOM % 20))
    
    case $SCENARIO in
        1)
            # Scenario A: Excessive data usage (over 1GB in downlink)
            DOWNLINK=$((RANDOM % 5000 + 1000))
            ;;
        2)
            # Scenario B: Very short session with unusual uplink
            DURATION=$((RANDOM % 5 + 1))
            UPLINK=$((RANDOM % 100 + 50))
            ;;
        3)
            # Scenario C: Zero session duration
            DURATION=0
            ;;
    esac
    
    # Generate random QCI (Quality Class Indicator): 1-9
    QCI=$((RANDOM % 9 + 1))
    
    # Generate random IMSI (International Mobile Subscriber Identity)
    # Format: 310000000000000 (first 3 are MCC, next 2 are MNC)
    IMSI="310$(printf "%014d" $((RANDOM % 1000000000)))"
    
    # Format: RecordType, IMSI, MSISDN, APN, Duration, Uplink(MB), Downlink(MB), QCI, Timestamp
    echo "2,$IMSI,$MSISDN,$APN,$DURATION,$UPLINK,$DOWNLINK,$QCI,$TIMESTAMP" > "$FTP_DIR/$FILENAME"
    
    # Ensure the FTP user has permissions to read the generated files
    chown pgw_user:pgw_user "$FTP_DIR/$FILENAME"
    
    echo "Generated $FILENAME | MSISDN: $MSISDN | APN: $APN | UL: ${UPLINK}MB | DL: ${DOWNLINK}MB | Duration: ${DURATION}s"
    
    sleep 10
done
