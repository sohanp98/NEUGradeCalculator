# NEU Grade Calculator

A comprehensive JavaFX application for Northeastern University College of Engineering students to track, calculate, and analyze their academic performance.

## Features

- **User Authentication**: Secure login and registration system
- **Semester Management**: Create and manage up to 4 semesters
- **Subject Tracking**: Add up to 2 subjects per semester with customizable assessment configurations
- **Flexible Grading System**: Support for various assessment types:
  - Assignments
  - Quizzes
  - Midterm exams
  - Final exams
  - Final projects
- **Grade Calculation**: Automatic calculation of percentages, letter grades, and GPAs
- **Goal Setting**: Set target grades and get recommendations on required scores
- **Performance Analytics**:
  - Grade distribution visualization
  - Performance trends over time
  - GPA projections
  - Personalized recommendations
- **Data Export**: Export transcripts and reports as CSV or HTML

## Technical Requirements

- Java Development Kit (JDK) 11 or higher
- Eclipse IDE
- JavaFX SDK (version 11 or higher)
- SQLite JDBC Driver

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/sohanp98/NEUGradeCalculator.git
cd NEUGradeCalculator
```

### 2. Setting up Eclipse Project

1. Open Eclipse
2. Go to File > Import > General > Existing Projects into Workspace
3. Select the cloned repository directory
4. Click Finish

### 3. Adding JavaFX to the Project

1. Right-click on the project in Project Explorer
2. Select Build Path > Configure Build Path
3. Go to "Libraries" tab
4. Click "Add Library..." > "User Library" > "Next"
5. Click "User Libraries..." > "New..." and name it "JavaFX"
6. With "JavaFX" selected, click "Add External JARs..."
7. Navigate to your JavaFX SDK lib folder and select all JAR files
8. Click "Open" > "OK" > "Finish"

### 4. Setting up SQLite

1. Download the SQLite JDBC driver (e.g., sqlite-jdbc-3.42.0.0.jar)
2. Right-click on the project
3. Select Build Path > Add External Archives
4. Navigate to the downloaded JAR file and select it

### 5. Running the Application

1. Right-click on `Main.java` in the `application` package
2. Select "Run As" > "Java Application"

If you encounter VM arguments issues with JavaFX, add these to your run configuration:
```
--module-path /path/to/javafx-sdk/lib --add-modules=javafx.controls,javafx.fxml
```

## Usage Guide

### Login/Registration

- New users can create an account by clicking "Sign Up"
- Returning users can login with their first name and password

### Home Screen

- View your overall GPA
- Set GPA goals and calculate required future performance
- Create new semesters or access existing ones

### Semester Management

- Click "Add Semester" to create a new semester
- Each semester card shows the name and current GPA
- Click "View Details" to manage subjects and grades

### Subject Management

1. Click "Add Subject" to create a new subject
2. Enter the subject name
3. Configure assessment types and their weights (must total 100%)
4. Click "Add Subject" to save

### Grade Entry

1. Click "Input Grades" on a subject card
2. Enter scores for each assessment
3. Check "Mark as Final" for completed assessments
4. Click "Save" to update grades and GPA

### Analytics

1. Click "Analytics" button on the semester screen
2. Navigate through different tabs to explore:
   - Overview of performance
   - Grade distribution charts
   - Performance trends
   - GPA projections
   - Personalized recommendations

### Exporting Data

1. Click "Export Reports" in the Analytics view
2. Choose CSV or HTML format
3. Select a location to save the file

## Project Structure

```
src/
  application/
    Main.java               # Application entry point
    controllers/            # MVC controllers for handling user interactions
    models/                 # Data models (User, Semester, Subject, etc.)
    views/                  # UI view classes
    utils/                  # Utility classes
      AnalyticsUtility.java # Advanced analytics functions
      DataStructures.java   # Various data structure implementations
      ExportUtility.java    # Data export functionality
      GradeCalculator.java  # Abstract grade calculation
      GraphUtility.java     # Graph-based analysis
    services/               # Business logic layer
    database/               # Database access layer
  resources/
    css/                    # Stylesheets
    images/                 # Application images
```

## Programming Concepts Implemented

The application demonstrates advanced Java programming concepts:

1. **Class Definition**: Foundation of the object-oriented design
2. **Inheritance/Polymorphism**: GradeCalculator hierarchy
3. **Abstract Classes/Interfaces**: GradeCalculator abstract class, EventListener interface
4. **Generics/Collections**: Type-safe collections throughout
5. **Lists**: ArrayList for storing collections of objects
6. **Stacks**: Used in graph algorithms and assessment tracking
7. **Queues/Priority Queues**: Task prioritization and graph traversal
8. **Set/Maps**: Storing unique elements and key-value relationships
9. **Graph**: Modeling subject dependencies with advanced algorithms
10. **Recursion**: Used in depth-first search and recursive analysis
