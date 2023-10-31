import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name="PlaceOrderServlet", urlPatterns = "/pay")
public class PlaceOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;

    public void init() {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Use http POST
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String creditCardNumber = request.getParameter("creditCardNumber");
        String expirationDate = request.getParameter("expirationDate");

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Prepare a SQL query to check if the information exists in the database
            String sql = "SELECT * FROM creditcards " +
                    "WHERE firstName = '" +firstName+ "' AND lastName = '" + lastName +
                    "' AND id = '" + creditCardNumber + "' AND expiration = '" + expirationDate + "';";

            ResultSet rs = statement.executeQuery(sql);

            if (rs.next()) {
                // Successful login, redirect to the movie-list page
                request.getSession().setAttribute("cart", new ShoppingCart());
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                response.sendRedirect(sql); // You can adjust the URL as needed
            } else {
                // Invalid login, return an error message
                response.setContentType("application/json");
                response.getWriter().write("{ \"error\": \"Invalid username or password\" }");
            }

            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();



        } catch (Exception e) {
            request.getServletContext().log("Error: ", e);
            return;
        }
        out.close();
    }
}
