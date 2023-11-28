import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "MovieSearchServlet", urlPatterns = "/api/movie-search")
public class MovieSearchServlet extends HttpServlet {

    private DataSource dataSource;

    public void init() {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // setup the response json array
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }
            String[] tokens = query.split("\\s+");
            StringBuilder formattedQuery = new StringBuilder();
            for (String token : tokens) {
                formattedQuery.append("+").append(token).append("* ");
            }

            // TODO: Replace this example match with full-text search on movies in your project
            // Prepare a full-text search SQL query
            String sqlQuery = "SELECT id, title, director FROM movie_search WHERE MATCH(title) AGAINST(? IN BOOLEAN MODE) order by title LIMIT 10";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sqlQuery)) {


                statement.setString(1, formattedQuery.toString().trim());
                System.out.println(statement);


                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        jsonArray.add(generateJsonObject(rs.getString("id"), rs.getString("title"), rs.getString("director")));
                    }
                }
            }

            response.getWriter().write(jsonArray.toString());
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    private static JsonObject generateJsonObject(String movieId, String title, String director) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", movieId);
        additionalDataJsonObject.addProperty("director", director);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }
}
