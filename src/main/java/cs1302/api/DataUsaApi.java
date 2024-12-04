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
     * @param locationValue The value of the location (e.g., "New York").
     * @return A formatted string of educational statistics.
     * @throws Exception If the API call fails.
     */
    public String getEducationalStatistics(String locationValue)
        throws Exception {
        // Build the query
        String query = String.format(
                "?drilldowns=Institution&measures=Enrollment,Acceptance Rate,Location",
            URI.create(locationValue).toString().replace(" ", "+")
        );


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

        // Format the results
        if (data.isEmpty()) {
            return "No data found for the specified institution.";
        }

        StringBuilder result = new StringBuilder();
        for (Map<String, Object> record : data) {
            String institution = record.get("Institution").toString();
            int enrollment = ((Double) record.get("Enrollment")).intValue();
            double acceptanceRate = (record.containsKey("Acceptance Rate"))
                    ? (Double) record.get("Acceptance Rate")
                    : 0.0;
            String location = record.get("Location").toString();

            result.append("Institution: ").append(institution).append("\n");
            result.append("Enrollment: ").append(enrollment).append("\n");
            result.append("Acceptance Rate: ").append(acceptanceRate).append("%\n");
            result.append("Location: ").append(location).append("\n");

            result.append("\n\n");
        }

        return result.toString();

    }
}
