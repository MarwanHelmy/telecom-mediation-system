package NODE_PK;

import DB_PK.DB;
import DOCKER_PK.DOCKER_MANAGER;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;

public class SaveNodeServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Connection con = null;

        try {

            con = DB.getConnection();

            String nodeId
                    = request.getParameter("nodeId");

            String nodeName
                    = request.getParameter("nodeName");

            String nodeIp
                    = request.getParameter("nodeIp");

            String nodePort
                    = request.getParameter("nodePort");

            String nodeProtocol
                    = request.getParameter("nodeProtocol");

            String nodeType
                    = request.getParameter("nodeType");

            String nodeUserName
                    = request.getParameter("nodeUserName");

            String nodePassword
                    = request.getParameter("nodePassword");

            String nodeStatus
                    = request.getParameter("nodeStatus");

            String nodeSubtype
                    = request.getParameter("nodeSubtype");

            if (nodeSubtype == null || nodeSubtype.trim().isEmpty()) {

                nodeSubtype = "MSC";
            }

            String nodeSource = "";
            String nodeArchive = "";

            // =====================================================
            // DIRECTORIES
            // =====================================================
            if (nodeProtocol.equalsIgnoreCase("FTP")) {

                nodeSource = "ftp/cdrs";
                nodeArchive = "ftp/archive";

            } else {

                nodeSource = "cdrs";
                nodeArchive = "archive";
            }

            int NODE_ID = 0;

            if (nodeId != null && !nodeId.isEmpty()) {

                NODE_ID = Integer.parseInt(nodeId);
            }

            int NODE_PORT
                    = Integer.parseInt(nodePort);

            boolean NODE_STATUS
                    = Boolean.parseBoolean(nodeStatus);

            // =====================================================
            // CHECK DUPLICATE IP
            // =====================================================
            if (NODE_ID == 0) {

                if (NODE.IS_NODE_IP_EXISTS(nodeIp, con)) {

                    NODE existingNode
                            = NODE.GET_NODE_BY_IP(
                                    nodeIp,
                                    con
                            );

                    String existingNodeName
                            = existingNode != null
                            ? existingNode.getNODE_NAME()
                            : "another node";

                    String jsonResponse
                            = "{"
                            + "\"success\": false, "
                            + "\"message\": \"IP address "
                            + nodeIp
                            + " is already assigned to node: "
                            + escapeJson(existingNodeName)
                            + "\""
                            + "}";

                    response.getWriter().write(jsonResponse);

                    return;
                }

            } else {

                NODE existingNode
                        = NODE.GET_NODE_BY_IP(
                                nodeIp,
                                con
                        );

                if (existingNode != null
                        && existingNode.getNODE_ID() != NODE_ID) {

                    response.getWriter().write(
                            "{"
                            + "\"success\": false, "
                            + "\"message\": \"IP address already assigned to another node\""
                            + "}"
                    );

                    return;
                }
            }

            // =====================================================
            // BUILD NODE
            // =====================================================
            NODE node = new NODE();

            node.setNODE_ID(NODE_ID);

            node.setNODE_NAME(nodeName);

            node.setNODE_TYPE(
                    nodeType.toUpperCase()
            );

            node.setNODE_PROTOCOL(
                    nodeProtocol.toUpperCase()
            );

            node.NODE_AUTH = "password";

            node.setNODE_USER_NAME(nodeUserName);

            node.setNODE_PASSWORD(nodePassword);

            node.setNODE_IP(nodeIp);

            node.setNODE_PORT(NODE_PORT);

            node.setSOURCE_DIRECTORY(nodeSource);

            node.setARCHIVE_DIRECTORY(nodeArchive);

            node.setNODE_STATUS(NODE_STATUS);

            node.setNODE_SUBTYPE(nodeSubtype);

            // =====================================================
            // ADD NODE
            // =====================================================
            if (NODE_ID == 0) {

                int insertedId
                        = NODE.Add_Node(node, con);

                if (insertedId > 0) {

                    boolean containerCreated
                            = DOCKER_MANAGER.CREATE_NODE(node);

                    if (containerCreated) {

                        response.getWriter().write(
                                "{"
                                + "\"success\": true, "
                                + "\"message\": \"Node Added Successfully\", "
                                + "\"nodeId\": " + insertedId + ", "
                                + "\"action\": \"created\""
                                + "}"
                        );

                    } else {

                        response.getWriter().write(
                                "{"
                                + "\"success\": false, "
                                + "\"message\": \"Node Added In Database But Docker Creation Failed\""
                                + "}"
                        );
                    }

                } else {

                    response.getWriter().write(
                            "{"
                            + "\"success\": false, "
                            + "\"message\": \"Failed To Add Node\""
                            + "}"
                    );
                }
            }

            // =====================================================
            // UPDATE NODE
            // =====================================================
            else {

                // GET OLD NODE BEFORE UPDATE
                NODE oldNode
                        = NODE.GET_NODE_BY_ID(
                                NODE_ID,
                                con
                        );

                boolean updated
                        = NODE.UPDATE_NODE_BY_ID(
                                node,
                                con
                        );

                if (updated) {

                    // REMOVE OLD CONTAINER
                    if (oldNode != null) {

                        DOCKER_MANAGER.REMOVE_NODE_CONTAINER(
                                oldNode.getNODE_NAME()
                        );
                    }

                    // CREATE NEW CONTAINER
                    boolean recreated
                            = DOCKER_MANAGER.CREATE_NODE(
                                    node
                            );

                    if (recreated) {

                        response.getWriter().write(
                                "{"
                                + "\"success\": true, "
                                + "\"message\": \"Node Updated Successfully\", "
                                + "\"nodeId\": " + NODE_ID + ", "
                                + "\"action\": \"updated\""
                                + "}"
                        );

                    } else {

                        response.getWriter().write(
                                "{"
                                + "\"success\": false, "
                                + "\"message\": \"Database Updated But Container Recreation Failed\""
                                + "}"
                        );
                    }

                } else {

                    response.getWriter().write(
                            "{"
                            + "\"success\": false, "
                            + "\"message\": \"Failed To Update Node\""
                            + "}"
                    );
                }
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            response.getWriter().write(
                    "{"
                    + "\"success\": false, "
                    + "\"message\": \"Server Error : "
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

    // =========================================================
    // ESCAPE JSON
    // =========================================================
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