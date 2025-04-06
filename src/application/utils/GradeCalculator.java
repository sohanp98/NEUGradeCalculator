package application.utils;

/**
 * Abstract class for grade calculation
 * Demonstrates abstraction and will be used for inheritance
 */
public abstract class GradeCalculator {
    // Constants for grade scales
    protected static final double A_THRESHOLD = 93.0;
    protected static final double A_MINUS_THRESHOLD = 90.0;
    protected static final double B_PLUS_THRESHOLD = 87.0;
    protected static final double B_THRESHOLD = 83.0;
    protected static final double B_MINUS_THRESHOLD = 80.0;
    protected static final double C_PLUS_THRESHOLD = 77.0;
    protected static final double C_THRESHOLD = 73.0;
    protected static final double C_MINUS_THRESHOLD = 70.0;
    protected static final double D_PLUS_THRESHOLD = 67.0;
    protected static final double D_THRESHOLD = 63.0;
    protected static final double D_MINUS_THRESHOLD = 60.0;
    
    /**
     * Calculate letter grade based on percentage
     * 
     * @param percentage The percentage score
     * @return The letter grade
     */
    public String calculateLetterGrade(double percentage) {
        if (percentage >= A_THRESHOLD) return "A";
        if (percentage >= A_MINUS_THRESHOLD) return "A-";
        if (percentage >= B_PLUS_THRESHOLD) return "B+";
        if (percentage >= B_THRESHOLD) return "B";
        if (percentage >= B_MINUS_THRESHOLD) return "B-";
        if (percentage >= C_PLUS_THRESHOLD) return "C+";
        if (percentage >= C_THRESHOLD) return "C";
        if (percentage >= C_MINUS_THRESHOLD) return "C-";
        if (percentage >= D_PLUS_THRESHOLD) return "D+";
        if (percentage >= D_THRESHOLD) return "D";
        if (percentage >= D_MINUS_THRESHOLD) return "D-";
        return "F";
    }
    
    /**
     * Calculate GPA value based on letter grade
     * 
     * @param letterGrade The letter grade
     * @return The GPA value
     */
    public double calculateGPA(String letterGrade) {
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
     * Calculate overall percentage score (to be implemented by subclasses)
     * 
     * @return The calculated percentage
     */
    public abstract double calculateOverallPercentage();
    
    /**
     * Calculate required score to achieve a goal (to be implemented by subclasses)
     * 
     * @param goalPercentage The goal percentage
     * @return The required score
     */
    public abstract double calculateRequiredScore(double goalPercentage);
}