package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.models.Assessment;
import application.utils.DatabaseHelper;

/**
 * Repository class for handling database operations related to assessments
 */
public class AssessmentRepository {
    private Connection connection;
    
    public AssessmentRepository() {
        connection = DatabaseHelper.getInstance().getConnection();
    }
    
    /**
     * Create a new assessment in the database
     * 
     * @param assessment The assessment to create
     * @return The created assessment with ID set
     * @throws SQLException If there's an error during the database operation
     */
    public Assessment createAssessment(Assessment assessment) throws SQLException {
        String sql = "INSERT INTO grades (assessment_type_id, assessment_number, score, is_final) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, assessment.getAssessmentTypeId());
            pstmt.setInt(2, assessment.getNumber());
            pstmt.setDouble(3, assessment.getScore());
            pstmt.setBoolean(4, assessment.isFinal());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        assessment.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        
        return assessment;
    }
    
    /**
     * Find all assessments for a specific assessment type
     * 
     * @param assessmentTypeId The assessment type ID
     * @return A list of assessments
     * @throws SQLException If there's an error during the database operation
     */
    public List<Assessment> findAllByAssessmentTypeId(int assessmentTypeId) throws SQLException {
        String sql = "SELECT * FROM grades WHERE assessment_type_id = ? ORDER BY assessment_number ASC";
        List<Assessment> assessments = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentTypeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Assessment assessment = new Assessment(
                        rs.getInt("id"),
                        rs.getInt("assessment_type_id"),
                        rs.getInt("assessment_number"),
                        rs.getDouble("score"),
                        rs.getBoolean("is_final")
                    );
                    assessments.add(assessment);
                }
            }
        }
        
        return assessments;
    }
    
    /**
     * Find an assessment by ID
     * 
     * @param id The assessment ID
     * @return The assessment if found, null otherwise
     * @throws SQLException If there's an error during the database operation
     */
    public Assessment findById(int id) throws SQLException {
        String sql = "SELECT * FROM grades WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Assessment(
                        rs.getInt("id"),
                        rs.getInt("assessment_type_id"),
                        rs.getInt("assessment_number"),
                        rs.getDouble("score"),
                        rs.getBoolean("is_final")
                    );
                }
            }
        }
        
        return null;
    }
    
    /**
     * Update an assessment
     * 
     * @param assessment The assessment to update
     * @return The updated assessment
     * @throws SQLException If there's an error during the database operation
     */
    public Assessment updateAssessment(Assessment assessment) throws SQLException {
        String sql = "UPDATE grades SET score = ?, is_final = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            System.out.println("Executing SQL update: " + sql);
            System.out.println("Parameters: score=" + assessment.getScore() + 
                              ", is_final=" + assessment.isFinal() + 
                              ", id=" + assessment.getId());
            
            pstmt.setDouble(1, assessment.getScore());
            pstmt.setBoolean(2, assessment.isFinal());
            pstmt.setInt(3, assessment.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            
            if (rowsAffected == 0) {
                System.out.println("Warning: No rows were updated! Assessment ID might not exist: " + assessment.getId());
            }
        }
        
        return assessment;
    }
    
    /**
     * Delete an assessment
     * 
     * @param id The assessment ID
     * @throws SQLException If there's an error during the database operation
     */
    public void deleteAssessment(int id) throws SQLException {
        String sql = "DELETE FROM grades WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Create multiple assessments in batch
     * 
     * @param assessments The list of assessments to create
     * @throws SQLException If there's an error during the database operation
     */
    public void createBatch(List<Assessment> assessments) throws SQLException {
        String sql = "INSERT INTO grades (assessment_type_id, assessment_number, score, is_final) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (Assessment assessment : assessments) {
                pstmt.setInt(1, assessment.getAssessmentTypeId());
                pstmt.setInt(2, assessment.getNumber());
                pstmt.setDouble(3, assessment.getScore());
                pstmt.setBoolean(4, assessment.isFinal());
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
        }
    }
}