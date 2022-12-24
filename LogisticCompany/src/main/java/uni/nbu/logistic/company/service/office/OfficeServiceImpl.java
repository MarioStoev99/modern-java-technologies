package uni.nbu.logistic.company.service.office;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import uni.nbu.logistic.company.model.Office;
import uni.nbu.logistic.company.repository.OfficeRepository;

@Service
@AllArgsConstructor
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;

    @Override
    public Office create(Office office) {
        return officeRepository.saveAndFlush(office);
    }

    @Override
    public List<Office> getAll() {
        return officeRepository.findAll();
    }

    @Override
    public Office update(UUID id, Office office) {
        if (!officeRepository.existsById(id)) {
            throw new IllegalStateException("The provided id does not exist!");
        }
        return officeRepository.saveAndFlush(office);
    }

    @Override
    public void delete(UUID id) {
        if (!officeRepository.existsById(id)) {
            throw new IllegalStateException("Office not exist!");
        }
        officeRepository.deleteById(id);
    }
}
