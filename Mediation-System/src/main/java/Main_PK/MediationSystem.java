package Main_PK;

import NODE_PK.NODE;
import NODE_PK.NODES_UP_STREAM;

import java.io.File;
import java.util.List;

public class MediationSystem 
{

    public static void main(String[] args) 
    {
         List<NODE> up_stream_nodes = NODES_UP_STREAM.GET_NODES();
         
        while (true) 
        {

            try 
            {

                System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
                System.out.println("[MEDIATION ⚙️ ]  START MEDIATION CYCLE 🔄");
                System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");

                for (NODE node : up_stream_nodes) {

                    System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
                    System.out.println("[NODE      📡 ] [" + node.getNODE_NAME() + "]");
                    System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");

                    // CONNECT
                    if (!node.connect()) {
                        System.out.println("CONNECT FAILED");
                        continue;
                    }

                    // OPEN CHANNEL
                    if (!node.open_channel()) {
                        System.out.println("CHANNEL FAILED");
                        node.disconnect();
                        continue;
                    }

                    // CHANGE DIRECTORY
                    if (!node.change_directory()) {
                        System.out.println("DIRECTORY FAILED");
                        node.disconnect();
                        continue;
                    }

                    // LIST FILES
                    String[] files = node.list_cdr_files();

                    if (files == null || files.length == 0) {
                        System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
                        System.out.println("[NODE      📡 ] [" + node.getNODE_NAME() + "] NO CDR FILES FOUND  ❌📄");
                        System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");

                        node.disconnect();
                        continue;
                    }
                    System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
                    System.out.println("[NODE      📡 ] [" + node.getNODE_NAME() + "] TOTAL CDR FILES : " + files.length + "  🔢");
                    System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");

                    //====================================================
                    // PROCESS FILES
                    //====================================================
                    System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
                    for (String file : files) 
                    {
                        System.out.println("[NODE      📡 ] [" + node.getNODE_NAME() + "] GET FILE : " + file + "  ⚡📄");
                        System.out.println("-------------------------------------------------------------------------------------------");
                        // DOWNLOAD
                        File downloadedFile = node.download_file(file);

                        if (downloadedFile == null) {

                            System.out.println("DOWNLOAD FAILED : " + file);
                            continue;
                        }
                        
                        System.out.println("[MEDIATION ⚙️  ] DOWNLOAD ' " + file + " ' FILE SUCCESS  ✅📥");
                        System.out.println("-------------------------------------------------------------------------------------------");
                        
                        
                        // MEDIATION FILTER
                        
                        // SEND TO DOWNSTREAM
                        
                        // ARCHIVE FILE AT MEDIATION
                            
                        // ARCHIVE FILE AT NODE
                       
                        boolean archived = node.archive_file(file);

                        if (archived) 
                        {
                            System.out.println("[NODE      📡 ] [" + node.getNODE_NAME() + "] FILE :  ' " + file + " '  ARCHIVE SUCCESS ➡️  🗄️  ✅");
                            System.out.println("-------------------------------------------------------------------------------------------"); 
                        } 
                        else 
                        {
                            System.out.println("ARCHIVE FAILED");
                        }
                    }

                    // DISCONNECT
                    node.disconnect();
                }
                System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
                Thread.sleep(5000);

            } 
            catch (Exception ex) 
            {
                System.out.println("MAIN ERROR : " + ex.getMessage());
            }
        }
    }
}