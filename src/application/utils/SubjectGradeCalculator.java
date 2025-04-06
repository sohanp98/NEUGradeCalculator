package application.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import application.models.Assessment;
import application.models.AssessmentType;
import application.models.Subject;

/**
 * Concrete implementation of GradeCalculator for Subjects
 * Demonstrates inheritance and polymorphism
 */
public class SubjectGradeCalculator extends GradeCalculator {
    private Subject subject;
    
    public SubjectGradeCalculator(Subject subject) {
        this.subject = subject;
    }
    
    @Override
    public double calculateOverallPercentage() {
        double totalScore = 0.0;
        double totalWeight = 0.0;
        
        for (AssessmentType assessmentType : subject.getAssessmentTypes().values()) {
            if (assessmentType.getWeight() > 0) {
                totalScore += assessmentType.calculateWeightedScore();
                totalWeight += assessmentType.getWeight();
            }
        }
        
        if (totalWeight == 0) {
            return 0.0;
        }
        
        // Scale the score based on the total weight
        return (totalScore / totalWeight) * 100.0;
    }
    
    @Override
    public double calculateRequiredScore(double goalPercentage) {
        if (goalPercentage <= 0) {
            return 0.0;
        }
        
        double currentScore = 0.0;
        double finalizedWeight = 0.0;
        double remainingWeight = 0.0;
        
        for (AssessmentType assessmentType : subject.getAssessmentTypes().values()) {
            if (assessmentType.getWeight() > 0) {
                double typeWeight = assessmentType.getWeight();
                
                // Calculate the weight of finalized assessments
                List<Double> finalizedScores = new ArrayList<>();
                List<Double> remainingScores = new ArrayList<>();
                
                for (Assessment assessment : assessmentType.getAssessments()) {
                    if (assessment.isFinal()) {
                        finalizedScores.add(assessment.getScore());
                    } else {
                        remainingScores.add(assessment.getScore());
                    }
                }
                
                if (!finalizedScores.isEmpty()) {
                    double avgFinalizedScore = finalizedScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    double typeContributionFinalized = (avgFinalizedScore / 100.0) * typeWeight * (finalizedScores.size() / (double)(finalizedScores.size() + remainingScores.size()));
                    
                    currentScore += typeContributionFinalized;
                    finalizedWeight += typeWeight * (finalizedScores.size() / (double)(finalizedScores.size() + remainingScores.size()));
                }
                
                if (!remainingScores.isEmpty()) {
                    remainingWeight += typeWeight * (remainingScores.size() / (double)(finalizedScores.size() + remainingScores.size()));
                }
            }
        }
        
        if (remainingWeight == 0) {
            return 0.0;
        }
        
        // Calculate the required score for the remaining assessments
        double requiredScore = ((goalPercentage - currentScore) / remainingWeight) * 100.0;
        
        // Ensure the score is within reasonable bounds
        return Math.max(0.0, Math.min(100.0, requiredScore));
    }
    
    /**
     * Calculate the letter grade for this subject
     * 
     * @return The letter grade
     */
    public String getLetterGrade() {
        double percentage = calculateOverallPercentage();
        return calculateLetterGrade(percentage);
    }
    
    /**
     * Calculate the GPA for this subject
     * 
     * @return The GPA
     */
    public double getGPA() {
        String letterGrade = getLetterGrade();
        return calculateGPA(letterGrade);
    }
    
    /**
     * Get a map of assessment type names to their weighted scores
     * 
     * @return The weighted scores map
     */
    public Map<String, Double> getWeightedScores() {
        return DataStructures.calculateWeightedScores(subject);
    }
}