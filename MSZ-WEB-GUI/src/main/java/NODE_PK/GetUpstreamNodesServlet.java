package NODE_PK;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import DB_PK.DB;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GetUpstreamNodesServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        List<Node> upstreamNodes = new ArrayList<>();
        
        String sql = "SELECT id, name, ip, port FROM nodes WHERE type = 'UPSTREAM' AND isdeleted = false ORDER BY name";
        
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Node node = new Node();
                node.setId(rs.getInt("id"));
                node.setName(rs.getString("name"));
                node.setIp(rs.getString("ip"));
                node.setPort(rs.getInt("port"));
                upstreamNodes.add(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Gson gson = new Gson();
        String json = gson.toJson(upstreamNodes);
        response.getWriter().write(json);
    }
    
    class Node {
        private int id;
        private String name;
        private String ip;
        private int port;
        
        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
    }
}