package application.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for managing database connections and operations
 */
public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:gradecalculator.db";
    private static DatabaseHelper instance;
    private Connection connection;
    
    private DatabaseHelper() {
        try {
            // Create a connection to the database
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
            
            // Initialize the database schema
            initializeDatabase();
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }
    
    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    private void initializeDatabase() {
        try (Statement statement = connection.createStatement()) {
            // Create users table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "first_name TEXT NOT NULL," +
                "last_name TEXT NOT NULL," +
                "password TEXT NOT NULL" +
                ");"
            );
            
            // Create semesters table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS semesters (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");"
            );
            
            // Create subjects table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS subjects (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "semester_id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "FOREIGN KEY (semester_id) REFERENCES semesters(id)" +
                ");"
            );
            
            // Create assessment_types table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS assessment_types (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "subject_id INTEGER NOT NULL," +
                "type TEXT NOT NULL," +
                "count INTEGER," +
                "weight REAL NOT NULL," +
                "FOREIGN KEY (subject_id) REFERENCES subjects(id)" +
                ");"
            );
            
            // Create grades table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS grades (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "assessment_type_id INTEGER NOT NULL," +
                "assessment_number INTEGER," +
                "score REAL DEFAULT 0," +
                "is_final BOOLEAN DEFAULT 0," +
                "FOREIGN KEY (assessment_type_id) REFERENCES assessment_types(id)" +
                ");"
            );
            
            System.out.println("Database schema initialized.");
        } catch (SQLException e) {
            System.out.println("Error initializing database schema: " + e.getMessage());
        }
    }
}