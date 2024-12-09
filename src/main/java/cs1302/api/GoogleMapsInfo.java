package cs1302.api;

import java.util.Map;

/** Handles extraction and display of Google Maps information. */
public class GoogleMapsInfo {
    private final String description;
    private final String address;
    private final double rating;

    /**
     * Constructs a `GoogleMapsInfo` object with details from the Google Maps API.
     *
     * @param googleDetails A map containing the details from the Google Maps API response.
     */
    public GoogleMapsInfo(Map<String, Object> googleDetails) {
        this.description = googleDetails.containsKey("name")
                && googleDetails.get("name") instanceof String
                ? (String) googleDetails.get("name")
                : "Unknown Name";

        this.address = googleDetails.containsKey("formatted_address")
                && googleDetails.get("formatted_address") instanceof String
                ? (String) googleDetails.get("formatted_address")
                : "Unknown Address";

        this.rating = googleDetails.containsKey("rating")
                && googleDetails.get("rating") instanceof Double
                ? (Double) googleDetails.get("rating")
                : -1.0; // Use -1.0 to indicate missing rating
    }

    /**
     * Returns the description of the location.
     *
     * @return A description string.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the formatted address of the location.
     *
     * @return A formatted address string.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the rating of the location.
     *
     * @return A double representing the rating, or -1.0 if unavailable.
     */
    public double getRating() {
        return rating;
    }

    /**
     * Returns a formatted string with Google Maps details.
     *
     * @return A string containing the description, address, and rating.
     */
    @Override
    public String toString() {
        return String.format("Description: %s\nAddress: %s\nRating: %s",
                description,
                address,
                rating >= 0 ? String.format("%.1f", rating) : "Not Available");
    }
}
