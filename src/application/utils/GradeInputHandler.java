package application.utils;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import application.controllers.SemesterController;
import application.models.Assessment;
import application.models.AssessmentType;
import application.models.Subject;

/**
 * Utility class to handle grade input operations
 */
public class GradeInputHandler {
    
    private Subject subject;
    private SemesterController controller;
    private Map<Integer, TextField> scoreFields;
    private Map<Integer, CheckBox> finalCheckboxes;
    private Stage dialog;
    
    /**
     * Constructor
     * 
     * @param subject The subject to input grades for
     * @param controller The semester controller
     * @param scoreFields Map of assessment IDs to score text fields
     * @param finalCheckboxes Map of assessment IDs to finalization checkboxes
     * @param dialog The dialog window
     */
    public GradeInputHandler(Subject subject, SemesterController controller,
                          Map<Integer, TextField> scoreFields,
                          Map<Integer, CheckBox> finalCheckboxes,
                          Stage dialog) {
        this.subject = subject;
        this.controller = controller;
        this.scoreFields = scoreFields;
        this.finalCheckboxes = finalCheckboxes;
        this.dialog = dialog;
    }
    
    /**
     * Save the input grades to the database
     * 
     * @return true if successful, false otherwise
     */
    public boolean saveGrades() {
        try {
            boolean anyChange = false;
            
            System.out.println("============= SAVING GRADES =============");
            System.out.println("Subject: " + subject.getName() + ", ID: " + subject.getId());
            
            // Update assessment scores and finalization status
            for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                String typeName = entry.getKey();
                AssessmentType assessmentType = entry.getValue();
                
                System.out.println("Processing type: " + typeName + ", ID: " + assessmentType.getId());
                
                if (assessmentType.getWeight() > 0) {
                    for (Assessment assessment : assessmentType.getAssessments()) {
                        TextField scoreField = scoreFields.get(assessment.getId());
                        CheckBox finalizedCheckbox = finalCheckboxes.get(assessment.getId());
                        
                        if (scoreField != null && finalizedCheckbox != null) {
                            try {
                                double score = Double.parseDouble(scoreField.getText().trim());
                                
                                // Validate score
                                if (score < 0 || score > 100) {
                                    throw new IllegalArgumentException("Score must be between 0 and 100");
                                }
                                
                                boolean isFinalized = finalizedCheckbox.isSelected();
                                
                                System.out.println("  Assessment ID: " + assessment.getId() + 
                                                  ", Old Score: " + assessment.getScore() + 
                                                  ", New Score: " + score + 
                                                  ", Old Final: " + assessment.isFinal() + 
                                                  ", New Final: " + isFinalized);
                                
                                // Check if anything changed
                                if (score != assessment.getScore() || isFinalized != assessment.isFinal()) {
                                    // Set the values in the model
                                    assessment.setScore(score);
                                    assessment.setFinal(isFinalized);
                                    
                                    // Update in database
                                    System.out.println("  Updating assessment in database");
                                    Assessment updatedAssessment = controller.updateAssessment(assessment);
                                    
                                    if (updatedAssessment != null) {
                                        System.out.println("  Database update successful");
                                        anyChange = true;
                                    } else {
                                        System.out.println("  Database update FAILED!");
                                    }
                                } else {
                                    System.out.println("  No changes detected for this assessment");
                                }
                            } catch (NumberFormatException ex) {
                                throw new IllegalArgumentException("Invalid score: " + scoreField.getText() + 
                                                                 " for " + assessment.getDisplayName(assessmentType.getType()));
                            }
                        } else {
                            System.out.println("  Warning: Missing UI controls for assessment ID: " + assessment.getId());
                        }
                    }
                } else {
                    System.out.println("  Skipping type with zero weight");
                }
            }
            
            if (anyChange) {
                // Refresh the entire app data
                System.out.println("Changes detected, refreshing data");
                controller.refreshEverything();
                controller.debugAllSubjects();  // Debug output to see the updated data
                showInfoAlert("Success", "Grades saved successfully!");
            } else {
                System.out.println("No changes detected");
            }
            
            System.out.println("======================================");
            
            dialog.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error saving grades: " + ex.getMessage());
            showErrorAlert("Error", ex.getMessage());
            return false;
        }
    }
    
    /**
     * Show an error alert
     * 
     * @param title The alert title
     * @param message The alert message
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show an information alert
     * 
     * @param title The alert title
     * @param message The alert message
     */
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}