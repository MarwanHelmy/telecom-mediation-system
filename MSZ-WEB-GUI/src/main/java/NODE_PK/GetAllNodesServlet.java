package NODE_PK;

import DB_PK.DB;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;

public class GetAllNodesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try 
        {
            Connection con = DB.getConnection();
            List<NODE> nodes = NODE.GET_ALL_NODES(con);

            Gson gson = new Gson();

            String json = gson.toJson(nodes);

            response.setContentType("application/json");

            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(json);
            con.close();
        } catch (SQLException ex) {
            System.getLogger(GetAllNodesServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
