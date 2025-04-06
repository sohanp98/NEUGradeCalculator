package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.models.Semester;
import application.utils.DatabaseHelper;

/**
 * Repository class for handling database operations related to semesters
 */
public class SemesterRepository {
    private Connection connection;
    
    public SemesterRepository() {
        connection = DatabaseHelper.getInstance().getConnection();
    }
    
    /**
     * Create a new semester in the database
     * 
     * @param semester The semester to create
     * @return The created semester with ID set
     * @throws SQLException If there's an error during the database operation
     */
    public Semester createSemester(Semester semester) throws SQLException {
        String sql = "INSERT INTO semesters (user_id, name) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, semester.getUserId());
            pstmt.setString(2, semester.getName());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        semester.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        
        return semester;
    }
    
    
    /**
     * Find all semesters for a specific user
     * 
     * @param userId The user ID
     * @return A list of semesters
     * @throws SQLException If there's an error during the database operation
     */
    public List<Semester> findAllByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM semesters WHERE user_id = ? ORDER BY id ASC";
        List<Semester> semesters = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Semester semester = new Semester(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name")
                    );
                    semesters.add(semester);
                }
            }
        }
        
        return semesters;
    }
    
    /**
     * Find a semester by ID
     * 
     * @param id The semester ID
     * @return The semester if found, null otherwise
     * @throws SQLException If there's an error during the database operation
     */
    public Semester findById(int id) throws SQLException {
        String sql = "SELECT * FROM semesters WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Semester(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name")
                    );
                }
            }
        }
        
        return null;
    }
    
    /**
     * Update a semester
     * 
     * @param semester The semester to update
     * @return The updated semester
     * @throws SQLException If there's an error during the database operation
     */
    public Semester updateSemester(Semester semester) throws SQLException {
        String sql = "UPDATE semesters SET name = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, semester.getName());
            pstmt.setInt(2, semester.getId());
            
            pstmt.executeUpdate();
        }
        
        return semester;
    }
    
    /**
     * Delete a semester and all its related data (subjects, assessment types, grades)
     * 
     * @param id The semester ID
     * @throws SQLException If there's an error during the database operation
     */
    public void deleteSemester(int id) throws SQLException {
        // First, we need to get all subjects for this semester to delete their related data
        List<Integer> subjectIds = new ArrayList<>();
        
        String findSubjectsSql = "SELECT id FROM subjects WHERE semester_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(findSubjectsSql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    subjectIds.add(rs.getInt("id"));
                }
            }
        }
        
        // For each subject, delete assessment types and grades
        for (Integer subjectId : subjectIds) {
            // Get assessment types for this subject
            List<Integer> assessmentTypeIds = new ArrayList<>();
            
            String findAssessmentTypesSql = "SELECT id FROM assessment_types WHERE subject_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(findAssessmentTypesSql)) {
                pstmt.setInt(1, subjectId);
                
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
                pstmt.setInt(1, subjectId);
                int deletedTypes = pstmt.executeUpdate();
                System.out.println("Deleted " + deletedTypes + " assessment types for subject ID " + subjectId);
            }
        }
        
        // Delete subjects
        String deleteSubjectsSql = "DELETE FROM subjects WHERE semester_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSubjectsSql)) {
            pstmt.setInt(1, id);
            int deletedSubjects = pstmt.executeUpdate();
            System.out.println("Deleted " + deletedSubjects + " subjects for semester ID " + id);
        }
        
        // Finally, delete the semester
        String deleteSemesterSql = "DELETE FROM semesters WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSemesterSql)) {
            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            
            if (result == 0) {
                System.out.println("Warning: No semester with ID " + id + " was found to delete");
            } else {
                System.out.println("Successfully deleted semester with ID " + id);
            }
        }
    }
    
    /**
     * Count the number of semesters for a user
     * 
     * @param userId The user ID
     * @return The number of semesters
     * @throws SQLException If there's an error during the database operation
     */
    public int countByUserId(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM semesters WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
}