package DOCKER_PK;

import DB_PK.DB_Local;
import NODE_PK.NODE;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;

public class ToggleDockerContainerServlet extends HttpServlet
{
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("application/json");

        try
        (
            Connection con = DB_Local.getConnection()
        )
        {
            int nodeId =
                    Integer.parseInt(
                            request.getParameter("nodeId")
                    );

            String action =
                    request.getParameter("action");

            NODE node =
                    NODE.GET_NODE_BY_ID(nodeId, con);

            System.out.println(
                    "ID : " + nodeId
            );

            if(node == null)
            {
                response.getWriter().write(
                        "{\"success\":false,"
                        + "\"message\":\"Node Not Found\"}"
                );

                return;
            }

            String containerName =
                    node.getNODE_NAME()
                            .toLowerCase()
                    + "-node";

            boolean result = false;

            // ================= STOP =================

            if(action.equalsIgnoreCase("STOP"))
            {
                result =
                        DOCKER_MANAGER
                                .STOP_CONTAINER(
                                        containerName
                                );

                if(result)
                {
                    node.setNODE_STATUS(false);

                    NODE.UPDATE_NODE_STATUS(
                            nodeId,
                            con,
                            false
                    );
                }
            }

            // ================= START =================

            else if(action.equalsIgnoreCase("START"))
            {
                result =
                        DOCKER_MANAGER
                                .START_CONTAINER(
                                        containerName
                                );

                if(result)
                {
                    node.setNODE_STATUS(true);

                    NODE.UPDATE_NODE_STATUS(
                            nodeId,
                            con,
                            true
                    );
                }
            }

            // ================= INVALID ACTION =================

            else
            {
                response.getWriter().write(
                        "{\"success\":false,"
                        + "\"message\":\"Invalid Action\"}"
                );

                return;
            }

            // ================= RESPONSE =================

            if(result)
            {
                response.getWriter().write(
                        "{\"success\":true,"
                        + "\"message\":\"Container "
                        + action.toUpperCase()
                        + " Successfully\"}"
                );
            }
            else
            {
                response.getWriter().write(
                        "{\"success\":false,"
                        + "\"message\":\"Docker Operation Failed\"}"
                );
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();

            response.getWriter().write(
                    "{\"success\":false,"
                    + "\"message\":\"Server Error\"}"
            );
        }
    }
}