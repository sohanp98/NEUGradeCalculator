package application.controllers;

import java.sql.SQLException;
import java.util.List;

import application.models.Semester;
import application.models.Subject;
import application.services.SemesterService;
import application.services.SubjectService;
import application.services.UserService;
import application.utils.Navigator;
import application.views.SemesterView;

/**
 * Controller class for the analytics screen
 */
public class AnalyticsController {
    private Semester semester;
    private SemesterService semesterService;
    private SubjectService subjectService;
    
    public AnalyticsController(Semester semester) {
        this.semester = semester;
        this.semesterService = new SemesterService();
        this.subjectService = new SubjectService();
    }
    
    /**
     * Navigate back to the semester view
     */
    public void navigateToSemesterView() {
        try {
            // Refresh semester data first
            semester = semesterService.getSemesterById(semester.getId());
            
            SemesterView semesterView = new SemesterView(semester);
            Navigator.navigateTo(semesterView.getView(), "Grade Calculator - " + semester.getName());
        } catch (SQLException e) {
            showErrorAlert("Error", "Failed to navigate back: " + e.getMessage());
        }
    }
    
    /**
     * Get all semesters for the current user
     * 
     * @return List of all semesters
     */
    public List<Semester> getAllSemesters() {
        try {
            return semesterService.getSemestersByUser(UserService.getCurrentUser());
        } catch (SQLException e) {
            showErrorAlert("Error", "Failed to load semesters: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get fresh subject data by ID
     * 
     * @param subjectId The subject ID
     * @return The refreshed subject
     */
    public Subject getSubjectById(int subjectId) {
        try {
            System.out.println("AnalyticsController: Getting fresh data for subject ID " + subjectId);
            return subjectService.getSubjectById(subjectId);
        } catch (SQLException e) {
            System.err.println("Error getting subject: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get the current semester with fresh data
     * 
     * @return The semester
     */
    public Semester getSemester() {
        try {
            // Always get fresh data
            semester = semesterService.getSemesterById(semester.getId());
            return semester;
        } catch (SQLException e) {
            System.err.println("Error refreshing semester: " + e.getMessage());
            return semester; // Return the existing semester as fallback
        }
    }
    
    /**
     * Show an error alert
     * 
     * @param title The alert title
     * @param message The alert message
     */
    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}