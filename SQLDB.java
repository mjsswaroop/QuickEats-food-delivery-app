// SQLDB.java
import java.sql.*;

public class SQLDB {
    public static Connection conn;
    public static Statement stmt;
    public static ResultSet rset;

    public static void connect(String dbPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            stmt = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeQuery(String sql) {
        try {
            rset = stmt.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeUpdate(String sql) {
        try {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
