package ADMIN_PK;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ADMIN {

    public static boolean LOGIN(
            String username,
            String password,
            Connection con
    ) {

        boolean isValid = false;

        try {

            String sql =
                    "SELECT admin_login(?, ?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                isValid = rs.getBoolean(1);
            }

            rs.close();
            ps.close();

        } catch (Exception ex) {

            System.out.println(
                    "LOGIN ERROR : "
                    + ex.getMessage()
            );

            ex.printStackTrace();
        }

        return isValid;
    }
}