package application.controllers;

import java.sql.SQLException;
import java.util.List;

import application.models.Semester;
import application.models.Subject;
import application.models.User;
import application.services.SemesterService;
import application.services.UserService;
import application.utils.Navigator;
import application.views.LoginView;
import application.views.SemesterView;

/**
 * Controller class for the home screen
 */
public class HomeController {
    private SemesterService semesterService;
    private User currentUser;
    
    public HomeController() {
        semesterService = new SemesterService();
        currentUser = UserService.getCurrentUser();
    }
    
    /**
     * Handle user logout
     */
    public void logout() {
        UserService.logout();
        navigateToLogin();
    }
    
    /**
     * Navigate to the login screen
     */
    private void navigateToLogin() {
        LoginView loginView = new LoginView();
        Navigator.navigateTo(loginView.getView(), "Grade Calculator - Login");
    }
    
    /**
     * Get all semesters for the current user with fresh data from the database
     * This method ensures semesters have their subjects and assessments loaded
     * and GPA values recalculated
     * 
     * @return List of semesters with fresh data
     * @throws SQLException If there's an error during database operation
     */
    public List<Semester> getAllSemesters() throws SQLException {
        System.out.println("\n===== GETTING ALL SEMESTERS WITH FRESH DATA =====");
        
        // Get semesters with all subjects loaded
        List<Semester> semesters = semesterService.getSemestersByUser(currentUser);
        
        System.out.println("Found " + semesters.size() + " semesters for user ID " + currentUser.getId());
        
        // For each semester, force load all subjects with fresh data
        for (Semester semester : semesters) {
            System.out.println("Processing semester: " + semester.getName() + ", ID: " + semester.getId());
            
            // Get subjects for this semester
            List<Subject> subjects = semester.getSubjects();
            System.out.println("  Subjects: " + subjects.size());
            
            // For each subject, force load assessment data
            for (Subject subject : subjects) {
                // Force load assessment data
                subject.forceLoadData();
                
                // Force recalculation of values for debugging
                double percentage = subject.calculateOverallPercentage();
                String letterGrade = subject.calculateLetterGrade();
                double gpa = subject.calculateGPA();
                
                System.out.println("  Subject " + subject.getName() + 
                                 ": Percentage=" + percentage + 
                                 ", Grade=" + letterGrade + 
                                 ", GPA=" + gpa);
            }
            
            // Calculate semester GPA
            double semesterGPA = semester.calculateGPA();
            System.out.println("  Semester GPA: " + semesterGPA);
        }
        
        System.out.println("===== FINISHED GETTING SEMESTERS =====\n");
        return semesters;
    }
    /**
     * Create a new semester
     * 
     * @param name The semester name
     * @return The created semester
     * @throws Exception If there's an error during creation
     */
//    public Semester createSemester(String name) throws Exception {
//        try {
//            return semesterService.createSemester(currentUser, name);
//        } catch (SQLException e) {
//            throw new Exception("Database error: " + e.getMessage());
//        } catch (IllegalArgumentException e) {
//            throw new Exception(e.getMessage());
//        }
//    }
    
 // Update the createSemester method
    public Semester createSemester(String name) throws Exception {
        try {
            Semester semester = semesterService.createSemester(currentUser, name);
            
            // Broadcast a data change event explicitly
            System.out.println("Broadcasting data changed event after creating semester");
            application.utils.EventBus.getInstance().post(new application.utils.DataChangedEvent());
            
            return semester;
        } catch (SQLException e) {
            throw new Exception("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * Delete a semester
     * 
     * @param semesterId The ID of the semester to delete
     * @throws Exception If there's an error during deletion
     */
//    public void deleteSemester(int semesterId) throws Exception {
//        try {
//            System.out.println("Deleting semester with ID: " + semesterId);
//            
//            // Delete the semester from the database
//            semesterService.deleteSemester(semesterId);
//            
//            // Trigger a data change event to refresh other views
//            application.utils.EventBus.getInstance().post(new application.utils.DataChangedEvent());
//            
//            System.out.println("Semester deleted successfully");
//        } catch (SQLException e) {
//            System.err.println("Database error while deleting semester: " + e.getMessage());
//            throw new Exception("Database error: " + e.getMessage());
//        } catch (Exception e) {
//            System.err.println("Error deleting semester: " + e.getMessage());
//            throw new Exception("Error deleting semester: " + e.getMessage());
//        }
//    }
//    
 // Update the deleteSemester method
    public void deleteSemester(int semesterId) throws Exception {
        try {
            System.out.println("Deleting semester with ID: " + semesterId);
            
            // Delete the semester from the database
            semesterService.deleteSemester(semesterId);
            
            // Trigger a data change event to refresh other views
            System.out.println("Broadcasting data changed event after deleting semester");
            application.utils.EventBus.getInstance().post(new application.utils.DataChangedEvent());
            
            System.out.println("Semester deleted successfully");
        } catch (SQLException e) {
            System.err.println("Database error while deleting semester: " + e.getMessage());
            throw new Exception("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error deleting semester: " + e.getMessage());
            throw new Exception("Error deleting semester: " + e.getMessage());
        }
    }
    /**
     * Calculate the overall GPA for the current user
     * This method ensures fresh GPA data is calculated from the database
     * 
     * @return The overall GPA
     * @throws SQLException If there's an error during database operation
     */
    public double calculateOverallGPA() throws SQLException {
        System.out.println("HomeController: Calculating fresh overall GPA");
        List<Semester> semesters = getAllSemesters();
        
        if (semesters.isEmpty()) {
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
            return 0.0;
        }
        
        double overallGPA = totalGPA / semesterCount;
        System.out.println("  Overall GPA: " + overallGPA);
        return overallGPA;
    }
    
    /**
     * Calculate the required GPA for future semesters to achieve a goal GPA
     * 
     * @param goalGPA The goal GPA
     * @return The required GPA
     * @throws SQLException If there's an error during database operation
     */
    public double calculateRequiredGPA(double goalGPA) throws SQLException {
        return semesterService.calculateRequiredGPA(currentUser, goalGPA);
    }


    
    
    /**
     * Navigate to the semester details screen
     * 
     * @param semester The semester to view
     */
    public void navigateToSemesterDetails(Semester semester) {
        try {
            SemesterView semesterView = new SemesterView(semester);
            Navigator.navigateTo(semesterView.getView(), "Grade Calculator - " + semester.getName());
        } catch (Exception e) {
            // Show error dialog
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to open semester: " + e.getMessage());
            alert.showAndWait();
        }
    }
}