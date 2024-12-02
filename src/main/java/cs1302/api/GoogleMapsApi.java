package cs1302.api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** Implementing Google Maps API.
 *
 */

public class GoogleMapsApi {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api";
    private static final String API_KEY = "AIzaSyDAW_yi5_v4HeeAZENVmF5Rk7xlqp1T5xY";

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

        /**
     * Geocodes an address and returns the JSON response.
     *
     * @param address the address to geocode
     * @return the JSON response as a string
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the HTTP request is interrupted
     */
    public String geocodeAddress(String address) throws IOException, InterruptedException {
        String url = String.format(
            "%s/geocode/json?address=%s&key=%s",
            BASE_URL,
            URLEncoder.encode(address, StandardCharsets.UTF_8),
            API_KEY
        );
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
        HttpResponse<String> response =
            HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed request with status code: " + response.statusCode());
        }
        return response.body();
    }

    /**
     * Generates a Static Map URL with given locations as markers.
     *
     * @param locations a list of location strings (e.g., "New York, NY")
     * @return the Static Map URL
     */

    public String generateStaticMapUrl(String[] locations) {
        StringBuilder markers = new StringBuilder();
        for (String location : locations) {
            markers.append("&markers=").append(URLEncoder.encode(location, StandardCharsets.UTF_8));
        }
        return String.format("%s/staticmap?size=600x400%s&key=%s", BASE_URL, markers.toString(),
        API_KEY);
    }
}

}
