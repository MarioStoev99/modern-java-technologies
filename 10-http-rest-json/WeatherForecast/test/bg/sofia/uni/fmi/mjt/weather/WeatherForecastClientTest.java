package bg.sofia.uni.fmi.mjt.weather;

import bg.sofia.uni.fmi.mjt.weather.dto.WeatherCondition;
import bg.sofia.uni.fmi.mjt.weather.dto.WeatherData;
import bg.sofia.uni.fmi.mjt.weather.dto.WeatherForecast;
import bg.sofia.uni.fmi.mjt.weather.exceptions.LocationNotFoundException;
import bg.sofia.uni.fmi.mjt.weather.exceptions.WeatherForecastClientException;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeatherForecastClientTest {

    private static final String API_KEY = "ca48cd4ba08e6ca796a7d7f3c2652908";

    private WeatherForecastClient weatherForecastClient;

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    @Before
    public void setUp() {
        weatherForecastClient = new WeatherForecastClient(httpClientMock, API_KEY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetForecastPassNullAsArgument() throws Exception {
        weatherForecastClient.getForecast(null);
    }

    @Test
    public void testGetForecastSuccess() throws Exception {
        WeatherCondition weatherCondition = new WeatherCondition("предимно ясно");
        WeatherData weatherData = new WeatherData(12.6, 11.56);
        WeatherForecast expectedWeatherForecast = new WeatherForecast(new WeatherCondition[]{weatherCondition}, weatherData);

        String expectedWeatherForecastJson = new Gson().toJson(expectedWeatherForecast);

        when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(httpResponseMock.body()).thenReturn(expectedWeatherForecastJson);

        WeatherForecast actualWeatherForecast = weatherForecastClient.getForecast("Sofia");

        assertEquals(expectedWeatherForecast, actualWeatherForecast);
    }

    @Test(expected = LocationNotFoundException.class)
    public void testGetForecastWhenSuchCityDoesNotExist() throws IOException, InterruptedException, WeatherForecastClientException {
        when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);

        weatherForecastClient.getForecast("sss");
    }

    @Test(expected = WeatherForecastClientException.class)
    public void testGetForecastFailedWithHttpInternalServerError() throws IOException, InterruptedException, WeatherForecastClientException {
        when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);

        weatherForecastClient.getForecast("sss");
    }
}
