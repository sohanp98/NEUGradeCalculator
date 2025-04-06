package application.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import application.models.Assessment;
import application.models.AssessmentType;
import application.models.Semester;
import application.models.Subject;
import application.models.User;

/**
 * Utility class for exporting data to various formats
 */
public class ExportUtility {
    
    /**
     * Export semester data to CSV
     * 
     * @param semester The semester to export
     * @param filePath The file path to write to
     * @throws IOException If an error occurs during writing
     */
    public static void exportSemesterToCSV(Semester semester, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println("Semester: " + semester.getName());
            writer.println("GPA: " + String.format("%.2f", semester.calculateGPA()));
            writer.println();
            
            // Write subject data
            List<Subject> subjects = semester.getSubjects();
            for (Subject subject : subjects) {
                writer.println("Subject: " + subject.getName());
                writer.println("Overall Percentage: " + String.format("%.2f%%", subject.calculateOverallPercentage()));
                writer.println("Letter Grade: " + subject.calculateLetterGrade());
                writer.println("GPA: " + String.format("%.2f", subject.calculateGPA()));
                writer.println();
                
                writer.println("Assessment Type,Count,Weight,Average Score,Weighted Score");
                
                for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                    AssessmentType assessmentType = entry.getValue();
                    if (assessmentType.getWeight() > 0) {
                        writer.printf("%s,%d,%.2f%%,%.2f%%,%.2f%%\n",
                            assessmentType.getDisplayName(),
                            assessmentType.getCount(),
                            assessmentType.getWeight(),
                            assessmentType.calculateAverageScore(),
                            assessmentType.calculateWeightedScore()
                        );
                    }
                }
                
                writer.println();
                
                writer.println("Assessment,Score,Finalized");
                
                for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                    AssessmentType assessmentType = entry.getValue();
                    if (assessmentType.getWeight() > 0) {
                        for (Assessment assessment : assessmentType.getAssessments()) {
                            writer.printf("%s,%.2f%%,%s\n",
                                assessment.getDisplayName(assessmentType.getType()),
                                assessment.getScore(),
                                assessment.isFinal() ? "Yes" : "No"
                            );
                        }
                    }
                }
                
                writer.println("\n");
            }
        }
    }
    
    /**
     * Export all semesters data for a user to CSV
     * 
     * @param user The user
     * @param semesters The list of semesters to export
     * @param filePath The file path to write to
     * @throws IOException If an error occurs during writing
     */
    public static void exportAllSemestersToCSV(User user, List<Semester> semesters, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println("Student: " + user.getFullName());
            
            // Calculate overall GPA
            double totalGPA = 0.0;
            int semesterCount = 0;
            
            for (Semester semester : semesters) {
                double semesterGPA = semester.calculateGPA();
                if (semesterGPA > 0) {
                    totalGPA += semesterGPA;
                    semesterCount++;
                }
            }
            
            double overallGPA = semesterCount > 0 ? totalGPA / semesterCount : 0.0;
            
            writer.println("Overall GPA: " + String.format("%.2f", overallGPA));
            writer.println();
            
            writer.println("Semester,GPA");
            for (Semester semester : semesters) {
                writer.printf("%s,%.2f\n", semester.getName(), semester.calculateGPA());
            }
            
            writer.println("\n");
            
            writer.println("Subject,Semester,Percentage,Letter Grade,GPA");
            
            for (Semester semester : semesters) {
                for (Subject subject : semester.getSubjects()) {
                    writer.printf("%s,%s,%.2f%%,%s,%.2f\n",
                        subject.getName(),
                        semester.getName(),
                        subject.calculateOverallPercentage(),
                        subject.calculateLetterGrade(),
                        subject.calculateGPA()
                    );
                }
            }
        }
    }
    
    /**
     * Export semester data to HTML
     * 
     * @param semester The semester to export
     * @param filePath The file path to write to
     * @throws IOException If an error occurs during writing
     */
    public static void exportSemesterToHTML(Semester semester, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // HTML header
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang=\"en\">");
            writer.println("<head>");
            writer.println("    <meta charset=\"UTF-8\">");
            writer.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            writer.println("    <title>Semester Report - " + semester.getName() + "</title>");
            writer.println("    <style>");
            writer.println("        body { font-family: Arial, sans-serif; margin: 20px; }");
            writer.println("        h1, h2, h3 { color: #333; }");
            writer.println("        table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }");
            writer.println("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            writer.println("        th { background-color: #f2f2f2; }");
            writer.println("        .subject { margin-bottom: 30px; border: 1px solid #ccc; padding: 15px; border-radius: 5px; }");
            writer.println("        .header { display: flex; justify-content: space-between; align-items: center; }");
            writer.println("        .good { color: green; }");
            writer.println("        .average { color: orange; }");
            writer.println("        .poor { color: red; }");
            writer.println("        .finalized { font-weight: bold; }");
            writer.println("    </style>");
            writer.println("</head>");
            writer.println("<body>");
            
            // Semester header
            writer.println("    <h1>Semester Report</h1>");
            writer.println("    <div class=\"header\">");
            writer.println("        <h2>" + semester.getName() + "</h2>");
            
            double semesterGPA = semester.calculateGPA();
            String gpaClass = semesterGPA >= 3.5 ? "good" : (semesterGPA >= 2.5 ? "average" : "poor");
            
            writer.println("        <h3>GPA: <span class=\"" + gpaClass + "\">" + String.format("%.2f", semesterGPA) + "</span></h3>");
            writer.println("    </div>");
            
            // Subjects
            List<Subject> subjects = semester.getSubjects();
            for (Subject subject : subjects) {
                writer.println("    <div class=\"subject\">");
                writer.println("        <div class=\"header\">");
                writer.println("            <h3>" + subject.getName() + "</h3>");
                
                double subjectGPA = subject.calculateGPA();
                String subjectGpaClass = subjectGPA >= 3.5 ? "good" : (subjectGPA >= 2.5 ? "average" : "poor");
                
                writer.println("            <h4>GPA: <span class=\"" + subjectGpaClass + "\">" + String.format("%.2f", subjectGPA) + "</span></h4>");
                writer.println("        </div>");
                
                // Overall performance
                writer.println("        <p>Overall Percentage: " + String.format("%.2f%%", subject.calculateOverallPercentage()) + "</p>");
                writer.println("        <p>Letter Grade: " + subject.calculateLetterGrade() + "</p>");
                
                // Assessment types
                writer.println("        <h4>Assessment Types</h4>");
                writer.println("        <table>");
                writer.println("            <tr>");
                writer.println("                <th>Assessment Type</th>");
                writer.println("                <th>Count</th>");
                writer.println("                <th>Weight</th>");
                writer.println("                <th>Average Score</th>");
                writer.println("                <th>Weighted Score</th>");
                writer.println("            </tr>");
                
                for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                    AssessmentType assessmentType = entry.getValue();
                    if (assessmentType.getWeight() > 0) {
                        writer.println("            <tr>");
                        writer.println("                <td>" + assessmentType.getDisplayName() + "</td>");
                        writer.println("                <td>" + assessmentType.getCount() + "</td>");
                        writer.println("                <td>" + String.format("%.2f%%", assessmentType.getWeight()) + "</td>");
                        
                        double avgScore = assessmentType.calculateAverageScore();
                        String avgScoreClass = avgScore >= 90 ? "good" : (avgScore >= 70 ? "average" : "poor");
                        
                        writer.println("                <td class=\"" + avgScoreClass + "\">" + String.format("%.2f%%", avgScore) + "</td>");
                        writer.println("                <td>" + String.format("%.2f%%", assessmentType.calculateWeightedScore()) + "</td>");
                        writer.println("            </tr>");
                    }
                }
                
                writer.println("        </table>");
                
                // Individual assessments
                writer.println("        <h4>Individual Assessments</h4>");
                writer.println("        <table>");
                writer.println("            <tr>");
                writer.println("                <th>Assessment</th>");
                writer.println("                <th>Score</th>");
                writer.println("                <th>Status</th>");
                writer.println("            </tr>");
                
                for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                    AssessmentType assessmentType = entry.getValue();
                    if (assessmentType.getWeight() > 0) {
                        for (Assessment assessment : assessmentType.getAssessments()) {
                            String rowClass = assessment.isFinal() ? "finalized" : "";
                            
                            writer.println("            <tr class=\"" + rowClass + "\">");
                            writer.println("                <td>" + assessment.getDisplayName(assessmentType.getType()) + "</td>");
                            
                            double score = assessment.getScore();
                            String scoreClass = score >= 90 ? "good" : (score >= 70 ? "average" : "poor");
                            
                            writer.println("                <td class=\"" + scoreClass + "\">" + String.format("%.2f%%", score) + "</td>");
                            writer.println("                <td>" + (assessment.isFinal() ? "Finalized" : "Pending") + "</td>");
                            writer.println("            </tr>");
                        }
                    }
                }
                
                writer.println("        </table>");
                writer.println("    </div>");
            }
            
            // Footer
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            writer.println("    <footer>");
            writer.println("        <p>Generated on " + dateFormat.format(new Date()) + "</p>");
            writer.println("    </footer>");
            
            writer.println("</body>");
            writer.println("</html>");
        }
    }
    
    /**
     * Create a directory for exports if it doesn't exist
     * 
     * @return The path to the exports directory
     * @throws IOException If an error occurs during directory creation
     */
    public static String getExportsDirectory() throws IOException {
        String userHomeDir = System.getProperty("user.home");
        Path exportsPath = Paths.get(userHomeDir, "GradeCalculator", "exports");
        
        if (!Files.exists(exportsPath)) {
            Files.createDirectories(exportsPath);
        }
        
        return exportsPath.toString();
    }
    
    /**
     * Generate a file name for an export based on the type and date
     * 
     * @param baseFileName The base file name
     * @param extension The file extension
     * @return The generated file name
     */
    public static String generateExportFileName(String baseFileName, String extension) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        
        return baseFileName + "_" + timestamp + "." + extension;
    }
    
    /**
     * Show a file save dialog to the user
     * 
     * @param title The dialog title
     * @param initialFileName The initial file name
     * @param extension The file extension
     * @return The selected file, or null if the dialog was cancelled
     */
    public static File showSaveFileDialog(String title, String initialFileName, String extension) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle(title);
        
        try {
            fileChooser.setInitialDirectory(new File(getExportsDirectory()));
        } catch (IOException e) {
            // Default to user home directory if exports directory doesn't exist
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        
        fileChooser.setInitialFileName(initialFileName + "." + extension);
        
        // Add extension filter
        javafx.stage.FileChooser.ExtensionFilter extFilter;
        switch (extension.toLowerCase()) {
            case "csv":
                extFilter = new javafx.stage.FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv");
                break;
            case "html":
                extFilter = new javafx.stage.FileChooser.ExtensionFilter("HTML Files (*.html)", "*.html");
                break;
            default:
                extFilter = new javafx.stage.FileChooser.ExtensionFilter("All Files (*.*)", "*.*");
        }
        
        fileChooser.getExtensionFilters().add(extFilter);
        
        return fileChooser.showSaveDialog(null);
    }
}