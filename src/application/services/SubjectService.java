package application.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import application.database.AssessmentRepository;
import application.database.AssessmentTypeRepository;
import application.database.SubjectRepository;
import application.models.Assessment;
import application.models.AssessmentType;
import application.models.Subject;

/**
 * Service class for subject-related business logic
 */
public class SubjectService {
    private SubjectRepository subjectRepository;
    private AssessmentTypeRepository assessmentTypeRepository;
    private AssessmentRepository assessmentRepository;
    
    public SubjectService() {
        subjectRepository = new SubjectRepository();
        assessmentTypeRepository = new AssessmentTypeRepository();
        assessmentRepository = new AssessmentRepository();
    }
    
    /**
     * Create a new subject with assessment types
     * 
     * @param semesterId The semester ID
     * @param name The subject name
     * @param assessmentConfig Map of assessment type names to their configurations
     * @return The created subject
     * @throws SQLException If there's an error during database operation
     * @throws IllegalArgumentException If validation fails
     */
    public Subject createSubject(int semesterId, String name, Map<String, Object[]> assessmentConfig) 
            throws SQLException, IllegalArgumentException {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name cannot be empty");
        }
        
        // Check if semester already has maximum subjects (2)
        int subjectCount = subjectRepository.countBySemesterId(semesterId);
        if (subjectCount >= 2) {
            throw new IllegalArgumentException("Maximum number of subjects (2) reached for this semester");
        }
        
        // Create subject
        Subject subject = new Subject(semesterId, name);
        subject = subjectRepository.createSubject(subject);
        
        System.out.println("Created subject: " + subject.getId() + ", " + subject.getName());
        
        // Create assessment types
        for (Map.Entry<String, Object[]> entry : assessmentConfig.entrySet()) {
            String type = entry.getKey();
            Object[] config = entry.getValue();
            
            int count = 0;
            double weight = 0.0;
            
            if (type.equals("assignment") || type.equals("quiz")) {
                count = (int) config[0];
                weight = (double) config[1];
                System.out.println("Adding assessment type: " + type + ", count: " + count + ", weight: " + weight);
            } else {
                // For midterm, final_exam, and final_project
                weight = (double) config[0];
                System.out.println("Adding assessment type: " + type + ", weight: " + weight);
            }
            
            if (weight > 0) {
                AssessmentType assessmentType = new AssessmentType(subject.getId(), type, count, weight);
                assessmentType = assessmentTypeRepository.createAssessmentType(assessmentType);
                
                System.out.println("Created assessment type: " + assessmentType.getId() + ", " + assessmentType.getType());
                
                // Create individual assessments
                List<Assessment> assessments = new ArrayList<>();
                
                if (type.equals("assignment") || type.equals("quiz")) {
                    for (int i = 1; i <= count; i++) {
                        Assessment assessment = new Assessment(assessmentType.getId(), i);
                        assessments.add(assessment);
                    }
                } else {
                    // For midterm, final_exam, and final_project (count = 0)
                    Assessment assessment = new Assessment(assessmentType.getId(), 0);
                    assessments.add(assessment);
                }
                
                if (!assessments.isEmpty()) {
                    System.out.println("Creating " + assessments.size() + " assessments");
                    assessmentRepository.createBatch(assessments);
                }
                
                subject.addAssessmentType(assessmentType);
            }
        }
        
        // Reload the subject to get all assessment types and assessments
        return getSubjectById(subject.getId());
    }
    
    /**
     * Get all subjects for a semester
     * 
     * @param semesterId The semester ID
     * @return The list of subjects
     * @throws SQLException If there's an error during database operation
     */
    public List<Subject> getSubjectsBySemester(int semesterId) throws SQLException {
        List<Subject> subjects = subjectRepository.findAllBySemesterId(semesterId);
        
        // Load assessment types and assessments for each subject
        for (Subject subject : subjects) {
            loadAssessmentData(subject);
        }
        
        return subjects;
    }
    
    /**
     * Get a subject by ID, including its assessment types and assessments
     * 
     * @param id The subject ID
     * @return The subject
     * @throws SQLException If there's an error during database operation
     */
    public Subject getSubjectById(int id) throws SQLException {
        Subject subject = subjectRepository.findById(id);
        
        if (subject != null) {
            // Debug
            System.out.println("Loading assessment data for subject ID: " + id);
            subjectRepository.debugSubjectData(id);  // Call the debug method
            
            loadAssessmentData(subject);
            
            // Verify loaded data
            int assessmentTypeCount = subject.getAssessmentTypes().size();
            System.out.println("Loaded " + assessmentTypeCount + " assessment types into subject model.");
            
            for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                System.out.println("Type: " + entry.getKey() + ", Count: " + entry.getValue().getAssessments().size());
            }
        }
        
        return subject;
    }

    /**
     * Load assessment types and assessments for a subject
     * 
     * @param subject The subject
     * @throws SQLException If there's an error during database operation
     */
    private void loadAssessmentData(Subject subject) throws SQLException {
        List<AssessmentType> assessmentTypes = assessmentTypeRepository.findAllBySubjectId(subject.getId());
        
        for (AssessmentType assessmentType : assessmentTypes) {
            System.out.println("Loading assessments for type: " + assessmentType.getType() + ", ID: " + assessmentType.getId());
            List<Assessment> assessments = assessmentRepository.findAllByAssessmentTypeId(assessmentType.getId());
            System.out.println("Found " + assessments.size() + " assessments");
            
            assessmentType.setAssessments(assessments);
            subject.addAssessmentType(assessmentType);
        }
    }
    
    /**
     * Update a subject
     * 
     * @param subject The subject to update
     * @return The updated subject
     * @throws SQLException If there's an error during database operation
     * @throws IllegalArgumentException If validation fails
     */
    public Subject updateSubject(Subject subject) throws SQLException, IllegalArgumentException {
        // Validate input
        if (subject.getName() == null || subject.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name cannot be empty");
        }
        
        return subjectRepository.updateSubject(subject);
    }
    
    /**
     * Update assessment type weightages
     * 
     * @param assessmentType The assessment type to update
     * @return The updated assessment type
     * @throws SQLException If there's an error during database operation
     */
    public AssessmentType updateAssessmentType(AssessmentType assessmentType) throws SQLException {
        return assessmentTypeRepository.updateAssessmentType(assessmentType);
    }
    
    /**
     * Update assessment score and finalization status
     * 
     * @param assessment The assessment to update
     * @return The updated assessment
     * @throws SQLException If there's an error during database operation
     */
    public Assessment updateAssessment(Assessment assessment) throws SQLException {
        // Validate input
        if (assessment == null) {
            throw new IllegalArgumentException("Assessment cannot be null");
        }
        
        if (assessment.getScore() < 0 || assessment.getScore() > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        
        // Log the update
        System.out.println("SubjectService: Updating assessment ID: " + assessment.getId() + 
                          ", Score: " + assessment.getScore() + 
                          ", Final: " + assessment.isFinal());
        
        // Perform the update
        Assessment updatedAssessment = assessmentRepository.updateAssessment(assessment);
        
        return updatedAssessment;
    }
    
    /**
     * Delete a subject
     * 
     * @param id The subject ID
     * @throws SQLException If there's an error during database operation
     */
    public void deleteSubject(int id) throws SQLException {
        subjectRepository.deleteSubject(id);
    }
    
}