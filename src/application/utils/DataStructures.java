package application.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import application.models.Assessment;
import application.models.AssessmentType;
import application.models.Semester;
import application.models.Subject;

/**
 * Utility class demonstrating different data structures used in the application
 * This class is used to fulfill the data structure requirements of the project
 */
public class DataStructures {
    
    /**
     * Get subjects sorted by GPA using ArrayList
     * Demonstrates use of Lists
     * 
     * @param semester The semester containing subjects
     * @return A sorted list of subjects
     */
    public static List<Subject> getSortedSubjects(Semester semester) {
        // Create a list from subjects
        List<Subject> sortedSubjects = new ArrayList<>(semester.getSubjects());
        
        // Sort subjects by GPA (demonstrates Comparator)
        sortedSubjects.sort(new Comparator<Subject>() {
            @Override
            public int compare(Subject s1, Subject s2) {
                return Double.compare(s2.calculateGPA(), s1.calculateGPA());
            }
        });
        
        return sortedSubjects;
    }
    
    /**
     * Find subjects with GPA less than target value using recursion
     * Demonstrates use of Recursion
     * 
     * @param subjects The list of subjects
     * @param targetGPA The target GPA
     * @param index The current index in recursion
     * @param result The result list
     */
    public static void findSubjectsBelowGPA(List<Subject> subjects, double targetGPA, int index, List<Subject> result) {
        // Base case: if we've processed all subjects
        if (index >= subjects.size()) {
            return;
        }
        
        // Check if current subject has GPA below target
        Subject subject = subjects.get(index);
        if (subject.calculateGPA() < targetGPA) {
            result.add(subject);
        }
        
        // Recursively process the next subject
        findSubjectsBelowGPA(subjects, targetGPA, index + 1, result);
    }
    
    /**
     * Group subjects by letter grade using Map
     * Demonstrates use of Maps
     * 
     * @param subjects The list of subjects
     * @return A map from letter grade to list of subjects
     */
    public static Map<String, List<Subject>> groupSubjectsByGrade(List<Subject> subjects) {
        Map<String, List<Subject>> gradeMap = new TreeMap<>(); // TreeMap sorts the keys
        
        for (Subject subject : subjects) {
            String letterGrade = subject.calculateLetterGrade();
            
            if (!gradeMap.containsKey(letterGrade)) {
                gradeMap.put(letterGrade, new ArrayList<>());
            }
            
            gradeMap.get(letterGrade).add(subject);
        }
        
        return gradeMap;
    }
    
    /**
     * Get unique assessment types using Set
     * Demonstrates use of Sets
     * 
     * @param subjects The list of subjects
     * @return A set of unique assessment type names
     */
    public static Set<String> getUniqueAssessmentTypes(List<Subject> subjects) {
        Set<String> uniqueTypes = new TreeSet<>(); // TreeSet sorts the elements
        
        for (Subject subject : subjects) {
            for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                if (entry.getValue().getWeight() > 0) {
                    uniqueTypes.add(entry.getKey());
                }
            }
        }
        
        return uniqueTypes;
    }
    
    /**
     * Get assessments in order of completion using Stack
     * Demonstrates use of Stacks
     * 
     * @param subject The subject containing assessments
     * @return A list of assessments in order of completion
     */
    public static List<Assessment> getCompletedAssessments(Subject subject) {
        List<Assessment> result = new ArrayList<>();
        Stack<Assessment> completionStack = new Stack<>();
        
        for (AssessmentType assessmentType : subject.getAssessmentTypes().values()) {
            for (Assessment assessment : assessmentType.getAssessments()) {
                if (assessment.isFinal()) {
                    completionStack.push(assessment);
                }
            }
        }
        
        // Pop from stack to get assessments in reverse order of completion
        while (!completionStack.isEmpty()) {
            result.add(completionStack.pop());
        }
        
        return result;
    }
    
    /**
     * Get assessments by priority (based on weight) using PriorityQueue
     * Demonstrates use of Priority Queues
     * 
     * @param subject The subject containing assessments
     * @return A list of assessments sorted by their type's weight
     */
    public static List<Assessment> getPrioritizedAssessments(Subject subject) {
        List<Assessment> result = new ArrayList<>();
        
        // Create a priority queue sorted by assessment type weight
        PriorityQueue<AssessmentWithWeight> priorityQueue = new PriorityQueue<>(
            Comparator.comparingDouble(AssessmentWithWeight::getWeight).reversed()
        );
        
        // Add assessments to the priority queue
        for (AssessmentType assessmentType : subject.getAssessmentTypes().values()) {
            double weight = assessmentType.getWeight();
            
            if (weight > 0) {
                for (Assessment assessment : assessmentType.getAssessments()) {
                    if (!assessment.isFinal()) {
                        priorityQueue.add(new AssessmentWithWeight(assessment, weight));
                    }
                }
            }
        }
        
        // Poll from priority queue to get assessments in order of priority
        while (!priorityQueue.isEmpty()) {
            result.add(priorityQueue.poll().getAssessment());
        }
        
        return result;
    }
    
    /**
     * Get assessments in FIFO order (based on their number) using Queue
     * Demonstrates use of Queues
     * 
     * @param subject The subject containing assessments
     * @return A list of assessments in FIFO order
     */
    public static List<Assessment> getAssessmentsInFIFOOrder(Subject subject) {
        List<Assessment> result = new ArrayList<>();
        Queue<Assessment> queue = new LinkedList<>();
        
        // For simplicity, we'll just use assignments to demonstrate
        AssessmentType assignmentType = subject.getAssessmentType("assignment");
        
        if (assignmentType != null && assignmentType.getCount() > 0) {
            // Add assessments to the queue in order of their number
            for (Assessment assessment : assignmentType.getAssessments()) {
                queue.add(assessment);
            }
            
            // Poll from queue to get assessments in FIFO order
            while (!queue.isEmpty()) {
                result.add(queue.poll());
            }
        }
        
        return result;
    }
    
    /**
     * Calculate weighted scores for each assessment type
     * Demonstrates use of a custom data structure
     * 
     * @param subject The subject
     * @return A map from assessment type to weighted score
     */
    public static Map<String, Double> calculateWeightedScores(Subject subject) {
        Map<String, Double> weightedScores = new HashMap<>();
        
        for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
            AssessmentType assessmentType = entry.getValue();
            
            if (assessmentType.getWeight() > 0) {
                double weightedScore = assessmentType.calculateWeightedScore();
                weightedScores.put(entry.getKey(), weightedScore);
            }
        }
        
        return weightedScores;
    }
    
    /**
     * Helper class for prioritized assessments
     * Demonstrates use of a custom class
     */
    private static class AssessmentWithWeight {
        private Assessment assessment;
        private double weight;
        
        public AssessmentWithWeight(Assessment assessment, double weight) {
            this.assessment = assessment;
            this.weight = weight;
        }
        
        public Assessment getAssessment() {
            return assessment;
        }
        
        public double getWeight() {
            return weight;
        }
    }
}