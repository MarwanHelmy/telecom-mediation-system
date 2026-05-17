#!/bin/bash

# =========================================================
# cdr-generator.sh
# =========================================================

TARGET_NUMBERS=(
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

    FILENAME="${NODE_NAME}_CDR_${TIMESTAMP}.csv"

    INDEX=$((RANDOM % ${#TARGET_NUMBERS[@]}))

    CALLED_PARTY=${TARGET_NUMBERS[$INDEX]}

    DURATION=$((RANDOM % 300 + 1))

    SCENARIO=$((RANDOM % 10))

    case $SCENARIO in

        1)
            DURATION=0
            ;;

        2)
            DURATION=$((RANDOM % 3600 + 3600))
            ;;

    esac

    echo "1,201012345678,$CALLED_PARTY,$DURATION,$TIMESTAMP" \
    > "$CDR_DIR/$FILENAME"

    chown $USERNAME:$USERNAME \
    "$CDR_DIR/$FILENAME"

    echo "GENERATED: $FILENAME"

    sleep ${GENERATION_INTERVAL:-10}

done
