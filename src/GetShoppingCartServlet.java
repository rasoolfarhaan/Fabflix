import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/GetShoppingCartServlet")
public class GetShoppingCartServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the user's session
        HttpSession session = request.getSession();

        // Retrieve the shopping cart from the session
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");

        // Set the response content type to JSON
        response.setContentType("application/json");

        // Write the cart data as JSON to the response
        PrintWriter out = response.getWriter();
        out.print(cart.toJSON());
        out.flush();
    }
}
