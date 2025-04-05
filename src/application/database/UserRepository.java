package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.models.User;
import application.utils.DatabaseHelper;

/**
 * Repository class for handling database operations related to users
 */
public class UserRepository {
    private Connection connection;
    
    public UserRepository() {
        connection = DatabaseHelper.getInstance().getConnection();
    }
    
    /**
     * Create a new user in the database
     * 
     * @param user The user to create
     * @return The created user with ID set
     * @throws SQLException If there's an error during the database operation
     */
    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (first_name, last_name, password) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getPassword());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        
        return user;
    }
    
    /**
     * Find a user by first name and password for authentication
     * 
     * @param firstName The user's first name
     * @param password The user's password
     * @return The user if found, null otherwise
     * @throws SQLException If there's an error during the database operation
     */
    public User findByFirstNameAndPassword(String firstName, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE first_name = ? AND password = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password")
                    );
                }
            }
        }
        
        return null;
    }
    
    /**
     * Check if a user with the given first name already exists
     * 
     * @param firstName The first name to check
     * @return true if a user with the given first name exists, false otherwise
     * @throws SQLException If there's an error during the database operation
     */
    public boolean existsByFirstName(String firstName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE first_name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Find a user by ID
     * 
     * @param id The user ID
     * @return The user if found, null otherwise
     * @throws SQLException If there's an error during the database operation
     */
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password")
                    );
                }
            }
        }
        
        return null;
    }
}