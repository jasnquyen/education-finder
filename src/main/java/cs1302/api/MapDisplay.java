package cs1302.api;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.control.Label;

/** A class to manage map display in the application. */
public class MapDisplay {
    public final StackPane container;
    private final ImageView mapView;
    private final Label errorLabel;

    /** Constructs a MapDisplay instance.
     * @param width
     *@param height
     */
    public MapDisplay(int width, int height) {
        mapView = new ImageView();
        mapView.setFitWidth(width); // Set desired map width
        mapView.setFitHeight(height); // Set desired map height
        mapView.setPreserveRatio(true);

        errorLabel = new Label("Map can't be loaded");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // Customize text style
        errorLabel.setVisible(false); // Hidden by default

        container = new StackPane();
        container.setPrefSize(width, height);
        container.getChildren().addAll(mapView, errorLabel); // Add the mapView to the container
    }

    /** Returns the container StackPane.
     * @return StackPane container
     */
    public StackPane getContainer() {
        return container;
    }

    /** Updates the map with a given URL.
     * @param mapUrl the URL of the map to display
     */
    public void updateMap(String mapUrl) {
        if (mapUrl == null || mapUrl.isEmpty()) {
            mapView.setImage(null); // Clear the map if URL is invalid
            errorLabel.setVisible(true);
        } else {
            mapView.setImage(new Image(mapUrl)); // Load the new map
            errorLabel.setVisible(false);
        }
    }
}
