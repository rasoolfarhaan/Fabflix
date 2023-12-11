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
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;



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
        HttpSession session =    request.getSession();

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

            StringBuilder sqlBuilder = new StringBuilder(
                    "SELECT " +
                            "m.id AS id, " +
                            "m.title AS title, " +
                            "m.year AS year, " +
                            "m.director AS director, " +
                            "(SELECT GROUP_CONCAT(g.name ORDER BY g.name ASC) " +
                            " FROM genres g " +
                            " INNER JOIN genres_in_movies gim ON g.id = gim.genreId " +
                            " WHERE gim.movieId = m.id " +
                            " LIMIT 3) AS genres, " +
                            "(SELECT GROUP_CONCAT(concat(s.name,'@',s.id) ORDER BY movieCount DESC, s.name ASC) " +
                            " FROM (SELECT stars.id, stars.name, COUNT(sm.movieId) AS movieCount " +
                            "       FROM stars " +
                            "       INNER JOIN stars_in_movies sm ON stars.id = sm.starId " +
                            "       WHERE sm.movieId = m.id " +
                            "       GROUP BY stars.id, stars.name " +
                            "       ORDER BY movieCount DESC, stars.name ASC " +
                            "       LIMIT 3) AS s) AS stars, " +
                            "COALESCE(r.rating, 'N/A') AS rating " +
                            "FROM movies m " +
                            "LEFT JOIN ratings r ON m.id = r.movieId " +
                            "INNER JOIN genres_in_movies gim ON m.id = gim.movieId " +
                            "INNER JOIN genres g ON gim.genreId = g.id " +
                            "WHERE 1 = 1");

// Using StringBuilder for efficient string concatenation
            List<Object> parameters = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                sqlBuilder.append(" AND m.title LIKE ?");
                parameters.add("%" + title + "%");
            }
            if (year != null && !year.isEmpty()) {
                sqlBuilder.append(" AND m.year = ?");
                parameters.add(Integer.parseInt(year));
            }
            if (director != null && !director.isEmpty()) {
                sqlBuilder.append(" AND m.director LIKE ?");
                parameters.add("%" + director + "%");
            }
            if (star != null && !star.isEmpty()) {
                sqlBuilder.append(" AND s.name LIKE ?");
                parameters.add("%" + star + "%");
            }
            if (genre != null && !genre.isEmpty()) {
                sqlBuilder.append(" AND g.name LIKE ?");
                parameters.add("%" + genre + "%");
            }
            if (titleStartsWith != null && !titleStartsWith.isEmpty()) {
                if (titleStartsWith.equals("*")) {
                    sqlBuilder.append(" AND m.title REGEXP '^[^a-zA-Z0-9]'");
                } else {
                    sqlBuilder.append(" AND m.title LIKE ?");
                    parameters.add(titleStartsWith + "%");
                }
            }

            String orderBy = " ORDER BY m.title ASC, rating ASC"; // Default sorting
            if (sorting != null && !sorting.isEmpty()) {
                switch (sorting) {
                    case "titleAscRatingAsc":
                        orderBy = " ORDER BY m.title ASC, rating ASC";
                        break;
                    case "titleAscRatingDesc":
                        orderBy = " ORDER BY m.title ASC, rating DESC";
                        break;
                    case "titleDescRatingAsc":
                        orderBy = " ORDER BY m.title DESC, rating ASC";
                        break;
                    case "titleDescRatingDesc":
                        orderBy = " ORDER BY m.title DESC, rating DESC";
                        break;
                    case "ratingAscTitleAsc":
                        orderBy = " ORDER BY rating ASC, m.title ASC";
                        break;
                    case "ratingAscTitleDesc":
                        orderBy = " ORDER BY rating ASC, m.title DESC";
                        break;
                    case "ratingDescTitleAsc":
                        orderBy = " ORDER BY rating DESC, m.title ASC";
                        break;
                    case "ratingDescTitleDesc":
                        orderBy = " ORDER BY rating DESC, m.title DESC";
                        break;
                }
            }

            sqlBuilder.append(" GROUP BY m.id ");
            sqlBuilder.append(orderBy);
            sqlBuilder.append(" LIMIT 20");

            PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString());

            int index = 1;
            for (Object param : parameters) {
                if (param instanceof String) {
                    pstmt.setString(index++, (String) param);
                } else if (param instanceof Integer) {
                    pstmt.setInt(index++, (Integer) param);
                } else if (param instanceof Long) {
                    pstmt.setLong(index++, (Long) param);
                } else if (param instanceof Double) {
                    pstmt.setDouble(index++, (Double) param);
                } else if (param instanceof Boolean) {
                    pstmt.setBoolean(index++, (Boolean) param);
                }
            }

            ResultSet rs = pstmt.executeQuery();


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
            pstmt.close();
            //statement.close();
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
