package bg.sofia.uni.fmi.mjt.warehouse;

import java.time.LocalDateTime;

public record Parcel<P>(P parcel, LocalDateTime submissionDate) {
}