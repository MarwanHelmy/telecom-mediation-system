package DOCKER_PK;

import NODE_PK.NODE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DOCKER_MANAGER {

    private static final Logger LOGGER
            = Logger.getLogger(DOCKER_MANAGER.class.getName());

    // =========================================================
    // FORMAT CONTAINER NAME
    // =========================================================
    public static String FORMAT_CONTAINER_NAME(String nodeName) {
        return nodeName
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                + "-node";
    }

    // =========================================================
    // CHECK IF CONTAINER EXISTS
    // =========================================================
    public static boolean IS_CONTAINER_EXISTS(String containerName) {
        try {
            ProcessBuilder pb
                    = new ProcessBuilder(
                            "docker",
                            "ps",
                            "-a",
                            "--format",
                            "{{.Names}}"
                    );

            Process process = pb.start();

            BufferedReader reader
                    = new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
                    );

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(containerName)) {
                    process.waitFor();

                    return true;
                }
            }

            process.waitFor();
        } catch (Exception ex) {
            LOGGER.log(
                    Level.SEVERE,
                    "CHECK CONTAINER ERROR",
                    ex
            );
        }

        return false;
    }

    // =========================================================
    // REMOVE CONTAINER
    // =========================================================
    public static boolean REMOVE_CONTAINER(String containerName) {
        try {
            // STOP CONTAINER

            Process stopProcess
                    = new ProcessBuilder(
                            "docker",
                            "stop",
                            containerName
                    ).start();

            stopProcess.waitFor();

            // REMOVE CONTAINER
            Process rmProcess
                    = new ProcessBuilder(
                            "docker",
                            "rm",
                            "-f",
                            containerName
                    ).start();

            int exitCode = rmProcess.waitFor();

            return exitCode == 0;
        } catch (Exception ex) {
            LOGGER.log(
                    Level.SEVERE,
                    "REMOVE CONTAINER ERROR",
                    ex
            );
        }

        return false;
    }

// =========================================================
// CREATE NODE
// =========================================================
    public static boolean CREATE_NODE(NODE node) {
        try {
            String containerName
                    = FORMAT_CONTAINER_NAME(
                            node.getNODE_NAME()
                    );

            // =====================================================
            // REMOVE OLD CONTAINER IF EXISTS
            // =====================================================
            if (IS_CONTAINER_EXISTS(containerName)) {
                REMOVE_CONTAINER(containerName);
            }

            // =====================================================
            // DETERMINE INTERNAL PORT
            // =====================================================
            String internalPort;

            switch (node.getNODE_PROTOCOL()) {
                case "FTP":
                    internalPort = "21";
                    break;

                case "SFTP":
                    internalPort = "22";
                    break;

                default:
                    throw new RuntimeException(
                            "UNSUPPORTED PROTOCOL"
                    );
            }

            // =====================================================
            // DETERMINE IMAGE
            // =====================================================
            String imageName;

            switch (node.getNODE_TYPE()) {
                case "UPSTREAM":
                    imageName = "upstream-node-image";
                    break;

                case "DOWNSTREAM":
                    imageName = "downstream-node-image";
                    break;

                default:
                    throw new RuntimeException(
                            "UNSUPPORTED NODE TYPE"
                    );
            }

            // =====================================================
            // CREATE BASE COMMAND
            // =====================================================
            java.util.List<String> command
                    = new java.util.ArrayList<>();

            command.add("docker");
            command.add("run");
            command.add("-d");

            command.add("--name");
            command.add(containerName);

            command.add("--network");
            command.add("telecom-network");

            command.add("--ip");
            command.add(node.getNODE_IP());

            // =====================================================
            // MAIN PORT MAPPING
            // =====================================================
            command.add("-p");

            command.add(
                    node.getNODE_PORT()
                    + ":"
                    + internalPort
            );

            // =====================================================
            // FTP PASSIVE PORTS
            // =====================================================

            if ("FTP".equals(node.getNODE_PROTOCOL())) {
                command.add("-p");

                int baseHostPort = 20000 + (node.getNODE_PORT() * 100); 
                int endHostPort = baseHostPort + 10;

                command.add(baseHostPort + "-" + endHostPort + ":21100-21110");
            }

            // =====================================================
            // ENV VARIABLES
            // =====================================================
            command.add("-e");
            command.add(
                    "NODE_NAME="
                    + node.getNODE_NAME()
            );

            command.add("-e");
            command.add(
                    "PROTOCOL="
                    + node.getNODE_PROTOCOL()
            );

            command.add("-e");
            command.add(
                    "USERNAME="
                    + node.getNODE_USER_NAME()
            );

            command.add("-e");
            command.add(
                    "PASSWORD="
                    + node.getNODE_PASSWORD()
            );

            command.add("-e");
            command.add(
                    "NODE_PORT="
                    + internalPort
            );

            command.add("-e");
            command.add(
                    "NODE_IP="
                    + node.getNODE_IP()
            );

            command.add("-e");
            command.add(
                    "SOURCE_DIR=/home/"
                    + node.getNODE_USER_NAME()
                    + "/"
                    + node.getSOURCE_DIRECTORY()
            );

            command.add("-e");
            command.add(
                    "ARCHIVE_DIR=/home/"
                    + node.getNODE_USER_NAME()
                    + "/"
                    + node.getARCHIVE_DIRECTORY()
            );

            // =====================================================
            // GENERATOR ONLY FOR UPSTREAM
            // =====================================================
            if ("UPSTREAM".equals(node.getNODE_TYPE())) {
                command.add("-e");

                command.add(
                        "GENERATION_INTERVAL=10"
                );
            }

            command.add(imageName);

            ProcessBuilder pb
                    = new ProcessBuilder(command);

            Process process = pb.start();

            BufferedReader stdOut
                    = new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
                    );

            String line;

            while ((line = stdOut.readLine()) != null) {
                LOGGER.info(line);
            }

            BufferedReader stdErr
                    = new BufferedReader(
                            new InputStreamReader(
                                    process.getErrorStream()
                            )
                    );

            while ((line = stdErr.readLine()) != null) {
                LOGGER.severe(line);
            }

            int exitCode = process.waitFor();

            LOGGER.info(
                    "DOCKER EXIT CODE : "
                    + exitCode
            );

            return exitCode == 0;
        } catch (Exception ex) {
            LOGGER.log(
                    Level.SEVERE,
                    "CREATE NODE ERROR",
                    ex
            );
        }

        return false;
    }

    public static boolean START_CONTAINER(String containerName) {
        try {
            ProcessBuilder pb
                    = new ProcessBuilder(
                            "docker",
                            "start",
                            containerName
                    );

            pb.inheritIO();

            Process process = pb.start();

            int exitCode = process.waitFor();

            return exitCode == 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static boolean STOP_CONTAINER(String containerName) {
        try {
            ProcessBuilder pb
                    = new ProcessBuilder(
                            "docker",
                            "stop",
                            containerName
                    );

            pb.inheritIO();

            Process process = pb.start();

            int exitCode = process.waitFor();

            return exitCode == 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static boolean REMOVE_NODE_CONTAINER(String nodeName) {

        try {

            String containerName = FORMAT_CONTAINER_NAME(nodeName);

            LOGGER.info("REMOVING CONTAINER : " + containerName);

            // Check if exists first
            if (!IS_CONTAINER_EXISTS(containerName)) {

                LOGGER.warning("CONTAINER DOES NOT EXIST : " + containerName);

                return true;
            }

            return REMOVE_CONTAINER(containerName);

        } catch (Exception ex) {

            LOGGER.log(
                    Level.SEVERE,
                    "REMOVE NODE CONTAINER ERROR",
                    ex
            );
        }

        return false;
    }
}
