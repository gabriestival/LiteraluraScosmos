import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConnAPI {
    private static final String GUTENDEX_API_URL = "https://gutendex.com/books?search=";

    public static void searchAndRegisterBook(String title) {
        try {
            
            String jsonResponse = searchBookInAPI(title);
            if (jsonResponse != null) {
                
                JSONObject bookData = new JSONObject(jsonResponse);
                JSONArray results = bookData.getJSONArray("results");
                if (results.length() > 0) {
                    JSONObject bookInfo = results.getJSONObject(0);
                    String bookTitle = bookInfo.getString("title");
                    String bookAuthor = formatAuthorName(bookInfo.getJSONArray("authors").getJSONObject(0).getString("name"));
                    String gutenbergId = bookInfo.getString("id");

                    registerBookInDatabase(bookTitle, bookAuthor, gutenbergId);
                    System.out.println("Book registered: " + bookTitle + " by " + bookAuthor);
                } else {
                    System.out.println("No book found with the title: " + title);
                }
            }
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static String searchBookInAPI(String title) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GUTENDEX_API_URL + title.replace(" ", "%20")))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            return response.body();
        } else {
            System.out.println("Error fetching book data from API");
            return null;
        }
    }

    private static void registerBookInDatabase(String title, String author, String gutenbergId) throws SQLException {
        String insertSQL = "INSERT INTO books(title, author, gutenberg_id) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, gutenbergId);
            pstmt.executeUpdate();
        }
    }

    private static String formatAuthorName(String fullName) {
        String[] parts = fullName.split(" ");
        if (parts.length >= 2) {
            String lastName = parts[parts.length - 1];
            String firstName = String.join(" ", parts, 0, parts.length - 1);
            return lastName + ", " + firstName;
        }
        return fullName; 
    }
}
