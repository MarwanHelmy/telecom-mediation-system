package Main_PK;

import NODE_PK.NODE;
import NODE_PK.NODE_PROTOCOL;
import NODE_PK.NODE_TYPE;

import java.io.File;

public class MediationSystem {

    public static void main(String[] args) {

        NODE mscNode = new NODE(
                1,
                "MSC_NODE",
                "172.30.0.10",
                21,
                "msc_user",
                "pass123",
                NODE_PROTOCOL.FTP,
                NODE_TYPE.UPSTREAM,
                "/home/msc_user/ftp/cdrs",
                "archive"
        );

        NODE smscNode = new NODE(
                2,
                "SMSC_NODE",
                "172.30.0.30",
                22,
                "smsc",
                "smsc123",
                NODE_PROTOCOL.SFTP,
                NODE_TYPE.UPSTREAM,
                "cdr",
                "archive"
        );

        NODE pgwNode = new NODE(
                3,
                "PGW_NODE",
                "172.30.0.50",
                2323,
                "pgw_user",
                "pass789",
                NODE_PROTOCOL.FTP,
                NODE_TYPE.UPSTREAM,
                "/home/msc_user/ftp/cdrs",
                "archive"
        );

        NODE[] nodes = {mscNode, smscNode, pgwNode};

        while (true) {

            try {

                System.out.println("\n=====================================");
                System.out.println("START MEDIATION CYCLE");
                System.out.println("=====================================");

                for (NODE node : nodes) {

                    System.out.println("\nNODE : " + node.getNODE_NAME());

                    if (!node.connect()) continue;
                    if (!node.open_channel()) { node.disconnect(); continue; }
                    if (!node.change_directory()) { node.disconnect(); continue; }

                    String[] files = node.list_cdr_files();

                    if (files == null || files.length == 0) {
                        System.out.println("NO FILES");
                        node.disconnect();
                        continue;
                    }

                    for (String file : files) {

                        System.out.println("FILE : " + file);

                        File downloaded = node.download_file(file);

                        if (downloaded == null) {
                            System.out.println("DOWNLOAD FAILED");
                            continue;
                        }

                        System.out.println("FILTER OK : " + file);
                        System.out.println("READY TO SEND : " + file);

                        node.archive_file(file);
                    }

                    node.disconnect();
                }

                System.out.println("\nWAITING 10 SECONDS...");
                Thread.sleep(10000);

            } catch (Exception ex) {
                System.out.println("MAIN ERROR : " + ex.getMessage());
            }
        }
    }
}