package application.controllers;

import java.sql.SQLException;
import javafx.application.Platform;
import java.util.List;
import java.util.Map;

import application.models.Semester;
import application.models.Subject;
import application.services.SemesterService;
import application.services.SubjectService;
import application.utils.Navigator;
import application.views.HomeView;

/**
 * Controller class for the semester details screen
 */
public class SemesterController {
    private Semester semester;
    private SemesterService semesterService;
    private SubjectService subjectService;
    
    public SemesterController(Semester semester) {
        this.semester = semester;
        this.semesterService = new SemesterService();
        this.subjectService = new SubjectService();
    }
    
    /**
     * Create a new subject
     * 
     * @param name The subject name
     * @param assessmentConfig Map of assessment type names to their configurations
     * @return The created subject
     * @throws Exception If there's an error during creation
     */
    public Subject createSubject(String name, Map<String, Object[]> assessmentConfig) throws Exception {
        try {
            System.out.println("\n===== CREATING NEW SUBJECT =====");
            System.out.println("Name: " + name);
            System.out.println("Semester ID: " + semester.getId());
            
            // Print assessment config
            System.out.println("Assessment Configuration:");
            for (Map.Entry<String, Object[]> entry : assessmentConfig.entrySet()) {
                String type = entry.getKey();
                Object[] config = entry.getValue();
                
                if (type.equals("assignment") || type.equals("quiz")) {
                    int count = (int) config[0];
                    double weight = (double) config[1];
                    System.out.println("  " + type + ": count=" + count + ", weight=" + weight);
                } else {
                    double weight = (double) config[0];
                    System.out.println("  " + type + ": weight=" + weight);
                }
            }
            
            // Create the subject
            Subject subject = subjectService.createSubject(semester.getId(), name, assessmentConfig);
            
            if (subject != null) {
                System.out.println("Subject created successfully with ID: " + subject.getId());
                
                // Refresh semester data to include the new subject
                refreshSemester();
                
                // Broadcast a data change event
                System.out.println("Broadcasting data changed event");
                application.utils.EventBus.getInstance().post(new application.utils.DataChangedEvent());
            } else {
                System.out.println("Failed to create subject - null returned from service");
            }
            
            System.out.println("===== SUBJECT CREATION COMPLETE =====\n");
            
            return subject;
        } catch (SQLException e) {
            System.err.println("Database error creating subject: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error creating subject: " + e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error creating subject: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Unexpected error: " + e.getMessage());
        }
    }
    
    /**
     * Delete a subject
     * 
     * @param subjectId The ID of the subject to delete
     * @throws Exception If there's an error during deletion
     */
    public void deleteSubject(int subjectId) throws Exception {
        try {
            System.out.println("Deleting subject with ID: " + subjectId);
            
            // Delete the subject from the database
            subjectService.deleteSubject(subjectId);
            
            // Refresh semester data
            refreshSemester();
            
            // Notify the application that data has changed
            application.utils.EventBus.getInstance().post(new application.utils.DataChangedEvent());
            
            System.out.println("Subject deleted successfully");
        } catch (SQLException e) {
            System.err.println("Database error while deleting subject: " + e.getMessage());
            throw new Exception("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error deleting subject: " + e.getMessage());
            throw new Exception("Error deleting subject: " + e.getMessage());
        }
    }
    
    /**
     * Navigate to the home screen with forced UI update
     */
//    public void navigateToHome() {
//        try {
//            System.out.println("\n===== NAVIGATING BACK TO HOME SCREEN WITH FORCED UPDATE =====");
//            
//            // Create a new HomeView
//            HomeView homeView = new HomeView();
//            
//            // Navigate to the home screen
//            Navigator.navigateTo(homeView.getView(), "Grade Calculator - Home");
//            
//            // Force an immediate UI update after navigation
//            homeView.forceUpdateUI();
//            
//            System.out.println("===== NAVIGATION COMPLETE =====\n");
//        } catch (Exception e) {
//            System.err.println("Error navigating to home screen: " + e.getMessage());
//            e.printStackTrace();
//            
//            // Show error dialog
//            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText(null);
//            alert.setContentText("Failed to navigate to home screen: " + e.getMessage());
//            alert.showAndWait();
//        }
//    }
    
    public void navigateToHome() {
        try {
            System.out.println("\n===== NAVIGATING BACK TO HOME SCREEN WITH FORCED UPDATE =====");
            
            // Create a new HomeView
            HomeView homeView = new HomeView();
            
            // Navigate to the home screen
            Navigator.navigateTo(homeView.getView(), "Grade Calculator - Home");
            
            // Just force the UI update directly - it will handle the database refresh internally
            Platform.runLater(() -> {
                try {
                    System.out.println("Forcing UI update after navigation...");
                    homeView.forceDisplayGPAs();
                    System.out.println("Successfully forced UI update after navigation");
                } catch (Exception ex) {
                    System.err.println("Error forcing UI update: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
            
            System.out.println("===== NAVIGATION COMPLETE =====\n");
        } catch (Exception e) {
            System.err.println("Error navigating to home screen: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to navigate to home screen: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    /**
     * Get the current semester
     * 
     * @return The semester
     */
    public Semester getSemester() {
        return semester;
    }
    
    /**
     * Refresh the semester data
     * 
     * @throws SQLException If there's an error during database operation
     */
    public void refreshSemester() throws SQLException {
        semester = semesterService.getSemesterById(semester.getId());
    }
    
    /**
     * Update an assessment type
     * 
     * @param assessmentType The assessment type to update
     * @return The updated assessment type
     * @throws Exception If there's an error during update
     */
    public application.models.AssessmentType updateAssessmentType(application.models.AssessmentType assessmentType) throws Exception {
        try {
            return subjectService.updateAssessmentType(assessmentType);
        } catch (SQLException e) {
            throw new Exception("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Update an assessment
     * 
     * @param assessment The assessment to update
     * @return The updated assessment
     * @throws Exception If there's an error during update
     */
    public application.models.Assessment updateAssessment(application.models.Assessment assessment) throws Exception {
        try {
            // Log the update for debugging
            System.out.println("Updating assessment ID " + assessment.getId() + 
                              ", Score: " + assessment.getScore() + 
                              ", Final: " + assessment.isFinal());
            
            // Call the service method to update the assessment in the database
            application.models.Assessment updatedAssessment = subjectService.updateAssessment(assessment);
            
            // Refresh the semester data to reflect the changes
            refreshSemester();
            
            return updatedAssessment;
        } catch (SQLException e) {
            throw new Exception("Database error while updating assessment: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Error updating assessment: " + e.getMessage());
        }
    }
    
    /**
     * Navigate to the analytics screen
     */
    public void navigateToAnalytics() {
        try {
            // Refresh semester data first
            refreshSemester();
            
            application.views.AnalyticsView analyticsView = new application.views.AnalyticsView(semester);
            Navigator.navigateTo(analyticsView.getView(), "Grade Calculator - Analytics for " + semester.getName());
        } catch (SQLException e) {
            // Show error dialog
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to navigate to analytics: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    /**
     * Refresh subject data by ID
     * 
     * @param subjectId The subject ID
     * @return The refreshed subject
     */
    public Subject refreshSubjectData(int subjectId) {
        try {
            System.out.println("Refreshing subject data for ID: " + subjectId);
            Subject refreshedSubject = subjectService.getSubjectById(subjectId);
            
            if (refreshedSubject != null) {
                System.out.println("  Subject refreshed successfully: " + refreshedSubject.getName());
                
                // Calculate values
                double percentage = refreshedSubject.calculateOverallPercentage();
                String letterGrade = refreshedSubject.calculateLetterGrade();
                double gpa = refreshedSubject.calculateGPA();
                
                System.out.println("  Current values - Percentage: " + percentage + 
                                 ", Grade: " + letterGrade + 
                                 ", GPA: " + gpa);
            } else {
                System.out.println("  Warning: Failed to refresh subject!");
            }
            
            return refreshedSubject;
        } catch (SQLException e) {
            System.err.println("Database error while refreshing subject: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to refresh subject data: " + e.getMessage());
            alert.showAndWait();
            return null;
        } catch (Exception e) {
            System.err.println("Error refreshing subject: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Refresh all data in the application
     * This ensures all GPAs and metrics are recalculated and UI is updated
     */
    public void refreshEverything() {
        try {
            System.out.println("\n===== PERFORMING COMPLETE APPLICATION REFRESH =====");
            
            // First refresh the current semester with fresh data from database
            refreshSemester();
            semester = semesterService.getSemesterById(semester.getId());
            
            System.out.println("Refreshed semester: " + semester.getName() + ", ID: " + semester.getId());
            System.out.println("Subjects count: " + semester.getSubjects().size());
            
            // Force recalculation of GPA for all subjects
            for (Subject subject : semester.getSubjects()) {
                // Ensure we have the latest data for this subject
                Subject refreshedSubject = subjectService.getSubjectById(subject.getId());
                
                if (refreshedSubject != null) {
                    // Replace the subject in the semester with the refreshed one
                    for (int i = 0; i < semester.getSubjects().size(); i++) {
                        if (semester.getSubjects().get(i).getId() == subject.getId()) {
                            semester.getSubjects().set(i, refreshedSubject);
                            break;
                        }
                    }
                    
                    // Recalculate values on the refreshed subject
                    double percentage = refreshedSubject.calculateOverallPercentage();
                    String letterGrade = refreshedSubject.calculateLetterGrade();
                    double gpa = refreshedSubject.calculateGPA();
                    
                    System.out.println("Recalculated for subject " + refreshedSubject.getName() + 
                                      ": Percentage=" + percentage + 
                                      ", Grade=" + letterGrade + 
                                      ", GPA=" + gpa);
                } else {
                    System.out.println("Warning: Failed to refresh subject ID " + subject.getId());
                }
            }
            
            // Force recalculation of semester GPA
            double semesterGpa = semester.calculateGPA();
            System.out.println("Recalculated GPA for semester " + semester.getName() + ": " + semesterGpa);
            
            // Create a fresh DataChangedEvent
            System.out.println("Broadcasting data changed event to all views");
            application.utils.DataChangedEvent event = new application.utils.DataChangedEvent();
            
            // Notify the app that data has changed - use Platform.runLater to ensure UI thread safety
            javafx.application.Platform.runLater(() -> {
                application.utils.EventBus.getInstance().post(event);
                
                // REMOVED: initialize() call - this method doesn't exist in SemesterController
                // The view will be updated via the DataChangedEvent
            });
            
            System.out.println("===== COMPLETE APPLICATION REFRESH FINISHED =====\n");
        } catch (Exception e) {
            System.err.println("Error refreshing application data: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to refresh application data: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    
    /**
     * Debug method to print detailed information about all subjects in the semester
     */
    public void debugAllSubjects() {
        try {
            // Refresh semester data
            refreshSemester();
            
            System.out.println("============= DEBUGGING ALL SUBJECTS =============");
            System.out.println("Semester ID: " + semester.getId());
            System.out.println("Semester Name: " + semester.getName());
            
            List<Subject> subjects = semester.getSubjects();
            System.out.println("Number of subjects: " + subjects.size());
            
            for (Subject subject : subjects) {
                subject.debug();
            }
            
            System.out.println("Semester GPA: " + semester.calculateGPA());
            System.out.println("==================================================");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during debug: " + e.getMessage());
        }
    }
}