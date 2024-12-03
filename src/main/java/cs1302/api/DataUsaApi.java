package cs1302.api;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/** DataUSA API.
 *
 */

public class DataUsaApi {

    private static final String BASE_URL = "https://datausa.io/api/data";
    private final HttpClient httpClient;
    private final Gson gson;

    /** DataUsa API.
     *
     */

    public DataUsaApi() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Fetches educational statistics for a given location.
     *
     * @param locationType The type of location (State, City, or Address).
     * @param locationValue The value of the location (e.g., "New York").
     * @return A formatted string of educational statistics.
     * @throws Exception If the API call fails.
     */
    public String getEducationalStatistics(String locationType, String locationValue)
        throws Exception {
        // Build the query
        String query = String.format("?drilldowns=%s&measures=Population&Geography=%s",
                locationType, URI.create(locationValue).toString());

        // Create the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + query))
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = httpClient.send
            (request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseEducationalStatistics(response.body());
        } else {
            throw new Exception("Failed to fetch data from DataUSA API: HTTP " +
            response.statusCode());
        }
    }

    /**
     * Parses and formats the educational statistics from the JSON response.
     *
     * @param jsonResponse The JSON response string.
     * @return A formatted string of educational statistics.
     */
    private String parseEducationalStatistics(String jsonResponse) {
        // Deserialize JSON response into a Map
        Map<String, Object> responseMap = gson.fromJson(jsonResponse, Map.class);

        // Extract "data" field as a list
        List<Map<String, Object>> data = (List<Map<String, Object>>) responseMap.get("data");

        // Format the statistics into a readable string
        StringBuilder result = new StringBuilder();
        for (Map<String, Object> record : data) {
            String educationLevel = record.get("Education Attainment").toString();
            int population = ((Double) record.get("Population")).intValue();
            result.append(educationLevel).append(": ").append(population).append("\n");
        }

        return result.toString();
    }
}
