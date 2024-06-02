import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseDatos {
    private static final String DB_URL = "jdbc:sqlite:library.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                String createTableSQL = "CREATE TABLE IF NOT EXISTS books ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "title TEXT NOT NULL,"
                        + "author TEXT NOT NULL,"
                        + "gutenberg_id TEXT NOT NULL"
                        + ");";
                Statement stmt = conn.createStatement();
                stmt.execute(createTableSQL);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
