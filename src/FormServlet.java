import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "FormServlet", urlPatterns = "/form")
public class FormServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
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

        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String username = request.getParameter("email");
            String password = request.getParameter("password");

            // Assuming you have a table named 'customers' in your database
            String query = "SELECT * FROM customers WHERE email = '" + username + "' AND password = '" + password + "'";

            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                // Successful login, redirect to the movie-list page
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                request.getSession().setAttribute("email", username);
                response.sendRedirect("movies"); // You can adjust the URL as needed
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

            out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", e.getMessage()));
            return;
        }
        out.close();
    }
}