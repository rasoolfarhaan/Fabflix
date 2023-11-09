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
        HttpSession session = request.getSession();

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
            String sorting = request.getParameter("sorting");

            String sqlQuery = "SELECT \n" +
                    "m.id AS id, \n" +
                    "m.title AS title, \n" +
                    "m.year AS year, \n" +
                    "m.director AS director, \n" +
                    "(SELECT GROUP_CONCAT(g.name ORDER BY g.name ASC) \n" +
                    " FROM genres g \n" +
                    " INNER JOIN genres_in_movies gim ON g.id = gim.genreId \n" +
                    " WHERE gim.movieId = m.id \n" +
                    " LIMIT 3) AS genres, \n" +
                    "(SELECT GROUP_CONCAT(concat(s.name,'@',s.id) ORDER BY movieCount DESC, s.name ASC) \n" +
                    " FROM (SELECT stars.id, stars.name, COUNT(sm.movieId) AS movieCount \n" +
                    "       FROM stars \n" +
                    "       INNER JOIN stars_in_movies sm ON stars.id = sm.starId \n" +
                    "       WHERE sm.movieId = m.id \n" +
                    "       GROUP BY stars.id, stars.name \n" +
                    "       ORDER BY movieCount DESC, stars.name ASC \n" +
                    "       LIMIT 3) AS s) AS stars, \n" +
                    "COALESCE(r.rating, 'N/A') AS rating \n"+
                    "FROM movies m \n" +
                    "LEFT JOIN ratings r ON m.id = r.movieId \n" +
                    "INNER JOIN genres_in_movies gim ON m.id = gim.movieId\n" +
                    "INNER JOIN genres g ON gim.genreId = g.id\n"+
                    "WHERE 1 = 1 ";


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
                sqlQuery += " AND g.name like '%" + genre + "%'";
            }
            if (titleStartsWith != null && !titleStartsWith.isEmpty()) {
                if (titleStartsWith.equals("*")) {
                    sqlQuery += " AND m.title REGEXP '^[^a-zA-Z0-9]'";
                } else {
                    sqlQuery += " AND m.title LIKE '" + titleStartsWith + "%'";
                }
            }
            if (sorting != null && !sorting.isEmpty()) {
                switch (sorting) {
                    case "titleAscRatingAsc":
                        sqlQuery += " ORDER BY m.title ASC, rating ASC";
                        break;
                    case "titleAscRatingDesc":
                        sqlQuery += " ORDER BY m.title ASC, rating DESC";
                        break;
                    case "titleDescRatingAsc":
                        sqlQuery += " ORDER BY m.title DESC, rating ASC";
                        break;
                    case "titleDescRatingDesc":
                        sqlQuery += " ORDER BY m.title DESC, rating DESC";
                        break;
                    case "ratingAscTitleAsc":
                        sqlQuery += " ORDER BY rating ASC, m.title ASC";
                        break;
                    case "ratingAscTitleDesc":
                        sqlQuery += " ORDER BY rating ASC, m.title DESC";
                        break;
                    case "ratingDescTitleAsc":
                        sqlQuery += " ORDER BY rating DESC, m.title ASC";
                        break;
                    case "ratingDescTitleDesc":
                        sqlQuery += " ORDER BY rating DESC, m.title DESC";
                        break;
                    default:
                        // Default sorting
                        sqlQuery += " ORDER BY m.title ASC, rating ASC";
                }
            }
            sqlQuery += " GROUP BY m.id LIMIT 20;";
            System.out.println(sqlQuery);

            // Perform the query
            ResultSet rs = statement.executeQuery(sqlQuery);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genres = rs.getString("genres");
                String movie_stars = rs.getString("stars");
                String movie_rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                String[] genres = rs.getString("genres").split(",");
                JsonArray jsonGenres = new JsonArray();
                for (int i = 0; i < genres.length; i++) {
                    if (i >= 3) {
                        break;
                    }
                    jsonGenres.add(genres[i]);
                }
                jsonObject.add("movie_genres", jsonGenres);
                String[] stars = rs.getString("stars").split(",");
                JsonArray jsonStars = new JsonArray();
                for (int i = 0; i < stars.length; i++) {
                    if (i >= 3) {
                        break;
                    }
                    jsonStars.add(stars[i]);
                }
                jsonObject.add("movie_stars", jsonStars);

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
