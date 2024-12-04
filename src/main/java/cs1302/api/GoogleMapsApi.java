package cs1302.api;

import cs1302.api.Schools;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** Implementing Google Maps API.
 *
 */

public class GoogleMapsApi {

    private static final String PLACES_BASE_URL =
        "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static final String GEOCODING_BASE_URL =
        "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String STATIC_MAP_BASE_URL =
        "https://www.google.com/maps/embed/v1/place";
    private static final String API_KEY = "AIzaSyDAW_yi5_v4HeeAZENVmF5Rk7xlqp1T5xY";
    private final HttpClient httpClient;
    private final Gson gson;

    /** Google Maps API.
     *
     */

    public GoogleMapsApi() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /** Converts address to coordinates.
     * @return String
     * @param address
     */
    public String convertAddress(String address) throws Exception {
        String urlString = String.format(
            "%s?address=%s&key=%s",GEOCODING_BASE_URL,
            URI.create(address).toString().replace(" ", "+"),
            API_KEY
        );

        // Create the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .GET()
                .build();

        // Send the request and process the response
        HttpResponse<String> response =
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            Map<String, Object> responseMap = gson.fromJson(response.body(), Map.class);
            List<Map<String, Object>> results =
                (List<Map<String, Object>>) responseMap.get("results");

            if (!results.isEmpty()) {
                Map<String, Object> location = (Map<String, Object>)
                    ((Map<String, Object>) results.get(0).get("geometry")).get("location");
                double lat = (double) location.get("lat");
                double lng = (double) location.get("lng");
                return lat + "," + lng;
            }
        }

        throw new Exception("Failed to convert address to coordinates.");
    }


    /**
     * Fetches nearby educational institutions based on location and school type.
     *
     * @param location The location (latitude,longitude) as a string.
     * @return A list of institutions.
     * @throws Exception If the API call fails.
     */

    public List<Schools> findSchools(String location)
        throws Exception {
        // Build the query
        String query =
            String.format("?location=%s&radius=5000&type=%s&key=%s", location);

        // Create the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PLACES_BASE_URL + query))
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response
            = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseNearbyInstitutions(response.body());
        } else {
            throw new Exception("Failed to fetch data from Google Maps API: HTTP "
            + response.statusCode());
        }
    }

    /**
     * Parses the nearby institutions from the JSON response.
     *
     * @param jsonResponse The JSON response string.
     * @return A list of Institution objects.
     */
    private List<Schools> parseNearbyInstitutions(String jsonResponse) {
        // Deserialize JSON response into a Map
        Map<String, Object> responseMap = gson.fromJson(jsonResponse, Map.class);

        // Extract "results" field as a list
        List<Map<String, Object>> results = (List<Map<String, Object>>) responseMap.get("results");

        // Convert the results into a list of Institution objects
        List<Schools> institutions = new ArrayList<>();
        for (Map<String, Object> result : results) {
            String name = result.get("name").toString();
            String address = result.containsKey("vicinity") ?
                result.get("vicinity").toString() : "Unknown";

            institutions.add(new Schools(name, address));
        }

        return institutions;
    }

    /**
     * Generates a Static Map URL with given locations as markers.
     *
     * @param address a list of location strings (e.g., "New York, NY")
     * @return the Static Map URL
     */

    public String getStaticMapUrl(String address) {
        try {
        // Convert the address to coordinates
            String coordinates = convertAddress(address);

        // Construct the Static Map URL with the coordinates
            return String.format(
            "https://maps.googleapis.com/maps/api/staticmap?center=%s&zoom=15&size=600x300&markers=color:red|label:S|%s&key=%s",
            coordinates, coordinates, API_KEY
        );
        } catch (Exception e) {
            System.out.println("Error generating Static Map URL: " + e.getMessage());
            return null;
        }
    }



} //GoogleMapsApi
