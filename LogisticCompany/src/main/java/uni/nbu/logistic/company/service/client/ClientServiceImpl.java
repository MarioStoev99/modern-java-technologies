package uni.nbu.logistic.company.service.client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import uni.nbu.logistic.company.model.LoginRequest;
import uni.nbu.logistic.company.model.User;
import uni.nbu.logistic.company.model.UserRole;
import uni.nbu.logistic.company.repository.ClientRepository;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public User create(User user) {
        if (UserRole.UNKNOWN.equals(user.getRole())) {
            throw new IllegalStateException("Invalid user role!");
        }
        return clientRepository.saveAndFlush(user);
    }

    @Override
    public List<String> getAll() {
        return clientRepository.findAll().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

    @Override
    public User update(UUID id, User user) {
        if (!clientRepository.existsById(id)) {
            throw new IllegalStateException("The provided id does not exist!");
        }
        return clientRepository.saveAndFlush(user);
    }

    @Override
    public void delete(UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new IllegalStateException("User not exist");
        }
        clientRepository.deleteById(id);
    }

    @Override
    public void login(LoginRequest loginRequest) {
        Optional<User> opUser = clientRepository.getByUsername(loginRequest.getUsername());
        if (!opUser.isPresent()) {
            throw new IllegalStateException("Provided username does not exist!");
        }

        User user = opUser.get();
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalStateException("Passwords are not the same!");
        }

        if (user.isLoggedIn()) {
            throw new IllegalStateException("Provided username is already loggedIn!");
        }

        user.setLoggedIn(true);
        clientRepository.saveAndFlush(user);
    }

    @Override
    public boolean isLoggedIn(UUID originId) {
        User user = clientRepository.getReferenceById(originId);
        return user.isLoggedIn();
    }
}
