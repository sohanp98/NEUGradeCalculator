package application.utils;

import java.util.List;

import application.models.Semester;
import application.models.Subject;

/**
 * Factory class for creating different types of grade calculators
 * Demonstrates the factory design pattern
 */
public class GradeCalculatorFactory {
    
    /**
     * Create a grade calculator for a subject
     * 
     * @param subject The subject
     * @return A subject grade calculator
     */
    public static SubjectGradeCalculator createSubjectCalculator(Subject subject) {
        return new SubjectGradeCalculator(subject);
    }
    
    /**
     * Create a grade calculator for a semester
     * 
     * @param semester The semester
     * @return A semester grade calculator
     */
    public static SemesterGradeCalculator createSemesterCalculator(Semester semester) {
        return new SemesterGradeCalculator(semester);
    }
    
    /**
     * Create a grade calculator for overall GPA across multiple semesters
     * 
     * @param semesters The list of semesters
     * @return An overall grade calculator
     */
    public static OverallGradeCalculator createOverallCalculator(List<Semester> semesters) {
        return new OverallGradeCalculator(semesters);
    }
    
    /**
     * Inner class for semester grade calculation
     */
    public static class SemesterGradeCalculator extends GradeCalculator {
        private Semester semester;
        
        public SemesterGradeCalculator(Semester semester) {
            this.semester = semester;
        }
        
        @Override
        public double calculateOverallPercentage() {
            List<Subject> subjects = semester.getSubjects();
            
            if (subjects.isEmpty()) {
                return 0.0;
            }
            
            double totalPercentage = 0.0;
            
            for (Subject subject : subjects) {
                SubjectGradeCalculator calculator = new SubjectGradeCalculator(subject);
                totalPercentage += calculator.calculateOverallPercentage();
            }
            
            return totalPercentage / subjects.size();
        }
        
        @Override
        public double calculateRequiredScore(double goalPercentage) {
            // Not applicable for semesters
            return 0.0;
        }
        
        /**
         * Calculate the GPA for this semester
         * 
         * @return The GPA
         */
        public double calculateGPA() {
            List<Subject> subjects = semester.getSubjects();
            
            if (subjects.isEmpty()) {
                return 0.0;
            }
            
            double totalGPA = 0.0;
            
            for (Subject subject : subjects) {
                SubjectGradeCalculator calculator = new SubjectGradeCalculator(subject);
                totalGPA += calculator.getGPA();
            }
            
            return totalGPA / subjects.size();
        }
    }
    
    /**
     * Inner class for overall GPA calculation across multiple semesters
     */
    public static class OverallGradeCalculator extends GradeCalculator {
        private List<Semester> semesters;
        
        public OverallGradeCalculator(List<Semester> semesters) {
            this.semesters = semesters;
        }
        
        @Override
        public double calculateOverallPercentage() {
            if (semesters.isEmpty()) {
                return 0.0;
            }
            
            double totalPercentage = 0.0;
            
            for (Semester semester : semesters) {
                SemesterGradeCalculator calculator = new SemesterGradeCalculator(semester);
                totalPercentage += calculator.calculateOverallPercentage();
            }
            
            return totalPercentage / semesters.size();
        }
        
        @Override
        public double calculateRequiredScore(double goalPercentage) {
            // Not directly applicable for overall GPA
            return 0.0;
        }
        
        /**
         * Calculate the overall GPA across all semesters
         * 
         * @return The overall GPA
         */
        public double calculateOverallGPA() {
            if (semesters.isEmpty()) {
                return 0.0;
            }
            
            double totalGPA = 0.0;
            
            for (Semester semester : semesters) {
                SemesterGradeCalculator calculator = new SemesterGradeCalculator(semester);
                totalGPA += calculator.calculateGPA();
            }
            
            return totalGPA / semesters.size();
        }
        
        /**
         * Calculate the required GPA for future semesters to achieve a goal GPA
         * 
         * @param goalGPA The goal GPA
         * @param totalSemesters The total number of semesters in the program
         * @return The required GPA for future semesters
         */
        public double calculateRequiredGPA(double goalGPA, int totalSemesters) {
            if (semesters.isEmpty()) {
                return goalGPA;
            }
            
            double currentGPA = calculateOverallGPA();
            int completedSemesters = semesters.size();
            int remainingSemesters = totalSemesters - completedSemesters;
            
            if (remainingSemesters <= 0) {
                return currentGPA;
            }
            
            double requiredGPA = ((goalGPA * totalSemesters) - (currentGPA * completedSemesters)) / remainingSemesters;
            
            // Ensure the GPA is within valid range (0.0 to 4.0)
            return Math.max(0.0, Math.min(4.0, requiredGPA));
        }
    }
}