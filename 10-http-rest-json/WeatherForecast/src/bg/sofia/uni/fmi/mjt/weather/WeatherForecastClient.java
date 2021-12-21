package bg.sofia.uni.fmi.mjt.weather;

import bg.sofia.uni.fmi.mjt.weather.dto.WeatherForecast;
import bg.sofia.uni.fmi.mjt.weather.exceptions.LocationNotFoundException;
import bg.sofia.uni.fmi.mjt.weather.exceptions.WeatherForecastClientException;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherForecastClient {

    private static final String EXCEPTION_MESSAGE = "A problem occurred while getting weather forecast!";

    private static final String WEATHER_FORECAST_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=bg&appid=%s";

    private static final String WHITE_SPACE = "\\s+";
    private static final String REPLACEMENT_FOR_WHITE_SPACE = "%20";
    private static final Gson GSON = new Gson();

    private final HttpClient weatherHttpClient;
    private final String apiKey;

    public WeatherForecastClient(HttpClient weatherHttpClient, String apiКey) {
        this.weatherHttpClient = weatherHttpClient;
        this.apiKey = apiКey;
    }

    public WeatherForecast getForecast(String city) throws WeatherForecastClientException {
        if (city == null || city.isEmpty()) {
            throw new IllegalArgumentException("City cannot be null!");
        }

        String cityWithoutSpaces = city.replaceAll(WHITE_SPACE, REPLACEMENT_FOR_WHITE_SPACE);
        HttpResponse<String> httpResponse = sendHttpRequest(cityWithoutSpaces);

        int statusCode = httpResponse.statusCode();
        if (statusCode == HttpURLConnection.HTTP_OK) {
            return GSON.fromJson(httpResponse.body(), WeatherForecast.class);
        } else if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new LocationNotFoundException("Location " + city + "doesn't exist!");
        } else {
            throw new WeatherForecastClientException("The weather for location " + city + " could not be retrieved! Status code: " + statusCode);
        }
    }

    private HttpResponse<String> sendHttpRequest(String city) throws WeatherForecastClientException {
        try {
            URI uri = new URI(String.format(WEATHER_FORECAST_URL, city, apiKey));
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            return weatherHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new WeatherForecastClientException(EXCEPTION_MESSAGE, e);
        }
    }
}