package bg.sofia.uni.fmi.mjt.weather;

import bg.sofia.uni.fmi.mjt.weather.dto.WeatherForecast;
import bg.sofia.uni.fmi.mjt.weather.exceptions.WeatherForecastClientException;

import java.net.http.HttpClient;

public class Main {

    public static void main(String[] args) {
        HttpClient client = HttpClient.newBuilder().build();
        WeatherForecastClient weatherForecastClient = new WeatherForecastClient(client, "ca48cd4ba08e6ca796a7d7f3c2652908");
        try {
            WeatherForecast weatherForecast = weatherForecastClient.getForecast("Sofia");
            System.out.println(weatherForecast);
        } catch (WeatherForecastClientException e) {
            System.out.println(e.getMessage());
        }
    }

}
