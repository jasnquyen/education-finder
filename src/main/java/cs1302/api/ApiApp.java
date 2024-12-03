package cs1302.api;

import cs1302.api.Schools;
import cs1302.api.DataUsaApi;
import cs1302.api.GoogleMapsApi;

import java.util.List;

/** Implements all the APIs to collect data.
 *
 */

public class ApiApp {
    private final DataUsaApi dataUsaApi = new DataUsaApi();
    private final GoogleMapsApi googleMapsApi = new GoogleMapsApi();

    /** Collect list of schools.
     * @param locationType
     * @param locationValue
     * @param schoolType
     * @return list of Schools
     */

    public List<Schools> getInstitutions
    (String locationType, String locationValue, String schoolType) throws Exception {
        // Combine DataUSA and Google Maps API logic to fetch institutions
        return googleMapsApi.getInstitutions(locationValue, schoolType);
    }

    /** Use Google Maps Api URL.
     * @param institution
     * @return String
     */

    public String getMapUrl(Schools institution) {
        return googleMapsApi.getStaticMapUrl(institution.getAddress());
    }
}
