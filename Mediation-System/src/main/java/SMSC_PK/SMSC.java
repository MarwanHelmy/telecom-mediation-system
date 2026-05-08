package SMSC_PK;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class SMSC
{
    //================= SMSC Attributes ====================

    private int SMSC_ID;
    private String SMSC_IP;
    private int SMSC_PORT;
    private String SMSC_USER_NAME;
    private String SMSC_PASSWORD;
    private Session session;
    private ChannelSftp sftp;

    //================= SMSC Constructor ====================

    public SMSC
    (
        int SMSC_ID,
        String SMSC_IP,
        int SMSC_PORT,
        String SMSC_USER_NAME,
        String SMSC_PASSWORD
    )
    {
        this.SMSC_ID = SMSC_ID;
        this.SMSC_IP = SMSC_IP;
        this.SMSC_PORT = SMSC_PORT;
        this.SMSC_USER_NAME = SMSC_USER_NAME;
        this.SMSC_PASSWORD = SMSC_PASSWORD;
    }

    //================= SMSC Setter ====================

    public void setSMSC_ID(int SMSC_ID)
    {
        this.SMSC_ID = SMSC_ID;
    }

    public void setSMSC_IP(String SMSC_IP)
    {
        this.SMSC_IP = SMSC_IP;
    }

    public void setSMSC_PORT(int SMSC_PORT)
    {
        this.SMSC_PORT = SMSC_PORT;
    }

    public void setSMSC_USER_NAME(String SMSC_USER_NAME)
    {
        this.SMSC_USER_NAME = SMSC_USER_NAME;
    }

    public void setSMSC_PASSWORD(String SMSC_PASSWORD)
    {
        this.SMSC_PASSWORD = SMSC_PASSWORD;
    }

    //================= SMSC Getter ====================

    public int getSMSC_ID()
    {
        return SMSC_ID;
    }

    public String getSMSC_IP()
    {
        return SMSC_IP;
    }

    public int getSMSC_PORT()
    {
        return SMSC_PORT;
    }

    public String getSMSC_USER_NAME()
    {
        return SMSC_USER_NAME;
    }

    public String getSMSC_PASSWORD()
    {
        return SMSC_PASSWORD;
    }

    //================= SMSC Methods ====================

    //===================================================
    //===========     Connect To SMSC ===================
    //===================================================

    public boolean connect()
    {
        try
        {
            JSch jsch = new JSch();

            session = jsch.getSession
            (
                    SMSC_USER_NAME,
                    SMSC_IP,
                    SMSC_PORT
            );

            session.setPassword(SMSC_PASSWORD);

            session.setConfig( "StrictHostKeyChecking","no");

            session.connect();

            System.out.println("\nConnected To SMSC Node...");

            return true;
        }
        catch (JSchException ex)
        {
            System.out.println("\nConnection Failed : "+ ex.getMessage());

            return false;
        }
    }

    //===================================================
    //===========      Open SFTP Channel       ==========
    //===================================================

    public boolean open_sftp_channel()
    {
        try
        {
            Channel channel = session.openChannel("sftp");

            channel.connect();

            sftp = (ChannelSftp) channel;

            System.out.println("\nSFTP Channel Opened...");

            return true;
        }
        catch (JSchException ex)
        {
            System.out.println("\nFailed To Open SFTP Channel : " + ex.getMessage());

            return false;
        }
    }

    //===================================================
    //=====      Change CDR Dir in SMSC Node       ======
    //===================================================

    public boolean cd_cdr_dir()
    {
        try
        {
            sftp.cd("cdr");

            System.out.println("\nChanged To CDR Directory...");

            System.out.println("Current Directory : "+ sftp.pwd());

            return true;
        }
        catch (Exception ex)
        {
            System.out.println("\nFailed To Change Directory : " + ex.getMessage());

            return false;
        }
    }

    //===================================================
    //===========      Read CDR Files          ==========
    //===================================================

    public void read_cdr_files()
    {
        try
        {
            Vector<ChannelSftp.LsEntry> files =sftp.ls("*");

            for (ChannelSftp.LsEntry file : files)
            {
                String fileName =file.getFilename();

                // Skip Directories
                if (file.getAttrs().isDir())
                {
                    continue;
                }

                System.out.println("\n=================================");
                System.out.println("Reading File : " + fileName);
                System.out.println("=================================");

                InputStream inputStream =sftp.get(fileName);

                BufferedReader reader =new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null)
                {
                    System.out.println(line);
                }

                reader.close();

                // Move File To Archive
                sftp.rename(fileName,"../archive/" + fileName);

                System.out.println("\nFile Archived : "+ fileName);
            }
        }
        catch (Exception ex)
        {
            System.out.println("\nError Reading Files : "+ ex.getMessage());
        }
    }

    //===================================================
    //===========      Disconnect Session      ==========
    //===================================================

    public void disconnect()
    {
        if (sftp != null)
        {
            sftp.disconnect();
        }

        if (session != null)
        {
            session.disconnect();
        }

        System.out.println("\nDisconnected...");
    }
}