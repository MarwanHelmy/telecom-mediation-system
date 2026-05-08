package Main_PK;

import SMSC_PK.SMSC;

public class MediationSystem
{
    public static void main(String[] args)
    {
        SMSC smsc = new SMSC
        (
                1,
                "172.30.0.30",
                22,
                "smsc",
                "smsc123"
        );

        while (true)
        {
            try
            {
                if (smsc.connect())
                {
                    if (smsc.open_sftp_channel())
                    {
                        if (smsc.cd_cdr_dir())
                        {
                            smsc.read_cdr_files();
                        }
                    }
                }

                smsc.disconnect();

                System.out.println("\nWaiting 2 Seconds...");

                Thread.sleep(2);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}