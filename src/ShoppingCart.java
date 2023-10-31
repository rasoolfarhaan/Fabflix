import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ShoppingCart {
    private Map<String, Integer> cartItems; // Use movie titles as keys

    public ShoppingCart() {
        cartItems = new HashMap<>();
    }

    public void addMovie(String movieTitle) {
        // Check if the movie title is already in the cart
        if (cartItems.containsKey(movieTitle)) {
            // If it is, increment the quantity
            int currentQuantity = cartItems.get(movieTitle);
            cartItems.put(movieTitle, currentQuantity + 1);
        } else {
            // If not, add it to the cart with a quantity of 1
            cartItems.put(movieTitle, 1);
        }
    }

    // Remove a movie from the cart
    public void removeMovie(String movieId) {
        if (cartItems.containsKey(movieId)) {
            int currentQuantity = cartItems.get(movieId);
            if (currentQuantity > 1) {
                // If the quantity is greater than 1, decrement it
                cartItems.put(movieId, currentQuantity - 1);
            } else {
                // If the quantity is 1, remove the movie from the cart
                cartItems.remove(movieId);
            }
        }
    }

    public void deleteMovie(String movieId) {
        if (cartItems.containsKey(movieId)) {
            int currentQuantity = cartItems.get(movieId);
            cartItems.remove(movieId);
        }
    }

    // Get the cart items as a map of movie IDs and quantities
    public Map<String, Integer> getCartItems() {
        return cartItems;
    }

    // Clear the cart (e.g., when the user completes the purchase)
    public void clearCart() {
        cartItems.clear();
    }

    public String toJSON() {
        // Create a Gson instance
        Gson gson = new Gson();
        // Serialize the shopping cart to JSON
        return gson.toJson(cartItems);
    }
}
