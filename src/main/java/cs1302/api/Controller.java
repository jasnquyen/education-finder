package cs1302.api;

import java.util.Map;
import cs1302.api.ApiApp;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import cs1302.api.Schools;

import java.util.List;

/** Manages all GUI functions.
 *
 */

public class Controller {

    private final BorderPane rootLayout;
    private final TextField searchField;
    private final Button searchButton;
    private final ListView<Schools> resultsList;
    private final ImageView mapView;
    private final ApiApp apiApp;
    private final HBox bottomBar;
    private Schools selectedSchool;

    /** Main Controller method.
     *
     */

    public Controller() {

        resultsList = new ListView<>();
        resultsList.setPrefHeight(200);

        resultsList.getSelectionModel().selectedItemProperty().
            addListener((obs, oldVal, newVal) -> {
                selectedSchool = newVal; // Store the selected school
                if (selectedSchool != null) {
                    handleSchoolSelection(selectedSchool); // Pass the selected school
                }
            });
        apiApp = new ApiApp();
        HBox headerLayout = new HBox(10);
        headerLayout.setPadding(new Insets(10));
        searchField = new TextField();
        searchField.setPromptText("Enter address");

        searchButton = new Button("Search");
        searchButton.setOnAction(event -> handleSearch());

        headerLayout.getChildren().addAll
            (searchField, searchButton);


        // Map View
        mapView = new ImageView();
        mapView.setFitWidth(600);
        mapView.setFitHeight(200);
        mapView.setPreserveRatio(true);

        // School Info List
        rootLayout = new BorderPane();
        rootLayout.setPadding(new Insets(10));
        rootLayout.setTop(headerLayout);        // Header at the top
        rootLayout.setCenter(new VBox(10, resultsList, mapView)); // Center with results and map
        rootLayout.setRight(new VBox()); // Placeholder for DataUSA information


        // Bottom Bar
        bottomBar = new HBox();
        bottomBar.setPadding(new Insets(10));
        bottomBar.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #c0c0c0;");
        Label statusLabel = new Label("Loading...");
        statusLabel.setPadding(new Insets(5, 10, 5, 10));
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(0);
        progressBar.setPrefWidth(300);
        progressBar.setPrefHeight(25);
        progressBar.setVisible(true);

        bottomBar.getChildren().addAll(statusLabel, progressBar);

        rootLayout.setBottom(bottomBar);
    }

    /** Get layout.
     * @return BorderPane
     */

    public BorderPane getLayout() {
        return rootLayout;
    }

    /** Makes sure all fields are answered.
     *
     */

    private void handleSearch() {

        String searchQuery = searchField.getText().trim();

        System.out.println("Location:" + searchQuery);

        if (searchQuery.isBlank()) {
            showAlert("Input error:", "Please enter valid Address");
            return;
        }

        new Thread(() -> {
            try {
                 // Fetch schools based on location
                String locationType = searchQuery.length() == 2 ? "state" : "city";
            // Determine if it's a state or city
                List<Schools> schools = apiApp.getSchoolsByLocation(searchQuery, locationType);

            // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    resultsList.getItems().clear();
                    resultsList.getItems().addAll(schools);
                    if (schools.isEmpty()) {
                        showAlert("No Results", "No schools found in the given location.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Error", "Failed to fetch schools: " + e.getMessage());
                });
            }
        }).start();

    }

    /** Manage school selection.
     * @param selectedSchool
     */

    private void handleSchoolSelection(Schools selectedSchool) {
        if (selectedSchool == null) {
            return;
        }
        String schoolName = selectedSchool.getName().trim();
        String schoolAddress = selectedSchool.getAddress().trim();
        String schoolCity = selectedSchool.getCity().trim();
        String addressQuery = "Unknown Address".equals(schoolAddress)
            ? schoolName + ", " + schoolCity : schoolAddress;
        System.out.println("Selected School: " + schoolName);
        System.out.println("Selected School Address: " + schoolAddress);
        System.out.println("Selected School City: " + schoolCity);
        // Fetch educational statistics in a background thread
        new Thread(() -> {
            try {
                // Fetch educational statistics
                String educationalStats = apiApp.getEducationalStatistics
                    (schoolName, schoolCity);
                if (educationalStats == null || educationalStats.isBlank()) {
                    educationalStats = "No educational statistics available for this school.";
                }
                String coordinates = apiApp.getGoogleMapsApi().convertAddress(addressQuery);
                String mapUrl = apiApp.getGoogleMapsApi().getStaticMapUrl(coordinates);
                System.out.println("Map URL: " + mapUrl);
                Map<String, Object> googleDetails = apiApp.getGoogleMapsDetails(schoolName);

            // Extract data from the Google Maps response
                String description = googleDetails.containsKey("name")
                    && googleDetails.get("name") instanceof String
                    ? (String) googleDetails.get("name")
                    : "Unknown Name";
                String address = googleDetails.containsKey("formatted_address")
                    && googleDetails.get("formatted_address") instanceof String
                    ? (String) googleDetails.get("formatted_address")
                    : "Unknown Address";
                double rating = googleDetails.containsKey("rating")
                    && googleDetails.get("rating") instanceof Double
                    ? (Double) googleDetails.get("rating")
                    : -1.0; // Use -1.0 to indicate a missing rating

                final String finalEducationalStats = educationalStats;
                final String finalMapUrl = mapUrl;

                Platform.runLater(() -> {
                    try {
                        mapView.setImage(new Image(finalMapUrl));
                        updateStatisticsDisplay(finalEducationalStats, schoolName,
                            schoolCity, address, rating);
                    } catch (Exception e) {
                        showAlert("Error", "Failed to extract school details: " + e.getMessage());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Error", "Failed to fetch school details: " + e.getMessage());
                });
            }
        }).start();

    }
     /** Updates the statistics display area with educational information.
     * @param educationalStats
     * @param schoolName
     * @param schoolCity
     * @param address
     * @param rating
     */

    private void updateStatisticsDisplay(String educationalStats,
        String schoolName, String schoolCity, String address, double rating) {
        VBox statsBox = new VBox(10);
        statsBox.setPadding(new Insets(10));

        Label statsLabel = new Label("Educational Statistics:");
        statsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        TextArea statsArea = new TextArea(educationalStats);
        statsArea.setWrapText(true);
        statsArea.setEditable(false);

        Label googleLabel = new Label("Google Maps Details:");
        googleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label descriptionLabel = new Label("Description: " + (schoolName != null ?
            schoolName : "null"));
        Label cityLabel = new Label("City: " + (schoolCity != null ? schoolCity : "null"));
        Label addressLabel = new Label("Address: " + (address != null ? address : "null"));
        Label ratingLabel = new Label("Rating: " + (rating >= 0 ? rating : "null"));


        System.out.println("Educational Stats: " + educationalStats);
        System.out.println("School Name: " + schoolName + " (Type: " +
            (schoolName != null ? schoolName.getClass().getName() : "null") + ")");
        System.out.println("School City: " + schoolCity + " (Type: " + (schoolCity != null
            ? schoolCity.getClass().getName() : "null") + ")");
        System.out.println("Address: " + address + " (Type: " + (address != null
            ? address.getClass().getName() : "null") + ")");
        System.out.println("Rating: " + rating + " (Type: " +
            ((Object) rating).getClass().getName() + ")");

        statsBox.getChildren().addAll(statsLabel, statsArea,
            googleLabel, descriptionLabel, cityLabel, addressLabel, ratingLabel);
        rootLayout.setRight(statsBox);
    }

    /** Pop up an alert for errors.
     * @param title
     * @param message
     */

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
