//import org.w3c.dom.*;
//import javax.xml.parsers.*;
//import java.io.File;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//public class XMLParser {
//
//    public static void main(String[] args) {
//        try {
//            // Load the XML files
//            File mainsXMLFile = new File("mains243.xml");
//            File castsXMLFile = new File("casts124.xml");
//
//            // Parse XML files
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            Document mainsDoc = dBuilder.parse(mainsXMLFile);
//            Document castsDoc = dBuilder.parse(castsXMLFile);
//
//            // Parse and insert data into the database
//            parseAndInsertData(mainsDoc, castsDoc);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void parseAndInsertData(Document mainsDoc, Document castsDoc) {
//        try {
//            // Initialize database connection
//            Connection connection = DriverManager.getConnection("jdbc:mysql://your_database_url", "username", "password");
//            MovieDBHandler movieDBHandler = new MovieDBHandler(connection);
//
//            // Process mains243.xml
//            NodeList directorFilmsList = mainsDoc.getElementsByTagName("directorfilms");
//            for (int i = 0; i < directorFilmsList.getLength(); i++) {
//                Element directorFilms = (Element) directorFilmsList.item(i);
//                String directorName = directorFilms.getElementsByTagName("director").item(0).getTextContent();
//
//                // Iterate through movies
//                NodeList filmsList = directorFilms.getElementsByTagName("film");
//                for (int j = 0; j < filmsList.getLength(); j++) {
//                    Element film = (Element) filmsList.item(j);
//                    String title = film.getElementsByTagName("t").item(0).getTextContent();
//                    // ... continue to extract other movie details
//
//                    // Insert or update movie in the database
//                    movieDBHandler.insertOrUpdateMovie(title, directorName, /* other details */);
//                }
//            }
//
//            // Process casts124.xml
//            // Similar logic to extract and insert actors and their roles
//
//            // Close the database connection
//            connection.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
//
//class MovieDBHandler {
//    private Connection connection;
//
//    public MovieDBHandler(Connection connection) {
//        this.connection = connection;
//    }
//
//    public void insertOrUpdateMovie(String title, String director, /* other parameters */) throws SQLException {
//        // Implement the logic to insert or update the movie in the database
//        // You may need to check if the movie, director, genre, etc., already exist in the database
//        // and insert them if not present, then update the necessary tables
//    }
//
//    // Other methods for handling database operations
//}
