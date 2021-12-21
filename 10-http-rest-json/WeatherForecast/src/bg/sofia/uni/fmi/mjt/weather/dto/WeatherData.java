package bg.sofia.uni.fmi.mjt.weather.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class WeatherData {

    private final double temp;
    @SerializedName("feels_like")
    private final double feelsLike;

    public WeatherData(double temp, double feelsLike) {
        this.temp = temp;
        this.feelsLike = feelsLike;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "temp=" + temp +
                ", feelsLike=" + feelsLike +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherData that = (WeatherData) o;
        return Double.compare(that.temp, temp) == 0 && Double.compare(that.feelsLike, feelsLike) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(temp, feelsLike);
    }
}
