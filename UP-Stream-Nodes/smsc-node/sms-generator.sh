#!/bin/bash
CDR_DIR="/home/smsc/cdrs"
ARCHIVE_DIR="/home/smsc/archive"

# Define the pool of 20 allowed phone numbers (same as MSC for consistency)
TARGET_NUMBERS=(
    "201098765432" "201123456789" "201234567890" "201543210987" "201011223344"
    "201122334455" "201233445566" "201544556677" "201055667788" "201166778899"
    "201277889900" "201588990011" "201099001122" "201100112233" "201211223344"
    "201522334455" "201033445566" "201144556677" "201255667788" "201566778899"
)

# SMS Message templates
MESSAGES=(
    "Your verification code is: 123456"
    "Your account has been credited with 50 EGP"
    "Special offer: 20% off your next purchase"
    "Reminder: Appointment tomorrow at 10:00 AM"
    "Your bill for this month is 250 EGP"
    "Welcome to our service! Reply STOP to unsubscribe"
    "Security alert: New login detected from Cairo"
    "Your OTP code is: 789012 (valid for 5 minutes)"
    "Thank you for your payment of 100 EGP"
    "Your package has been delivered"
)

# SMS Types: 0=Normal, 1=Flash SMS, 2=Binary
SMS_TYPES=(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 2)

while true; do
    TIMESTAMP=$(date +"%Y%m%d%H%M%S")
    FILENAME="SMSC_CDR_${TIMESTAMP}.csv"
    ARCHIVE_FILENAME="SMSC_CDR_${TIMESTAMP}.csv"
    
    # Pick a random index between 0 and 19 to select a number from the array
    INDEX=$((RANDOM % 20))
    DESTINATION_NUMBER=${TARGET_NUMBERS[$INDEX]}
    SOURCE_NUMBER="201012345678"  # SMSC short code or source number
    
    # Generate random message ID (1-99999)
    MESSAGE_ID=$((RANDOM % 99999 + 1))
    
    # Default normal parameters
    MESSAGE_LENGTH=$((RANDOM % 140 + 1))  # SMS max 160 chars, but let's do 1-140
    MESSAGE_TEXT="${MESSAGES[$((RANDOM % ${#MESSAGES[@]}))]}"
    # Truncate or pad message to desired length
    while [ ${#MESSAGE_TEXT} -lt $MESSAGE_LENGTH ]; do
        MESSAGE_TEXT="${MESSAGE_TEXT} ${MESSAGES[$((RANDOM % ${#MESSAGES[@]}))]}"
    done
    MESSAGE_TEXT="${MESSAGE_TEXT:0:$MESSAGE_LENGTH}"
    
    SMS_STATUS="DELIVERED"  # Default status
    ERROR_CODE="0"
    
    # Randomly apply anomaly scenarios (20% chance)
    SCENARIO=$((RANDOM % 10))
    
    case $SCENARIO in
        1)
            # Scenario: Failed delivery
            SMS_STATUS="FAILED"
            ERROR_CODE=$((RANDOM % 10 + 1))
            MESSAGE_LENGTH=0
            MESSAGE_TEXT=""
            ;;
        2)
            # Scenario: Very long message (multi-part SMS)
            MESSAGE_LENGTH=$((RANDOM % 300 + 150))
            while [ ${#MESSAGE_TEXT} -lt $MESSAGE_LENGTH ]; do
                MESSAGE_TEXT="${MESSAGE_TEXT} ${MESSAGES[$((RANDOM % ${#MESSAGES[@]}))]}"
            done
            MESSAGE_TEXT="${MESSAGE_TEXT:0:$MESSAGE_LENGTH}"
            SMS_STATUS="DELIVERED_AS_MULTIPART"
            ;;
        3)
            # Scenario: Flash SMS (appears directly on screen)
            SMS_TYPE=1
            SMS_STATUS="DELIVERED_FLASH"
            ;;
        *)
            # Normal SMS
            SMS_TYPE=0
            ;;
    esac
    
    # Select random SMS type if not set by scenario
    if [ -z "$SMS_TYPE" ]; then
        SMS_TYPE=${SMS_TYPES[$((RANDOM % ${#SMS_TYPES[@]}))]}
    fi
    
    # Format: RecordType,MessageID,SourceNumber,DestinationNumber,MessageLength,MessageText,Status,ErrorCode,Timestamp,SMS
    echo "2,$MESSAGE_ID,$SOURCE_NUMBER,$DESTINATION_NUMBER,$MESSAGE_LENGTH,\"$MESSAGE_TEXT\",$SMS_STATUS,$ERROR_CODE,$TIMESTAMP,SMS" > "$CDR_DIR/$FILENAME"
    
    # Also archive the file after a delay (simulate archiving)
    # For now, just copy to archive as well
    cp "$CDR_DIR/$FILENAME" "$ARCHIVE_DIR/$ARCHIVE_FILENAME"
    
    # Ensure proper permissions
    chown smsc:smsc "$CDR_DIR/$FILENAME"
    chown smsc:smsc "$ARCHIVE_DIR/$ARCHIVE_FILENAME"
    
    echo "Generated $FILENAME | To: $DESTINATION_NUMBER | Status: $SMS_STATUS | Length: $MESSAGE_LENGTH"
    
    # Generate files at different intervals (every 5-15 seconds for more realistic traffic)
    INTERVAL=$((RANDOM % 10 + 5))
    sleep $INTERVAL
done
