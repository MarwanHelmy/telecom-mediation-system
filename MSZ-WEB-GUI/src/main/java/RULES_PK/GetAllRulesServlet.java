package RULES_PK;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import DB_PK.DB;

public class GetAllRulesServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        List<Rule> rules = new ArrayList<>();
        
        String sql = "SELECT id, source_node_id, destination_node_id, is_active, created_at FROM routing_rules ORDER BY id";
        
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Rule rule = new Rule();
                rule.setId(rs.getInt("id"));
                rule.setSource_node_id(rs.getInt("source_node_id"));
                rule.setDestination_node_id(rs.getInt("destination_node_id"));
                rule.setIs_active(rs.getBoolean("is_active"));
                rule.setCreated_at(rs.getTimestamp("created_at"));
                rules.add(rule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Gson gson = new Gson();
        String json = gson.toJson(rules);
        response.getWriter().write(json);
    }
    
    class Rule {
        private int id;
        private int source_node_id;
        private int destination_node_id;
        private boolean is_active;
        private java.sql.Timestamp created_at;
        
        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getSource_node_id() { return source_node_id; }
        public void setSource_node_id(int source_node_id) { this.source_node_id = source_node_id; }
        public int getDestination_node_id() { return destination_node_id; }
        public void setDestination_node_id(int destination_node_id) { this.destination_node_id = destination_node_id; }
        public boolean getIs_active() { return is_active; }
        public void setIs_active(boolean is_active) { this.is_active = is_active; }
        public java.sql.Timestamp getCreated_at() { return created_at; }
        public void setCreated_at(java.sql.Timestamp created_at) { this.created_at = created_at; }
    }
}