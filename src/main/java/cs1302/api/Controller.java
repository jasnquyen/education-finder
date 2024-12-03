package cs1302.api;

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

    private final VBox rootLayout;
    private final TextField searchField;
    private final ComboBox<String> locationTypeDropdown;
    private final ComboBox<String> schoolTypeDropdown;
    private final Button searchButton;
    private final ListView<Schools> resultsList;
    private final ImageView mapView;
    private final ProgressBar progressBar;

    private final ApiApp apiApp;

    /** Main Controller method.
     *
     */

    public Controller() {
        apiApp = new ApiApp();

        // Header Layout
        HBox headerLayout = new HBox(10);
        headerLayout.setPadding(new Insets(10));

        searchField = new TextField();
        searchField.setPromptText("Enter location");

        locationTypeDropdown = new ComboBox<>();
        locationTypeDropdown.getItems().addAll("State", "City", "Address");
        locationTypeDropdown.setPromptText("Location Type");

        schoolTypeDropdown = new ComboBox<>();
        schoolTypeDropdown.getItems().addAll("High School", "University/College");
        schoolTypeDropdown.setPromptText("School Type");

        searchButton = new Button("Search");
        searchButton.setOnAction(event -> handleSearch());

        headerLayout.getChildren().addAll
            (searchField, locationTypeDropdown, schoolTypeDropdown, searchButton);

        // Results List
        resultsList = new ListView<>();
        resultsList.setPrefHeight(200);
        resultsList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> handleSchoolSelection(newVal));

        // Map View
        mapView = new ImageView();
        mapView.setFitWidth(600);
        mapView.setFitHeight(200);
        mapView.setPreserveRatio(true);

        // Progress Bar
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(10));
        progressBar = new ProgressBar();
        progressBar.setPrefWidth(600);
        progressBar.setVisible(false);
        bottomBar.getChildren().add(progressBar);

        // Root Layout
        rootLayout = new VBox(10);
        rootLayout.setPadding(new Insets(10));
        rootLayout.getChildren().addAll(headerLayout, resultsList, mapView, bottomBar);
    }

    /** Get layout.
     * @return VBox
     */

    public VBox getLayout() {
        return rootLayout;
    }

    /** Makes sure all fields are answered.
     *
     */

    private void handleSearch() {
        String locationType = locationTypeDropdown.getValue();
        String locationValue = searchField.getText();
        String schoolType = schoolTypeDropdown.getValue();

        if (locationType == null || locationValue.isBlank() || schoolType == null) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        progressBar.setVisible(true);
        new Thread(() -> {
            try {
                // Fetch data
                List<Schools> institutions = apiApp.getInstitutions
                    (locationType, locationValue, schoolType);

                // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    resultsList.getItems().clear();
                    resultsList.getItems().addAll(institutions.subList
                        (0, Math.min(institutions.size(), 5)));
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    showAlert("Error", "Failed to fetch results: " + e.getMessage());
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

        String mapUrl = apiApp.getMapUrl(selectedSchool);
        Image mapImage = new Image(mapUrl);
        mapView.setImage(mapImage);
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
