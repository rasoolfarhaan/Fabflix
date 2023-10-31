import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.google.gson.JsonArray;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet(name = "AddToCartServlet", urlPatterns = "/AddToCartServlet")
public class AddToCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
//        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
//        } catch (NamingException e) {
//            e.printStackTrace();
//        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get the movie ID from the request
        String movieTitle = request.getParameter("movieId");
        String decrease = "null";
        String delete = "null";

        try {
            decrease=request.getParameter("decrease");
        } catch (Exception e) {}

        try {
            delete=request.getParameter("delete");
        } catch (Exception e) {}

        try {
            DataSource dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            Connection conn = dataSource.getConnection();
            String sqlQuery = "SELECT title FROM movies WHERE id = '" + movieTitle + "';";

            Statement statement = conn.createStatement();

            ResultSet rs = statement.executeQuery(sqlQuery);

            if(rs.next()) {
                movieTitle = rs.getString("title");
            }

            rs.close();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Get the user's session
        HttpSession session = request.getSession();

        // Retrieve or create the user's shopping cart from the session
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null) {
            cart = new ShoppingCart();
            session.setAttribute("cart", cart);
        }

        if(delete.equals("true")) {
            cart.deleteMovie(movieTitle);
        } else if(decrease.equals("true")) {
            cart.removeMovie(movieTitle);
        } else {
            cart.addMovie(movieTitle);
        }

        // You may want to send a response to indicate success
        response.getWriter().write("Movie added to cart: " + movieTitle);
    }
}
