package INFO_PK;

import DB_PK.DB_Local;
import NODE_PK.NODE;
import RULE_PK.RULE;

import java.io.IOException;
import java.sql.Connection;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GetInfoServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Connection con = null;

        try {

            con = DB_Local.getConnection();

            int activeNodes
                    = NODE.GetActiveNodes(con);
            int activeRules
                    = RULE.GetActiveRulesCount(con);

            String json
                    = "{"
                    + "\"success\": true,"
                    + "\"activeNodes\": " + activeNodes + ","
                    + "\"ruleCount\": " + activeRules
                    + "}";

            response.getWriter().write(json);

        } catch (Exception ex) {

            ex.printStackTrace();

            response.getWriter().write(
                    "{"
                    + "\"success\": false,"
                    + "\"message\": \"" + escapeJson(ex.getMessage()) + "\""
                    + "}"
            );

        } finally {

            try {

                if (con != null) {
                    con.close();
                }

            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }
    }

    private String escapeJson(String str) {

        if (str == null) {
            return "";
        }

        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
