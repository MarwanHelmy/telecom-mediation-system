package Main_PK;

import NODE_PK.NODE;
import NODE_PK.NODES_UP_STREAM;
import DB_PK.ROUTING_MANAGER;
import PARSER_PK.CDR_Parser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class MediationSystem {

    public static void main(String[] args) {
         List<NODE> up_stream_nodes = NODES_UP_STREAM.GET_NODES();
         
        while (true) {
            try {
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
                    for (String file : files) {
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
                        // Validate CDR duration column using the parser package
                        boolean isValid = CDR_Parser.is_valid_cdr(downloadedFile);

                        if (!isValid) {
                            System.out.println("[MEDIATION ⚙️  ] FILE ' " + file + " ' HAS 0 DURATION OR INVALID. SKIPPING UPLOAD. 🚫");
                        } else {
                            // SEND TO DOWNSTREAM
                            // Fetch active target downstream destinations via database routing manager
                            List<NODE> downstream_nodes = ROUTING_MANAGER.get_target_downstream_nodes(node.getNODE_ID());
                            
                            if (downstream_nodes.isEmpty()) {
                                System.out.println("[ROUTING   🔀 ] NO ACTIVE DOWNSTREAM RULES FOR NODE : " + node.getNODE_NAME() + " ⚠️");
                            } else {
                                for (NODE targetNode : downstream_nodes) {
                                    System.out.println("[ROUTING   🔀 ] SENDING TO DOWNSTREAM : " + targetNode.getNODE_NAME() + " 🚀");
                                    
                                    if (targetNode.connect() && targetNode.open_channel() && targetNode.change_directory()) {
                                        boolean uploaded = targetNode.upload_file(downloadedFile);
                                        if (uploaded) {
                                            System.out.println("[UPLOAD    📤 ] SUCCESS TO " + targetNode.getNODE_NAME() + " ✅");
                                        } else {
                                            System.out.println("[UPLOAD    📤 ] FAILED TO " + targetNode.getNODE_NAME() + " ❌");
                                        }
                                    } else {
                                        System.out.println("[UPLOAD    📤 ] CONNECTION FAILED TO " + targetNode.getNODE_NAME() + " ❌");
                                    }
                                    targetNode.disconnect();
                                }
                            }
                        }

                        // ARCHIVE FILE AT MEDIATION
                        // Move the processed local file into the historical repository workspace
                        try {
                            File archiveDir = new File("collection-cdr/old-cdr/" + node.getNODE_NAME());
                            if (!archiveDir.exists()) {
                                archiveDir.mkdirs();
                            }
                            File localArchiveFile = new File(archiveDir, file);
                            Files.move(downloadedFile.toPath(), localArchiveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("[ARCHIVE   🗄️ ] FILE MOVED TO LOCAL old-cdr FOLDER ✅");
                        } catch (Exception e) {
                            System.out.println("[ARCHIVE   🗄️ ] FAILED TO MOVE LOCAL FILE : " + e.getMessage());
                        }
                       
                        // ARCHIVE FILE AT NODE
                        // Trigger remote server file relocation to clear the original data path
                        boolean archived = node.archive_file(file);

                        if (archived) {
                            System.out.println("[NODE      📡 ] [" + node.getNODE_NAME() + "] FILE :  ' " + file + " '  ARCHIVED AT UPSTREAM ➡️  ✅");
                            System.out.println("-------------------------------------------------------------------------------------------"); 
                        } else {
                            System.out.println("ARCHIVE FAILED AT UPSTREAM NODE ❌");
                        }
                    }

                    // DISCONNECT
                    node.disconnect();
                }
                System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
                Thread.sleep(5000);

            } catch (Exception ex) {
                System.out.println("MAIN ERROR : " + ex.getMessage());
            }
        }
    }
}