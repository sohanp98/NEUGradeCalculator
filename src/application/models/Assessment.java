package application.models;

/**
 * Model class representing an individual assessment (e.g., Assignment 1, Quiz 2)
 */
public class Assessment {
    private int id;
    private int assessmentTypeId;
    private int number; // The sequence number (e.g., Assignment "1", Quiz "2")
    private double score;
    private boolean isFinal;
    
    public Assessment() {
        this.score = 0.0;
        this.isFinal = false;
    }
    
    public Assessment(int assessmentTypeId, int number) {
        this.assessmentTypeId = assessmentTypeId;
        this.number = number;
        this.score = 0.0;
        this.isFinal = false;
    }
    
    public Assessment(int id, int assessmentTypeId, int number, double score, boolean isFinal) {
        this.id = id;
        this.assessmentTypeId = assessmentTypeId;
        this.number = number;
        this.score = score;
        this.isFinal = isFinal;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getAssessmentTypeId() {
        return assessmentTypeId;
    }
    
    public void setAssessmentTypeId(int assessmentTypeId) {
        this.assessmentTypeId = assessmentTypeId;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public boolean isFinal() {
        return isFinal;
    }
    
    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }
    
    public String getDisplayName(String assessmentType) {
        if (assessmentType.equals("midterm") || 
            assessmentType.equals("final_exam") || 
            assessmentType.equals("final_project")) {
            return capitalizeFirstLetter(assessmentType.replace("_", " "));
        } else {
            return capitalizeFirstLetter(assessmentType) + " " + number;
        }
    }
    
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
    
    @Override
    public String toString() {
        return "Assessment [id=" + id + ", number=" + number + ", score=" + score + ", isFinal=" + isFinal + "]";
    }
}