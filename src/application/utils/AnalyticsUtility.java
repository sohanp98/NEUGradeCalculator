package application.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import application.models.Assessment;
import application.models.AssessmentType;
import application.models.Semester;
import application.models.Subject;

/**
 * Utility class for advanced analytics on grade data
 */
public class AnalyticsUtility {
    
    /**
     * Calculate descriptive statistics for a list of scores
     * 
     * @param scores The list of scores
     * @return A map containing various statistics
     */
    public static Map<String, Double> calculateStatistics(List<Double> scores) {
        Map<String, Double> stats = new HashMap<>();
        
        if (scores == null || scores.isEmpty()) {
            stats.put("count", 0.0);
            stats.put("min", 0.0);
            stats.put("max", 0.0);
            stats.put("mean", 0.0);
            stats.put("median", 0.0);
            stats.put("standardDeviation", 0.0);
            return stats;
        }
        
        // Count
        int count = scores.size();
        stats.put("count", (double) count);
        
        // Min and Max
        double min = Collections.min(scores);
        double max = Collections.max(scores);
        stats.put("min", min);
        stats.put("max", max);
        
        // Mean
        double sum = 0.0;
        for (Double score : scores) {
            sum += score;
        }
        double mean = sum / count;
        stats.put("mean", mean);
        
        // Median
        List<Double> sortedScores = new ArrayList<>(scores);
        Collections.sort(sortedScores);
        double median;
        if (count % 2 == 0) {
            median = (sortedScores.get(count / 2 - 1) + sortedScores.get(count / 2)) / 2.0;
        } else {
            median = sortedScores.get(count / 2);
        }
        stats.put("median", median);
        
        // Standard Deviation
        double sumOfSquaredDifferences = 0.0;
        for (Double score : scores) {
            double difference = score - mean;
            sumOfSquaredDifferences += difference * difference;
        }
        double variance = sumOfSquaredDifferences / count;
        double standardDeviation = Math.sqrt(variance);
        stats.put("standardDeviation", standardDeviation);
        
        return stats;
    }
    
    /**
     * Get the grade distribution for a subject
     * 
     * @param subject The subject
     * @return A map of assessment types to their grade distributions
     */
    public static Map<String, Map<String, Integer>> getGradeDistribution(Subject subject) {
        Map<String, Map<String, Integer>> distribution = new HashMap<>();
        
        for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
            AssessmentType assessmentType = entry.getValue();
            
            if (assessmentType.getWeight() > 0) {
                Map<String, Integer> gradeCount = new HashMap<>();
                
                // Initialize grade ranges
                gradeCount.put("90-100", 0);
                gradeCount.put("80-89", 0);
                gradeCount.put("70-79", 0);
                gradeCount.put("60-69", 0);
                gradeCount.put("Below 60", 0);
                
                for (Assessment assessment : assessmentType.getAssessments()) {
                    double score = assessment.getScore();
                    
                    if (score >= 90) {
                        gradeCount.put("90-100", gradeCount.get("90-100") + 1);
                    } else if (score >= 80) {
                        gradeCount.put("80-89", gradeCount.get("80-89") + 1);
                    } else if (score >= 70) {
                        gradeCount.put("70-79", gradeCount.get("70-79") + 1);
                    } else if (score >= 60) {
                        gradeCount.put("60-69", gradeCount.get("60-69") + 1);
                    } else {
                        gradeCount.put("Below 60", gradeCount.get("Below 60") + 1);
                    }
                }
                
                distribution.put(assessmentType.getDisplayName(), gradeCount);
            }
        }
        
        return distribution;
    }
    
    /**
     * Calculate the trend in grades for a subject
     * 
     * @param subject The subject
     * @return A list of assessment names and their scores, in chronological order
     */
    public static List<Map.Entry<String, Double>> calculateGradeTrend(Subject subject) {
        List<Map.Entry<String, Double>> trend = new ArrayList<>();
        
        // For simplicity, we'll focus on assignments to show a clear trend
        AssessmentType assignmentType = subject.getAssessmentType("assignment");
        
        if (assignmentType != null && assignmentType.getWeight() > 0) {
            for (Assessment assessment : assignmentType.getAssessments()) {
                trend.add(new java.util.AbstractMap.SimpleEntry<>(
                    assessment.getDisplayName(assignmentType.getType()),
                    assessment.getScore()
                ));
            }
            
            // Sort by assessment number (assumes format "Assignment X")
            trend.sort(Comparator.comparingInt(entry -> {
                String name = entry.getKey();
                int spaceIndex = name.lastIndexOf(' ');
                if (spaceIndex >= 0 && spaceIndex < name.length() - 1) {
                    try {
                        return Integer.parseInt(name.substring(spaceIndex + 1));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
                return 0;
            }));
        }
        
        return trend;
    }
    
    /**
     * Identify strengths and weaknesses for a subject
     * 
     * @param subject The subject
     * @return A map containing strengths and weaknesses
     */
    public static Map<String, List<String>> identifyStrengthsAndWeaknesses(Subject subject) {
        Map<String, List<String>> result = new HashMap<>();
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        
        // Create min heap for strengths (top 3 scores)
        PriorityQueue<AssessmentScore> strengthsHeap = new PriorityQueue<>(
            Comparator.comparingDouble(AssessmentScore::getScore)
        );
        
        // Create max heap for weaknesses (bottom 3 scores)
        PriorityQueue<AssessmentScore> weaknessesHeap = new PriorityQueue<>(
            Comparator.comparingDouble(AssessmentScore::getScore).reversed()
        );
        
        for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
            AssessmentType assessmentType = entry.getValue();
            
            if (assessmentType.getWeight() > 0) {
                for (Assessment assessment : assessmentType.getAssessments()) {
                    double score = assessment.getScore();
                    String name = assessment.getDisplayName(assessmentType.getType());
                    AssessmentScore assessmentScore = new AssessmentScore(name, score);
                    
                    // Update strengths heap
                    strengthsHeap.offer(assessmentScore);
                    if (strengthsHeap.size() > 3) {
                        strengthsHeap.poll();
                    }
                    
                    // Update weaknesses heap
                    weaknessesHeap.offer(assessmentScore);
                    if (weaknessesHeap.size() > 3) {
                        weaknessesHeap.poll();
                    }
                }
            }
        }
        
        // Extract strengths (in descending score order)
        List<AssessmentScore> strengthsList = new ArrayList<>();
        while (!strengthsHeap.isEmpty()) {
            strengthsList.add(strengthsHeap.poll());
        }
        Collections.reverse(strengthsList);
        
        for (AssessmentScore assessmentScore : strengthsList) {
            strengths.add(assessmentScore.getName() + ": " + String.format("%.1f%%", assessmentScore.getScore()));
        }
        
        // Extract weaknesses (in ascending score order)
        List<AssessmentScore> weaknessesList = new ArrayList<>();
        while (!weaknessesHeap.isEmpty()) {
            weaknessesList.add(weaknessesHeap.poll());
        }
        
        for (AssessmentScore assessmentScore : weaknessesList) {
            weaknesses.add(assessmentScore.getName() + ": " + String.format("%.1f%%", assessmentScore.getScore()));
        }
        
        result.put("strengths", strengths);
        result.put("weaknesses", weaknesses);
        
        return result;
    }
    
    /**
     * Calculate recommendation for improving grades
     * 
     * @param subject The subject
     * @return A list of recommendation strings
     */
    public static List<String> calculateRecommendations(Subject subject) {
        List<String> recommendations = new ArrayList<>();
        
        double overallPercentage = subject.calculateOverallPercentage();
        String letterGrade = subject.calculateLetterGrade();
        
        // Check for pending assessments and their impact
        List<AssessmentScore> pendingAssessments = new ArrayList<>();
        double pendingWeight = 0.0;
        
        for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
            AssessmentType assessmentType = entry.getValue();
            
            if (assessmentType.getWeight() > 0) {
                for (Assessment assessment : assessmentType.getAssessments()) {
                    if (!assessment.isFinal()) {
                        pendingAssessments.add(new AssessmentScore(
                            assessment.getDisplayName(assessmentType.getType()),
                            assessment.getScore(),
                            assessmentType.getWeight() / assessmentType.getAssessments().size()
                        ));
                        
                        pendingWeight += assessmentType.getWeight() / assessmentType.getAssessments().size();
                    }
                }
            }
        }
        
        // Sort pending assessments by weight (highest impact first)
        pendingAssessments.sort(Comparator.comparingDouble(AssessmentScore::getWeight).reversed());
        
        // Overall recommendation
        if (letterGrade.equals("A")) {
            recommendations.add("Excellent work! You're performing at an A level.");
        } else {
            // Calculate needed score for next grade level
            double nextGradeThreshold;
            String nextGrade;
            
            switch (letterGrade) {
                case "A-":
                    nextGradeThreshold = GradeCalculator.A_THRESHOLD;
                    nextGrade = "A";
                    break;
                case "B+":
                    nextGradeThreshold = GradeCalculator.A_MINUS_THRESHOLD;
                    nextGrade = "A-";
                    break;
                case "B":
                    nextGradeThreshold = GradeCalculator.B_PLUS_THRESHOLD;
                    nextGrade = "B+";
                    break;
                case "B-":
                    nextGradeThreshold = GradeCalculator.B_THRESHOLD;
                    nextGrade = "B";
                    break;
                case "C+":
                    nextGradeThreshold = GradeCalculator.B_MINUS_THRESHOLD;
                    nextGrade = "B-";
                    break;
                case "C":
                    nextGradeThreshold = GradeCalculator.C_PLUS_THRESHOLD;
                    nextGrade = "C+";
                    break;
                case "C-":
                    nextGradeThreshold = GradeCalculator.C_THRESHOLD;
                    nextGrade = "C";
                    break;
                case "D+":
                    nextGradeThreshold = GradeCalculator.C_MINUS_THRESHOLD;
                    nextGrade = "C-";
                    break;
                case "D":
                    nextGradeThreshold = GradeCalculator.D_PLUS_THRESHOLD;
                    nextGrade = "D+";
                    break;
                case "D-":
                    nextGradeThreshold = GradeCalculator.D_THRESHOLD;
                    nextGrade = "D";
                    break;
                default:
                    nextGradeThreshold = GradeCalculator.D_MINUS_THRESHOLD;
                    nextGrade = "D-";
                    break;
            }
            
            double pointsNeeded = nextGradeThreshold - overallPercentage;
            
            if (pendingWeight > 0) {
                double requiredScore = (pointsNeeded / pendingWeight) * 100.0 + overallPercentage;
                
                if (requiredScore <= 100.0) {
                    recommendations.add(String.format("You need to score an average of %.1f%% on remaining assessments to achieve a %s grade.",
                        requiredScore, nextGrade));
                } else {
                    recommendations.add(String.format("You need more than 100%% on remaining assessments to achieve a %s grade. Focus on maximizing your current grade.",
                        nextGrade));
                }
            } else {
                recommendations.add("All assessments are finalized. Your final grade is " + letterGrade + ".");
            }
        }
        
        // Specific recommendations for pending assessments
        if (!pendingAssessments.isEmpty()) {
            recommendations.add("\nFocus on these upcoming assessments (in order of impact):");
            
            for (int i = 0; i < Math.min(3, pendingAssessments.size()); i++) {
                AssessmentScore assessment = pendingAssessments.get(i);
                recommendations.add(String.format("%s (%.1f%% weight) - Current score: %.1f%%",
                    assessment.getName(), assessment.getWeight(), assessment.getScore()));
            }
        }
        
        // Analyze performance by assessment type
        Map<String, Double> typeAverages = new HashMap<>();
        
        for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
            AssessmentType assessmentType = entry.getValue();
            
            if (assessmentType.getWeight() > 0) {
                typeAverages.put(assessmentType.getType(), assessmentType.calculateAverageScore());
            }
        }
        
        // Find the lowest performing assessment type
        String lowestType = null;
        double lowestAverage = Double.MAX_VALUE;
        
        for (Map.Entry<String, Double> entry : typeAverages.entrySet()) {
            if (entry.getValue() < lowestAverage) {
                lowestAverage = entry.getValue();
                lowestType = entry.getKey();
            }
        }
        
        if (lowestType != null) {
            String displayName = capitalizeAndFormat(lowestType);
            recommendations.add(String.format("\nYou could improve your performance on %s (average: %.1f%%).",
                displayName, lowestAverage));
        }
        
        return recommendations;
    }
    
    /**
     * Calculate GPA projection based on current standing and future scenarios
     * 
     * @param semesters The list of completed semesters
     * @param totalSemesters The total number of semesters in the program
     * @return A map containing different GPA projections
     */
    public static Map<String, Double> calculateGPAProjections(List<Semester> semesters, int totalSemesters) {
        Map<String, Double> projections = new HashMap<>();
        
        if (semesters.isEmpty()) {
            projections.put("current", 0.0);
            projections.put("best", 4.0);
            projections.put("worst", 0.0);
            projections.put("realistic", 3.0);
            return projections;
        }
        
        // Calculate current GPA
        double totalGPA = 0.0;
        for (Semester semester : semesters) {
            totalGPA += semester.calculateGPA();
        }
        double currentGPA = totalGPA / semesters.size();
        projections.put("current", currentGPA);
        
        // Calculate remaining semesters
        int completedSemesters = semesters.size();
        int remainingSemesters = totalSemesters - completedSemesters;
        
        if (remainingSemesters <= 0) {
            // No remaining semesters, all projections equal current GPA
            projections.put("best", currentGPA);
            projections.put("worst", currentGPA);
            projections.put("realistic", currentGPA);
        } else {
            // Calculate best case (4.0 for all remaining semesters)
            double bestCase = ((currentGPA * completedSemesters) + (4.0 * remainingSemesters)) / totalSemesters;
            projections.put("best", bestCase);
            
            // Calculate worst case (2.0 for all remaining semesters)
            double worstCase = ((currentGPA * completedSemesters) + (2.0 * remainingSemesters)) / totalSemesters;
            projections.put("worst", worstCase);
            
            // Calculate realistic case (current GPA for all remaining semesters)
            double realisticCase = currentGPA;
            projections.put("realistic", realisticCase);
        }
        
        return projections;
    }
    
    /**
     * Calculate the difficulty of achieving a target GPA
     * 
     * @param currentGPA The current GPA
     * @param targetGPA The target GPA
     * @param completedSemesters The number of completed semesters
     * @param totalSemesters The total number of semesters
     * @return A difficulty rating from 0.0 (easiest) to 1.0 (hardest)
     */
    public static double calculateDifficultyRating(double currentGPA, double targetGPA, 
                                                int completedSemesters, int totalSemesters) {
        if (completedSemesters >= totalSemesters) {
            return 1.0; // Maximum difficulty if all semesters are completed
        }
        
        int remainingSemesters = totalSemesters - completedSemesters;
        
        // Calculate required GPA for remaining semesters
        double requiredGPA = ((targetGPA * totalSemesters) - (currentGPA * completedSemesters)) / remainingSemesters;
        
        if (requiredGPA <= 0.0) {
            return 0.0; // Target already achieved
        }
        
        if (requiredGPA > 4.0) {
            return 1.0; // Impossible to achieve
        }
        
        // Scale difficulty based on required GPA
        double difficulty = requiredGPA / 4.0;
        
        // Increase difficulty as the required GPA approaches 4.0
        if (requiredGPA > 3.5) {
            difficulty = 0.8 + ((requiredGPA - 3.5) / 2.5); // Scales from 0.8 to 1.0
        }
        
        return difficulty;
    }
    
    /**
     * Helper class for assessment scores
     */
    private static class AssessmentScore {
        private String name;
        private double score;
        private double weight;
        
        public AssessmentScore(String name, double score) {
            this.name = name;
            this.score = score;
            this.weight = 0.0;
        }
        
        public AssessmentScore(String name, double score, double weight) {
            this.name = name;
            this.score = score;
            this.weight = weight;
        }
        
        public String getName() {
            return name;
        }
        
        public double getScore() {
            return score;
        }
        
        public double getWeight() {
            return weight;
        }
    }
    
    /**
     * Helper method to capitalize and format strings
     * 
     * @param input The input string
     * @return The formatted string
     */
    private static String capitalizeAndFormat(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        String formatted = input.replace("_", " ");
        return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
    }
}