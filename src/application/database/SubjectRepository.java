package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.models.Subject;
import application.utils.DatabaseHelper;

/**
 * Repository class for handling database operations related to subjects
 */
public class SubjectRepository {
    private Connection connection;
    
    public SubjectRepository() {
        connection = DatabaseHelper.getInstance().getConnection();
    }
    
    /**
     * Create a new subject in the database
     * 
     * @param subject The subject to create
     * @return The created subject with ID set
     * @throws SQLException If there's an error during the database operation
     */
    public Subject createSubject(Subject subject) throws SQLException {
        String sql = "INSERT INTO subjects (semester_id, name) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, subject.getSemesterId());
            pstmt.setString(2, subject.getName());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        subject.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        
        return subject;
    }
    
    /**
     * Find all subjects for a specific semester
     * 
     * @param semesterId The semester ID
     * @return A list of subjects
     * @throws SQLException If there's an error during the database operation
     */
    public List<Subject> findAllBySemesterId(int semesterId) throws SQLException {
        String sql = "SELECT * FROM subjects WHERE semester_id = ? ORDER BY id ASC";
        List<Subject> subjects = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, semesterId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Subject subject = new Subject(
                        rs.getInt("id"),
                        rs.getInt("semester_id"),
                        rs.getString("name")
                    );
                    subjects.add(subject);
                }
            }
        }
        
        return subjects;
    }
    
    /**
     * Find a subject by ID
     * 
     * @param id The subject ID
     * @return The subject if found, null otherwise
     * @throws SQLException If there's an error during the database operation
     */
    public Subject findById(int id) throws SQLException {
        String sql = "SELECT * FROM subjects WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Subject(
                        rs.getInt("id"),
                        rs.getInt("semester_id"),
                        rs.getString("name")
                    );
                }
            }
        }
        
        return null;
    }
    
    /**
     * Update a subject
     * 
     * @param subject The subject to update
     * @return The updated subject
     * @throws SQLException If there's an error during the database operation
     */
    public Subject updateSubject(Subject subject) throws SQLException {
        String sql = "UPDATE subjects SET name = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, subject.getName());
            pstmt.setInt(2, subject.getId());
            
            pstmt.executeUpdate();
        }
        
        return subject;
    }
    
    /**
     * Delete a subject and all its related data (assessment types, grades)
     * 
     * @param id The subject ID
     * @throws SQLException If there's an error during the database operation
     */
    public void deleteSubject(int id) throws SQLException {
        // First, get assessment types for this subject
        List<Integer> assessmentTypeIds = new ArrayList<>();
        
        String findAssessmentTypesSql = "SELECT id FROM assessment_types WHERE subject_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(findAssessmentTypesSql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    assessmentTypeIds.add(rs.getInt("id"));
                }
            }
        }
        
        // Delete grades for each assessment type
        for (Integer assessmentTypeId : assessmentTypeIds) {
            String deleteGradesSql = "DELETE FROM grades WHERE assessment_type_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteGradesSql)) {
                pstmt.setInt(1, assessmentTypeId);
                int deletedGrades = pstmt.executeUpdate();
                System.out.println("Deleted " + deletedGrades + " grades for assessment type ID " + assessmentTypeId);
            }
        }
        
        // Delete assessment types
        String deleteAssessmentTypesSql = "DELETE FROM assessment_types WHERE subject_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteAssessmentTypesSql)) {
            pstmt.setInt(1, id);
            int deletedTypes = pstmt.executeUpdate();
            System.out.println("Deleted " + deletedTypes + " assessment types for subject ID " + id);
        }
        
        // Finally, delete the subject
        String deleteSubjectSql = "DELETE FROM subjects WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSubjectSql)) {
            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            
            if (result == 0) {
                System.out.println("Warning: No subject with ID " + id + " was found to delete");
            } else {
                System.out.println("Successfully deleted subject with ID " + id);
            }
        }
    }
    
    /**
     * Count the number of subjects for a semester
     * 
     * @param semesterId The semester ID
     * @return The number of subjects
     * @throws SQLException If there's an error during the database operation
     */
    public int countBySemesterId(int semesterId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM subjects WHERE semester_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, semesterId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Debug method to log assessment types and assessments for a subject
     */
    public void debugSubjectData(int subjectId) {
        try {
            System.out.println("DEBUG: Checking assessment types and assessments for subject ID: " + subjectId);
            
            // Query assessment types
            String typeSql = "SELECT * FROM assessment_types WHERE subject_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(typeSql)) {
                pstmt.setInt(1, subjectId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    int typeCount = 0;
                    while (rs.next()) {
                        typeCount++;
                        int id = rs.getInt("id");
                        String type = rs.getString("type");
                        int count = rs.getInt("count");
                        double weight = rs.getDouble("weight");
                        
                        System.out.println("  Assessment Type: ID=" + id + ", Type=" + type + 
                                          ", Count=" + count + ", Weight=" + weight);
                        
                        // Query assessments for this type
                        String assessmentSql = "SELECT * FROM grades WHERE assessment_type_id = ?";
                        try (PreparedStatement apstmt = connection.prepareStatement(assessmentSql)) {
                            apstmt.setInt(1, id);
                            
                            try (ResultSet ars = apstmt.executeQuery()) {
                                int assessmentCount = 0;
                                while (ars.next()) {
                                    assessmentCount++;
                                    int aId = ars.getInt("id");
                                    int number = ars.getInt("assessment_number");
                                    double score = ars.getDouble("score");
                                    boolean isFinal = ars.getBoolean("is_final");
                                    
                                    System.out.println("    Assessment: ID=" + aId + 
                                                     ", Number=" + number + 
                                                     ", Score=" + score + 
                                                     ", Final=" + isFinal);
                                }
                                
                                if (assessmentCount == 0) {
                                    System.out.println("    No assessments found for this type!");
                                }
                            }
                        }
                    }
                    
                    if (typeCount == 0) {
                        System.out.println("  No assessment types found for this subject!");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during debug: " + e.getMessage());
            e.printStackTrace();
        }
    }
}