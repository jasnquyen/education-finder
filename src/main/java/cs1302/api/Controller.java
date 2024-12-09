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
import cs1302.api.GoogleMapsInfo;
import cs1302.api.MapDisplay;
import javafx.geometry.Pos;


import java.util.List;

/** Manages all GUI functions.
 *
 */

public class Controller {

    private final MapDisplay mapDisplay;
    private final BorderPane rootLayout;
    private final TextField searchField;
    private final Button searchButton;
    private final ListView<Schools> resultsList;
    private final ApiApp apiApp;
    private final HBox bottomBar;
    private Schools selectedSchool;

    /** Main Controller method.
     *
     */

    public Controller() {

        resultsList = new ListView<>();
        resultsList.setPrefHeight(200);
        resultsList.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 5px;");
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
        headerLayout.setAlignment(Pos.CENTER);

        Label searchLabel = new Label("Enter City:");
        searchLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        searchField = new TextField();
        searchButton = new Button("Search");
        searchButton.setOnAction(event -> handleSearch());

        headerLayout.getChildren().addAll
            (searchLabel, searchField, searchButton);


        // Map View
        mapDisplay = new MapDisplay(400, 200);


        // School Info List
        rootLayout = new BorderPane();
        rootLayout.setPadding(new Insets(10));
        rootLayout.setTop(headerLayout);        // Header at the top
        rootLayout.setCenter(new VBox(10, resultsList, mapDisplay.getContainer()));
        rootLayout.setRight(new VBox()); // Placeholder for DataUSA information


        // Bottom Bar
        bottomBar = new HBox();
        bottomBar.setPadding(new Insets(10));
        bottomBar.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #c0c0c0;");
        Label statusLabel = new Label("University Finder");
        statusLabel.setStyle("-fx-text-fill: gray;");
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.getChildren().add(statusLabel);

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
            showAlert("Input error:", "Please enter valid City");
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
        GoogleMapsApi googleMapsApi = new GoogleMapsApi();
        String normalizedSchoolName = googleMapsApi.
            normalizeUniversityName(selectedSchool.getName());
        String schoolAddress = selectedSchool.getAddress().trim();
        String schoolCity = selectedSchool.getCity().trim();
        String addressQuery = "Unknown Address".equals(schoolAddress)
            ? normalizedSchoolName + ", " + schoolCity : schoolAddress;
        new Thread(() -> {
            String educationalStats = "No educational statistics available for this school.";
            String mapUrl = null;
            Map<String, Object> googleDetails = null;

            try {
            // Fetch educational statistics
                educationalStats =
                    apiApp.getEducationalStatistics(normalizedSchoolName, schoolCity);
            } catch (Exception e) {
                System.err.println("Failed to fetch educational statistics: " + e.getMessage());
            }

            try {
                googleDetails = apiApp.getGoogleMapsDetails(normalizedSchoolName);
            } catch (Exception e) {
                System.err.println("Failed to fetch Google Maps details: " + e.getMessage());
            }
            try {
                String coordinates = apiApp.getGoogleMapsApi().convertAddress(addressQuery);
                mapUrl = apiApp.getGoogleMapsApi().getStaticMapUrl(coordinates);
            } catch (Exception e) {
                System.err.println("Failed to load map: " + e.getMessage());
                mapUrl = null;
            }
            final String finalEducationalStats = educationalStats;
            final Map<String, Object> finalGoogleDetails = googleDetails;
            final String finalMapUrl = mapUrl;
            Platform.runLater(() -> {
                try {
                    mapDisplay.updateMap(finalMapUrl);
                    if (finalGoogleDetails != null) {
                        GoogleMapsInfo googleMapsInfo = new GoogleMapsInfo(finalGoogleDetails);
                        updateStatisticsDisplay(finalEducationalStats, normalizedSchoolName,
                            schoolCity,googleMapsInfo.getAddress(), googleMapsInfo.getRating());
                    } else {
                        updateStatisticsDisplay(finalEducationalStats, normalizedSchoolName,
                            schoolCity, "Unknown Address", -1.0
                        );
                    }
                } catch (Exception e) {
                    showAlert("Error", "Failed to update UI: " + e.getMessage());
                }
            });
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
        statsArea.setPrefSize(300, 150); // Set preferred width and height
        statsArea.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        Label googleLabel = new Label("Google Maps Details:");
        googleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        TextArea googleDetailsArea = new TextArea();
        googleDetailsArea.setWrapText(true);
        googleDetailsArea.setEditable(false);
        googleDetailsArea.setPrefSize(300, 150); // Set preferred size for Google Maps Details box
        googleDetailsArea.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        // Populate Google Maps details text box
        String googleDetailsContent = String.format(
            "Description: %s\nCity: %s\nAddress: %s\nRating: %s",
            schoolName != null ? schoolName : "null",
            schoolCity != null ? schoolCity : "null",
            address != null ? address : "null",
            rating >= 0 ? rating : "null"
        );
        googleDetailsArea.setText(googleDetailsContent);

        statsBox.getChildren().addAll(statsLabel, statsArea,
            googleLabel, googleDetailsArea);
        ScrollPane scrollPane = new ScrollPane(statsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(320, 350); // Adjust dimensions as needed
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
