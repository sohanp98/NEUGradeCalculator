package application.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import application.models.Assessment;
import application.models.AssessmentType;
import application.models.Semester;
import application.models.Subject;

/**
 * Utility class for graph operations
 * Demonstrates the use of graph data structures
 */
public class GraphUtility {
    
    /**
     * A graph representation of subject dependencies
     * Each subject has prerequisite assessments
     */
    public static class SubjectGraph {
        private Map<String, Node> nodes; // Map of subject name to node
        
        public SubjectGraph() {
            this.nodes = new HashMap<>();
        }
        
        /**
         * Add a subject node to the graph
         * 
         * @param subject The subject
         */
        public void addSubject(Subject subject) {
            String name = subject.getName();
            if (!nodes.containsKey(name)) {
                nodes.put(name, new Node(subject));
            }
        }
        
        /**
         * Add a dependency between subjects
         * 
         * @param fromSubject The prerequisite subject
         * @param toSubject The dependent subject
         */
        public void addDependency(Subject fromSubject, Subject toSubject) {
            String fromName = fromSubject.getName();
            String toName = toSubject.getName();
            
            if (!nodes.containsKey(fromName)) {
                addSubject(fromSubject);
            }
            
            if (!nodes.containsKey(toName)) {
                addSubject(toSubject);
            }
            
            nodes.get(fromName).addEdge(nodes.get(toName));
        }
        
        /**
         * Find all subjects reachable from a given subject using BFS
         * Demonstrates Breadth-First Search algorithm
         * 
         * @param startSubject The starting subject
         * @return List of reachable subjects
         */
        public List<Subject> findReachableSubjects(Subject startSubject) {
            String startName = startSubject.getName();
            if (!nodes.containsKey(startName)) {
                return new ArrayList<>();
            }
            
            List<Subject> result = new ArrayList<>();
            Queue<Node> queue = new LinkedList<>();
            Map<String, Boolean> visited = new HashMap<>();
            
            // Initialize visited status
            for (String name : nodes.keySet()) {
                visited.put(name, false);
            }
            
            // Start BFS
            Node startNode = nodes.get(startName);
            queue.add(startNode);
            visited.put(startName, true);
            
            while (!queue.isEmpty()) {
                Node current = queue.poll();
                result.add(current.subject);
                
                for (Node neighbor : current.edges) {
                    String neighborName = neighbor.subject.getName();
                    if (!visited.get(neighborName)) {
                        visited.put(neighborName, true);
                        queue.add(neighbor);
                    }
                }
            }
            
            return result;
        }
        
        /**
         * Find critical subjects (those that are prerequisites for many others) using DFS
         * Demonstrates Depth-First Search algorithm
         * 
         * @return List of critical subjects sorted by importance
         */
        public List<Subject> findCriticalSubjects() {
            Map<String, Integer> importance = new HashMap<>();
            
            // Initialize importance values
            for (String name : nodes.keySet()) {
                importance.put(name, 0);
            }
            
            // For each node, do DFS to count descendants
            for (Node node : nodes.values()) {
                Map<String, Boolean> visited = new HashMap<>();
                
                // Initialize visited status
                for (String name : nodes.keySet()) {
                    visited.put(name, false);
                }
                
                dfsCountDescendants(node, visited, importance);
            }
            
            // Create result list
            List<Subject> result = new ArrayList<>();
            for (Node node : nodes.values()) {
                result.add(node.subject);
            }
            
            // Sort by importance (number of descendants)
            result.sort((s1, s2) -> {
                String name1 = s1.getName();
                String name2 = s2.getName();
                return Integer.compare(importance.get(name2), importance.get(name1));
            });
            
            return result;
        }
        
        /**
         * Helper method for DFS to count descendants
         * 
         * @param node The current node
         * @param visited Map of visited status
         * @param importance Map of importance values
         * @return Number of descendants
         */
        private int dfsCountDescendants(Node node, Map<String, Boolean> visited, Map<String, Integer> importance) {
            String name = node.subject.getName();
            visited.put(name, true);
            
            int count = 0;
            for (Node neighbor : node.edges) {
                String neighborName = neighbor.subject.getName();
                if (!visited.get(neighborName)) {
                    count += 1 + dfsCountDescendants(neighbor, visited, importance);
                }
            }
            
            importance.put(name, importance.get(name) + count);
            return count;
        }
        
        /**
         * Find an optimal order of subjects using topological sort
         * Demonstrates Topological Sort algorithm
         * 
         * @return List of subjects in optimal order
         */
        public List<Subject> findOptimalOrder() {
            List<Subject> result = new ArrayList<>();
            Map<String, Boolean> visited = new HashMap<>();
            Stack<Node> stack = new Stack<>();
            
            // Initialize visited status
            for (String name : nodes.keySet()) {
                visited.put(name, false);
            }
            
            // Perform topological sort
            for (Node node : nodes.values()) {
                if (!visited.get(node.subject.getName())) {
                    topologicalSort(node, visited, stack);
                }
            }
            
            // Create result list from stack
            while (!stack.isEmpty()) {
                result.add(stack.pop().subject);
            }
            
            return result;
        }
        
        /**
         * Helper method for topological sort
         * 
         * @param node The current node
         * @param visited Map of visited status
         * @param stack Stack for result
         */
        private void topologicalSort(Node node, Map<String, Boolean> visited, Stack<Node> stack) {
            String name = node.subject.getName();
            visited.put(name, true);
            
            for (Node neighbor : node.edges) {
                String neighborName = neighbor.subject.getName();
                if (!visited.get(neighborName)) {
                    topologicalSort(neighbor, visited, stack);
                }
            }
            
            stack.push(node);
        }
        
        /**
         * Inner class representing a node in the graph
         */
        private static class Node {
            private Subject subject;
            private List<Node> edges;
            
            public Node(Subject subject) {
                this.subject = subject;
                this.edges = new ArrayList<>();
            }
            
            public void addEdge(Node node) {
                edges.add(node);
            }
        }
    }
    
    /**
     * Create a subject graph from a semester
     * 
     * @param semester The semester
     * @return A subject graph
     */
    public static SubjectGraph createSubjectGraph(Semester semester) {
        SubjectGraph graph = new SubjectGraph();
        List<Subject> subjects = semester.getSubjects();
        
        // Add all subjects to the graph
        for (Subject subject : subjects) {
            graph.addSubject(subject);
        }
        
        // Add dependencies based on assessment weights
        // (higher weight subjects are prerequisites for lower weight subjects)
        for (int i = 0; i < subjects.size(); i++) {
            for (int j = 0; j < subjects.size(); j++) {
                if (i != j) {
                    Subject s1 = subjects.get(i);
                    Subject s2 = subjects.get(j);
                    
                    // Check if s1 has a higher weight final exam/project than s2
                    boolean s1HasHigherWeight = false;
                    
                    AssessmentType s1FinalExam = s1.getAssessmentType("final_exam");
                    AssessmentType s2FinalExam = s2.getAssessmentType("final_exam");
                    
                    if (s1FinalExam != null && s2FinalExam != null) {
                        if (s1FinalExam.getWeight() > s2FinalExam.getWeight()) {
                            s1HasHigherWeight = true;
                        }
                    }
                    
                    if (s1HasHigherWeight) {
                        // s1 is a prerequisite for s2
                        graph.addDependency(s1, s2);
                    }
                }
            }
        }
        
        return graph;
    }
    
    /**
     * Analyze assessment dependencies for a subject
     * 
     * @param subject The subject
     * @return A map of assessment ID to list of dependent assessment IDs
     */
    public static Map<Integer, List<Integer>> analyzeAssessmentDependencies(Subject subject) {
        Map<Integer, List<Integer>> dependencies = new HashMap<>();
        
        // For each assessment type
        for (AssessmentType assessmentType : subject.getAssessmentTypes().values()) {
            List<Assessment> assessments = assessmentType.getAssessments();
            
            // For each assessment
            for (int i = 0; i < assessments.size(); i++) {
                Assessment current = assessments.get(i);
                int currentId = current.getId();
                
                dependencies.put(currentId, new ArrayList<>());
                
                // If this is an assignment or quiz, it depends on previous ones
                if (assessmentType.getType().equals("assignment") || assessmentType.getType().equals("quiz")) {
                    for (int j = 0; j < i; j++) {
                        Assessment previous = assessments.get(j);
                        dependencies.get(currentId).add(previous.getId());
                    }
                }
            }
        }
        
        // Midterms depend on assignments and quizzes
        AssessmentType midtermType = subject.getAssessmentType("midterm");
        if (midtermType != null && !midtermType.getAssessments().isEmpty()) {
            Assessment midterm = midtermType.getAssessments().get(0);
            int midtermId = midterm.getId();
            
            dependencies.put(midtermId, new ArrayList<>());
            
            // Add dependencies on assignments and quizzes
            for (AssessmentType assessmentType : subject.getAssessmentTypes().values()) {
                if (assessmentType.getType().equals("assignment") || assessmentType.getType().equals("quiz")) {
                    for (Assessment assessment : assessmentType.getAssessments()) {
                        dependencies.get(midtermId).add(assessment.getId());
                    }
                }
            }
        }
        
        // Final exam depends on everything
        AssessmentType finalExamType = subject.getAssessmentType("final_exam");
        if (finalExamType != null && !finalExamType.getAssessments().isEmpty()) {
            Assessment finalExam = finalExamType.getAssessments().get(0);
            int finalExamId = finalExam.getId();
            
            dependencies.put(finalExamId, new ArrayList<>());
            
            // Add dependencies on all other assessments
            for (AssessmentType assessmentType : subject.getAssessmentTypes().values()) {
                if (!assessmentType.getType().equals("final_exam")) {
                    for (Assessment assessment : assessmentType.getAssessments()) {
                        dependencies.get(finalExamId).add(assessment.getId());
                    }
                }
            }
        }
        
        return dependencies;
    }
}