package application.services;

import java.sql.SQLException;

import application.database.UserRepository;
import application.models.User;

/**
 * Service class for user-related business logic
 */
public class UserService {
    private UserRepository userRepository;
    private static User currentUser;
    
    public UserService() {
        userRepository = new UserRepository();
    }
    
    /**
     * Register a new user
     * 
     * @param firstName User's first name
     * @param lastName User's last name
     * @param password User's password
     * @return The registered user
     * @throws SQLException If there's an error during database operation
     * @throws IllegalArgumentException If validation fails
     */
    public User register(String firstName, String lastName, String password) throws SQLException, IllegalArgumentException {
        // Validate input
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        // Check if user already exists
        if (userRepository.existsByFirstName(firstName)) {
            throw new IllegalArgumentException("User with this first name already exists");
        }
        
        // Create user
        User user = new User(firstName, lastName, password);
        return userRepository.createUser(user);
    }
    
    /**
     * Login a user
     * 
     * @param firstName User's first name
     * @param password User's password
     * @return The logged in user
     * @throws SQLException If there's an error during database operation
     * @throws IllegalArgumentException If login fails
     */
    public User login(String firstName, String password) throws SQLException, IllegalArgumentException {
        // Validate input
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        // Find user
        User user = userRepository.findByFirstNameAndPassword(firstName, password);
        
        if (user == null) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        // Set current user
        currentUser = user;
        
        return user;
    }
    
    /**
     * Get the currently logged in user
     * 
     * @return The current user
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Logout the current user
     */
    public static void logout() {
        currentUser = null;
    }
}