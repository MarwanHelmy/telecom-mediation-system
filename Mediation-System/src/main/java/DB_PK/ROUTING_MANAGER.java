/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DB_PK;

import NODE_PK.NODE;
import NODE_PK.NODE_PROTOCOL;
import NODE_PK.NODE_TYPE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marwan
 */


public class ROUTING_MANAGER {

    public static List<NODE> get_target_downstream_nodes(int upstreamNodeId) {
        List<NODE> downstream_nodes = new ArrayList<>();
        
        try {
            Connection con = DB_Local.getConnection();
            
            String sql = "SELECT n.id, n.name, n.type, n.protocol, n.auth_type, " +
                         "n.username, n.password, n.ip, n.port, n.data_path, n.archive_path " +
                         "FROM nodes n " +
                         "JOIN routing_rules r ON n.id = r.destination_node_id " +
                         "WHERE r.source_node_id = ? AND r.is_active = true " +
                         "AND n.isactive = true AND n.isdeleted = false";
                         
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, upstreamNodeId);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                NODE node = new NODE(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("ip"),
                    rs.getInt("port"),
                    rs.getString("username"),
                    rs.getString("password"),
                    NODE_PROTOCOL.valueOf(rs.getString("protocol").toUpperCase()),
                    NODE_TYPE.valueOf(rs.getString("type").toUpperCase()),
                    rs.getString("data_path"),
                    rs.getString("archive_path"),
                    rs.getString("auth_type")
                );
                downstream_nodes.add(node);
            }
        } catch (Exception ex) {
            System.out.println("ERROR FETCHING ROUTING RULES : " + ex.getMessage());
        }
        
        return downstream_nodes;
    }
}