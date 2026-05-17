package NODE_PK;

import DB_PK.DB_Local;
import DOCKER_PK.DOCKER_MANAGER;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;

public class DeleteNodeServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Connection con = null;

        try {

            con = DB_Local.getConnection();

            String nodeIdParam
                    = request.getParameter("nodeId");

            // =====================================================
            // VALIDATION
            // =====================================================
            if (nodeIdParam == null
                    || nodeIdParam.isEmpty()) {

                response.getWriter().write(
                        "{\"success\": false, "
                        + "\"message\": \"Node ID is required\"}"
                );

                return;
            }

            int nodeId
                    = Integer.parseInt(nodeIdParam);

            // =====================================================
            // GET NODE
            // =====================================================
            NODE node
                    = NODE.GET_NODE_BY_ID(
                            nodeId,
                            con
                    );

            if (node == null) {

                response.getWriter().write(
                        "{\"success\": false, "
                        + "\"message\": \"Node not found\"}"
                );

                return;
            }

            // =====================================================
            // REMOVE CONTAINER FIRST
            // =====================================================
            boolean containerRemoved
                    = DOCKER_MANAGER.REMOVE_NODE_CONTAINER(
                            node.getNODE_NAME()
                    );

            // =====================================================
            // DELETE DATABASE RECORD
            // =====================================================
            String deletedNodeName
                    = NODE.deleteNodeById(
                            nodeId,
                            con
                    );

            // =====================================================
            // SUCCESS
            // =====================================================
            response.getWriter().write(
                    "{"
                    + "\"success\": true,"
                    + "\"deletedNode\": \"" + escapeJson(deletedNodeName) + "\","
                    + "\"containerRemoved\": " + containerRemoved + ","
                    + "\"message\": \"Node Deleted Successfully\""
                    + "}"
            );

        } catch (NumberFormatException ex) {

            response.getWriter().write(
                    "{\"success\": false, "
                    + "\"message\": \"Invalid Node ID format\"}"
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            response.getWriter().write(
                    "{"
                    + "\"success\": false,"
                    + "\"message\": \"Server Error: "
                    + escapeJson(ex.getMessage())
                    + "\""
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