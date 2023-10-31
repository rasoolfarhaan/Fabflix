import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movies"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movies")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let the resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            Statement statement = conn.createStatement();
            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star");
            String genre = request.getParameter("genre");
            String titleStartsWith = request.getParameter("titleStartsWith");

            String sqlQuery = "SELECT " +
                    "CONCAT('<a href=\"single-movie.html?id=', m.id, '\">', m.title, '</a>') AS title, " +
                    "m.year AS year, " +
                    "m.director AS director, " +
                    "GROUP_CONCAT(DISTINCT g.name ORDER BY g.id ASC) AS genres, " +
                    "GROUP_CONCAT(DISTINCT s.name ORDER BY s.id ASC) AS stars, " +
                    "r.rating AS rating " +
                    "FROM " +
                    "movies m " +
                    "JOIN " +
                    "genres_in_movies gm ON m.id = gm.movieId " +
                    "JOIN " +
                    "genres g ON gm.genreId = g.id " +
                    "LEFT JOIN " +
                    "stars_in_movies sm ON m.id = sm.movieId " +
                    "LEFT JOIN " +
                    "stars s ON sm.starId = s.id " +
                    "JOIN " +
                    "ratings r ON m.id = r.movieId " +
                    "WHERE 1=1"; // Placeholder for conditions

            // Add conditions based on query parameters
            if (title != null && !title.isEmpty()) {
                sqlQuery += " AND m.title LIKE '%" + title + "%'";
            }
            if (year != null && !year.isEmpty()) {
                sqlQuery += " AND m.year = " + year;
            }
            if (director != null && !director.isEmpty()) {
                sqlQuery += " AND m.director LIKE '%" + director + "%'";
            }
            if (star != null && !star.isEmpty()) {
                sqlQuery += " AND s.name LIKE '%" + star + "%'";
            }
            if (genre != null && !genre.isEmpty()) {
                sqlQuery += " AND g.name = '" + genre + "'";
            }
            if (titleStartsWith != null && !titleStartsWith.isEmpty()) {
                if (titleStartsWith.equals("*")) {
                    sqlQuery += " AND m.title REGEXP '^[^a-zA-Z0-9]'";
                } else {
                    sqlQuery += " AND m.title LIKE '" + titleStartsWith + "%'";
                }
            }

            sqlQuery += " GROUP BY m.id LIMIT 20;";

            // Perform the query
            ResultSet rs = statement.executeQuery(sqlQuery);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genres = rs.getString("genres");
                String movie_stars = rs.getString("stars");
                String movie_rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genres", movie_genres);
                jsonObject.addProperty("movie_stars", movie_stars);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
