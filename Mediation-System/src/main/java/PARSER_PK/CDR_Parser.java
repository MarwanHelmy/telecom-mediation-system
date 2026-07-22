package PARSER_PK;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class CDR_Parser {

    // List of APNs that should be filtered out as they are non-charged sites
    private static final List<String> BLOCKED_APNS = Arrays.asList("ekb.eg", "digital.gov.eg");

    public static File filterValidRecords(File inputFile) throws IOException {
        File tempDir = new File(inputFile.getParent(), "temp_filtered");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        // Create a unique temporary file for the filtered output
        File outputFile = new File(tempDir, "filtered_" + UUID.randomUUID().toString() + "_" + inputFile.getName());
        boolean hasValidRecords = false;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (isValidRecord(line)) {
                    bw.write(line);
                    bw.newLine();
                    hasValidRecords = true;
                }
            }
        }

        if (!hasValidRecords) {
            outputFile.delete();
            return null;
        }

        return outputFile;
    }

    private static boolean isValidRecord(String cdrLine) {
        if (cdrLine == null || cdrLine.trim().isEmpty()) {
            return false;
        }

        String[] parts = cdrLine.split(",");
        if (parts.length < 1) return false;

        String recordType = parts[0].trim();

        try {
            switch (recordType) {
                case "1": // MSC (Voice Call)
                    // Format: 1,CallingParty,CalledParty,Duration,Timestamp (5 parts)
                    return parts.length == 5 && Integer.parseInt(parts[3].trim()) > 0;
                case "2": // SMSC (SMS)
                    // Format: 2,Sender,Receiver,MessageLength,Timestamp (5 parts)
                    return parts.length == 5 && Integer.parseInt(parts[3].trim()) > 0;
                case "3": // PGW (Data)
                    // Format: 3,SUBSCRIBER,APN,BytesUp,BytesDown,DurationSec,Timestamp (7 parts)
                    return parts.length == 7 && !BLOCKED_APNS.contains(parts[2].trim()) && Integer.parseInt(parts[5].trim()) > 0;
                default:
                    System.err.println("Unknown CDR record type: " + recordType + " in line: " + cdrLine);
                    return false;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("ERROR PARSING CDR record: '" + cdrLine + "' - " + e.getMessage());
            return false;
        }
    }
}
