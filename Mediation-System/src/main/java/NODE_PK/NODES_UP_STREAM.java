package NODE_PK;

public class NODES_UP_STREAM 
{
    public static NODE[] GET_NODES()
    {
        NODE mscNode = new NODE(
                1,
                "MSC_NODE",
                "172.30.0.10",
                21,
                "msc_user",
                "pass123",
                NODE_PROTOCOL.FTP,
                NODE_TYPE.UPSTREAM,
                "ftp/cdrs",
                "ftp/archive"
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
                "cdrs",
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
                "ftp/cdrs",
                "ftp/archive"
        );
        NODE[] nodes = {mscNode, smscNode, pgwNode};
        
        return nodes;
    }
}
