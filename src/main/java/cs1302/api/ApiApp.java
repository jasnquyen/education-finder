package cs1302.api;

import cs1302.api.Schools;
import cs1302.api.CollegeScorecardApi;
import cs1302.api.GoogleMapsApi;
import java.util.Map;

import java.util.List;

/** Implements all the APIs to collect data.
 *
 */

public class ApiApp {
    private final CollegeScorecardApi dataUsaApi = new CollegeScorecardApi();
    private final GoogleMapsApi googleMapsApi = new GoogleMapsApi();

      /**
     * Fetches schools based on location.
     *
     * @param location The location filter (e.g., city or state).
     * @param locationType The type of location ("city" or "state").
     * @return A list of Schools objects.
     * @throws Exception If the API call fails.
     */
    public List<Schools> getSchoolsByLocation(String location, String locationType)
        throws Exception {
        return dataUsaApi.searchSchoolsByLocation(location, locationType);
    }


    /** Collect list of schools.
     * @param locationValue
     * @return list of Schools
     */

    public List<Schools> getSchools
    (String locationValue) throws Exception {
        // Combine DataUSA and Google Maps API logic to fetch institutions
        return googleMapsApi.findSchools(locationValue);
    }

    /** Use Google Maps Api URL.
     * @param institution
     * @return String
     */

    public String getMapUrl(Schools institution) {
        GoogleMapsApi mapsApi = new GoogleMapsApi();
        return googleMapsApi.getStaticMapUrl(institution.getAddress());
    }

     /** Fetch educational statistics for an institution.
     * @param schoolName The name of the school.
     * @param schoolCity
     * @return A string containing educational statistics.
     * @throws Exception if the API call fails
     */
    public String getEducationalStatistics(String schoolName, String schoolCity)
        throws Exception {

        return dataUsaApi.getEducationalStatistics(schoolName, schoolCity);
    }

    /** Google Map Details.
     * @param schoolName
     * @return map
     */

    public Map<String, Object> getGoogleMapsDetails(String schoolName) throws Exception {
        return googleMapsApi.getSchoolDetails(schoolName);
    }

    /** Return googleMapsAPI.
     * @return GoogleMapsAPI
     */

    public GoogleMapsApi getGoogleMapsApi() {
        return googleMapsApi;
    }
}
