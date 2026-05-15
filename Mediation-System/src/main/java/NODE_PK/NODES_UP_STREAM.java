package NODE_PK;

import DB_PK.DB_Local;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NODES_UP_STREAM 
{
    public static List<NODE> GET_NODES()
    {
        List<NODE> up_stream_nodes = new ArrayList<>();
        try
        {
            String sql = "select * from get_upstream_nodes()";
            Connection con = DB_Local.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                int node_id = rs.getInt("id");
                String node_name = rs.getString("name");
                String typeStr = rs.getString("type");
                NODE_TYPE node_type = NODE_TYPE.valueOf(typeStr.toUpperCase());
                String protocolStr = rs.getString("protocol");
                NODE_PROTOCOL node_protocol = NODE_PROTOCOL.valueOf(protocolStr.toUpperCase());
                String node_auth_type = rs.getString("auth_type");
                String node_username = rs.getString("username");
                String node_password = rs.getString("password");
                String node_IP = rs.getString("ip");
                int node_port = rs.getInt("port");
                String node_data_path = rs.getString("data_path");
                String node_archive_path = rs.getString("archive_path");
                
                NODE node = new NODE
                (
                        node_id,
                        node_name,
                        node_IP,
                        node_port,
                        node_username,
                        node_password,
                        node_protocol,
                        node_type,
                        node_data_path,
                        node_archive_path,
                        node_auth_type
                );
                
                up_stream_nodes.add(node);
            }
        }
        catch(Exception ex)
        {
            System.out.println("Error : " + ex.getMessage());
        }
        return up_stream_nodes;
    }
}
