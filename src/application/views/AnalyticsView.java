package application.views;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import application.controllers.AnalyticsController;
import application.models.Assessment;
import application.models.AssessmentType;
import application.models.Semester;
import application.models.Subject;
import application.utils.AnalyticsUtility;
import application.utils.ExportUtility;
import application.utils.GradeCalculatorFactory;

/**
 * View class for the analytics screen
 */
public class AnalyticsView {
    private BorderPane mainLayout;
    private AnalyticsController controller;
    private Semester semester;
    
    /**
     * Constructor for AnalyticsView
     * 
     * @param semester The semester to analyze
     */
    public AnalyticsView(Semester semester) {
        try {
            System.out.println("\n===== CREATING ANALYTICS VIEW =====");
            System.out.println("Initial semester: " + semester.getName() + ", ID: " + semester.getId());
            
            this.semester = semester;
            this.controller = new AnalyticsController(semester);
            
            // Get fresh semester data
            this.semester = controller.getSemester();
            System.out.println("Refreshed semester data");
            
            // Force calculations to verify data
            double semesterGPA = this.semester.calculateGPA();
            System.out.println("Semester GPA: " + semesterGPA);
            
            // Initialize the view with fresh data
            initialize();
            
            System.out.println("===== ANALYTICS VIEW CREATION COMPLETE =====\n");
        } catch (Exception e) {
            System.err.println("Error creating analytics view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize the analytics view with fresh data
     */
    private void initialize() {
        try {
            System.out.println("\n===== INITIALIZING ANALYTICS VIEW =====");
            
            // Get fresh semester data
            semester = controller.getSemester();
            System.out.println("Working with semester: " + semester.getName() + ", ID: " + semester.getId());
            
            // Refresh all subjects with fresh data from database
            List<Subject> subjects = semester.getSubjects();
            System.out.println("Found " + subjects.size() + " subjects");
            
            // For each subject, get fresh data and force calculations
            for (int i = 0; i < subjects.size(); i++) {
                Subject subject = subjects.get(i);
                
                // Get fresh data directly from database
                Subject freshSubject = controller.getSubjectById(subject.getId());
                
                if (freshSubject != null) {
                    System.out.println("Refreshed subject: " + freshSubject.getName());
                    
                    // Replace with fresh data
                    subjects.set(i, freshSubject);
                    
                    // Force calculations to verify data
                    double percentage = freshSubject.calculateOverallPercentage();
                    String grade = freshSubject.calculateLetterGrade();
                    double gpa = freshSubject.calculateGPA();
                    
                    System.out.println("  Values - Percentage: " + percentage + 
                                      ", Grade: " + grade + 
                                      ", GPA: " + gpa);
                } else {
                    System.out.println("Could not refresh subject ID " + subject.getId());
                }
            }
            
            // Create the UI with the refreshed data
            mainLayout = new BorderPane();
            mainLayout.setPadding(new Insets(20));
            
            // Create header
            Label headerLabel = new Label("Analytics - " + semester.getName());
            headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            
            HBox headerBox = new HBox(20);
            headerBox.setAlignment(Pos.CENTER_LEFT);
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Button backButton = new Button("Back to Semester");
            Button exportButton = new Button("Export Reports");
            
            headerBox.getChildren().addAll(headerLabel, spacer, exportButton, backButton);
            mainLayout.setTop(headerBox);
            BorderPane.setMargin(headerBox, new Insets(0, 0, 20, 0));
            
            // Create tab pane with fresh data
            TabPane tabPane = new TabPane();
            
            // Overview tab
            Tab overviewTab = new Tab("Overview");
            overviewTab.setClosable(false);
            ScrollPane overviewScrollPane = new ScrollPane();
            overviewScrollPane.setFitToWidth(true);
            overviewScrollPane.setContent(createOverviewContent());
            overviewTab.setContent(overviewScrollPane);
            
            // Grade Distribution tab
            Tab distributionTab = new Tab("Grade Distribution");
            distributionTab.setClosable(false);
            ScrollPane distributionScrollPane = new ScrollPane();
            distributionScrollPane.setFitToWidth(true);
            distributionScrollPane.setContent(createDistributionContent());
            distributionTab.setContent(distributionScrollPane);
            
            // Trends tab
            Tab trendsTab = new Tab("Trends");
            trendsTab.setClosable(false);
            ScrollPane trendsScrollPane = new ScrollPane();
            trendsScrollPane.setFitToWidth(true);
            trendsScrollPane.setContent(createTrendsContent());
            trendsTab.setContent(trendsScrollPane);
            
            // Projections tab
            Tab projectionsTab = new Tab("Projections");
            projectionsTab.setClosable(false);
            ScrollPane projectionsScrollPane = new ScrollPane();
            projectionsScrollPane.setFitToWidth(true);
            projectionsScrollPane.setContent(createProjectionsContent());
            projectionsTab.setContent(projectionsScrollPane);
            
            // Recommendations tab
            Tab recommendationsTab = new Tab("Recommendations");
            recommendationsTab.setClosable(false);
            ScrollPane recommendationsScrollPane = new ScrollPane();
            recommendationsScrollPane.setFitToWidth(true);
            recommendationsScrollPane.setContent(createRecommendationsContent());
            recommendationsTab.setContent(recommendationsScrollPane);
            
            tabPane.getTabs().addAll(overviewTab, distributionTab, trendsTab, projectionsTab, recommendationsTab);
            mainLayout.setCenter(tabPane);
            
            // Set up event handlers
            backButton.setOnAction(e -> controller.navigateToSemesterView());
            exportButton.setOnAction(e -> showExportOptions());
            
            System.out.println("===== ANALYTICS VIEW INITIALIZATION COMPLETE =====\n");
        } catch (Exception e) {
            System.err.println("Error initializing analytics view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load fresh subject data directly from the database
     * 
     * @return A list of subjects with fresh data
     */
    private List<Subject> loadFreshSubjectData() {
        List<Subject> freshSubjects = new ArrayList<>();
        
        try {
            // Get subjects from semester
            List<Subject> subjects = semester.getSubjects();
            
            // Create a SubjectService to directly access the database
            application.services.SubjectService subjectService = new application.services.SubjectService();
            
            for (Subject subject : subjects) {
                // Load fresh data directly using repository pattern
                Subject freshSubject = subjectService.getSubjectById(subject.getId());
                
                if (freshSubject != null) {
                    freshSubjects.add(freshSubject);
                    
                    // Pre-calculate values to ensure they're ready
                    double percentage = freshSubject.calculateOverallPercentage();
                    String letterGrade = freshSubject.calculateLetterGrade();
                    double gpa = freshSubject.calculateGPA();
                    
                    System.out.println("Loaded fresh data for " + freshSubject.getName() + 
                                      ": " + percentage + "%, " + 
                                      letterGrade + ", " + 
                                      "GPA " + gpa);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading fresh subject data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return freshSubjects;
    }
    
    /**
     * Create content for the overview tab
     * 
     * @return The overview content
     */
    private VBox createOverviewContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Semester Overview");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // First, get fresh subject data
        List<Subject> freshSubjects = loadFreshSubjectData();
        
        if (freshSubjects.isEmpty()) {
            Label noSubjectsLabel = new Label("No subjects to analyze.");
            content.getChildren().addAll(titleLabel, noSubjectsLabel);
            return content;
        }
        
        // Calculate semester GPA from fresh subjects
        double totalGPA = 0.0;
        int validSubjectCount = 0;
        
        for (Subject subject : freshSubjects) {
            double subjectGPA = subject.calculateGPA();
            if (subjectGPA > 0) {
                totalGPA += subjectGPA;
                validSubjectCount++;
            }
        }
        
        double semesterGPA = validSubjectCount > 0 ? totalGPA / validSubjectCount : 0.0;
        
        // Semester GPA section
        VBox gpaBox = new VBox(10);
        gpaBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        Label gpaLabel = new Label("Semester GPA: " + String.format("%.2f", semesterGPA));
        gpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // GPA classification
        String gpaClassification;
        if (semesterGPA >= 3.7) {
            gpaClassification = "Excellent - Dean's List";
            gpaLabel.setTextFill(Color.GREEN);
        } else if (semesterGPA >= 3.0) {
            gpaClassification = "Good";
            gpaLabel.setTextFill(Color.BLUE);
        } else if (semesterGPA >= 2.0) {
            gpaClassification = "Satisfactory";
            gpaLabel.setTextFill(Color.ORANGE);
        } else {
            gpaClassification = "Needs Improvement";
            gpaLabel.setTextFill(Color.RED);
        }
        
        Label classificationLabel = new Label("Classification: " + gpaClassification);
        
        gpaBox.getChildren().addAll(gpaLabel, classificationLabel);
        
        // Subject performance section
        Label subjectsSectionLabel = new Label("Subject Performance");
        subjectsSectionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        VBox subjectsBox = new VBox(15);
        
        for (Subject subject : freshSubjects) {
            BorderPane subjectPane = new BorderPane();
            subjectPane.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            
            Label subjectNameLabel = new Label(subject.getName());
            subjectNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            
            // Create a small bar chart for overall percentage
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis(0, 100, 10);
            yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, null, "%"));
            
            BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
            chart.setLegendVisible(false);
            chart.setAnimated(false);
            chart.setMaxHeight(150);
            chart.setMaxWidth(300);
            chart.setTitle(null);
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            double percentage = subject.calculateOverallPercentage();
            series.getData().add(new XYChart.Data<>("Overall", percentage));
            
            chart.getData().add(series);
            
            // Color bar based on grade
            String letterGrade = subject.calculateLetterGrade();
            String barColor;
            
            if (letterGrade.startsWith("A")) {
                barColor = "-fx-bar-fill: green;";
            } else if (letterGrade.startsWith("B")) {
                barColor = "-fx-bar-fill: blue;";
            } else if (letterGrade.startsWith("C")) {
                barColor = "-fx-bar-fill: orange;";
            } else {
                barColor = "-fx-bar-fill: red;";
            }
            
            // Apply the color to the bar
            if (!series.getData().isEmpty() && series.getData().get(0).getNode() != null) {
                series.getData().get(0).getNode().setStyle(barColor);
            }
            
            // Summary text
            VBox summaryBox = new VBox(5);
            Label percentageLabel = new Label(String.format("%.1f%%", percentage));
            percentageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            
            Label gradeLabel = new Label("Grade: " + letterGrade);
            double gpa = subject.calculateGPA();
            Label gpaValueLabel = new Label("GPA: " + String.format("%.1f", gpa));
            
            // Assessment types summary
            StringBuilder assessmentSummary = new StringBuilder("Assessment Types: ");
            for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                AssessmentType assessmentType = entry.getValue();
                if (assessmentType.getWeight() > 0) {
                    assessmentSummary.append(assessmentType.getDisplayName())
                                    .append(" (").append(String.format("%.0f%%", assessmentType.getWeight()))
                                    .append("), ");
                }
            }
            
            // Remove trailing comma and space
            if (assessmentSummary.length() > 18) {
                assessmentSummary.setLength(assessmentSummary.length() - 2);
            }
            
            Label assessmentTypesLabel = new Label(assessmentSummary.toString());
            assessmentTypesLabel.setWrapText(true);
            
            summaryBox.getChildren().addAll(percentageLabel, gradeLabel, gpaValueLabel, assessmentTypesLabel);
            
            // Combine chart and summary
            HBox contentBox = new HBox(20);
            contentBox.setAlignment(Pos.CENTER_LEFT);
            contentBox.getChildren().addAll(chart, summaryBox);
            
            subjectPane.setTop(subjectNameLabel);
            subjectPane.setCenter(contentBox);
            
            BorderPane.setMargin(subjectNameLabel, new Insets(0, 0, 10, 0));
            
            subjectsBox.getChildren().add(subjectPane);
        }
        
        // Statistical summary
        Label statisticsSectionLabel = new Label("Statistical Summary");
        statisticsSectionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(10);
        statsGrid.setPadding(new Insets(15));
        statsGrid.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        // Collect all assessment scores
        List<Double> allScores = new ArrayList<>();
        for (Subject subject : freshSubjects) {
            for (AssessmentType assessmentType : subject.getAssessmentTypes().values()) {
                if (assessmentType.getWeight() > 0) {
                    for (Assessment assessment : assessmentType.getAssessments()) {
                        double score = assessment.getScore();
                        // Only include non-zero scores to avoid skewing statistics
                        if (score > 0) {
                            allScores.add(score);
                        }
                    }
                }
            }
        }
        
        // Calculate statistics
        Map<String, Double> stats = application.utils.AnalyticsUtility.calculateStatistics(allScores);
        
        // Add statistics to grid
        statsGrid.add(new Label("Total Assessments:"), 0, 0);
        statsGrid.add(new Label(String.format("%.0f", stats.get("count"))), 1, 0);
        
        statsGrid.add(new Label("Minimum Score:"), 0, 1);
        statsGrid.add(new Label(String.format("%.1f%%", stats.get("min"))), 1, 1);
        
        statsGrid.add(new Label("Maximum Score:"), 0, 2);
        statsGrid.add(new Label(String.format("%.1f%%", stats.get("max"))), 1, 2);
        
        statsGrid.add(new Label("Mean Score:"), 0, 3);
        statsGrid.add(new Label(String.format("%.1f%%", stats.get("mean"))), 1, 3);
        
        statsGrid.add(new Label("Median Score:"), 0, 4);
        statsGrid.add(new Label(String.format("%.1f%%", stats.get("median"))), 1, 4);
        
        statsGrid.add(new Label("Standard Deviation:"), 0, 5);
        statsGrid.add(new Label(String.format("%.1f", stats.get("standardDeviation"))), 1, 5);
        
        content.getChildren().addAll(titleLabel, gpaBox, subjectsSectionLabel, subjectsBox, 
                                  statisticsSectionLabel, statsGrid);
        
        return content;
    }
    
    /**
     * Create content for the grade distribution tab
     * 
     * @return The grade distribution content
     */
    private VBox createDistributionContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Grade Distribution");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Get fresh subject data
        List<Subject> freshSubjects = loadFreshSubjectData();
        
        if (freshSubjects.isEmpty()) {
            Label noSubjectsLabel = new Label("No subjects to analyze.");
            content.getChildren().addAll(titleLabel, noSubjectsLabel);
            return content;
        }
        
        for (Subject subject : freshSubjects) {
            VBox subjectBox = new VBox(15);
            subjectBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            
            Label subjectLabel = new Label(subject.getName());
            subjectLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            
            // Get scores for each assessment
            boolean hasAssessmentData = false;
            Map<String, List<Double>> assessmentScores = new HashMap<>();
            
            for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                AssessmentType assessmentType = entry.getValue();
                
                // Only process types with weight > 0
                if (assessmentType.getWeight() > 0) {
                    List<Double> scores = new ArrayList<>();
                    
                    for (Assessment assessment : assessmentType.getAssessments()) {
                        // Only include scores that have been set (> 0)
                        double score = assessment.getScore();
                        if (score > 0) {
                            scores.add(score);
                            hasAssessmentData = true;
                        }
                    }
                    
                    if (!scores.isEmpty()) {
                        assessmentScores.put(assessmentType.getDisplayName(), scores);
                    }
                }
            }
            
            if (!hasAssessmentData) {
                Label noDistributionLabel = new Label("No assessment grades entered yet.");
                subjectBox.getChildren().addAll(subjectLabel, noDistributionLabel);
            } else {
                TabPane assessmentTabPane = new TabPane();
                assessmentTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                
                // Create a summary tab for all assessments combined
                Tab summaryTab = new Tab("All Assessments");
                VBox summaryContent = new VBox(15);
                summaryContent.setPadding(new Insets(15));
                
                // Combine all scores
                List<Double> allScores = new ArrayList<>();
                for (List<Double> scores : assessmentScores.values()) {
                    allScores.addAll(scores);
                }
                
                // Create map for grade distribution
                Map<String, Integer> gradeDistribution = new HashMap<>();
                gradeDistribution.put("90-100", 0);
                gradeDistribution.put("80-89", 0);
                gradeDistribution.put("70-79", 0);
                gradeDistribution.put("60-69", 0);
                gradeDistribution.put("Below 60", 0);
                
                // Count scores by grade range
                for (Double score : allScores) {
                    if (score >= 90) {
                        gradeDistribution.put("90-100", gradeDistribution.get("90-100") + 1);
                    } else if (score >= 80) {
                        gradeDistribution.put("80-89", gradeDistribution.get("80-89") + 1);
                    } else if (score >= 70) {
                        gradeDistribution.put("70-79", gradeDistribution.get("70-79") + 1);
                    } else if (score >= 60) {
                        gradeDistribution.put("60-69", gradeDistribution.get("60-69") + 1);
                    } else {
                        gradeDistribution.put("Below 60", gradeDistribution.get("Below 60") + 1);
                    }
                }
                
                // Create pie chart
                PieChart pieChart = new PieChart();
                pieChart.setTitle("Overall Grade Distribution");
                
                for (Map.Entry<String, Integer> entry : gradeDistribution.entrySet()) {
                    if (entry.getValue() > 0) {
                        PieChart.Data slice = new PieChart.Data(
                            entry.getKey() + " (" + entry.getValue() + ")", 
                            entry.getValue()
                        );
                        pieChart.getData().add(slice);
                    }
                }
                
                summaryContent.getChildren().add(pieChart);
                summaryTab.setContent(summaryContent);
                assessmentTabPane.getTabs().add(summaryTab);
                
                // Add a tab for each assessment type with data
                for (Map.Entry<String, List<Double>> entry : assessmentScores.entrySet()) {
                    String typeName = entry.getKey();
                    List<Double> scores = entry.getValue();
                    
                    if (scores.isEmpty()) {
                        continue;
                    }
                    
                    Tab typeTab = new Tab(typeName);
                    VBox typeContent = new VBox(15);
                    typeContent.setPadding(new Insets(15));
                    
                    // Create grade distribution for this type
                    Map<String, Integer> typeDistribution = new HashMap<>();
                    typeDistribution.put("90-100", 0);
                    typeDistribution.put("80-89", 0);
                    typeDistribution.put("70-79", 0);
                    typeDistribution.put("60-69", 0);
                    typeDistribution.put("Below 60", 0);
                    
                    // Count scores by grade range
                    for (Double score : scores) {
                        if (score >= 90) {
                            typeDistribution.put("90-100", typeDistribution.get("90-100") + 1);
                        } else if (score >= 80) {
                            typeDistribution.put("80-89", typeDistribution.get("80-89") + 1);
                        } else if (score >= 70) {
                            typeDistribution.put("70-79", typeDistribution.get("70-79") + 1);
                        } else if (score >= 60) {
                            typeDistribution.put("60-69", typeDistribution.get("60-69") + 1);
                        } else {
                            typeDistribution.put("Below 60", typeDistribution.get("Below 60") + 1);
                        }
                    }
                    
                    // Create pie chart
                    PieChart typePieChart = new PieChart();
                    typePieChart.setTitle(typeName + " Grade Distribution");
                    
                    for (Map.Entry<String, Integer> distEntry : typeDistribution.entrySet()) {
                        if (distEntry.getValue() > 0) {
                            PieChart.Data slice = new PieChart.Data(
                                distEntry.getKey() + " (" + distEntry.getValue() + ")", 
                                distEntry.getValue()
                            );
                            typePieChart.getData().add(slice);
                        }
                    }
                    
                    typeContent.getChildren().add(typePieChart);
                    typeTab.setContent(typeContent);
                    assessmentTabPane.getTabs().add(typeTab);
                }
                
                subjectBox.getChildren().addAll(subjectLabel, assessmentTabPane);
            }
            
            content.getChildren().add(subjectBox);
        }
        
        return content;
    }
    /**
     * Create content for the trends tab
     * 
     * @return The trends content
     */
    private VBox createTrendsContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Grade Trends");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Get fresh subject data
        List<Subject> freshSubjects = loadFreshSubjectData();
        
        if (freshSubjects.isEmpty()) {
            Label noSubjectsLabel = new Label("No subjects to analyze.");
            content.getChildren().addAll(titleLabel, noSubjectsLabel);
            return content;
        }
        
        boolean anyTrendsFound = false;
        
        for (Subject subject : freshSubjects) {
            VBox subjectBox = new VBox(15);
            subjectBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            
            Label subjectLabel = new Label(subject.getName());
            subjectLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            
            // Find assessments with scores > 0
            Map<String, List<Map.Entry<String, Double>>> assessmentsByType = new HashMap<>();
            
            // Collect assessments by type
            for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                String typeName = entry.getKey();
                AssessmentType assessmentType = entry.getValue();
                
                if (assessmentType.getWeight() > 0) {
                    List<Map.Entry<String, Double>> assessmentEntries = new ArrayList<>();
                    
                    for (Assessment assessment : assessmentType.getAssessments()) {
                        // Only include scores > 0
                        double score = assessment.getScore();
                        if (score > 0) {
                            String name = assessment.getDisplayName(assessmentType.getType());
                            assessmentEntries.add(new AbstractMap.SimpleEntry<>(name, score));
                        }
                    }
                    
                    if (!assessmentEntries.isEmpty()) {
                        // Sort entries by assessment number
                        assessmentEntries.sort((a, b) -> {
                            // Extract numbers from names like "Assignment 1", "Quiz 2", etc.
                            try {
                                String aName = a.getKey();
                                String bName = b.getKey();
                                
                                // Special case for non-numbered assessments
                                if (aName.equals(bName)) {
                                    return 0;
                                }
                                
                                // Try to extract number at the end
                                int aNum = 0;
                                int bNum = 0;
                                
                                int aSpace = aName.lastIndexOf(' ');
                                int bSpace = bName.lastIndexOf(' ');
                                
                                if (aSpace >= 0 && aSpace < aName.length() - 1) {
                                    try {
                                        aNum = Integer.parseInt(aName.substring(aSpace + 1));
                                    } catch (NumberFormatException ex) {
                                        // Not a number, use string comparison
                                    }
                                }
                                
                                if (bSpace >= 0 && bSpace < bName.length() - 1) {
                                    try {
                                        bNum = Integer.parseInt(bName.substring(bSpace + 1));
                                    } catch (NumberFormatException ex) {
                                        // Not a number, use string comparison
                                    }
                                }
                                
                                // If both have numbers, compare them
                                if (aNum > 0 && bNum > 0) {
                                    return Integer.compare(aNum, bNum);
                                }
                                
                                // Otherwise compare names
                                return aName.compareTo(bName);
                            } catch (Exception ex) {
                                return a.getKey().compareTo(b.getKey());
                            }
                        });
                        
                        assessmentsByType.put(assessmentType.getDisplayName(), assessmentEntries);
                    }
                }
            }
            
            if (assessmentsByType.isEmpty()) {
                Label noTrendLabel = new Label("No assessment data to analyze trends.");
                subjectBox.getChildren().addAll(subjectLabel, noTrendLabel);
            } else {
                anyTrendsFound = true;
                
                // Create tab pane for assessment types
                TabPane trendTabPane = new TabPane();
                trendTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                
                // Create a tab for each assessment type
                for (Map.Entry<String, List<Map.Entry<String, Double>>> entry : assessmentsByType.entrySet()) {
                    String typeName = entry.getKey();
                    List<Map.Entry<String, Double>> assessments = entry.getValue();
                    
                    Tab typeTab = new Tab(typeName);
                    VBox typeContent = new VBox(15);
                    typeContent.setPadding(new Insets(15));
                    
                    // Create line chart for this type
                    CategoryAxis xAxis = new CategoryAxis();
                    NumberAxis yAxis = new NumberAxis(0, 100, 10);
                    xAxis.setLabel("Assessment");
                    yAxis.setLabel("Score (%)");
                    
                    LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
                    lineChart.setTitle("Grade Trend for " + typeName);
                    lineChart.setAnimated(false);
                    
                    // Create series for trend
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(typeName);
                    
                    for (Map.Entry<String, Double> assessment : assessments) {
                        series.getData().add(
                            new XYChart.Data<>(assessment.getKey(), assessment.getValue())
                        );
                    }
                    
                    lineChart.getData().add(series);
                    
                    // Add trend analysis
                    VBox analysisBox = new VBox(10);
                    analysisBox.setPadding(new Insets(15));
                    
                    // Calculate trend direction
                    String trendDirection;
                    if (assessments.size() < 2) {
                        trendDirection = "Not enough data to determine trend direction.";
                    } else {
                        double firstScore = assessments.get(0).getValue();
                        double lastScore = assessments.get(assessments.size() - 1).getValue();
                        double difference = lastScore - firstScore;
                        
                        if (Math.abs(difference) < 5.0) {
                            trendDirection = "Scores are relatively stable over time.";
                        } else if (difference > 0) {
                            trendDirection = "Scores are improving over time. (+" + 
                                String.format("%.1f", difference) + "%)";
                        } else {
                            trendDirection = "Scores are declining over time. (" + 
                                String.format("%.1f", difference) + "%)";
                        }
                    }
                    
                    Label directionLabel = new Label("Trend Direction: " + trendDirection);
                    directionLabel.setWrapText(true);
                    
                    // Calculate average
                    double sum = 0.0;
                    for (Map.Entry<String, Double> assessment : assessments) {
                        sum += assessment.getValue();
                    }
                    double average = assessments.isEmpty() ? 0 : sum / assessments.size();
                    
                    Label averageLabel = new Label("Average Score: " + String.format("%.1f%%", average));
                    
                    analysisBox.getChildren().addAll(directionLabel, averageLabel);
                    
                    typeContent.getChildren().addAll(lineChart, analysisBox);
                    typeTab.setContent(typeContent);
                    trendTabPane.getTabs().add(typeTab);
                }
                
                subjectBox.getChildren().addAll(subjectLabel, trendTabPane);
            }
            
            content.getChildren().add(subjectBox);
        }
        
        if (!anyTrendsFound) {
            Label noTrendsLabel = new Label(
                "No assessment grades found for any subjects. Enter grades through the 'Input Grades' option."
            );
            noTrendsLabel.setWrapText(true);
            content.getChildren().add(noTrendsLabel);
        }
        
        return content;
    }
    
    /**
     * Create content for the projections tab
     * 
     * @return The projections content
     */
    private VBox createProjectionsContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(20));
        
        Label titleLabel = new Label("GPA Projections");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Get fresh subject data
        List<Subject> freshSubjects = loadFreshSubjectData();
        
        // Calculate current GPA directly
        double totalGPA = 0.0;
        int validSubjectCount = 0;
        
        for (Subject subject : freshSubjects) {
            double gpa = subject.calculateGPA();
            if (gpa > 0) {
                totalGPA += gpa;
                validSubjectCount++;
            }
        }
        
        double currentGPA = validSubjectCount > 0 ? totalGPA / validSubjectCount : 0.0;
        
        // Get all semesters to calculate overall GPA
        List<Semester> allSemesters = controller.getAllSemesters();
        double overallTotalGPA = 0.0;
        int validSemesterCount = 0;
        
        // Calculate overall GPA manually
        for (Semester sem : allSemesters) {
            // For the current semester, use our freshly calculated GPA
            if (sem.getId() == semester.getId()) {
                if (currentGPA > 0) {
                    overallTotalGPA += currentGPA;
                    validSemesterCount++;
                }
            } else {
                // Calculate GPA for other semesters
                double semesterGPA = 0.0;
                int semesterValidSubjects = 0;
                
                for (Subject subject : sem.getSubjects()) {
                    double subjectGPA = subject.calculateGPA();
                    if (subjectGPA > 0) {
                        semesterGPA += subjectGPA;
                        semesterValidSubjects++;
                    }
                }
                
                if (semesterValidSubjects > 0) {
                    double semGPA = semesterGPA / semesterValidSubjects;
                    if (semGPA > 0) {
                        overallTotalGPA += semGPA;
                        validSemesterCount++;
                    }
                }
            }
        }
        
        double overallGPA = validSemesterCount > 0 ? overallTotalGPA / validSemesterCount : 0.0;
        
        // Create projections
        Map<String, Double> projections = new HashMap<>();
        projections.put("current", overallGPA);
        
        int completedSemesters = validSemesterCount;
        int totalSemesters = 8; // Assuming 8 semesters for a degree
        int remainingSemesters = totalSemesters - completedSemesters;
        
        if (remainingSemesters <= 0) {
            // No remaining semesters
            projections.put("best", overallGPA);
            projections.put("worst", overallGPA);
            projections.put("realistic", overallGPA);
        } else {
            // Best case: 4.0 for remaining semesters
            double bestCase = ((overallGPA * completedSemesters) + (4.0 * remainingSemesters)) / totalSemesters;
            projections.put("best", Math.min(4.0, bestCase)); // Cap at 4.0
            
            // Worst case: 2.0 for remaining semesters
            double worstCase = ((overallGPA * completedSemesters) + (2.0 * remainingSemesters)) / totalSemesters;
            projections.put("worst", worstCase);
            
            // Realistic case: current GPA for remaining semesters
            projections.put("realistic", overallGPA);
        }
        
        // Create projections display
        VBox projectionsBox = new VBox(15);
        projectionsBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        // Current GPA
        Label currentGpaLabel = new Label("Current Overall GPA: " + String.format("%.2f", overallGPA));
        currentGpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Create bar chart for GPA projections
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 4, 0.5);
        xAxis.setLabel("Scenario");
        yAxis.setLabel("GPA");
        
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("GPA Projections");
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Current", projections.get("current")));
        series.getData().add(new XYChart.Data<>("Best Case", projections.get("best")));
        series.getData().add(new XYChart.Data<>("Realistic", projections.get("realistic")));
        series.getData().add(new XYChart.Data<>("Worst Case", projections.get("worst")));
        
        chart.getData().add(series);
        
        // GPA goals section
        Label goalsLabel = new Label("GPA Goals");
        goalsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        GridPane goalsGrid = new GridPane();
        goalsGrid.setHgap(15);
        goalsGrid.setVgap(10);
        goalsGrid.setPadding(new Insets(15));
        
        // Add headers
        goalsGrid.add(new Label("Target GPA"), 0, 0);
        goalsGrid.add(new Label("Required GPA for Future Semesters"), 1, 0);
        goalsGrid.add(new Label("Difficulty"), 2, 0);
        
        // Add rows for different GPA targets
        double[] targets = {3.0, 3.3, 3.5, 3.7, 4.0};
        
        for (int i = 0; i < targets.length; i++) {
            double targetGPA = targets[i];
            
            // Calculate required GPA for remaining semesters
            double requiredGPA = remainingSemesters > 0 ? 
                ((targetGPA * totalSemesters) - (overallGPA * completedSemesters)) / remainingSemesters : 
                overallGPA;
                
            // Cap at 4.0 and floor at 0.0
            requiredGPA = Math.max(0.0, Math.min(4.0, requiredGPA));
            
            // Calculate difficulty
            String difficulty;
            String difficultyColor;
            
            if (requiredGPA > 4.0) {
                difficulty = "Not Possible";
                difficultyColor = "red";
            } else if (requiredGPA > 3.7) {
                difficulty = "Very Hard";
                difficultyColor = "red";
            } else if (requiredGPA > 3.3) {
                difficulty = "Hard";
                difficultyColor = "orange";
            } else if (requiredGPA > 3.0) {
                difficulty = "Moderate";
                difficultyColor = "blue";
            } else {
                difficulty = "Achievable";
                difficultyColor = "green";
            }
            
            goalsGrid.add(new Label(String.format("%.1f", targetGPA)), 0, i + 1);
            
            if (requiredGPA > 4.0) {
                Label notPossibleLabel = new Label("Not achievable");
                notPossibleLabel.setTextFill(javafx.scene.paint.Color.RED);
                goalsGrid.add(notPossibleLabel, 1, i + 1);
            } else {
                goalsGrid.add(new Label(String.format("%.2f", requiredGPA)), 1, i + 1);
            }
            
            Label difficultyLabel = new Label(difficulty);
            difficultyLabel.setTextFill(javafx.scene.paint.Color.web(difficultyColor));
            goalsGrid.add(difficultyLabel, 2, i + 1);
        }
        
        projectionsBox.getChildren().addAll(currentGpaLabel, chart, goalsLabel, goalsGrid);
        
        content.getChildren().addAll(titleLabel, projectionsBox);
        
        return content;
    }
    
    /**
     * Create content for the recommendations tab
     * 
     * @return The recommendations content
     */
    private VBox createRecommendationsContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Recommendations");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Get fresh subject data
        List<Subject> freshSubjects = loadFreshSubjectData();
        
        if (freshSubjects.isEmpty()) {
            Label noSubjectsLabel = new Label("No subjects to analyze.");
            content.getChildren().addAll(titleLabel, noSubjectsLabel);
            return content;
        }
        
        boolean anyRecommendations = false;
        
        for (Subject subject : freshSubjects) {
            VBox subjectBox = new VBox(15);
            subjectBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            
            Label subjectLabel = new Label(subject.getName());
            subjectLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            
            // Get assessment data
            boolean hasGradedAssessments = false;
            boolean hasPendingAssessments = false;
            int totalGradedAssessments = 0;
            
            double overallPercentage = subject.calculateOverallPercentage();
            String letterGrade = subject.calculateLetterGrade();
            
            // Check for graded and pending assessments
            for (AssessmentType assessmentType : subject.getAssessmentTypes().values()) {
                if (assessmentType.getWeight() > 0) {
                    for (Assessment assessment : assessmentType.getAssessments()) {
                        if (assessment.getScore() > 0) {
                            hasGradedAssessments = true;
                            totalGradedAssessments++;
                        }
                        if (!assessment.isFinal()) {
                            hasPendingAssessments = true;
                        }
                    }
                }
            }
            
            if (!hasGradedAssessments) {
                // No grades entered yet
                VBox noGradesBox = new VBox(10);
                Label noGradesLabel = new Label("No grades have been entered for this subject yet.");
                
                Label enterGradesLabel = new Label(
                    "Enter grades using the 'Input Grades' button on the subject card to see recommendations."
                );
                enterGradesLabel.setWrapText(true);
                
                noGradesBox.getChildren().addAll(noGradesLabel, enterGradesLabel);
                subjectBox.getChildren().addAll(subjectLabel, noGradesBox);
            } else {
                anyRecommendations = true;
                
                // Create recommendations list
                VBox recommendationsBox = new VBox(10);
                
                // Overall status
                Label currentStatusLabel = new Label(
                    "Current Status: " + String.format("%.1f%%", overallPercentage) + 
                    ", Grade: " + letterGrade + 
                    ", GPA: " + String.format("%.1f", subject.calculateGPA())
                );
                currentStatusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                
                recommendationsBox.getChildren().add(currentStatusLabel);
                
                // Main recommendation based on current grade
                Text mainRecommendation = new Text();
                
                if (letterGrade.equals("A")) {
                    mainRecommendation.setText(
                        "Excellent work! You're performing at an A level. Keep up the great work."
                    );
                } else {
                    // Calculate what's needed for next grade level
                    String nextGrade = "";
                    double pointsNeeded = 0;
                    
                    switch (letterGrade) {
                        case "A-":
                            nextGrade = "A";
                            pointsNeeded = 93.0 - overallPercentage;
                            break;
                        case "B+":
                            nextGrade = "A-";
                            pointsNeeded = 90.0 - overallPercentage;
                            break;
                        case "B":
                            nextGrade = "B+";
                            pointsNeeded = 87.0 - overallPercentage;
                            break;
                        case "B-":
                            nextGrade = "B";
                            pointsNeeded = 83.0 - overallPercentage;
                            break;
                        case "C+":
                            nextGrade = "B-";
                            pointsNeeded = 80.0 - overallPercentage;
                            break;
                        case "C":
                            nextGrade = "C+";
                            pointsNeeded = 77.0 - overallPercentage;
                            break;
                        case "C-":
                            nextGrade = "C";
                            pointsNeeded = 73.0 - overallPercentage;
                            break;
                        case "D+":
                            nextGrade = "C-";
                            pointsNeeded = 70.0 - overallPercentage;
                            break;
                        case "D":
                            nextGrade = "D+";
                            pointsNeeded = 67.0 - overallPercentage;
                            break;
                        case "D-":
                            nextGrade = "D";
                            pointsNeeded = 63.0 - overallPercentage;
                            break;
                        case "F":
                            nextGrade = "D-";
                            pointsNeeded = 60.0 - overallPercentage;
                            break;
                    }
                    
                    if (hasPendingAssessments) {
                        mainRecommendation.setText(
                            "You need to increase your overall percentage by " + 
                            String.format("%.1f", pointsNeeded) + 
                            " points to achieve a " + nextGrade + " grade."
                        );
                    } else {
                        mainRecommendation.setText(
                            "All assessments are finalized. Your final grade is " + letterGrade + "."
                        );
                    }
                }
                
                mainRecommendation.setWrappingWidth(600);
                recommendationsBox.getChildren().add(mainRecommendation);
                
                // Add specific recommendations for pending assessments
                if (hasPendingAssessments) {
                    // Find pending assessments grouped by type
                    Map<String, List<Assessment>> pendingByType = new HashMap<>();
                    
                    for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                        String typeName = entry.getKey();
                        AssessmentType assessmentType = entry.getValue();
                        
                        if (assessmentType.getWeight() > 0) {
                            for (Assessment assessment : assessmentType.getAssessments()) {
                                if (!assessment.isFinal()) {
                                    pendingByType.computeIfAbsent(
                                        assessmentType.getDisplayName(), 
                                        k -> new ArrayList<>()
                                    ).add(assessment);
                                }
                            }
                        }
                    }
                    
                    if (!pendingByType.isEmpty()) {
                        Label pendingLabel = new Label("Pending Assessments:");
                        pendingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                        pendingLabel.setPadding(new Insets(10, 0, 5, 0));
                        
                        recommendationsBox.getChildren().add(pendingLabel);
                        
                        // Add each type of pending assessment
                        for (Map.Entry<String, List<Assessment>> entry : pendingByType.entrySet()) {
                            String typeName = entry.getKey();
                            List<Assessment> assessments = entry.getValue();
                            
                            Text pendingText = new Text(
                                typeName + ": " + assessments.size() + " assessment(s) pending finalization. " +
                                "Focus on these to improve your grade."
                            );
                            pendingText.setWrappingWidth(600);
                            
                            recommendationsBox.getChildren().add(pendingText);
                        }
                    }
                }
                
                // Strengths and weaknesses section if there are enough graded assessments
                boolean hasEnoughGrades = totalGradedAssessments >= 3;
                
                if (hasEnoughGrades) {
                    Label strengthsWeaknessesLabel = new Label("Strengths and Areas for Improvement");
                    strengthsWeaknessesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    strengthsWeaknessesLabel.setPadding(new Insets(15, 0, 5, 0));
                    
                    recommendationsBox.getChildren().add(strengthsWeaknessesLabel);
                    
                    // Find best and worst assessment types
                    String bestType = null;
                    double bestAverage = 0;
                    String worstType = null;
                    double worstAverage = 100;
                    
                    for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                        AssessmentType assessmentType = entry.getValue();
                        
                        if (assessmentType.getWeight() > 0) {
                            // Only calculate if there are graded assessments
                            boolean hasTypeGradedAssessments = false;
                            double sum = 0;
                            int count = 0;
                            
                            for (Assessment assessment : assessmentType.getAssessments()) {
                                double score = assessment.getScore();
                                if (score > 0) {
                                    sum += score;
                                    count++;
                                    hasTypeGradedAssessments = true;
                                }
                            }
                            
                            if (hasTypeGradedAssessments && count > 0) {
                                double average = sum / count;
                                
                                if (average > bestAverage) {
                                    bestAverage = average;
                                    bestType = assessmentType.getDisplayName();
                                }
                                
                                if (average < worstAverage) {
                                    worstAverage = average;
                                    worstType = assessmentType.getDisplayName();
                                }
                            }
                        }
                    }
                    
                    // Add strengths and weaknesses
                    if (bestType != null && bestAverage > 0) {
                        Text strengthText = new Text("Strength: " + bestType + 
                            " - Average score: " + String.format("%.1f%%", bestAverage));
                        strengthText.setFill(Color.GREEN);
                        recommendationsBox.getChildren().add(strengthText);
                    }
                    
                    if (worstType != null && worstAverage < 100) {
                        Text weaknessText = new Text("Area for Improvement: " + worstType + 
                            " - Average score: " + String.format("%.1f%%", worstAverage));
                        weaknessText.setFill(Color.RED);
                        recommendationsBox.getChildren().add(weaknessText);
                        
                        // Add a specific recommendation
                        if (worstAverage < 70) {
                            Text improvementText = new Text(
                                "Consider getting additional help or putting more focus on " + 
                                worstType.toLowerCase() + " to improve your overall grade."
                            );
                            improvementText.setWrappingWidth(600);
                            recommendationsBox.getChildren().add(improvementText);
                        }
                    }
                }
                
                subjectBox.getChildren().addAll(subjectLabel, recommendationsBox);
            }
            
            content.getChildren().add(subjectBox);
        }
        
        if (!anyRecommendations) {
            Label noRecommendationsLabel = new Label(
                "No recommendations available. Enter grades for your subjects to see personalized recommendations."
            );
            noRecommendationsLabel.setWrapText(true);
            content.getChildren().add(noRecommendationsLabel);
        }
        
        return content;
    }
    
    /**
     * Create a difficulty indicator
     * 
     * @param text The difficulty text
     * @param color The color
     * @return The difficulty indicator
     */
    private HBox createDifficultyIndicator(String text, Color color) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        
        // Create a colored circle
        javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(5);
        circle.setFill(color);
        
        // Create text
        Label label = new Label(text);
        label.setTextFill(color);
        
        box.getChildren().addAll(circle, label);
        
        return box;
    }
    
    /**
     * Show export options dialog
     */
    private void showExportOptions() {
        // Create dialog
        javafx.stage.Stage dialog = new javafx.stage.Stage();
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle("Export Options");
        dialog.setMinWidth(400);
        
        VBox dialogVBox = new VBox(20);
        dialogVBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Export Options");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Export buttons
        Button exportCsvButton = new Button("Export to CSV");
        exportCsvButton.setPrefWidth(200);
        
        Button exportHtmlButton = new Button("Export to HTML");
        exportHtmlButton.setPrefWidth(200);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(200);
        
        VBox buttonsBox = new VBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(exportCsvButton, exportHtmlButton, cancelButton);
        
        dialogVBox.getChildren().addAll(titleLabel, buttonsBox);
        
        dialog.setScene(new javafx.scene.Scene(dialogVBox));
        
        // Set up event handlers
        exportCsvButton.setOnAction(e -> {
            dialog.close();
            exportToCSV();
        });
        
        exportHtmlButton.setOnAction(e -> {
            dialog.close();
            exportToHTML();
        });
        
        cancelButton.setOnAction(e -> dialog.close());
        
        dialog.showAndWait();
    }
    
    /**
     * Export semester data to CSV
     */
    private void exportToCSV() {
        try {
            // Show file save dialog
            String fileName = ExportUtility.generateExportFileName(semester.getName(), "csv");
            java.io.File file = ExportUtility.showSaveFileDialog("Export to CSV", fileName, "csv");
            
            if (file != null) {
                ExportUtility.exportSemesterToCSV(semester, file.getAbsolutePath());
                showInfoAlert("Export Successful", "Data exported to CSV successfully!");
            }
        } catch (Exception e) {
            showErrorAlert("Export Error", "Failed to export data: " + e.getMessage());
        }
    }
    
    /**
     * Export semester data to HTML
     */
    private void exportToHTML() {
        try {
            // Show file save dialog
            String fileName = ExportUtility.generateExportFileName(semester.getName(), "html");
            java.io.File file = ExportUtility.showSaveFileDialog("Export to HTML", fileName, "html");
            
            if (file != null) {
                ExportUtility.exportSemesterToHTML(semester, file.getAbsolutePath());
                showInfoAlert("Export Successful", "Data exported to HTML successfully!");
            }
        } catch (Exception e) {
            showErrorAlert("Export Error", "Failed to export data: " + e.getMessage());
        }
    }
    
    /**
     * Show an error alert
     * 
     * @param title The alert title
     * @param message The alert message
     */
    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show an information alert
     * 
     * @param title The alert title
     * @param message The alert message
     */
    private void showInfoAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public BorderPane getView() {
        return mainLayout;
    }
}