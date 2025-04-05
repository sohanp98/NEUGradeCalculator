package application.services;

import java.sql.SQLException;
import java.util.List;

import application.database.SemesterRepository;
import application.database.SubjectRepository;
import application.models.Semester;
import application.models.Subject;
import application.models.User;

/**
 * Service class for semester-related business logic
 */
public class SemesterService {
    private SemesterRepository semesterRepository;
    private SubjectRepository subjectRepository;
    
    public SemesterService() {
        semesterRepository = new SemesterRepository();
        subjectRepository = new SubjectRepository();
    }
    
    /**
     * Create a new semester
     * 
     * @param user The user creating the semester
     * @param name The name of the semester
     * @return The created semester
     * @throws SQLException If there's an error during database operation
     * @throws IllegalArgumentException If validation fails
     */
    public Semester createSemester(User user, String name) throws SQLException, IllegalArgumentException {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Semester name cannot be empty");
        }
        
        // Check if user has reached the maximum number of semesters (4)
        int semesterCount = semesterRepository.countByUserId(user.getId());
        if (semesterCount >= 4) {
            throw new IllegalArgumentException("Maximum number of semesters (4) reached");
        }
        
        // Create semester
        Semester semester = new Semester(user.getId(), name);
        return semesterRepository.createSemester(semester);
    }
    
    /**
     * Get all semesters for a user
     * 
     * @param user The user
     * @return The list of semesters
     * @throws SQLException If there's an error during database operation
     */
    public List<Semester> getSemestersByUser(User user) throws SQLException {
        List<Semester> semesters = semesterRepository.findAllByUserId(user.getId());
        
        // Load subjects for each semester
        for (Semester semester : semesters) {
            List<Subject> subjects = subjectRepository.findAllBySemesterId(semester.getId());
            semester.setSubjects(subjects);
        }
        
        return semesters;
    }
    
    /**
     * Get a semester by ID, including its subjects
     * 
     * @param id The semester ID
     * @return The semester
     * @throws SQLException If there's an error during database operation
     */
    public Semester getSemesterById(int id) throws SQLException {
        Semester semester = semesterRepository.findById(id);
        
        if (semester != null) {
            List<Subject> subjects = subjectRepository.findAllBySemesterId(semester.getId());
            semester.setSubjects(subjects);
        }
        
        return semester;
    }
    
    /**
     * Update a semester
     * 
     * @param semester The semester to update
     * @return The updated semester
     * @throws SQLException If there's an error during database operation
     * @throws IllegalArgumentException If validation fails
     */
    public Semester updateSemester(Semester semester) throws SQLException, IllegalArgumentException {
        // Validate input
        if (semester.getName() == null || semester.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Semester name cannot be empty");
        }
        
        return semesterRepository.updateSemester(semester);
    }
    
    /**
     * Delete a semester
     * 
     * @param id The semester ID
     * @throws SQLException If there's an error during database operation
     */
    public void deleteSemester(int id) throws SQLException {
        semesterRepository.deleteSemester(id);
    }
    
    /**
     * Calculate the overall GPA for a user across all semesters
     * 
     * @param user The user
     * @return The overall GPA
     * @throws SQLException If there's an error during database operation
     */
    public double calculateOverallGPA(User user) throws SQLException {
        System.out.println("SemesterService: Calculating overall GPA for user " + user.getFullName());
        List<Semester> semesters = getSemestersByUser(user);
        
        if (semesters.isEmpty()) {
            System.out.println("  No semesters found, GPA is 0.0");
            return 0.0;
        }
        
        double totalGPA = 0.0;
        int semesterCount = 0;
        
        for (Semester semester : semesters) {
            double semesterGPA = semester.calculateGPA();
            System.out.println("  Semester " + semester.getName() + " GPA: " + semesterGPA);
            
            if (semesterGPA > 0) {
                totalGPA += semesterGPA;
                semesterCount++;
            }
        }
        
        if (semesterCount == 0) {
            System.out.println("  No semesters with GPA > 0, returning 0.0");
            return 0.0;
        }
        
        double overallGPA = totalGPA / semesterCount;
        System.out.println("  Overall GPA: " + overallGPA);
        return overallGPA;
    }
    
    /**
     * Calculate the required GPA for future semesters to achieve a goal GPA
     * 
     * @param user The user
     * @param goalGPA The goal GPA
     * @return The required GPA for future semesters
     * @throws SQLException If there's an error during database operation
     */
    public double calculateRequiredGPA(User user, double goalGPA) throws SQLException {
        List<Semester> semesters = getSemestersByUser(user);
        int completedSemesters = 0;
        double currentGPA = 0.0;
        
        for (Semester semester : semesters) {
            double semesterGPA = semester.calculateGPA();
            if (semesterGPA > 0) {
                currentGPA += semesterGPA;
                completedSemesters++;
            }
        }
        
        // If no semesters completed, return the goal GPA
        if (completedSemesters == 0) {
            return goalGPA;
        }
        
        // Calculate the average GPA of completed semesters
        currentGPA = currentGPA / completedSemesters;
        
        // Assuming 8 total semesters in a degree program
        int remainingSemesters = 8 - completedSemesters;
        
        // If all semesters are completed, return current GPA
        if (remainingSemesters <= 0) {
            return currentGPA;
        }
        
        // Calculate required GPA for remaining semesters
        double requiredGPA = ((goalGPA * (completedSemesters + remainingSemesters)) - (currentGPA * completedSemesters)) / remainingSemesters;
        
        // Ensure the GPA is within valid range (0.0 to 4.0)
        return Math.max(0.0, Math.min(4.0, requiredGPA));
    }
}