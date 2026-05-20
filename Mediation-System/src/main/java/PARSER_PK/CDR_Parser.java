
package PARSER_PK;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class CDR_Parser {
    
    public static boolean is_valid_cdr(File cdrFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(cdrFile))) {
            String line = br.readLine();
            
            if (line != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                
                if (parts.length >= 4) {
                    int duration = Integer.parseInt(parts[3].trim());
                    
                    if (duration > 0) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR PARSING CDR FILE : " + cdrFile.getName() + " - " + e.getMessage());
        }
        
        return false; 
    }
}
