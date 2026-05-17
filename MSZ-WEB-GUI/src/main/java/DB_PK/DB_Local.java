package DB_PK;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_Local
{
    // ================= NEON CLOUD POSTGRES CONFIG =================

    private static final String URL =
            "jdbc:postgresql://ep-flat-meadow-amtyc26l.c-5.us-east-1.aws.neon.tech/neondb"
            + "?sslmode=require"
            + "&channel_binding=require"
            + "&connectTimeout=30"
            + "&socketTimeout=30";

    private static final String USER =
            "neondb_owner";

    private static final String PASSWORD =
            "npg_4HJUyiRrC7sZ";

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

            System.out.println("Successfully Connected to NEON Cloud Database!");
            return con;
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("PostgreSQL Driver Not Found");
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            System.out.println("Neon Database Connection Failed : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
