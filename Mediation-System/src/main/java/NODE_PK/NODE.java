package NODE_PK;

import com.jcraft.jsch.*;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

public class NODE {

    private int NODE_ID;
    private String NODE_NAME;
    private String NODE_IP;
    private int NODE_PORT;
    private String NODE_USER_NAME;
    private String NODE_PASSWORD;
    private NODE_PROTOCOL NODE_PROTOCOL;
    private NODE_TYPE NODE_TYPE;

    private String SOURCE_DIRECTORY;
    private String ARCHIVE_DIRECTORY;

    private Session session;
    private ChannelSftp sftp;
    private FTPClient ftpClient;

    public NODE(int NODE_ID, String NODE_NAME, String NODE_IP, int NODE_PORT,
                String NODE_USER_NAME, String NODE_PASSWORD,
                NODE_PROTOCOL NODE_PROTOCOL, NODE_TYPE NODE_TYPE,
                String SOURCE_DIRECTORY, String ARCHIVE_DIRECTORY) {

        this.NODE_ID = NODE_ID;
        this.NODE_NAME = NODE_NAME;
        this.NODE_IP = NODE_IP;
        this.NODE_PORT = NODE_PORT;
        this.NODE_USER_NAME = NODE_USER_NAME;
        this.NODE_PASSWORD = NODE_PASSWORD;
        this.NODE_PROTOCOL = NODE_PROTOCOL;
        this.NODE_TYPE = NODE_TYPE;
        this.SOURCE_DIRECTORY = SOURCE_DIRECTORY;
        this.ARCHIVE_DIRECTORY = ARCHIVE_DIRECTORY;
    }

    public String getNODE_NAME() {
        return NODE_NAME;
    }

    //====================================================
    // CONNECT
    //====================================================
    public boolean connect() {
        try {
            switch (NODE_PROTOCOL) {

                case SFTP:
                case SCP:

                    JSch jsch = new JSch();

                    session = jsch.getSession(NODE_USER_NAME, NODE_IP, NODE_PORT);
                    session.setPassword(NODE_PASSWORD);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.connect();

                    System.out.println("Connected To NODE : " + NODE_NAME);
                    break;

                case FTP:

                    ftpClient = new FTPClient();
                    ftpClient.connect(NODE_IP, NODE_PORT);

                    if (!ftpClient.login(NODE_USER_NAME, NODE_PASSWORD)) {
                        System.out.println("FTP Login Failed");
                        return false;
                    }

                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                    System.out.println("Connected To NODE : " + NODE_NAME);
                    break;
            }

            return true;

        } catch (Exception ex) {
            System.out.println("Connection Failed : " + ex.getMessage());
            return false;
        }
    }

    //====================================================
    // OPEN CHANNEL
    //====================================================
    public boolean open_channel() {
        try {
            if (NODE_PROTOCOL == NODE_PROTOCOL.SFTP || NODE_PROTOCOL == NODE_PROTOCOL.SCP) {

                Channel channel = session.openChannel("sftp");
                channel.connect();
                sftp = (ChannelSftp) channel;

                System.out.println("SFTP Channel Opened");
            } else {
                System.out.println("FTP Connection Ready");
            }

            return true;

        } catch (Exception ex) {
            System.out.println("Open Channel Failed : " + ex.getMessage());
            return false;
        }
    }

    //====================================================
    // CHANGE DIRECTORY
    //====================================================
    public boolean change_directory() {
        try {
            switch (NODE_PROTOCOL) {

                case SFTP:

                    sftp.cd(SOURCE_DIRECTORY);
                    System.out.println("SFTP Dir : " + sftp.pwd());
                    break;

                case FTP:

                    System.out.println("FTP Before : " + ftpClient.printWorkingDirectory());

                    boolean ok = ftpClient.changeWorkingDirectory(SOURCE_DIRECTORY);

                    if (!ok) {
                        System.out.println("FTP directory not found, fallback /");
                        ftpClient.changeWorkingDirectory("/");
                    }

                    System.out.println("FTP After : " + ftpClient.printWorkingDirectory());
                    break;
            }

            return true;

        } catch (Exception ex) {
            System.out.println("Directory Change Failed : " + ex.getMessage());
            return false;
        }
    }

    //====================================================
    // LIST FILES
    //====================================================
    public String[] list_cdr_files() {
        try {
            ArrayList<String> filesList = new ArrayList<>();

            switch (NODE_PROTOCOL) {

                case SFTP:

                    Vector<ChannelSftp.LsEntry> files = sftp.ls(".");

                    for (ChannelSftp.LsEntry f : files) {
                        if (!f.getAttrs().isDir()) {
                            filesList.add(f.getFilename());
                        }
                    }
                    break;

                case FTP:

                    FTPFile[] ftpFiles = ftpClient.listFiles();

                    for (FTPFile f : ftpFiles) {
                        if (f.isFile()) {
                            filesList.add(f.getName());
                        }
                    }
                    break;
            }

            return filesList.toArray(new String[0]);

        } catch (Exception ex) {
            System.out.println("List Files Failed : " + ex.getMessage());
            return null;
        }
    }

    //====================================================
    // DOWNLOAD FILE
    //====================================================
    public File download_file(String fileName) {
        try {

            File dir = new File("collection-cdr/new-cdr/" + NODE_NAME);
            if (!dir.exists()) dir.mkdirs();

            File localFile = new File(dir, fileName);

            switch (NODE_PROTOCOL) {

                case SFTP:
                    sftp.get(fileName, localFile.getAbsolutePath());
                    break;

                case FTP:

                    FileOutputStream out = new FileOutputStream(localFile);

                    boolean ok = ftpClient.retrieveFile(fileName, out);

                    out.close();

                    if (!ok) return null;
                    break;
            }

            System.out.println("Downloaded : " + fileName);
            return localFile;

        } catch (Exception ex) {
            System.out.println("Download Failed : " + ex.getMessage());
            return null;
        }
    }

    //====================================================
    // ARCHIVE FILE
    //====================================================
    public boolean archive_file(String fileName) {
        try {

            String src = fileName;
            String dest = ARCHIVE_DIRECTORY + "/" + fileName;

            switch (NODE_PROTOCOL) {

                case SFTP:
                    sftp.rename(src, dest);
                    break;

                case FTP:
                    ftpClient.rename(src, dest);
                    break;
            }

            System.out.println("Archived : " + fileName);
            return true;

        } catch (Exception ex) {
            System.out.println("Archive Failed : " + ex.getMessage());
            return false;
        }
    }

    //====================================================
    // DISCONNECT
    //====================================================
    public void disconnect() {
        try {

            if (sftp != null) sftp.disconnect();
            if (session != null) session.disconnect();

            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }

            System.out.println("Disconnected : " + NODE_NAME);

        } catch (Exception ex) {
            System.out.println("Disconnect Failed : " + ex.getMessage());
        }
    }
}