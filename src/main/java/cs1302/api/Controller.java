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

    private final BorderPane rootLayout;
    private final TextField searchField;
    private final Button searchButton;
    private final ListView<Schools> resultsList;
    private final ImageView mapView;
    private final ApiApp apiApp;
    private final HBox bottomBar;

    /** Main Controller method.
     *
     */

    public Controller() {
        apiApp = new ApiApp();

        // Header Layout
        HBox headerLayout = new HBox(10);
        headerLayout.setPadding(new Insets(10));

        searchField = new TextField();
        searchField.setPromptText("Enter address (1005 Macon Hwy)");

        searchButton = new Button("Search");
        searchButton.setOnAction(event -> handleSearch());

        headerLayout.getChildren().addAll
            (searchField, searchButton);

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

        // School Info List



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

        // Root Layout
        rootLayout = new BorderPane();
        rootLayout.setPadding(new Insets(10));
        rootLayout.setTop(headerLayout);        // Header at the top
        rootLayout.setCenter(new VBox(10, resultsList, mapView)); // Center with results and map
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

        String locationValue = searchField.getText();

        if (locationValue.isBlank()) {
            showAlert("Input error:", "Please enter valid Address");
            return;
        }

        new Thread(() -> {
            try {
                // Fetch data
                List<Schools> institutions = apiApp.getSchools
                    (locationValue);

                // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    resultsList.getItems().clear();
                    resultsList.getItems().addAll(institutions.subList
                        (0, Math.min(institutions.size(), 5)));
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
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
