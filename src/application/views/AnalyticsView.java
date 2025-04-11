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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.application.Platform;

import application.controllers.AnalyticsController;
import application.models.Assessment;
import application.models.AssessmentType;
import application.models.Semester;
import application.models.Subject;
import application.utils.AnalyticsUtility;
import application.utils.ExportUtility;
import application.utils.GradeCalculatorFactory;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

/**
 * View class for the analytics screen
 */
public class AnalyticsView {
    private BorderPane mainLayout;
    private AnalyticsController controller;
    private Semester semester;
    
    // Screen dimensions for responsive design
    private final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
    private final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    
    // Define color constants for consistent styling
    private static final Color PRIMARY_COLOR = Color.rgb(0, 59, 111); // Northeastern Blue
    private static final Color SECONDARY_COLOR = Color.rgb(200, 16, 46); // Northeastern Red
    private static final Color ACCENT_COLOR = Color.rgb(0, 173, 86); // Green for grades
    private static final Color LIGHT_GRAY = Color.rgb(240, 240, 240);
    
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
            mainLayout.setStyle("-fx-background-color: #f5f5ff;");
            mainLayout.setPadding(new Insets(20));
            
            // Enhanced size settings to ensure full-screen display
            mainLayout.setPrefSize(screenWidth, screenHeight);
            mainLayout.setMinSize(screenWidth, screenHeight);
            mainLayout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            
            // Force the BorderPane to use all available space
            VBox.setVgrow(mainLayout, Priority.ALWAYS);
            HBox.setHgrow(mainLayout, Priority.ALWAYS);
            
            // Create header
            Label headerLabel = new Label("Analytics - " + semester.getName());
            headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            headerLabel.setTextFill(PRIMARY_COLOR);
            
            HBox headerBox = new HBox(20);
            headerBox.setAlignment(Pos.CENTER_LEFT);
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            // Create styled buttons to match other views
            Button exportButton = createStyledButton("Export Reports", ACCENT_COLOR);
            exportButton.setTextFill(Color.WHITE);
            exportButton.setPrefWidth(150);
            exportButton.setPrefHeight(40);
            addButtonShadow(exportButton);
            
            Button backButton = createStyledButton("Back to Semester", LIGHT_GRAY);
            backButton.setTextFill(PRIMARY_COLOR);
            backButton.setPrefWidth(150);
            backButton.setPrefHeight(40);
            backButton.setStyle("-fx-background-color: white; -fx-border-color: " + toRgbString(PRIMARY_COLOR) + "; -fx-border-radius: 5; -fx-background-radius: 5;");
            addButtonShadow(backButton);
            
            headerBox.getChildren().addAll(headerLabel, spacer, exportButton, backButton);
            
            // Add a colored divider line
            Rectangle colorBar = new Rectangle();
            colorBar.setHeight(3);
            colorBar.widthProperty().bind(mainLayout.widthProperty().subtract(40));
            
            // Create gradient for color bar
            LinearGradient barGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, PRIMARY_COLOR),
                new Stop(0.5, SECONDARY_COLOR),
                new Stop(1, ACCENT_COLOR)
            );
            colorBar.setFill(barGradient);
            
            VBox headerWithDivider = new VBox(10);
            headerWithDivider.getChildren().addAll(headerBox, colorBar);
            
            mainLayout.setTop(headerWithDivider);
            BorderPane.setMargin(headerWithDivider, new Insets(0, 0, 20, 0));
            
            // Create a styled tab pane with container
            StackPane tabContainer = new StackPane();
            tabContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
            tabContainer.setPadding(new Insets(5));
            
            // Add shadow to tab container
            DropShadow shadow = new DropShadow();
            shadow.setRadius(8);
            shadow.setColor(Color.rgb(0, 0, 0, 0.15));
            shadow.setOffsetY(3);
            tabContainer.setEffect(shadow);
            
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            tabPane.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-tab-min-width: 120px; " +
                "-fx-tab-max-width: 120px; " +
                "-fx-tab-min-height: 35px;"
            );
            
            // Overview tab
            Tab overviewTab = new Tab("Overview");
            overviewTab.setClosable(false);
            ScrollPane overviewScrollPane = new ScrollPane();
            overviewScrollPane.setFitToWidth(true);
            overviewScrollPane.setPrefWidth(screenWidth - 40);
            overviewScrollPane.setStyle("-fx-background-color: transparent;");
            overviewScrollPane.setContent(createOverviewContent());
            overviewTab.setContent(overviewScrollPane);
            
            // Grade Distribution tab
            Tab distributionTab = new Tab("Grade Distribution");
            distributionTab.setClosable(false);
            ScrollPane distributionScrollPane = new ScrollPane();
            distributionScrollPane.setFitToWidth(true);
            distributionScrollPane.setPrefWidth(screenWidth - 40);
            distributionScrollPane.setStyle("-fx-background-color: transparent;");
            distributionScrollPane.setContent(createDistributionContent());
            distributionTab.setContent(distributionScrollPane);
            
            // Trends tab
            Tab trendsTab = new Tab("Trends");
            trendsTab.setClosable(false);
            ScrollPane trendsScrollPane = new ScrollPane();
            trendsScrollPane.setFitToWidth(true);
            trendsScrollPane.setPrefWidth(screenWidth - 40);
            trendsScrollPane.setStyle("-fx-background-color: transparent;");
            trendsScrollPane.setContent(createTrendsContent());
            trendsTab.setContent(trendsScrollPane);
            
            // Projections tab
            Tab projectionsTab = new Tab("Projections");
            projectionsTab.setClosable(false);
            ScrollPane projectionsScrollPane = new ScrollPane();
            projectionsScrollPane.setFitToWidth(true);
            projectionsScrollPane.setPrefWidth(screenWidth - 40);
            projectionsScrollPane.setStyle("-fx-background-color: transparent;");
            projectionsScrollPane.setContent(createProjectionsContent());
            projectionsTab.setContent(projectionsScrollPane);
            
            // Recommendations tab
            Tab recommendationsTab = new Tab("Recommendations");
            recommendationsTab.setClosable(false);
            ScrollPane recommendationsScrollPane = new ScrollPane();
            recommendationsScrollPane.setFitToWidth(true);
            recommendationsScrollPane.setPrefWidth(screenWidth - 40);
            recommendationsScrollPane.setStyle("-fx-background-color: transparent;");
            recommendationsScrollPane.setContent(createRecommendationsContent());
            recommendationsTab.setContent(recommendationsScrollPane);
            
            tabPane.getTabs().addAll(overviewTab, distributionTab, trendsTab, projectionsTab, recommendationsTab);
            tabContainer.getChildren().add(tabPane);
            
            mainLayout.setCenter(tabContainer);
            
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
     * Create a styled button with the specified text and background color
     * 
     * @param text The button text
     * @param bgColor The background color
     * @return The styled button
     */
    private Button createStyledButton(String text, Color bgColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Convert color to CSS format
        String colorString = toRgbString(bgColor);
        
        // Style the button
        button.setStyle(
            "-fx-background-color: " + colorString + "; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        
        // Add hover effect
        Color hoverColor = bgColor.darker();
        String hoverColorString = toRgbString(hoverColor);
        
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: " + hoverColorString + "; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: " + colorString + "; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            )
        );
        
        return button;
    }
    
    /**
     * Add shadow to a button
     * 
     * @param button The button to add shadow to
     */
    private void addButtonShadow(Button button) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        button.setEffect(shadow);
    }
    
    /**
     * Convert a JavaFX Color to an RGB string for CSS
     */
    private String toRgbString(Color color) {
        return String.format(
            "rgb(%d, %d, %d)",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255)
        );
    }
    
    /**
     * Create a card-style container for content
     * 
     * @param content The content to display in the card
     * @param title The title of the card
     * @return A styled card container
     */
    private VBox createContentCard(String title, VBox content) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20;");
        card.setPrefWidth(screenWidth - 100);
        
        // Add shadow to card
        DropShadow shadow = new DropShadow();
        shadow.setRadius(8);
        shadow.setColor(Color.rgb(0, 0, 0, 0.15));
        shadow.setOffsetY(3);
        card.setEffect(shadow);
        
        // Create title if provided
        if (title != null && !title.isEmpty()) {
            Label titleLabel = new Label(title);
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            titleLabel.setTextFill(PRIMARY_COLOR);
            card.getChildren().add(titleLabel);
        }
        
        // Add all content
        card.getChildren().addAll(content.getChildren());
        
        return card;
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
        content.setPrefWidth(screenWidth - 60);
        content.setAlignment(Pos.CENTER);
        
        // First, get fresh subject data
        List<Subject> freshSubjects = loadFreshSubjectData();
        
        if (freshSubjects.isEmpty()) {
            VBox noDataContent = new VBox(10);
            Label noSubjectsLabel = new Label("No subjects to analyze.");
            noSubjectsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            noDataContent.getChildren().add(noSubjectsLabel);
            
            VBox emptyCard = createContentCard("Semester Overview", noDataContent);
            content.getChildren().add(emptyCard);
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
        VBox gpaContent = new VBox(10);
        
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
        classificationLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        
        gpaContent.getChildren().addAll(gpaLabel, classificationLabel);
        
        // Create a card for GPA section
        VBox gpaCard = createContentCard("Semester Overview", gpaContent);
        
        // Subject performance section
        VBox subjectsContent = new VBox(15);
        
        for (Subject subject : freshSubjects) {
            VBox subjectBox = new VBox(12);
            subjectBox.setStyle("-fx-background-color: #f8f8ff; -fx-background-radius: 8; -fx-padding: 15;");
            
            // Add shadow to subject box
            DropShadow subjectShadow = new DropShadow();
            subjectShadow.setRadius(5);
            subjectShadow.setColor(Color.rgb(0, 0, 0, 0.1));
            subjectShadow.setOffsetY(2);
            subjectBox.setEffect(subjectShadow);
            
            Label subjectNameLabel = new Label(subject.getName());
            subjectNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            subjectNameLabel.setTextFill(PRIMARY_COLOR);
            
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
            gradeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            
            double gpa = subject.calculateGPA();
            Label gpaValueLabel = new Label("GPA: " + String.format("%.1f", gpa));
            gpaValueLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            
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
            assessmentTypesLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            
            summaryBox.getChildren().addAll(percentageLabel, gradeLabel, gpaValueLabel, assessmentTypesLabel);
            
            // Combine chart and summary
            HBox contentBox = new HBox(20);
            contentBox.setAlignment(Pos.CENTER_LEFT);
            contentBox.getChildren().addAll(chart, summaryBox);
            
            subjectBox.getChildren().addAll(subjectNameLabel, contentBox);
            subjectsContent.getChildren().add(subjectBox);
        }
        
        // Create a card for subjects section
        VBox subjectsCard = createContentCard("Subject Performance", subjectsContent);
        
        // Statistical summary section
        VBox statsContent = new VBox(15);
        
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(30);
        statsGrid.setVgap(12);
        statsGrid.setPadding(new Insets(15));
        statsGrid.setStyle("-fx-background-color: #f8f8ff; -fx-background-radius: 8;");
        
        // Add shadow to stats grid
        DropShadow gridShadow = new DropShadow();
        gridShadow.setRadius(5);
        gridShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        gridShadow.setOffsetY(2);
        statsGrid.setEffect(gridShadow);
        
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
        
        // Style for stat labels
        Font labelFont = Font.font("Arial", FontWeight.BOLD, 14);
        Font valueFont = Font.font("Arial", FontWeight.NORMAL, 14);
        
        // Add statistics to grid
        Label countHeaderLabel = new Label("Total Assessments:");
        countHeaderLabel.setFont(labelFont);
        countHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        Label countValueLabel = new Label(String.format("%.0f", stats.get("count")));
        countValueLabel.setFont(valueFont);
        
        statsGrid.add(countHeaderLabel, 0, 0);
        statsGrid.add(countValueLabel, 1, 0);
        
        Label minHeaderLabel = new Label("Minimum Score:");
        minHeaderLabel.setFont(labelFont);
        minHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        Label minValueLabel = new Label(String.format("%.1f%%", stats.get("min")));
        minValueLabel.setFont(valueFont);
        
        statsGrid.add(minHeaderLabel, 0, 1);
        statsGrid.add(minValueLabel, 1, 1);
        
        Label maxHeaderLabel = new Label("Maximum Score:");
        maxHeaderLabel.setFont(labelFont);
        maxHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        Label maxValueLabel = new Label(String.format("%.1f%%", stats.get("max")));
        maxValueLabel.setFont(valueFont);
        
        statsGrid.add(maxHeaderLabel, 0, 2);
        statsGrid.add(maxValueLabel, 1, 2);
        
        Label meanHeaderLabel = new Label("Mean Score:");
        meanHeaderLabel.setFont(labelFont);
        meanHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        Label meanValueLabel = new Label(String.format("%.1f%%", stats.get("mean")));
        meanValueLabel.setFont(valueFont);
        
        statsGrid.add(meanHeaderLabel, 0, 3);
        statsGrid.add(meanValueLabel, 1, 3);
        
        Label medianHeaderLabel = new Label("Median Score:");
        medianHeaderLabel.setFont(labelFont);
        medianHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        Label medianValueLabel = new Label(String.format("%.1f%%", stats.get("median")));
        medianValueLabel.setFont(valueFont);
        
        statsGrid.add(medianHeaderLabel, 0, 4);
        statsGrid.add(medianValueLabel, 1, 4);
        
        Label stdevHeaderLabel = new Label("Standard Deviation:");
        stdevHeaderLabel.setFont(labelFont);
        stdevHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        Label stdevValueLabel = new Label(String.format("%.1f", stats.get("standardDeviation")));
        stdevValueLabel.setFont(valueFont);
        
        statsGrid.add(stdevHeaderLabel, 0, 5);
        statsGrid.add(stdevValueLabel, 1, 5);
        
        statsContent.getChildren().add(statsGrid);
        
        // Create a card for statistics section
        VBox statsCard = createContentCard("Statistical Summary", statsContent);
        
        // Add all cards to the content
        content.getChildren().addAll(gpaCard, subjectsCard, statsCard);
        
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
        content.setPrefWidth(screenWidth - 60);
        content.setAlignment(Pos.CENTER);
        
        // Get fresh subject data
        List<Subject> freshSubjects = loadFreshSubjectData();
        
        if (freshSubjects.isEmpty()) {
            VBox noDataContent = new VBox(10);
            Label noSubjectsLabel = new Label("No subjects to analyze.");
            noSubjectsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            noDataContent.getChildren().add(noSubjectsLabel);
            
            VBox emptyCard = createContentCard("Grade Distribution", noDataContent);
            content.getChildren().add(emptyCard);
            return content;
        }
        
        for (Subject subject : freshSubjects) {
            VBox subjectContent = new VBox(15);
            
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
                noDistributionLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                subjectContent.getChildren().add(noDistributionLabel);
            } else {
                TabPane assessmentTabPane = new TabPane();
                assessmentTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                assessmentTabPane.setStyle(
                    "-fx-background-color: transparent; " +
                    "-fx-tab-min-width: 120px; " +
                    "-fx-tab-min-height: 35px;"
                );
                
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
                
                subjectContent.getChildren().add(assessmentTabPane);
            }
            
            VBox subjectCard = createContentCard(subject.getName(), subjectContent);
            content.getChildren().add(subjectCard);
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
        content.setPrefWidth(screenWidth - 60);
        content.setAlignment(Pos.CENTER);
        
        // Get fresh subject data
        List<Subject> freshSubjects = loadFreshSubjectData();
        
        if (freshSubjects.isEmpty()) {
            VBox noDataContent = new VBox(10);
            Label noSubjectsLabel = new Label("No subjects to analyze.");
            noSubjectsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            noDataContent.getChildren().add(noSubjectsLabel);
            
            VBox emptyCard = createContentCard("Grade Trends", noDataContent);
            content.getChildren().add(emptyCard);
            return content;
        }
        
        boolean anyTrendsFound = false;
        
        for (Subject subject : freshSubjects) {
            VBox subjectContent = new VBox(15);
            
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
                noTrendLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                subjectContent.getChildren().add(noTrendLabel);
            } else {
                anyTrendsFound = true;
                
                // Create tab pane for assessment types
                TabPane trendTabPane = new TabPane();
                trendTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                trendTabPane.setStyle(
                    "-fx-background-color: transparent; " +
                    "-fx-tab-min-width: 120px; " +
                    "-fx-tab-min-height: 35px;"
                );
                
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
                    analysisBox.setStyle("-fx-background-color: #f8f8ff; -fx-background-radius: 8;");
                    
                    // Add shadow to analysis box
                    DropShadow analysisShadow = new DropShadow();
                    analysisShadow.setRadius(5);
                    analysisShadow.setColor(Color.rgb(0, 0, 0, 0.1));
                    analysisShadow.setOffsetY(2);
                    analysisBox.setEffect(analysisShadow);
                    
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
                    directionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    
                    // Calculate average
                    double sum = 0.0;
                    for (Map.Entry<String, Double> assessment : assessments) {
                        sum += assessment.getValue();
                    }
                    double average = assessments.isEmpty() ? 0 : sum / assessments.size();
                    
                    Label averageLabel = new Label("Average Score: " + String.format("%.1f%%", average));
                    averageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                    
                    analysisBox.getChildren().addAll(directionLabel, averageLabel);
                    
                    typeContent.getChildren().addAll(lineChart, analysisBox);
                    typeTab.setContent(typeContent);
                    trendTabPane.getTabs().add(typeTab);
                }
                
                subjectContent.getChildren().add(trendTabPane);
            }
            
            VBox subjectCard = createContentCard(subject.getName() + " Trends", subjectContent);
            content.getChildren().add(subjectCard);
        }
        
        if (!anyTrendsFound) {
            VBox noDataContent = new VBox(10);
            Label noTrendsLabel = new Label(
                "No assessment grades found for any subjects. Enter grades through the 'Input Grades' option."
            );
            noTrendsLabel.setWrapText(true);
            noTrendsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            noDataContent.getChildren().add(noTrendsLabel);
            
            VBox emptyCard = createContentCard("Grade Trends", noDataContent);
            content.getChildren().add(emptyCard);
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
        content.setPrefWidth(screenWidth - 60);
        content.setAlignment(Pos.CENTER);
        
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
        VBox projectionsContent = new VBox(20);
        
        // Current GPA
        Label currentGpaLabel = new Label("Current Overall GPA: " + String.format("%.2f", overallGPA));
        currentGpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        currentGpaLabel.setTextFill(PRIMARY_COLOR);
        
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
        
        // Style the chart bars
        series.getData().get(0).getNode().setStyle("-fx-bar-fill: #6495ED;"); // Current - Blue
        series.getData().get(1).getNode().setStyle("-fx-bar-fill: #32CD32;"); // Best - Green
        series.getData().get(2).getNode().setStyle("-fx-bar-fill: #FFA500;"); // Realistic - Orange
        series.getData().get(3).getNode().setStyle("-fx-bar-fill: #FF6347;"); // Worst - Red
        
        // GPA goals section
        Label goalsLabel = new Label("GPA Goals");
        goalsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        goalsLabel.setTextFill(PRIMARY_COLOR);
        goalsLabel.setPadding(new Insets(15, 0, 10, 0));
        
        GridPane goalsGrid = new GridPane();
        goalsGrid.setHgap(25);
        goalsGrid.setVgap(12);
        goalsGrid.setPadding(new Insets(15));
        goalsGrid.setStyle("-fx-background-color: #f8f8ff; -fx-background-radius: 8;");
        
        // Add shadow to goals grid
        DropShadow gridShadow = new DropShadow();
        gridShadow.setRadius(5);
        gridShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        gridShadow.setOffsetY(2);
        goalsGrid.setEffect(gridShadow);
        
        // Add headers
        Label targetLabel = new Label("Target GPA");
        targetLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        targetLabel.setTextFill(PRIMARY_COLOR);
        
        Label requiredLabel = new Label("Required GPA for Future Semesters");
        requiredLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        requiredLabel.setTextFill(PRIMARY_COLOR);
        
        Label difficultyLabel = new Label("Difficulty");
        difficultyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        difficultyLabel.setTextFill(PRIMARY_COLOR);
        
        goalsGrid.add(targetLabel, 0, 0);
        goalsGrid.add(requiredLabel, 1, 0);
        goalsGrid.add(difficultyLabel, 2, 0);
        
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
            Color difficultyColor;
            
            if (requiredGPA > 4.0) {
                difficulty = "Not Possible";
                difficultyColor = Color.RED;
            } else if (requiredGPA > 3.7) {
                difficulty = "Very Hard";
                difficultyColor = Color.RED;
            } else if (requiredGPA > 3.3) {
                difficulty = "Hard";
                difficultyColor = Color.ORANGE;
            } else if (requiredGPA > 3.0) {
                difficulty = "Moderate";
                difficultyColor = Color.BLUE;
            } else {
                difficulty = "Achievable";
                difficultyColor = Color.GREEN;
            }
            
            Label targetValueLabel = new Label(String.format("%.1f", targetGPA));
            targetValueLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            
            goalsGrid.add(targetValueLabel, 0, i + 1);
            
            if (requiredGPA > 4.0) {
                Label notPossibleLabel = new Label("Not achievable");
                notPossibleLabel.setTextFill(Color.RED);
                notPossibleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                goalsGrid.add(notPossibleLabel, 1, i + 1);
            } else {
                Label requiredValueLabel = new Label(String.format("%.2f", requiredGPA));
                requiredValueLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                goalsGrid.add(requiredValueLabel, 1, i + 1);
            }
            
            Label difficultyValueLabel = new Label(difficulty);
            difficultyValueLabel.setTextFill(difficultyColor);
            difficultyValueLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            goalsGrid.add(difficultyValueLabel, 2, i + 1);
        }
        
        projectionsContent.getChildren().addAll(currentGpaLabel, chart, goalsLabel, goalsGrid);
        
        // Create a card for projections
        VBox projectionsCard = createContentCard("GPA Projections", projectionsContent);
        content.getChildren().add(projectionsCard);
        
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
        content.setPrefWidth(screenWidth - 60);
        content.setAlignment(Pos.CENTER);
        
        // Get fresh subject data
        List<Subject> freshSubjects = loadFreshSubjectData();
        
        if (freshSubjects.isEmpty()) {
            VBox noDataContent = new VBox(10);
            Label noSubjectsLabel = new Label("No subjects to analyze.");
            noSubjectsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            noDataContent.getChildren().add(noSubjectsLabel);
            
            VBox emptyCard = createContentCard("Recommendations", noDataContent);
            content.getChildren().add(emptyCard);
            return content;
        }
        
        boolean anyRecommendations = false;
        
        for (Subject subject : freshSubjects) {
            VBox subjectContent = new VBox(15);
            
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
                noGradesLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                
                Label enterGradesLabel = new Label(
                    "Enter grades using the 'Input Grades' button on the subject card to see recommendations."
                );
                enterGradesLabel.setWrapText(true);
                enterGradesLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                
                noGradesBox.getChildren().addAll(noGradesLabel, enterGradesLabel);
                subjectContent.getChildren().add(noGradesBox);
            } else {
                anyRecommendations = true;
                
                // Create recommendations list
                VBox recommendationsBox = new VBox(15);
                
                // Create recommendations content
                VBox statusBox = new VBox(8);
                statusBox.setStyle("-fx-background-color: #f8f8ff; -fx-background-radius: 8; -fx-padding: 15;");
                
             // Add shadow to status box
                DropShadow boxShadow = new DropShadow();
                boxShadow.setRadius(5);
                boxShadow.setColor(Color.rgb(0, 0, 0, 0.1));
                boxShadow.setOffsetY(2);
                statusBox.setEffect(boxShadow);
                
                // Overall status
                Label currentStatusLabel = new Label(
                    "Current Status: " + String.format("%.1f%%", overallPercentage) + 
                    ", Grade: " + letterGrade + 
                    ", GPA: " + String.format("%.1f", subject.calculateGPA())
                );
                currentStatusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                
                // Set color based on grade
                if (letterGrade.startsWith("A")) {
                    currentStatusLabel.setTextFill(Color.GREEN);
                } else if (letterGrade.startsWith("B")) {
                    currentStatusLabel.setTextFill(Color.BLUE);
                } else if (letterGrade.startsWith("C")) {
                    currentStatusLabel.setTextFill(Color.ORANGE);
                } else {
                    currentStatusLabel.setTextFill(Color.RED);
                }
                
                statusBox.getChildren().add(currentStatusLabel);
                
                // Main recommendation based on current grade
                Label mainRecommendationLabel = new Label();
                mainRecommendationLabel.setWrapText(true);
                mainRecommendationLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                
                if (letterGrade.equals("A")) {
                    mainRecommendationLabel.setText(
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
                        mainRecommendationLabel.setText(
                            "You need to increase your overall percentage by " + 
                            String.format("%.1f", pointsNeeded) + 
                            " points to achieve a " + nextGrade + " grade."
                        );
                    } else {
                        mainRecommendationLabel.setText(
                            "All assessments are finalized. Your final grade is " + letterGrade + "."
                        );
                    }
                }
                
                statusBox.getChildren().add(mainRecommendationLabel);
                recommendationsBox.getChildren().add(statusBox);
                
                // Add specific recommendations for pending assessments
                if (hasPendingAssessments) {
                    VBox pendingBox = new VBox(10);
                    pendingBox.setStyle("-fx-background-color: #f8f8ff; -fx-background-radius: 8; -fx-padding: 15;");
                    
                    // Add shadow to pending box
                    DropShadow pendingShadow = new DropShadow();
                    pendingShadow.setRadius(5);
                    pendingShadow.setColor(Color.rgb(0, 0, 0, 0.1));
                    pendingShadow.setOffsetY(2);
                    pendingBox.setEffect(pendingShadow);
                    
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
                        pendingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                        pendingLabel.setTextFill(PRIMARY_COLOR);
                        
                        pendingBox.getChildren().add(pendingLabel);
                        
                        // Add each type of pending assessment
                        for (Map.Entry<String, List<Assessment>> entry : pendingByType.entrySet()) {
                            String typeName = entry.getKey();
                            List<Assessment> assessments = entry.getValue();
                            
                            Label pendingText = new Label(
                                typeName + ": " + assessments.size() + " assessment(s) pending finalization. " +
                                "Focus on these to improve your grade."
                            );
                            pendingText.setWrapText(true);
                            pendingText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                            
                            pendingBox.getChildren().add(pendingText);
                        }
                        recommendationsBox.getChildren().add(pendingBox);
                    }
                }
                
                // Strengths and weaknesses section if there are enough graded assessments
                boolean hasEnoughGrades = totalGradedAssessments >= 3;
                
                if (hasEnoughGrades) {
                    VBox strengthsWeaknessesBox = new VBox(10);
                    strengthsWeaknessesBox.setStyle("-fx-background-color: #f8f8ff; -fx-background-radius: 8; -fx-padding: 15;");
                    
                    // Add shadow to strengths/weaknesses box
                    DropShadow swShadow = new DropShadow();
                    swShadow.setRadius(5);
                    swShadow.setColor(Color.rgb(0, 0, 0, 0.1));
                    swShadow.setOffsetY(2);
                    strengthsWeaknessesBox.setEffect(swShadow);
                    
                    Label strengthsWeaknessesLabel = new Label("Strengths and Areas for Improvement");
                    strengthsWeaknessesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                    strengthsWeaknessesLabel.setTextFill(PRIMARY_COLOR);
                    
                    strengthsWeaknessesBox.getChildren().add(strengthsWeaknessesLabel);
                    
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
                        Label strengthLabel = new Label("Strength: " + bestType + 
                            " - Average score: " + String.format("%.1f%%", bestAverage));
                        strengthLabel.setTextFill(Color.GREEN);
                        strengthLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                        strengthsWeaknessesBox.getChildren().add(strengthLabel);
                    }
                    
                    if (worstType != null && worstAverage < 100) {
                        Label weaknessLabel = new Label("Area for Improvement: " + worstType + 
                            " - Average score: " + String.format("%.1f%%", worstAverage));
                        weaknessLabel.setTextFill(Color.RED);
                        weaknessLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                        strengthsWeaknessesBox.getChildren().add(weaknessLabel);
                        
                        // Add a specific recommendation
                        if (worstAverage < 70) {
                            Label improvementLabel = new Label(
                                "Consider getting additional help or putting more focus on " + 
                                worstType.toLowerCase() + " to improve your overall grade."
                            );
                            improvementLabel.setWrapText(true);
                            improvementLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                            strengthsWeaknessesBox.getChildren().add(improvementLabel);
                        }
                    }
                    
                    recommendationsBox.getChildren().add(strengthsWeaknessesBox);
                }
                
                subjectContent.getChildren().add(recommendationsBox);
            }
            
            VBox subjectCard = createContentCard(subject.getName(), subjectContent);
            content.getChildren().add(subjectCard);
        }
        
        if (!anyRecommendations) {
            VBox noDataContent = new VBox(10);
            Label noRecommendationsLabel = new Label(
                "No recommendations available. Enter grades for your subjects to see personalized recommendations."
            );
            noRecommendationsLabel.setWrapText(true);
            noRecommendationsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            noDataContent.getChildren().add(noRecommendationsLabel);
            
            VBox emptyCard = createContentCard("Recommendations", noDataContent);
            content.getChildren().add(emptyCard);
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
        dialogVBox.setPadding(new Insets(25));
        dialogVBox.setStyle("-fx-background-color: #f8f8ff;");
        
        Label titleLabel = new Label("Export Options");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(PRIMARY_COLOR);
        
        // Create a styled underline
        Rectangle underline = new Rectangle(100, 3);
        underline.setFill(ACCENT_COLOR);
        underline.setArcWidth(3);
        underline.setArcHeight(3);
        
        // Export buttons with styling
        Button exportCsvButton = createStyledButton("Export to CSV", PRIMARY_COLOR);
        exportCsvButton.setTextFill(Color.WHITE);
        exportCsvButton.setPrefWidth(200);
        exportCsvButton.setPrefHeight(40);
        addButtonShadow(exportCsvButton);
        
        Button exportHtmlButton = createStyledButton("Export to HTML", PRIMARY_COLOR);
        exportHtmlButton.setTextFill(Color.WHITE);
        exportHtmlButton.setPrefWidth(200);
        exportHtmlButton.setPrefHeight(40);
        addButtonShadow(exportHtmlButton);
        
        Button cancelButton = createStyledButton("Cancel", LIGHT_GRAY);
        cancelButton.setTextFill(Color.rgb(80, 80, 80));
        cancelButton.setPrefWidth(200);
        cancelButton.setPrefHeight(40);
        cancelButton.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        addButtonShadow(cancelButton);
        
        VBox buttonsBox = new VBox(15);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(exportCsvButton, exportHtmlButton, cancelButton);
        
        dialogVBox.getChildren().addAll(titleLabel, underline, buttonsBox);
        
        // Add shadow to dialog
        StackPane dialogContainer = new StackPane();
        dialogContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        dialogContainer.getChildren().add(dialogVBox);
        
        DropShadow dialogShadow = new DropShadow();
        dialogShadow.setRadius(10);
        dialogShadow.setColor(Color.rgb(0, 0, 0, 0.25));
        dialogShadow.setOffsetY(5);
        dialogContainer.setEffect(dialogShadow);
        
        dialog.setScene(new javafx.scene.Scene(dialogContainer));
        
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
//    private void exportToCSV() {
//        try {
//            // Show file save dialog
//            String fileName = ExportUtility.generateExportFileName(semester.getName(), "csv");
//            java.io.File file = ExportUtility.showSaveFileDialog("Export to CSV", fileName, "csv");
//            
//            if (file != null) {
//                ExportUtility.exportSemesterToCSV(semester, file.getAbsolutePath());
//                showInfoAlert("Export Successful", "Data exported to CSV successfully!");
//            }
//        } catch (Exception e) {
//            showErrorAlert("Export Error", "Failed to export data: " + e.getMessage());
//        }
//    }
//    
//    /**
//     * Export semester data to HTML
//     */
//    private void exportToHTML() {
//        try {
//            // Show file save dialog
//            String fileName = ExportUtility.generateExportFileName(semester.getName(), "html");
//            java.io.File file = ExportUtility.showSaveFileDialog("Export to HTML", fileName, "html");
//            
//            if (file != null) {
//                ExportUtility.exportSemesterToHTML(semester, file.getAbsolutePath());
//                showInfoAlert("Export Successful", "Data exported to HTML successfully!");
//            }
//        } catch (Exception e) {
//            showErrorAlert("Export Error", "Failed to export data: " + e.getMessage());
//        }
//    }
//    
    /**
     * Export semester data to CSV with improved extension handling and detailed content
     */
    private void exportToCSV() {
        try {
            // Create a base filename WITHOUT any extension
            String baseFileName = semester.getName().replaceAll("\\s+", "_");
            
            // Show file save dialog with CSV type
            java.io.File file = ExportUtility.showSaveFileDialog("Export to CSV", baseFileName, "csv");
            
            if (file != null) {
                // Get the file path
                String filePath = file.getAbsolutePath();
                System.out.println("Original path: " + filePath);
                
                // Remove any existing .csv extension
                if (filePath.toLowerCase().endsWith(".csv")) {
                    filePath = filePath.substring(0, filePath.length() - 4);
                }
                
                // Always add .csv extension once
                filePath = filePath + ".csv";
                System.out.println("Final export path: " + filePath);
                
                final String finalPath = filePath;
                
                // Use direct export to file (bypassing ExportUtility's dialogs)
                try {
                    // Create a CSV writer
                    java.io.FileWriter writer = new java.io.FileWriter(finalPath);
                    java.io.BufferedWriter bufferedWriter = new java.io.BufferedWriter(writer);
                    
                    // Write semester info
                    bufferedWriter.write("Semester: " + semester.getName() + "\n");
                    double semesterGpa = semester.calculateGPA();
                    bufferedWriter.write("GPA: " + String.format("%.2f", semesterGpa) + "\n\n");
                    
                    // Write detailed information for each subject
                    for (Subject subject : semester.getSubjects()) {
                        bufferedWriter.write("Subject: " + subject.getName() + "\n");
                        
                        // Subject overall stats
                        double percentage = subject.calculateOverallPercentage();
                        String letterGrade = subject.calculateLetterGrade();
                        double gpa = subject.calculateGPA();
                        
                        bufferedWriter.write("Overall Percentage: " + String.format("%.2f%%", percentage) + "\n");
                        bufferedWriter.write("Letter Grade: " + letterGrade + "\n");
                        bufferedWriter.write("GPA: " + String.format("%.2f", gpa) + "\n\n");
                        
                        // Assessment Types table
                        bufferedWriter.write("Assessment Type,Count,Weight,Average Score,Weighted Score\n");
                        
                        for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                            AssessmentType assessmentType = entry.getValue();
                            
                            if (assessmentType.getWeight() > 0) {
                                // Calculate average score for this type
                                double totalScore = 0;
                                int scoredCount = 0;
                                
                                for (Assessment assessment : assessmentType.getAssessments()) {
                                    if (assessment.getScore() > 0) {
                                        totalScore += assessment.getScore();
                                        scoredCount++;
                                    }
                                }
                                
                                double averageScore = scoredCount > 0 ? totalScore / scoredCount : 0.0;
                                double weightedScore = averageScore * (assessmentType.getWeight() / 100.0);
                                
                                bufferedWriter.write(
                                    assessmentType.getDisplayName() + "," +
                                    assessmentType.getCount() + "," +
                                    String.format("%.2f%%", assessmentType.getWeight()) + "," +
                                    String.format("%.2f%%", averageScore) + "," +
                                    String.format("%.2f%%", weightedScore) + "\n"
                                );
                            }
                        }
                        
                        bufferedWriter.write("\n");
                        
                        // Individual Assessments
                        bufferedWriter.write("Assessment,Score,Finalized\n");
                        
                        for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                            AssessmentType assessmentType = entry.getValue();
                            
                            if (assessmentType.getWeight() > 0) {
                                for (Assessment assessment : assessmentType.getAssessments()) {
                                    String assessmentName = assessment.getDisplayName(assessmentType.getType());
                                    double score = assessment.getScore();
                                    boolean isFinal = assessment.isFinal();
                                    
                                    bufferedWriter.write(
                                        assessmentName + "," +
                                        String.format("%.2f%%", score) + "," +
                                        (isFinal ? "Yes" : "No") + "\n"
                                    );
                                }
                            }
                        }
                        
                        bufferedWriter.write("\n\n");
                    }
                    
                    bufferedWriter.close();
                    writer.close();
                    
                    // Show custom success dialog
                    Platform.runLater(() -> {
                        showCustomSuccessDialog("Data exported to CSV successfully!");
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        showCustomErrorDialog("Failed to export data: " + ex.getMessage());
                    });
                }
            }
        } catch (Exception e) {
            showCustomErrorDialog("Failed to export data: " + e.getMessage());
        }
    }

    /**
     * Export semester data to HTML with improved extension handling and detailed content
     */
    private void exportToHTML() {
        try {
            // Create a base filename WITHOUT any extension
            String baseFileName = semester.getName().replaceAll("\\s+", "_");
            
            // Show file save dialog with HTML type
            java.io.File file = ExportUtility.showSaveFileDialog("Export to HTML", baseFileName, "html");
            
            if (file != null) {
                // Get the file path
                String filePath = file.getAbsolutePath();
                System.out.println("Original path: " + filePath);
                
                // Remove any existing .html extension
                if (filePath.toLowerCase().endsWith(".html")) {
                    filePath = filePath.substring(0, filePath.length() - 5);
                }
                
                // Always add .html extension once
                filePath = filePath + ".html";
                System.out.println("Final export path: " + filePath);
                
                final String finalPath = filePath;
                
                // Use direct export to file (bypassing ExportUtility's dialogs)
                try {
                    // Create HTML writer
                    java.io.FileWriter writer = new java.io.FileWriter(finalPath);
                    java.io.BufferedWriter bufferedWriter = new java.io.BufferedWriter(writer);
                    
                    // HTML header
                    bufferedWriter.write("<!DOCTYPE html>\n");
                    bufferedWriter.write("<html lang=\"en\">\n");
                    bufferedWriter.write("<head>\n");
                    bufferedWriter.write("    <meta charset=\"UTF-8\">\n");
                    bufferedWriter.write("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
                    bufferedWriter.write("    <title>" + semester.getName() + " - Grade Report</title>\n");
                    bufferedWriter.write("    <style>\n");
                    bufferedWriter.write("        body { font-family: Arial, sans-serif; margin: 20px; color: #333; }\n");
                    bufferedWriter.write("        h1, h2, h3 { color: #003B6F; }\n"); // Northeastern Blue
                    bufferedWriter.write("        table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }\n");
                    bufferedWriter.write("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
                    bufferedWriter.write("        th { background-color: #f2f2f2; }\n");
                    bufferedWriter.write("        tr:nth-child(even) { background-color: #f9f9f9; }\n");
                    bufferedWriter.write("        .section { margin-bottom: 30px; }\n");
                    bufferedWriter.write("        .summary { font-weight: bold; margin: 20px 0; }\n");
                    bufferedWriter.write("        .subject-header { background-color: #e0e0f0; padding: 10px; margin-top: 30px; }\n");
                    bufferedWriter.write("        .finalized { color: green; }\n");
                    bufferedWriter.write("        .not-finalized { color: orange; }\n");
                    bufferedWriter.write("    </style>\n");
                    bufferedWriter.write("</head>\n");
                    bufferedWriter.write("<body>\n");
                    
                    // Semester title and info
                    bufferedWriter.write("    <h1>" + semester.getName() + " - Grade Report</h1>\n");
                    bufferedWriter.write("    <p>Generated on: " + java.time.LocalDate.now() + "</p>\n");
                    
                    double semesterGpa = semester.calculateGPA();
                    bufferedWriter.write("    <div class=\"summary\">\n");
                    bufferedWriter.write("        <p>Semester GPA: " + String.format("%.2f", semesterGpa) + "</p>\n");
                    bufferedWriter.write("    </div>\n");
                    
                    // For each subject
                    for (Subject subject : semester.getSubjects()) {
                        bufferedWriter.write("    <div class=\"section\">\n");
                        bufferedWriter.write("        <div class=\"subject-header\">\n");
                        bufferedWriter.write("            <h2>Subject: " + subject.getName() + "</h2>\n");
                        
                        // Subject overall stats
                        double percentage = subject.calculateOverallPercentage();
                        String letterGrade = subject.calculateLetterGrade();
                        double gpa = subject.calculateGPA();
                        
                        bufferedWriter.write("            <p><strong>Overall Percentage:</strong> " + 
                                            String.format("%.2f%%", percentage) + "</p>\n");
                        bufferedWriter.write("            <p><strong>Letter Grade:</strong> " + letterGrade + "</p>\n");
                        bufferedWriter.write("            <p><strong>GPA:</strong> " + String.format("%.2f", gpa) + "</p>\n");
                        bufferedWriter.write("        </div>\n");
                        
                        // Assessment Types table
                        bufferedWriter.write("        <div class=\"section\">\n");
                        bufferedWriter.write("            <h3>Assessment Types</h3>\n");
                        bufferedWriter.write("            <table>\n");
                        bufferedWriter.write("                <tr>\n");
                        bufferedWriter.write("                    <th>Assessment Type</th>\n");
                        bufferedWriter.write("                    <th>Count</th>\n");
                        bufferedWriter.write("                    <th>Weight</th>\n");
                        bufferedWriter.write("                    <th>Average Score</th>\n");
                        bufferedWriter.write("                    <th>Weighted Score</th>\n");
                        bufferedWriter.write("                </tr>\n");
                        
                        for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                            AssessmentType assessmentType = entry.getValue();
                            
                            if (assessmentType.getWeight() > 0) {
                                // Calculate average score for this type
                                double totalScore = 0;
                                int scoredCount = 0;
                                
                                for (Assessment assessment : assessmentType.getAssessments()) {
                                    if (assessment.getScore() > 0) {
                                        totalScore += assessment.getScore();
                                        scoredCount++;
                                    }
                                }
                                
                                double averageScore = scoredCount > 0 ? totalScore / scoredCount : 0.0;
                                double weightedScore = averageScore * (assessmentType.getWeight() / 100.0);
                                
                                bufferedWriter.write("                <tr>\n");
                                bufferedWriter.write("                    <td>" + assessmentType.getDisplayName() + "</td>\n");
                                bufferedWriter.write("                    <td>" + assessmentType.getCount() + "</td>\n");
                                bufferedWriter.write("                    <td>" + String.format("%.2f%%", assessmentType.getWeight()) + "</td>\n");
                                bufferedWriter.write("                    <td>" + String.format("%.2f%%", averageScore) + "</td>\n");
                                bufferedWriter.write("                    <td>" + String.format("%.2f%%", weightedScore) + "</td>\n");
                                bufferedWriter.write("                </tr>\n");
                            }
                        }
                        
                        bufferedWriter.write("            </table>\n");
                        bufferedWriter.write("        </div>\n");
                        
                        // Individual Assessments table
                        bufferedWriter.write("        <div class=\"section\">\n");
                        bufferedWriter.write("            <h3>Individual Assessments</h3>\n");
                        bufferedWriter.write("            <table>\n");
                        bufferedWriter.write("                <tr>\n");
                        bufferedWriter.write("                    <th>Assessment</th>\n");
                        bufferedWriter.write("                    <th>Score</th>\n");
                        bufferedWriter.write("                    <th>Status</th>\n");
                        bufferedWriter.write("                </tr>\n");
                        
                        for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                            AssessmentType assessmentType = entry.getValue();
                            
                            if (assessmentType.getWeight() > 0) {
                                for (Assessment assessment : assessmentType.getAssessments()) {
                                    String assessmentName = assessment.getDisplayName(assessmentType.getType());
                                    double score = assessment.getScore();
                                    boolean isFinal = assessment.isFinal();
                                    
                                    bufferedWriter.write("                <tr>\n");
                                    bufferedWriter.write("                    <td>" + assessmentName + "</td>\n");
                                    bufferedWriter.write("                    <td>" + String.format("%.2f%%", score) + "</td>\n");
                                    
                                    if (isFinal) {
                                        bufferedWriter.write("                    <td class=\"finalized\">Finalized</td>\n");
                                    } else {
                                        bufferedWriter.write("                    <td class=\"not-finalized\">Not Finalized</td>\n");
                                    }
                                    
                                    bufferedWriter.write("                </tr>\n");
                                }
                            }
                        }
                        
                        bufferedWriter.write("            </table>\n");
                        bufferedWriter.write("        </div>\n");
                        bufferedWriter.write("    </div>\n");
                    }
                    
                    // HTML footer
                    bufferedWriter.write("</body>\n");
                    bufferedWriter.write("</html>");
                    
                    bufferedWriter.close();
                    writer.close();
                    
                    // Show custom success dialog
                    Platform.runLater(() -> {
                        showCustomSuccessDialog("Data exported to HTML successfully!");
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        showCustomErrorDialog("Failed to export data: " + ex.getMessage());
                    });
                }
            }
        } catch (Exception e) {
            showCustomErrorDialog("Failed to export data: " + e.getMessage());
        }
    }

    /**
     * Show a clean success dialog with text and OK button
     */
    private void showCustomSuccessDialog(String message) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Export Successful");
        dialog.setWidth(400);
        dialog.setHeight(200);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: white;");
        
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Arial", 16));
        messageLabel.setTextFill(Color.rgb(0, 59, 111)); // Northeastern Blue
        messageLabel.setWrapText(true);
        
        Button okButton = new Button("OK");
        okButton.setPrefWidth(80);
        okButton.setPrefHeight(35);
        okButton.setFont(Font.font("Arial", 14));
        okButton.setStyle(
            "-fx-background-color: #00AD56; " + // Green color
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5;"
        );
        okButton.setOnAction(e -> dialog.close());
        
        content.getChildren().addAll(messageLabel, okButton);
        
        Scene scene = new Scene(content);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Show a clean error dialog with text and OK button
     */
    private void showCustomErrorDialog(String message) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Export Error");
        dialog.setWidth(400);
        dialog.setHeight(200);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: white;");
        
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Arial", 16));
        messageLabel.setTextFill(Color.rgb(200, 16, 46)); // Northeastern Red
        messageLabel.setWrapText(true);
        
        Button okButton = new Button("OK");
        okButton.setPrefWidth(80);
        okButton.setPrefHeight(35);
        okButton.setFont(Font.font("Arial", 14));
        okButton.setStyle(
            "-fx-background-color: #C8102E; " + // Red color
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5;"
        );
        okButton.setOnAction(e -> dialog.close());
        
        content.getChildren().addAll(messageLabel, okButton);
        
        Scene scene = new Scene(content);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
   
    /**
     * Show a styled error alert
     * 
     * @param title The alert title
     * @param message The alert message
     */
    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert dialog
        alert.getDialogPane().setStyle(
            "-fx-background-color: #f8f8ff; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1px; " +
            "-fx-font-size: 14px;"
        );
        
        alert.showAndWait();
    }
    
    /**
     * Show a styled information alert
     * 
     * @param title The alert title
     * @param message The alert message
     */
    private void showInfoAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert dialog
        alert.getDialogPane().setStyle(
            "-fx-background-color: #f8f8ff; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1px; " +
            "-fx-font-size: 14px;"
        );
        
        alert.showAndWait();
    }
    
    /**
     * Sets the application to full screen mode.
     * This method should be called after the stage is shown.
     * @param stage The primary stage of the application
     */
    public void setFullScreen(javafx.stage.Stage stage) {
        stage.setMaximized(true);
        
        // Additional step to ensure full screen after a short delay
        Platform.runLater(() -> {
            try {
                // Additional size adjustment after the stage is shown
                mainLayout.setPrefSize(stage.getWidth(), stage.getHeight());
                System.out.println("Adjusted size to stage dimensions: " + 
                                 stage.getWidth() + "x" + stage.getHeight());
            } catch (Exception e) {
                System.err.println("Error in setFullScreen: " + e.getMessage());
            }
        });
    }
    
    /**
     * Get the main layout for this view.
     * Additional sizing enforced here to ensure full screen display.
     * @return The main layout
     */
    public BorderPane getView() {
        // Ensure the layout uses maximum available space
        System.out.println("Getting AnalyticsView with dimensions: " + 
                         mainLayout.getPrefWidth() + "x" + mainLayout.getPrefHeight());
        
        // Force preferred width to screen width one more time before returning
        mainLayout.setPrefWidth(screenWidth);
        mainLayout.setPrefHeight(screenHeight);
        
        return mainLayout;
    }
}