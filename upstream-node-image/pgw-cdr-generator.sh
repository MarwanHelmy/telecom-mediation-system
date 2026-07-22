#!/bin/bash

# =========================================================
# PGW CDR Generator (Data Session / IP-CAN)
# =========================================================

SUBSCRIBERS=(
    "201098765432"
    "201123456789"
    "201234567890"
    "201543210987"
    "201011223344"
    "201122334455"
    "201233445566"
    "201544556677"
    "201055667788"
    "201166778899"
)

APN_LIST=("ekb.eg" "digital.gov.eg" "www.google.com" "www.youtube.com")

while true
do
    TIMESTAMP=$(date +"%Y%m%d%H%M%S")
    FILENAME="${NODE_NAME}_PGW_CDR_${TIMESTAMP}.csv"
    FILE_PATH="$CDR_DIR/$FILENAME"

    # Generate a file with multiple records (e.g., 5 to 15), some being invalid.
    NUM_RECORDS=$((RANDOM % 11 + 5))

    (
        for (( i=0; i<$NUM_RECORDS; i++ ))
        do
            SUBSCRIBER=${SUBSCRIBERS[$((RANDOM % ${#SUBSCRIBERS[@]}))]}
            APN=${APN_LIST[$((RANDOM % ${#APN_LIST[@]}))]}
            
            # Bytes (random sizes)
            BYTES_UPLOADED=$((RANDOM % 10000000 + 10240))
            BYTES_DOWNLOADED=$((RANDOM % 50000000 + 51200))
            
            # Randomly generate invalid records
            # Records with APNs "ekb.eg" or "digital.gov.eg" are also filtered by the parser.
            if (( RANDOM % 5 == 0 )); then
                SESSION_DURATION=0
            else
                SESSION_DURATION=$((RANDOM % 1800 + 1))
            fi

            RECORD_TIMESTAMP=$(date +"%Y%m%d%H%M%S")${i}
            # Format: RecordType,SUBSCRIBER,APN,BytesUp,BytesDown,DurationSec,Timestamp
            # This also corrects the original script which was generating records with missing fields.
            echo "3,$SUBSCRIBER,$APN,$BYTES_UPLOADED,$BYTES_DOWNLOADED,$SESSION_DURATION,$RECORD_TIMESTAMP"
        done
    ) > "$FILE_PATH"

    chown $USERNAME:$USERNAME "$FILE_PATH"
    echo "GENERATED PGW CDR FILE: $FILENAME with $NUM_RECORDS records"
    sleep ${GENERATION_INTERVAL:-10}
done