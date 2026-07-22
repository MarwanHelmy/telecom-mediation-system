#!/bin/bash

# =========================================================
# SMSC CDR Generator
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

while true
do
    TIMESTAMP=$(date +"%Y%m%d%H%M%S")
    FILENAME="${NODE_NAME}_SMS_CDR_${TIMESTAMP}.csv"
    FILE_PATH="$CDR_DIR/$FILENAME"

    # Generate a file with multiple records (e.g., 5 to 15), some being invalid.
    NUM_RECORDS=$((RANDOM % 11 + 5))

    (
        for (( i=0; i<$NUM_RECORDS; i++ ))
        do
            # Pick random sender and receiver
            SENDER=${SUBSCRIBERS[$((RANDOM % ${#SUBSCRIBERS[@]}))]}
            RECEIVER=${SUBSCRIBERS[$((RANDOM % ${#SUBSCRIBERS[@]}))]}
            while [ "$SENDER" == "$RECEIVER" ]; do
                RECEIVER=${SUBSCRIBERS[$((RANDOM % ${#SUBSCRIBERS[@]}))]}
            done

            # Randomly generate invalid records
            if (( RANDOM % 5 == 0 )); then
                MSG_LENGTH=0
            else
                MSG_LENGTH=$((RANDOM % 160 + 1))
            fi

            RECORD_TIMESTAMP=$(date +"%Y%m%d%H%M%S")${i}
            # Format: RecordType,Sender,Receiver,MessageLength,Timestamp
            echo "2,$SENDER,$RECEIVER,$MSG_LENGTH,$RECORD_TIMESTAMP"
        done
    ) > "$FILE_PATH"

    chown $USERNAME:$USERNAME "$FILE_PATH"
    echo "GENERATED SMS CDR FILE: $FILENAME with $NUM_RECORDS records"
    sleep ${GENERATION_INTERVAL:-10}
done