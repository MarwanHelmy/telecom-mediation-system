package NODE_PK;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NODE {
    //============= ATTRIBUTESS =========================

    private int NODE_ID;
    private String NODE_NAME;
    private String NODE_IP;
    private int NODE_PORT;
    private String NODE_USER_NAME;
    private String NODE_PASSWORD;
    public String NODE_AUTH;
    private String NODE_PROTOCOL;
    private String NODE_TYPE;
    private String SOURCE_DIRECTORY;
    private String ARCHIVE_DIRECTORY;
    private Boolean NODE_STATUS;
    private String NODE_SUBTYPE;

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

    public String getNODE_PROTOCOL() {
        return NODE_PROTOCOL;
    }

    public String getNODE_TYPE() {
        return NODE_TYPE;
    }

    public String getNODE_SUBTYPE() {
        return NODE_SUBTYPE;
    }

    public String getSOURCE_DIRECTORY() {
        return SOURCE_DIRECTORY;
    }

    public String getARCHIVE_DIRECTORY() {
        return ARCHIVE_DIRECTORY;
    }

    public boolean getNODE_STATUS() {
        return this.NODE_STATUS;
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

    public void setNODE_PROTOCOL(String NODE_PROTOCOL) {
        this.NODE_PROTOCOL = NODE_PROTOCOL;
    }

    public void setNODE_TYPE(String NODE_TYPE) {
        this.NODE_TYPE = NODE_TYPE;
    }

    public void setNODE_SUBTYPE(String NODE_SUBTYPE) {
        this.NODE_SUBTYPE = NODE_SUBTYPE;
    }

    public void setSOURCE_DIRECTORY(String SOURCE_DIRECTORY) {
        this.SOURCE_DIRECTORY = SOURCE_DIRECTORY;
    }

    public void setARCHIVE_DIRECTORY(String ARCHIVE_DIRECTORY) {
        this.ARCHIVE_DIRECTORY = ARCHIVE_DIRECTORY;
    }

    public void setNODE_STATUS(boolean NODE_STATUS) {
        this.NODE_STATUS = NODE_STATUS;
    }

    //============= CONSTRUCTORS ===============================
    public NODE() {
    }

    public NODE(
            int NODE_ID,
            String NODE_NAME,
            String NODE_IP,
            int NODE_PORT,
            String NODE_USER_NAME,
            String NODE_PASSWORD,
            String NODE_PROTOCOL,
            String NODE_TYPE,
            String SOURCE_DIRECTORY,
            String ARCHIVE_DIRECTORY,
            String NODE_AUTH,
            Boolean NODE_STATUS
    ) {

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
        this.NODE_STATUS = NODE_STATUS;
    }

    public static List<NODE> GET_ALL_NODES(Connection con) {
        List<NODE> nodes = new ArrayList<>();
        try {
            String sql = "select * from get_all_nodes()";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int node_id = rs.getInt("id");
                String node_name = rs.getString("name");
                String node_type = rs.getString("type");
                String node_protocol = rs.getString("protocol");
                String node_auth_type = rs.getString("auth_type");
                String node_username = rs.getString("username");
                String node_password = rs.getString("password");
                String node_IP = rs.getString("ip");
                int node_port = rs.getInt("port");
                String node_data_path = rs.getString("data_path");
                String node_archive_path = rs.getString("archive_path");
                Boolean node_status = rs.getBoolean("isactive");
                NODE node = new NODE(
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
                        node_auth_type,
                        node_status
                );

                node.setNODE_SUBTYPE(rs.getString("subtype"));

                nodes.add(node);
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            System.out.println("Error in GET_ALL_NODES: " + ex.getMessage());
        }
        return nodes;
    }

    public static int Add_Node(NODE node, Connection con) {
        int node_id = 0;

        try {
            String sql = "SELECT add_node(?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, node.getNODE_NAME());
            ps.setString(2, node.getNODE_TYPE());
            ps.setString(3, node.getNODE_PROTOCOL());
            ps.setString(4, "password");
            ps.setString(5, node.getNODE_USER_NAME());
            ps.setString(6, node.getNODE_PASSWORD());
            ps.setString(7, node.getNODE_IP());
            ps.setInt(8, node.getNODE_PORT());
            ps.setString(9, node.getSOURCE_DIRECTORY());
            ps.setString(10, node.getARCHIVE_DIRECTORY());
            ps.setBoolean(11, node.NODE_STATUS);
            ps.setString(12, node.getNODE_SUBTYPE());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                node_id = rs.getInt(1);
            }

            rs.close();
            ps.close();
        } catch (Exception ex) {
            System.out.println("Error in Add_Node: " + ex.getMessage());
            ex.printStackTrace();
        }

        return node_id;
    }

    public static boolean IS_NODE_IP_EXISTS(String ip, Connection con) {
        boolean exists = false;

        try {
            String sql = "SELECT is_node_ip_exists(?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ip);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                exists = rs.getBoolean(1);
            }

            rs.close();
            ps.close();
        } catch (Exception ex) {
            System.out.println("Error in IS_NODE_IP_EXISTS: " + ex.getMessage());
        }

        return exists;
    }

    public static NODE GET_NODE_BY_IP(String ip, Connection con) {
        NODE node = null;
        try {
            String sql = "SELECT * FROM get_node_by_ip(?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ip);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                node = new NODE();
                node.setNODE_ID(rs.getInt("id"));
                node.setNODE_NAME(rs.getString("name"));
                node.setNODE_IP(rs.getString("ip"));
                node.setNODE_PORT(rs.getInt("port"));
                node.setNODE_TYPE(rs.getString("type"));
                node.setNODE_PROTOCOL(rs.getString("protocol"));
                node.setNODE_USER_NAME(rs.getString("username"));
                node.setNODE_PASSWORD(rs.getString("password"));
                node.setNODE_STATUS(rs.getBoolean("isactive"));
                node.setSOURCE_DIRECTORY(rs.getString("data_path"));
                node.setARCHIVE_DIRECTORY(rs.getString("archive_path"));
                node.NODE_AUTH = rs.getString("auth_type");
                node.setNODE_SUBTYPE(rs.getString("subtype"));
            }

            rs.close();
            ps.close();
        } catch (Exception ex) {
            System.out.println("Error in GET_NODE_BY_IP: " + ex.getMessage());
        }
        return node;
    }

    public static boolean UPDATE_NODE_BY_ID(NODE node, Connection con) {
        boolean updated = false;

        try {
            String sql = "SELECT update_node_by_id(?,?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, node.getNODE_ID());
            ps.setString(2, node.getNODE_NAME());
            ps.setString(3, node.getNODE_TYPE());
            ps.setString(4, node.getNODE_PROTOCOL());
            ps.setString(5, node.NODE_AUTH);
            ps.setString(6, node.getNODE_USER_NAME());
            ps.setString(7, node.getNODE_PASSWORD());
            ps.setString(8, node.getNODE_IP());
            ps.setInt(9, node.getNODE_PORT());
            ps.setString(10, node.getSOURCE_DIRECTORY());
            ps.setString(11, node.getARCHIVE_DIRECTORY());
            ps.setBoolean(12, node.getNODE_STATUS());
            ps.setString(13, node.getNODE_SUBTYPE());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                updated = rs.getBoolean(1);
            }

            rs.close();
            ps.close();
        } catch (Exception ex) {
            System.out.println("Error in UPDATE_NODE_BY_ID: " + ex.getMessage());
            ex.printStackTrace();
        }

        return updated;
    }

    public static String deleteNodeById(int nodeId, Connection con) {

        String deletedNodeName = null;

        try {

            String sql = "SELECT delete_node_by_id(?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, nodeId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                deletedNodeName = rs.getString(1);
            }

            rs.close();
            ps.close();

        } catch (Exception ex) {

            System.out.println("Error in deleteNodeById: " + ex.getMessage());

            ex.printStackTrace();
        }

        return deletedNodeName;
    }

    public static boolean UPDATE_NODE_STATUS(
            int nodeId, Connection con,
            boolean status) {
        try {

            CallableStatement cs
                    = con.prepareCall(
                            "SELECT UPDATE_NODE_STATUS(?, ?)"
                    );

            cs.setInt(1, nodeId);

            cs.setBoolean(2, status);

            ResultSet rs
                    = cs.executeQuery();

            boolean result = false;

            if (rs.next()) {
                result = rs.getBoolean(1);
            }

            rs.close();

            cs.close();

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static NODE GET_NODE_BY_ID(int nodeId, Connection con) {
        try {

            CallableStatement cs
                    = con.prepareCall(
                            "SELECT * FROM GET_NODE_BY_ID(?)"
                    );

            cs.setInt(1, nodeId);

            ResultSet rs
                    = cs.executeQuery();

            if (rs.next()) {
                NODE node = new NODE();

                node.setNODE_ID(
                        rs.getInt("id")
                );

                node.setNODE_NAME(
                        rs.getString("name")
                );

                node.setNODE_TYPE(
                        rs.getString("type")
                );

                node.setNODE_PROTOCOL(
                        rs.getString("protocol")
                );

                node.NODE_AUTH
                        = rs.getString("auth_type");

                node.setNODE_USER_NAME(
                        rs.getString("username")
                );

                node.setNODE_PASSWORD(
                        rs.getString("password")
                );

                node.setNODE_IP(
                        rs.getString("ip")
                );

                node.setNODE_PORT(
                        rs.getInt("port")
                );

                node.setSOURCE_DIRECTORY(
                        rs.getString("data_path")
                );

                node.setARCHIVE_DIRECTORY(
                        rs.getString("archive_path")
                );

                node.setNODE_STATUS(
                        rs.getBoolean("isactive")
                );

                node.setNODE_SUBTYPE(
                        rs.getString("subtype")
                );

                rs.close();

                cs.close();

                return node;
            }

            rs.close();

            cs.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static int GetActiveNodes(Connection con) {

        int activeNodesCount = 0;

        try {

            String sql = "SELECT get_active_nodes_count()";

            PreparedStatement ps
                    = con.prepareStatement(sql);

            ResultSet rs
                    = ps.executeQuery();

            if (rs.next()) {

                activeNodesCount = rs.getInt(1);
            }

            rs.close();
            ps.close();

        } catch (Exception ex) {

            System.out.println(
                    "Error in GetActiveNodes : "
                    + ex.getMessage()
            );

            ex.printStackTrace();
        }

        return activeNodesCount;
    }
}
