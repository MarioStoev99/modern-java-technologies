package bg.sofia.uni.fmi.mjt.weather.dto;

import java.util.Arrays;
import java.util.Objects;

public class WeatherForecast {

    private final WeatherCondition[] weather;
    private final WeatherData main;

    public WeatherForecast(WeatherCondition[] weather, WeatherData main) {
        this.weather = weather;
        this.main = main;
    }

    @Override
    public String toString() {
        return "WeatherForecast{" +
                "weather=" + Arrays.toString(weather) +
                ", main=" + main +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherForecast that = (WeatherForecast) o;
        return Arrays.equals(weather, that.weather) && Objects.equals(main, that.main);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(main);
        result = 31 * result + Arrays.hashCode(weather);
        return result;
    }
}
