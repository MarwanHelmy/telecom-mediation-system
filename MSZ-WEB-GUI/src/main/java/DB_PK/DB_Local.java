package DB_PK;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_Local
{
    // ================= LOCAL POSTGRES CONFIG =================

    private static final String URL =
            "jdbc:postgresql://localhost:5432/msdb";

    private static final String USER =
            "postgres";

    private static final String PASSWORD =
            "20186047Ziad";

    // ================= GET CONNECTION =================

    public static Connection getConnection()
    {
        try
        {
            Class.forName("org.postgresql.Driver");

            Connection con =
                    DriverManager.getConnection(
                            URL,
                            USER,
                            PASSWORD
                    );

            System.out.println(
                    "Local Database Connected Successfully"
            );

            return con;
        }
        catch (ClassNotFoundException e)
        {
            System.out.println(
                    "PostgreSQL Driver Not Found"
            );

            e.printStackTrace();
        }
        catch (SQLException e)
        {
            System.out.println(
                    "Local Database Connection Failed : "
                    + e.getMessage()
            );

            e.printStackTrace();
        }

        return null;
    }
}