package application.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a semester
 */
public class Semester {
    private int id;
    private int userId;
    private String name;
    private List<Subject> subjects;
    
    public Semester() {
        this.subjects = new ArrayList<>();
    }
    
    public Semester(int userId, String name) {
        this.userId = userId;
        this.name = name;
        this.subjects = new ArrayList<>();
    }
    
    public Semester(int id, int userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.subjects = new ArrayList<>();
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Subject> getSubjects() {
        return subjects;
    }
    
    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
    
    public void addSubject(Subject subject) {
        subjects.add(subject);
    }
    
    /**
     * Calculate the GPA for this semester based on all subjects
     * 
     * @return The calculated GPA
     */
//    public double calculateGPA() {
//        if (subjects == null || subjects.isEmpty()) {
//            System.out.println("Semester " + name + ": No subjects, GPA is 0.0");
//            return 0.0;
//        }
//        
//        double totalGPA = 0.0;
//        int validSubjects = 0;
//        
//        System.out.println("\n----- CALCULATING SEMESTER GPA FOR: " + name + " (ID: " + id + ") -----");
//        
//        for (Subject subject : subjects) {
//            // Force recalculation of subject GPA
//            double subjectGPA = subject.calculateGPA();
//            System.out.println("Subject " + subject.getName() + " GPA: " + subjectGPA);
//            
//            // Only count subjects with GPAs > 0
//            if (subjectGPA > 0) {
//                totalGPA += subjectGPA;
//                validSubjects++;
//            }
//        }
//        
//        if (validSubjects == 0) {
//            System.out.println("No subjects with GPA > 0, semester GPA is 0.0");
//            return 0.0;
//        }
//        
//        double semesterGPA = totalGPA / validSubjects;
//        System.out.println("Semester GPA (average of " + validSubjects + " valid subjects): " + semesterGPA);
//        System.out.println("----- END SEMESTER GPA CALCULATION -----\n");
//        
//        return semesterGPA;
//    }
 // Replace the existing calculateGPA() method with this implementation
    /**
     * Calculate the GPA for this semester based on all subjects
     * This version aggressively logs all data and ensures proper calculation
     * 
     * @return The calculated GPA
     */
    public double calculateGPA() {
        if (subjects == null || subjects.isEmpty()) {
            System.out.println("Semester " + name + ": No subjects, GPA is 0.0");
            return 0.0;
        }
        
        double totalGPA = 0.0;
        int validSubjects = 0;
        
        System.out.println("\n----- CALCULATING SEMESTER GPA FOR: " + name + " (ID: " + id + ") -----");
        
        for (Subject subject : subjects) {
            try {
                // Force load assessment data before calculating GPA
                subject.forceLoadData();
                
                // Now calculate the GPA with fresh data
                double subjectGPA = subject.calculateGPA();
                System.out.println("Subject " + subject.getName() + " GPA: " + subjectGPA);
                
                // Only count subjects with GPAs > 0
                if (subjectGPA > 0) {
                    totalGPA += subjectGPA;
                    validSubjects++;
                }
            } catch (Exception e) {
                System.err.println("Error calculating GPA for subject " + subject.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        double semesterGPA = 0.0;
        if (validSubjects > 0) {
            semesterGPA = totalGPA / validSubjects;
        }
        
        System.out.println("Semester GPA (average of " + validSubjects + " valid subjects): " + semesterGPA);
        System.out.println("----- END SEMESTER GPA CALCULATION -----\n");
        
        return semesterGPA;
    }
    
    @Override
    public String toString() {
        return "Semester [id=" + id + ", name=" + name + ", subjects=" + subjects.size() + "]";
    }
}