package cs1302.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cs1302.api.Schools;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** JsonParser class.
 *
 */

public class JsonParser {

    private static final Gson GSON = new Gson();

    /**
     * Parses a JSON string and extracts educational statistics as a formatted string.
     *
     * @param jsonString The JSON response from the DataUSA API.
     * @return A formatted string of educational statistics.
     */
    public static String parseEducationalStatistics(String jsonString) {
        // Deserialize the JSON string into a Map
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> response = GSON.fromJson(jsonString, type);

        // Extract data array from the response
        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

        // Format the statistics
        StringBuilder result = new StringBuilder();
        for (Map<String, Object> record : data) {
            String educationLevel = record.get("Education Attainment").toString();
            int population = ((Double) record.get("Population")).intValue();
            result.append(educationLevel).append(": ").append(population).append("\n");
        }

        return result.toString();
    }

    /**
     * Parses a JSON string and extracts a list of nearby institutions.
     *
     * @param jsonString The JSON response from the Google Maps API.
     * @return A list of Institution objects.
     */
    public static List<Schools> parseNearbyInstitutions(String jsonString) {
        // Deserialize the JSON string into a Map
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> response = GSON.fromJson(jsonString, type);

        // Extract results array
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        // Convert results to a list of Institution objects
        List<Schools> institutions = new ArrayList<>();
        for (Map<String, Object> result : results) {
            String name = result.get("name").toString();
            String address = result.containsKey("vicinity") ?
                result.get("vicinity").toString() : "Unknown";
            double rating = result.containsKey("rating") ? (Double) result.get("rating") : 0.0;

            institutions.add(new Schools(name, address, rating));
        }

        return institutions;
    }
}
