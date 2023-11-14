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
import java.sql.PreparedStatement;
import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "EmployeeFormServlet", urlPatterns = "/employeeForm")
public class EmployeeFormServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            response.getWriter().write("{ \"error\": \"Invalid Captcha\" }");
            return;
        }

        try(Connection dbCon = dataSource.getConnection()) {
            String email = request.getParameter("email");
            String input = request.getParameter("password");

            String query = "SELECT employee.password FROM employees as employee WHERE employee.email = ?";

            PreparedStatement preparedStatement = dbCon.prepareStatement(query);
            preparedStatement.setString(1, email);

            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            String password = rs.getString("password");
            boolean success = new StrongPasswordEncryptor().checkPassword(input, password);
            if (success) {
                request.getSession().setAttribute("cart", new ShoppingCart());
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                request.getSession().setAttribute("email", email);
            } else {
                response.setContentType("application/json");
                response.getWriter().write("{ \"error\": \"Invalid username or password.\" }");
            }

            rs.close();
        } catch (Exception e) {
            request.getServletContext().log("Error: ", e);
            out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", e.getMessage()));
        } finally {
            out.close();
        }
    }
}
