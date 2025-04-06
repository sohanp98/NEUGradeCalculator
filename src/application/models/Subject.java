package application.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class representing a subject in a semester
 */
public class Subject {
    private int id;
    private int semesterId;
    private String name;
    private Map<String, AssessmentType> assessmentTypes;
    private double goalPercentage;
    
    public Subject() {
        this.assessmentTypes = new HashMap<>();
    }
    
    public Subject(int semesterId, String name) {
        this.semesterId = semesterId;
        this.name = name;
        this.assessmentTypes = new HashMap<>();
        this.goalPercentage = 0.0;
    }
    
    public Subject(int id, int semesterId, String name) {
        this.id = id;
        this.semesterId = semesterId;
        this.name = name;
        this.assessmentTypes = new HashMap<>();
        this.goalPercentage = 0.0;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getSemesterId() {
        return semesterId;
    }
    
    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Map<String, AssessmentType> getAssessmentTypes() {
        return assessmentTypes;
    }
    
    public void setAssessmentTypes(Map<String, AssessmentType> assessmentTypes) {
        this.assessmentTypes = assessmentTypes;
    }
    
    public void addAssessmentType(AssessmentType assessmentType) {
        assessmentTypes.put(assessmentType.getType(), assessmentType);
    }
    
    public AssessmentType getAssessmentType(String type) {
        return assessmentTypes.get(type);
    }
    
    public double getGoalPercentage() {
        return goalPercentage;
    }
    
    public void setGoalPercentage(double goalPercentage) {
        this.goalPercentage = goalPercentage;
    }
    
    /**
     * Calculate the overall percentage score for this subject
     * The most critical method that affects all GPA calculations
     * 
     * @return The calculated percentage
     */
    public double calculateOverallPercentage() {
        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;
        
        System.out.println("\n----- PERCENTAGE CALCULATION FOR: " + name + " (ID: " + id + ") -----");
        
        for (Map.Entry<String, AssessmentType> entry : assessmentTypes.entrySet()) {
            String typeName = entry.getKey();
            AssessmentType assessmentType = entry.getValue();
            
            if (assessmentType.getWeight() > 0) {
                System.out.println("Type: " + typeName + ", Weight: " + assessmentType.getWeight() + "%");
                
                // Calculate average score for this type
                double typeAverage = 0.0;
                List<Assessment> assessments = assessmentType.getAssessments();
                
                if (assessments != null && !assessments.isEmpty()) {
                    double typeTotal = 0.0;
                    
                    for (Assessment assessment : assessments) {
                        if (assessment != null) {
                            System.out.println("  Assessment " + assessment.getNumber() + 
                                             ": Score=" + assessment.getScore());
                            typeTotal += assessment.getScore();
                        }
                    }
                    
                    typeAverage = typeTotal / assessments.size();
                    System.out.println("  Average for " + typeName + ": " + typeAverage);
                    
                    // Calculate the weighted contribution
                    double weightedContribution = (typeAverage * assessmentType.getWeight()) / 100.0;
                    System.out.println("  Weighted contribution: " + weightedContribution);
                    
                    totalWeightedScore += weightedContribution;
                    totalWeight += assessmentType.getWeight();
                } else {
                    System.out.println("  No assessments found for " + typeName);
                }
            }
        }
        
        System.out.println("Total Weighted Score: " + totalWeightedScore);
        System.out.println("Total Weight: " + totalWeight);
        
        // Calculate the overall percentage
        double overallPercentage = 0.0;
        
        if (totalWeight > 0) {
            // Scale to 100%
            overallPercentage = (totalWeightedScore * 100.0) / totalWeight;
            System.out.println("Overall Percentage: " + overallPercentage + "%");
        } else {
            System.out.println("No assessment types with weight > 0");
        }
        
        System.out.println("----- END CALCULATION -----\n");
        return overallPercentage;
    }

    /**
     * Calculate the letter grade based on the overall percentage
     * 
     * @return The letter grade
     */
    public String calculateLetterGrade() {
        double percentage = calculateOverallPercentage();
        
        System.out.println("Calculating letter grade for " + percentage + "%");
        
        if (percentage >= 93.0) return "A";
        if (percentage >= 90.0) return "A-";
        if (percentage >= 87.0) return "B+";
        if (percentage >= 83.0) return "B";
        if (percentage >= 80.0) return "B-";
        if (percentage >= 77.0) return "C+";
        if (percentage >= 73.0) return "C";
        if (percentage >= 70.0) return "C-";
        if (percentage >= 67.0) return "D+";
        if (percentage >= 63.0) return "D";
        if (percentage >= 60.0) return "D-";
        return "F";
    }

    /**
     * Calculate the GPA value based on the letter grade
     * 
     * @return The GPA value
     */
    public double calculateGPA() {
        String letterGrade = calculateLetterGrade();
        
        System.out.println("Calculating GPA for grade " + letterGrade);
        
        switch (letterGrade) {
            case "A": return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C": return 2.0;
            case "C-": return 1.7;
            case "D+": return 1.3;
            case "D": return 1.0;
            case "D-": return 0.7;
            default: return 0.0;
        }
    }
    
    /**
     * Calculate required scores for remaining assessments to reach goal percentage
     * This method provides different strategies for achieving the goal based on assessment weights
     * 
     * @return Map with assessment type names as keys and required scores as values,
     *         or empty map if goal is not achievable
     */
    public Map<String, Double> calculateRequiredScores() {
        Map<String, Double> requiredScores = new HashMap<>();
        
        // Calculate current earned points and track remaining assessment details
        double currentEarnedPoints = 0.0;
        double totalWeight = 0.0;
        double totalRemainingWeight = 0.0;
        
        // Maps to track assessment type details
        Map<String, Double> typeRemainingWeights = new HashMap<>();
        Map<String, Integer> typeRemainingCounts = new HashMap<>();
        
        // First pass: calculate current points and identify remaining assessments
        for (AssessmentType assessmentType : assessmentTypes.values()) {
            String typeName = assessmentType.getDisplayName();
            double typeWeight = assessmentType.getWeight();
            totalWeight += typeWeight;
            
            List<Assessment> assessments = assessmentType.getAssessments();
            if (assessments == null || assessments.isEmpty()) {
                // If there are no assessments but the type has weight, it's all remaining
                if (typeWeight > 0) {
                    typeRemainingWeights.put(typeName, typeWeight);
                    typeRemainingCounts.put(typeName, 1); // Treat as one assessment
                    totalRemainingWeight += typeWeight;
                }
                continue;
            }
            
            // Calculate completed and remaining for this assessment type
            double typeRemaining = 0.0;
            int remainingCount = 0;
            
            for (Assessment assessment : assessments) {
                double assessmentWeight = typeWeight / assessments.size();
                
                if (assessment.isFinal()) {
                    // This assessment has a grade - add its contribution to current points
                    currentEarnedPoints += (assessment.getScore() / 100.0) * assessmentWeight;
                } else {
                    // This assessment is remaining
                    typeRemaining += assessmentWeight;
                    remainingCount++;
                }
            }
            
            // If we have remaining assessments for this type, track them
            if (typeRemaining > 0) {
                typeRemainingWeights.put(typeName, typeRemaining);
                typeRemainingCounts.put(typeName, remainingCount);
                totalRemainingWeight += typeRemaining;
            }
        }
        
        // Calculate total points needed to achieve goal
        double goalPoints = (goalPercentage / 100.0) * totalWeight;
        double additionalPointsNeeded = goalPoints - currentEarnedPoints;
        
        // Check if goal is achievable (can't get more than 100% on remaining assessments)
        if (additionalPointsNeeded > totalRemainingWeight || totalRemainingWeight == 0) {
            // Goal is not achievable - return empty map
            return new HashMap<>();
        }
        
        // Calculate uniform scenario - where all remaining assessments get the same score
        double uniformScoreRequired = (additionalPointsNeeded / totalRemainingWeight) * 100.0;
        
        // Calculate individual optimization scenarios
        // For each type, calculate required score if we aim to minimize effort by
        // focusing on assessment types with higher weight-to-count ratios
        Map<String, Double> optimizedScores = new HashMap<>();
        
        // Calculate the "efficiency" of each assessment type (weight per assessment)
        Map<String, Double> typeEfficiency = new HashMap<>();
        for (String type : typeRemainingWeights.keySet()) {
            double weight = typeRemainingWeights.get(type);
            int count = typeRemainingCounts.get(type);
            double efficiency = weight / count;
            typeEfficiency.put(type, efficiency);
        }
        
        // Sort types by efficiency (highest first)
        List<String> sortedTypes = new ArrayList<>(typeEfficiency.keySet());
        sortedTypes.sort((t1, t2) -> Double.compare(typeEfficiency.get(t2), typeEfficiency.get(t1)));
        
        // Calculate optimized scores - focus on high-efficiency types first
        double pointsStillNeeded = additionalPointsNeeded;
        double remainingWeightToUse = totalRemainingWeight;
        
        for (String type : sortedTypes) {
            double typeWeight = typeRemainingWeights.get(type);
            int count = typeRemainingCounts.get(type);
            
            // Calculate how many points we can get by scoring 100% on this type
            double maxPointsFromType = typeWeight;
            
            if (maxPointsFromType >= pointsStillNeeded) {
                // We can achieve our goal by focusing just on this type
                double requiredScore = (pointsStillNeeded / typeWeight) * 100.0;
                optimizedScores.put(type, requiredScore);
                
                // For other types, assume minimum passing score (e.g., 60%)
                double minPassingScore = 60.0;
                for (String otherType : typeRemainingWeights.keySet()) {
                    if (!otherType.equals(type) && !optimizedScores.containsKey(otherType)) {
                        optimizedScores.put(otherType, minPassingScore);
                    }
                }
                
                break;
            } else {
                // This type alone isn't enough - maximize it and continue
                optimizedScores.put(type, 100.0);
                pointsStillNeeded -= maxPointsFromType;
                remainingWeightToUse -= typeWeight;
            }
        }
        
        // If we went through all types and still need points, the goal might be very challenging
        // but technically achievable - in this case, revert to uniform distribution
        if (pointsStillNeeded > 0 && remainingWeightToUse > 0) {
            // Reset optimized scores and use uniform approach
            optimizedScores.clear();
            
            // Just use the uniform score for all types
            for (String type : typeRemainingWeights.keySet()) {
                optimizedScores.put(type, uniformScoreRequired);
            }
        }
        
        // Choose the strategy - for now, we're using the optimized approach
        // This gives different scores based on assessment type weights
        return optimizedScores;
    }

    /**
     * Calculate the maximum possible percentage score that can be achieved
     * based on current grades and assuming 100% on all remaining assessments
     * 
     * @return The maximum achievable percentage
     */
    public double calculateMaxPossibleScore() {
        double currentPoints = 0.0;
        double totalWeight = 0.0;
        
        // Calculate points from completed assessments and potential points from remaining
        for (AssessmentType assessmentType : assessmentTypes.values()) {
            double typeWeight = assessmentType.getWeight();
            totalWeight += typeWeight;
            
            List<Assessment> assessments = assessmentType.getAssessments();
            if (assessments == null || assessments.isEmpty()) {
                // If there are no assessments but the type has weight, count it as maximum possible
                if (typeWeight > 0) {
                    currentPoints += typeWeight;
                }
                continue;
            }
            
            // Calculate per-assessment weight for this type
            double assessmentWeight = typeWeight / assessments.size();
            
            for (Assessment assessment : assessments) {
                if (assessment.isFinal()) {
                    // This assessment is complete - add its actual points
                    currentPoints += (assessment.getScore() / 100.0) * assessmentWeight;
                } else {
                    // This assessment is not complete - assume 100% score
                    currentPoints += assessmentWeight;
                }
            }
        }
        
        // Calculate maximum possible percentage
        return totalWeight > 0 ? (currentPoints / totalWeight) * 100.0 : 0.0;
    }

    /**
     * Simple method to check if a goal is achievable
     * 
     * @return boolean indicating if the goal is achievable
     */
    public boolean isGoalAchievable() {
        return !calculateRequiredScores().isEmpty();
    }
    
    /**
     * Print detailed debug information about this subject and its assessments
     */
    public void debug() {
        System.out.println("==================== DEBUG SUBJECT ====================");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Semester ID: " + semesterId);
        
        if (assessmentTypes == null) {
            System.out.println("Assessment types map is NULL!");
            return;
        }
        
        System.out.println("Number of assessment types: " + assessmentTypes.size());
        
        // Debug each assessment type
        for (Map.Entry<String, AssessmentType> entry : assessmentTypes.entrySet()) {
            String type = entry.getKey();
            AssessmentType assessmentType = entry.getValue();
            
            System.out.println("\nAssessment Type: " + type);
            assessmentType.debug();
        }
        
        // Calculate overall percentage and GPA
        double overallPercentage = calculateOverallPercentage();
        String letterGrade = calculateLetterGrade();
        double gpa = calculateGPA();
        
        System.out.println("\nOverall Percentage: " + overallPercentage);
        System.out.println("Letter Grade: " + letterGrade);
        System.out.println("GPA: " + gpa);
        System.out.println("======================================================");
    }
    /**
     * Force loading of assessment types and assessments
     * This should be called before any GPA calculations
     */
    public void forceLoadData() {
        try {
            // Get a reference to the SubjectService
            application.services.SubjectService service = new application.services.SubjectService();
            
            // Load assessment types and assessments
            Subject freshData = service.getSubjectById(this.id);
            
            // Copy data from the fresh instance
            if (freshData != null) {
                this.assessmentTypes = freshData.getAssessmentTypes();
                System.out.println("Successfully loaded " + 
                                 (assessmentTypes != null ? assessmentTypes.size() : 0) + 
                                 " assessment types for subject " + name);
            }
        } catch (Exception e) {
            System.err.println("Error loading data for subject " + name + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Get the total percentage score for this subject
     * For debugging only
     */
    public double debugGetRawPercentage() {
        System.out.println("DEBUG - Raw percentage for " + getName() + ": " + calculateOverallPercentage());
        return calculateOverallPercentage();
    }
    
    @Override
    public String toString() {
        return "Subject [id=" + id + ", name=" + name + ", assessmentTypes=" + assessmentTypes.size() + "]";
    }
}