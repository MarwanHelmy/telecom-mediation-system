package RULES_PK;

import java.io.IOException;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SaveRuleServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        JsonObject jsonResponse = new JsonObject();
        
        try {
            String ruleIdParam = request.getParameter("ruleId");
            String sourceNodeIdParam = request.getParameter("sourceNodeId");
            String destinationNodeIdParam = request.getParameter("destinationNodeId");
            String isActiveParam = request.getParameter("isActive");
            
            // التحقق من صحة المعاملات
            if (sourceNodeIdParam == null || sourceNodeIdParam.isEmpty() ||
                destinationNodeIdParam == null || destinationNodeIdParam.isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Source and Destination nodes are required");
                jsonResponse.addProperty("errorType", "MISSING_PARAMETERS");
                response.getWriter().write(jsonResponse.toString());
                return;
            }
            
            int sourceNodeId = Integer.parseInt(sourceNodeIdParam);
            int destinationNodeId = Integer.parseInt(destinationNodeIdParam);
            boolean isActive = Boolean.parseBoolean(isActiveParam);
            
            boolean success;
            boolean isEdit = (ruleIdParam != null && !ruleIdParam.isEmpty());
            
            if (isEdit) {
                int ruleId = Integer.parseInt(ruleIdParam);
                
                // ✅ التحقق من وجود قاعدة مكررة مع تجاهل القاعدة الحالية
                boolean duplicateExists = RULE.checkDuplicateRuleExcludeId(sourceNodeId, destinationNodeId, ruleId);
                
                if (duplicateExists) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("errorType", "DUPLICATE_RULE");
                    jsonResponse.addProperty("message", "Rule already exists! Source → Destination combination is already configured.");
                    response.getWriter().write(jsonResponse.toString());
                    return;
                }
                
                // تحديث القاعدة
                success = RULE.updateRule(ruleId, sourceNodeId, destinationNodeId, isActive);
                
                if (success) {
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Rule updated successfully");
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Rule not found with ID: " + ruleId);
                    jsonResponse.addProperty("errorType", "RULE_NOT_FOUND");
                }
                
            } else {
                
                boolean duplicateExists = RULE.checkDuplicateRule(sourceNodeId, destinationNodeId);
                
                if (duplicateExists) {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("errorType", "DUPLICATE_RULE");
                    jsonResponse.addProperty("message", "Rule already exists! Source → Destination combination is already configured.");
                    response.getWriter().write(jsonResponse.toString());
                    return;
                }
                
               
                success = RULE.insertRule(sourceNodeId, destinationNodeId, isActive);
                
                if (success) {
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Rule added successfully");
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Failed to add rule");
                    jsonResponse.addProperty("errorType", "INSERT_FAILED");
                }
            }
            
        } catch (NumberFormatException e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid parameter format");
            jsonResponse.addProperty("errorType", "INVALID_FORMAT");
            
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Database error: " + e.getMessage());
            jsonResponse.addProperty("errorType", "DATABASE_ERROR");
        }
        
        response.getWriter().write(jsonResponse.toString());
    }
}