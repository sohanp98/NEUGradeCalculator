package application.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing an assessment type (e.g., assignments, quizzes, exams)
 */
public class AssessmentType {
    private int id;
    private int subjectId;
    private String type; // "assignment", "quiz", "midterm", "final_exam", "final_project"
    private int count;   // Only applicable for assignments and quizzes
    private double weight;
    private List<Assessment> assessments;
    
    public AssessmentType() {
        this.assessments = new ArrayList<>();
    }
    
    public AssessmentType(int subjectId, String type, int count, double weight) {
        this.subjectId = subjectId;
        this.type = type;
        this.count = count;
        this.weight = weight;
        this.assessments = new ArrayList<>();
    }
    
    public AssessmentType(int id, int subjectId, String type, int count, double weight) {
        this.id = id;
        this.subjectId = subjectId;
        this.type = type;
        this.count = count;
        this.weight = weight;
        this.assessments = new ArrayList<>();
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getSubjectId() {
        return subjectId;
    }
    
    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public List<Assessment> getAssessments() {
        return assessments;
    }
    
    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
    }
    
    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }
    
    public String getDisplayName() {
        switch (type) {
            case "assignment": return "Assignments";
            case "quiz": return "Quizzes";
            case "midterm": return "Midterm";
            case "final_exam": return "Final Exam";
            case "final_project": return "Final Project";
            default: return type;
        }
    }
    
    /**
     * Calculate the weighted score contribution of this assessment type
     * 
     * @return The weighted score
     */
    public double calculateWeightedScore() {
        double averageScore = calculateAverageScore();
        double weightedContribution = weight * (averageScore / 100.0);
        
        System.out.println("AssessmentType " + type + " weighted contribution: " + 
                          averageScore + " * " + weight + "% / 100 = " + weightedContribution);
        
        return weightedContribution;
    }

    /**
     * Calculate the average score for this assessment type
     * 
     * @return The average score
     */
    public double calculateAverageScore() {
        if (assessments == null || assessments.isEmpty()) {
            System.out.println("AssessmentType " + type + ": No assessments, returning 0.0");
            return 0.0;
        }
        
        System.out.println("Calculating average score for " + type + " (ID: " + id + "):");
        double totalScore = 0.0;
        int count = 0;
        
        for (Assessment assessment : assessments) {
            if (assessment != null) {
                System.out.println("  Assessment ID " + assessment.getId() + 
                                  ", Number " + assessment.getNumber() + 
                                  ", Score: " + assessment.getScore());
                totalScore += assessment.getScore();
                count++;
            } else {
                System.out.println("  Warning: Null assessment found!");
            }
        }
        
        if (count == 0) {
            System.out.println("  No valid assessments, returning 0.0");
            return 0.0;
        }
        
        double averageScore = totalScore / count;
        System.out.println("  Average score: " + averageScore);
        return averageScore;
    }
    
    /**
     * Print detailed debug information about this assessment type
     */
    public void debug() {
        System.out.println("========== DEBUG ASSESSMENT TYPE ==========");
        System.out.println("ID: " + id);
        System.out.println("Type: " + type);
        System.out.println("Count: " + count);
        System.out.println("Weight: " + weight);
        
        if (assessments == null) {
            System.out.println("Assessments list is NULL!");
            return;
        }
        
        System.out.println("Number of assessments: " + assessments.size());
        
        for (Assessment assessment : assessments) {
            System.out.println("  Assessment ID: " + assessment.getId());
            System.out.println("  Assessment Number: " + assessment.getNumber());
            System.out.println("  Assessment Score: " + assessment.getScore());
            System.out.println("  Assessment Final: " + assessment.isFinal());
            System.out.println("  ------------------------------");
        }
        
        System.out.println("Average score: " + calculateAverageScore());
        System.out.println("Weighted score: " + calculateWeightedScore());
        System.out.println("==========================================");
    }
    
    @Override
    public String toString() {
        return "AssessmentType [id=" + id + ", type=" + type + ", count=" + count + ", weight=" + weight + "]";
    }
}