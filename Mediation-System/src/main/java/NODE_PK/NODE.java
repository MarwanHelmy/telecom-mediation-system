package NODE_PK;

import com.jcraft.jsch.*;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Vector;

public class NODE 
{

    //============= ATTRIBUTESS =========================
    
    private int NODE_ID;
    private String NODE_NAME;
    private String NODE_IP;
    private int NODE_PORT;
    private String NODE_USER_NAME;
    private String NODE_PASSWORD;
    private String NODE_AUTH;
    private NODE_PROTOCOL NODE_PROTOCOL;
    private NODE_TYPE NODE_TYPE;
    private String SOURCE_DIRECTORY;
    private String ARCHIVE_DIRECTORY;
    
    //============= GETTERS ============================
    
    public int getNODE_ID() {
    return NODE_ID;
    }

    public String getNODE_NAME() {
        return NODE_NAME;
    }

    public String getNODE_IP() {
        return NODE_IP;
    }

    public int getNODE_PORT() {
        return NODE_PORT;
    }

    public String getNODE_USER_NAME() {
        return NODE_USER_NAME;
    }

    public String getNODE_PASSWORD() {
        return NODE_PASSWORD;
    }

    public NODE_PROTOCOL getNODE_PROTOCOL() {
        return NODE_PROTOCOL;
    }

    public NODE_TYPE getNODE_TYPE() {
        return NODE_TYPE;
    }

    public String getSOURCE_DIRECTORY() {
        return SOURCE_DIRECTORY;
    }

    public String getARCHIVE_DIRECTORY() {
        return ARCHIVE_DIRECTORY;
    }
    
     //============= SETTERS ============================
    
    public void setNODE_ID(int NODE_ID) {
    this.NODE_ID = NODE_ID;
    }

    public void setNODE_NAME(String NODE_NAME) {
        this.NODE_NAME = NODE_NAME;
    }

    public void setNODE_IP(String NODE_IP) {
        this.NODE_IP = NODE_IP;
    }

    public void setNODE_PORT(int NODE_PORT) {
        this.NODE_PORT = NODE_PORT;
    }

    public void setNODE_USER_NAME(String NODE_USER_NAME) {
        this.NODE_USER_NAME = NODE_USER_NAME;
    }

    public void setNODE_PASSWORD(String NODE_PASSWORD) {
        this.NODE_PASSWORD = NODE_PASSWORD;
    }

    public void setNODE_PROTOCOL(NODE_PROTOCOL NODE_PROTOCOL) {
        this.NODE_PROTOCOL = NODE_PROTOCOL;
    }

    public void setNODE_TYPE(NODE_TYPE NODE_TYPE) {
        this.NODE_TYPE = NODE_TYPE;
    }

    public void setSOURCE_DIRECTORY(String SOURCE_DIRECTORY) {
        this.SOURCE_DIRECTORY = SOURCE_DIRECTORY;
    }

    public void setARCHIVE_DIRECTORY(String ARCHIVE_DIRECTORY) {
        this.ARCHIVE_DIRECTORY = ARCHIVE_DIRECTORY;
    }
    
    //============= CONNECTION ATTRIBUTESS =====================
    
    private Session session;
    private ChannelSftp sftp;
    private FTPClient ftpClient;

    //============= CONSTRUCTORS ===============================
    
    public NODE
    (
        int NODE_ID, 
        String NODE_NAME, 
        String NODE_IP, 
        int NODE_PORT,
        String NODE_USER_NAME,
        String NODE_PASSWORD,
        NODE_PROTOCOL NODE_PROTOCOL, 
        NODE_TYPE NODE_TYPE,
        String SOURCE_DIRECTORY,
        String ARCHIVE_DIRECTORY,
        String NODE_AUTH
    ) 
    {

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
        this.NODE_AUTH = NODE_AUTH;
    }

    //============= METHODS NODE ============================
    
    // CONNECT METHOD
   
    public boolean connect() {
        try {
            switch (NODE_PROTOCOL) {

                case SFTP:
                    JSch jsch = new JSch();

                    session = jsch.getSession(NODE_USER_NAME, NODE_IP, NODE_PORT);
                    session.setPassword(NODE_PASSWORD);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.connect();
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
                    break;
            }

            return true;

        } 
        catch (Exception ex) 
        {
            System.out.println("Connection Failed : " + ex.getMessage());
            return false;
        }
    }

    //═══════════════════════════════════════════════════════════════════════════════════════════
    
    // OPEN CHANNEL METHOD
    
    public boolean open_channel() {
        try {
            if (NODE_PROTOCOL == NODE_PROTOCOL.SFTP) 
            {
                Channel channel = session.openChannel("sftp");
                channel.connect();
                sftp = (ChannelSftp) channel;
            } 
            
            return true;

        } 
        catch (Exception ex) 
        {
            System.out.println("Open Channel Failed : " + ex.getMessage());
            return false;
        }
    }

    
    //═══════════════════════════════════════════════════════════════════════════════════════════
    
    // CHANGE DIRECTORY METHOD
    
    public boolean change_directory() {
        try 
        {
            switch (NODE_PROTOCOL) 
            {

                case SFTP:

                    sftp.cd(SOURCE_DIRECTORY);
                    break;

                case FTP:

                    boolean ok = ftpClient.changeWorkingDirectory(SOURCE_DIRECTORY);

                    if (!ok) {
                        System.out.println("FTP directory not found, fallback /");
                        ftpClient.changeWorkingDirectory("/");
                    }
                    break;
            }

            return true;

        } 
        catch (Exception ex) 
        {
            System.out.println("Directory Change Failed : " + ex.getMessage());
            return false;
        }
    }

    
    //═══════════════════════════════════════════════════════════════════════════════════════════
    
    // LIST FILES METHOD
   
    public String[] list_cdr_files() 
    {
        try 
        {
            ArrayList<String> filesList = new ArrayList<>();

            switch (NODE_PROTOCOL) 
            {

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

        } 
        catch (Exception ex) 
        {
            System.out.println("List Files Failed : " + ex.getMessage());
            return null;
        }
    }

    
    //═══════════════════════════════════════════════════════════════════════════════════════════
    
    // DOWNLOAD FILE METHOD
    
    public File download_file(String fileName) 
    {
        try 
        {

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
            return localFile;

        } 
        catch (Exception ex) 
        {
            System.out.println("Download Failed : " + ex.getMessage());
            return null;
        }
    }

   
    //═══════════════════════════════════════════════════════════════════════════════════════════
    
    // ARCHIVE FILE METHOD
    
    public boolean archive_file(String fileName) {

        try {

            switch (NODE_PROTOCOL) {

                case SFTP:
                
                sftp.cd("/");
                
                String sourcePath = SOURCE_DIRECTORY + "/" + fileName;
                String destPath = ARCHIVE_DIRECTORY + "/" + fileName;
                
                
                try 
                {
                    sftp.lstat(ARCHIVE_DIRECTORY);
                } catch (SftpException e) {
                   
                    sftp.mkdir(ARCHIVE_DIRECTORY);
                }
                
                // Try the rename
                try 
                {
                    sftp.rename(sourcePath, destPath);
                   
                } 
                catch (SftpException e) 
                {
                    try 
                    {
                        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                        sftp.get(sourcePath, baos);
                        
                        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
                        sftp.put(bais, destPath);
                        
                        sftp.rm(sourcePath);
                        
                        baos.close();
                        bais.close();
                          
                    }
                    catch (Exception ex)
                    {
                        return false;
                    }
                }
                
                sftp.cd(SOURCE_DIRECTORY);
                break;

                case FTP:

                    String ftpDest = "../archive/" + fileName;

                    boolean moved = ftpClient.rename(fileName, ftpDest);

                    if (!moved) {

                        System.out.println("FTP MOVE FAILED");
                        System.out.println("REPLY : " + ftpClient.getReplyString());

                        return false;
                    }

                    break;
            }
            return true;

        } catch (Exception ex) {

            System.out.println("Archive Failed : " + ex.getMessage());
            return false;
        }
    }
    
    //═══════════════════════════════════════════════════════════════════════════════════════════
   
    // DISCONNECT METHOD
   
    public void disconnect()
    {
        try 
        {

            if (sftp != null) sftp.disconnect();
            if (session != null) session.disconnect();

            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }

        } 
        catch (Exception ex)
        {
            System.out.println("Disconnect Failed : " + ex.getMessage());
        }
    }
}