package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.models.AssessmentType;
import application.utils.DatabaseHelper;

/**
 * Repository class for handling database operations related to assessment types
 */
public class AssessmentTypeRepository {
    private Connection connection;
    
    public AssessmentTypeRepository() {
        connection = DatabaseHelper.getInstance().getConnection();
    }
    
    /**
     * Create a new assessment type in the database
     * 
     * @param assessmentType The assessment type to create
     * @return The created assessment type with ID set
     * @throws SQLException If there's an error during the database operation
     */
    public AssessmentType createAssessmentType(AssessmentType assessmentType) throws SQLException {
        String sql = "INSERT INTO assessment_types (subject_id, type, count, weight) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, assessmentType.getSubjectId());
            pstmt.setString(2, assessmentType.getType());
            pstmt.setInt(3, assessmentType.getCount());
            pstmt.setDouble(4, assessmentType.getWeight());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        assessmentType.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        
        return assessmentType;
    }
    
    /**
     * Find all assessment types for a specific subject
     * 
     * @param subjectId The subject ID
     * @return A list of assessment types
     * @throws SQLException If there's an error during the database operation
     */
    public List<AssessmentType> findAllBySubjectId(int subjectId) throws SQLException {
        String sql = "SELECT * FROM assessment_types WHERE subject_id = ? ORDER BY id ASC";
        List<AssessmentType> assessmentTypes = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, subjectId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AssessmentType assessmentType = new AssessmentType(
                        rs.getInt("id"),
                        rs.getInt("subject_id"),
                        rs.getString("type"),
                        rs.getInt("count"),
                        rs.getDouble("weight")
                    );
                    assessmentTypes.add(assessmentType);
                }
            }
        }
        
        return assessmentTypes;
    }
    
    /**
     * Find an assessment type by ID
     * 
     * @param id The assessment type ID
     * @return The assessment type if found, null otherwise
     * @throws SQLException If there's an error during the database operation
     */
    public AssessmentType findById(int id) throws SQLException {
        String sql = "SELECT * FROM assessment_types WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new AssessmentType(
                        rs.getInt("id"),
                        rs.getInt("subject_id"),
                        rs.getString("type"),
                        rs.getInt("count"),
                        rs.getDouble("weight")
                    );
                }
            }
        }
        
        return null;
    }
    
    /**
     * Update an assessment type
     * 
     * @param assessmentType The assessment type to update
     * @return The updated assessment type
     * @throws SQLException If there's an error during the database operation
     */
    public AssessmentType updateAssessmentType(AssessmentType assessmentType) throws SQLException {
        String sql = "UPDATE assessment_types SET count = ?, weight = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, assessmentType.getCount());
            pstmt.setDouble(2, assessmentType.getWeight());
            pstmt.setInt(3, assessmentType.getId());
            
            pstmt.executeUpdate();
        }
        
        return assessmentType;
    }
    
    /**
     * Delete an assessment type
     * 
     * @param id The assessment type ID
     * @throws SQLException If there's an error during the database operation
     */
    public void deleteAssessmentType(int id) throws SQLException {
        String sql = "DELETE FROM assessment_types WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            pstmt.executeUpdate();
        }
    }
}