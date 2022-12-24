package uni.nbu.logistic.company.service.parcel;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import uni.nbu.logistic.company.model.Parcel;
import uni.nbu.logistic.company.model.User;
import uni.nbu.logistic.company.repository.ClientRepository;
import uni.nbu.logistic.company.repository.ParcelRepository;

@Service
@AllArgsConstructor
public class ParcelServiceImpl implements ParcelService {

    private final ParcelRepository parcelRepository;
    private final ClientRepository clientRepository;

    @Override
    public Parcel create(Parcel parcel) {
        return parcelRepository.saveAndFlush(parcel);
    }

    @Override
    public List<Parcel> getAll(UUID originId) {
        User user = clientRepository.getReferenceById(originId);
        List<Parcel> parcels = parcelRepository.findAll();
        if (user != null) {
            String username = user.getUsername();
            List<Parcel> sentParcels = parcels
                    .stream()
                    .filter(parcel -> parcel.getSender().equals(username))
                    .collect(Collectors.toList());
            List<Parcel> receivedParcels = parcels
                    .stream()
                    .filter(parcel -> parcel.getRecipient().equals(username))
                    .collect(Collectors.toList());
            sentParcels.addAll(receivedParcels);
            return sentParcels;
        }
        return parcels;
    }

    @Override
    public Parcel update(UUID id, Parcel parcel) {
        if (!parcelRepository.existsById(id)) {
            throw new IllegalStateException("The provided id does not exist!");
        }
        return parcelRepository.saveAndFlush(parcel);
    }

    @Override
    public void delete(UUID id) {
        if (!parcelRepository.existsById(id)) {
            throw new IllegalStateException("Parcel not exist");
        }
        parcelRepository.deleteById(id);
    }
}
