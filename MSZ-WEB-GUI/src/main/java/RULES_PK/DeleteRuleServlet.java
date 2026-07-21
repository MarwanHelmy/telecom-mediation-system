package RULES_PK;

import java.io.IOException;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DeleteRuleServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        JsonObject jsonResponse = new JsonObject();
        
        try {
            
            String ruleIdParam = request.getParameter("ruleId");
            
            if (ruleIdParam == null || ruleIdParam.isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Rule ID is required");
                jsonResponse.addProperty("errorType", "MISSING_ID");
                response.getWriter().write(jsonResponse.toString());
                return;
            }
            
            int ruleId = Integer.parseInt(ruleIdParam);
            
            boolean deleted = RULE.deleteRule(ruleId);
            
            
            
            if (deleted) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Rule deleted successfully");
                jsonResponse.addProperty("deletedRuleId", ruleId);
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Rule not found with ID: " + ruleId);
                jsonResponse.addProperty("errorType", "RULE_NOT_FOUND");
            }
            
        } catch (NumberFormatException e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid rule ID format");
            jsonResponse.addProperty("errorType", "INVALID_ID_FORMAT");
            
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Database error: " + e.getMessage());
            jsonResponse.addProperty("errorType", "DATABASE_ERROR");
        }
        
        response.getWriter().write(jsonResponse.toString());
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect GET requests to POST for consistency
        doPost(request, response);
    }
}