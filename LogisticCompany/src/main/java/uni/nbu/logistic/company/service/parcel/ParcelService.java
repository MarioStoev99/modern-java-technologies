package uni.nbu.logistic.company.service.parcel;

import java.util.List;
import java.util.UUID;

import uni.nbu.logistic.company.model.Parcel;

public interface ParcelService {

    Parcel create(Parcel user);

    List<Parcel> getAll(UUID originId);

    Parcel update(UUID id, Parcel user);

    void delete(UUID id);

}
