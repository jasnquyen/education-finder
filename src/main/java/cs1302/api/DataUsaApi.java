package cs1302.api;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/** DataUSA API.
 *
 */

public class DataUsaApi {

    private static final String BASE_URL = "https://api.data.gov/ed/collegescorecard/v1/schools";
    private static final String API_KEY = "bY9zbrK3jNOVY3ipATl3iUal2NmNnDUxBXW51qKj";
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
     * Fetches schools based on city or state.
     *
     * @param location The location filter (city or state).
     * @param locationType The type of location ("city" or "state").
     * @return A list of schools in the specified location.
     * @throws Exception If the API call fails.
     */

    public List<Schools> searchSchoolsByLocation(String location, String locationType)
        throws Exception {
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Location must not be null or empty");
        }
        if (!locationType.equals("city") && !locationType.equals("state")) {
            throw new IllegalArgumentException("Location type must be either 'city' or 'state'");
        }

        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
        String query = String.format(
             "?api_key=%s&school.%s=%s&fields=school.name,school.city,school.state," +
             "school.address,latest.student.size",
             API_KEY, locationType, encodedLocation
        );

        String requestUrl = BASE_URL + query;
        System.out.println("Generated URL: " + requestUrl);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(requestUrl))
            .GET()
            .build();

        HttpResponse<String> response =
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return parseSchools(response.body());
        } else {
            throw new Exception("Failed to fetch schools: HTTP " + response.statusCode());
        }
    }






    /**
     * Fetches educational statistics for a given location.
     *
     * @param institutionName The value of the location (e.g., "New York").
     * @return A formatted string of educational statistics.
     * @throws Exception If the API call fails.
     * @param schoolCity
     */
    public String getEducationalStatistics(String institutionName, String schoolCity)
        throws Exception {

        if (institutionName == null || institutionName.isEmpty()) {
            throw new IllegalArgumentException("Institution name must not be null or empty");
        }
        // Build the query
        String encodedInstitutionName = URLEncoder.encode(institutionName, StandardCharsets.UTF_8);
        String encodedCityName = URLEncoder.encode(schoolCity, StandardCharsets.UTF_8);
        String query = String.format(
            "?api_key=%s&school.name=%s&school.city=%s&fields=school.name,school.city,"
            + "school.address,latest.admissions.admission_rate.overall," +
            "latest.cost.attendance.academic_year,latest.student.size",
            API_KEY, encodedInstitutionName, encodedCityName

            );


        // Create the request
        String requestUrl = BASE_URL + query;
        System.out.println("Generated URL: " + requestUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = httpClient.send
            (request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseEducationalStatistics(response.body());
        } else {
            throw new Exception("Failed to fetch data from API: HTTP " +
            response.statusCode());
        }


    }

    /**
     * Parses schools from the JSON response.
     *
     * @param jsonResponse The JSON response string.
     * @return A list of Schools objects.
     */
    private List<Schools> parseSchools(String jsonResponse) {
        Map<String, Object> responseMap = gson.fromJson(jsonResponse, Map.class);

        if (!responseMap.containsKey("results") || responseMap.get("results") == null) {
            throw new IllegalStateException("API response missing 'results' field");
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) responseMap.get("results");

        List<Schools> schools = new ArrayList<>();
        for (Map<String, Object> school : results) {
            String name = school.containsKey("school.name") && school.get("school.name")
                instanceof String
                ? (String) school.get("school.name")
                : "Unknown Name";

            String city = school.containsKey("school.city") ?
                (String) school.get("school.city") : "Unknown City";
            String address = school.containsKey("school.address") ?
                (String) school.get("school.address") : "Unknown Address";



            schools.add(new Schools(name, address, city));
        }

        return schools;
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

        // Check if "data" exists and is not null
        if (!responseMap.containsKey("results") || responseMap.get("results") == null) {
            return "No data found for the specified institution.";
        }

        // Extract "data" field as a list
        List<Map<String, Object>> results = (List<Map<String, Object>>) responseMap.get("results");

        // Format the results
        if (results.isEmpty()) {
            return "No data found for the specified institution.";
        }

        Map<String, Object> school = results.get(0);
        System.out.println("Parsing school data: " + school);
        String name = (String) school.get("school.name");
        int enrollment = school.containsKey("latest.student.size") ?
            ((Double) school.get("latest.student.size")).intValue() : 0;
        double admissionRate = school.containsKey("latest.admissions.admission_rate.overall") &&
            school.get("latest.admissions.admission_rate.overall") instanceof Number
            ? (Double) school.get("latest.admissions.admission_rate.overall")
            : Double.NaN; // Default to NaN if missing

        double annualCost = school.containsKey("latest.cost.attendance.academic_year") &&
            school.get("latest.cost.attendance.academic_year") instanceof Number
            ? (Double) school.get("latest.cost.attendance.academic_year")
            : Double.NaN; // Use NaN if not available

        return (String.format(
            "Institution: %s\nEnrollment: %d\nAdmission Rate: %.2f%%\nAnnual Cost: $%.2f\n\n",
            name,
            enrollment,
            admissionRate * 100,
            annualCost
        ));

    }
}
