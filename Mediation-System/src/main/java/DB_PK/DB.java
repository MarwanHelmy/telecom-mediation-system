package DB_PK;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB 
{
    final static private String URL = "jdbc:postgresql://ep-flat-meadow-amtyc26l-pooler.c-5.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require";
    final static private String USER = "neondb_owner";
    final static private String PASSWORD = "npg_4HJUyiRrC7sZ";

    public static Connection getDBConnection()
    {
        Connection con = null;
        try
        {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        catch(Exception ex)
        {
            System.out.println("Error : " + ex.getMessage());
        }
        return con;
    }
}
