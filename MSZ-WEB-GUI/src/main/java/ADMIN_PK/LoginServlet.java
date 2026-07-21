package ADMIN_PK;


import DB_PK.DB;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



public class LoginServlet extends HttpServlet
{

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    )
            throws ServletException, IOException
    {

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                "UTF-8"
        );

        PrintWriter out =
                response.getWriter();

        Connection con = null;

        try
        {

            // =========================
            // GET CONNECTION
            // =========================
            con =
                    DB.getConnection();

            // CHECK CONNECTION
            if (con == null)
            {
                out.write(
                        "{"
                        + "\"success\": false,"
                        + "\"message\": \"Database Connection Failed\""
                        + "}"
                );

                return;
            }

            // =========================
            // GET PARAMETERS
            // =========================
            String username =
                    request.getParameter(
                            "username"
                    );

            String password =
                    request.getParameter(
                            "password"
                    );

            // =========================
            // VALIDATION
            // =========================
            if (username == null
                    || username.trim().isEmpty()
                    || password == null
                    || password.trim().isEmpty())
            {

                out.write(
                        "{"
                        + "\"success\": false,"
                        + "\"message\": \"Username and Password are required\""
                        + "}"
                );

                return;
            }

            // =========================
            // LOGIN CHECK
            // =========================
            boolean isValid =
                    ADMIN.LOGIN(
                            username,
                            password,
                            con
                    );

            // =========================
            // SUCCESS
            // =========================
            if (isValid)
            {

                out.write(
                        "{"
                        + "\"success\": true,"
                        + "\"message\": \"Login Success\""
                        + "}"
                );
            }
            // =========================
            // FAILED LOGIN
            // =========================
            else
            {

                out.write(
                        "{"
                        + "\"success\": false,"
                        + "\"message\": \"Wrong Username or Password\""
                        + "}"
                );
            }

        }
        catch (Exception ex)
        {

            ex.printStackTrace();

            out.write(
                    "{"
                    + "\"success\": false,"
                    + "\"message\": \"Server Error\""
                    + "}"
            );
        }
        finally
        {

            try
            {

                if (con != null)
                {
                    con.close();
                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}