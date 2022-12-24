package uni.nbu.logistic.company.service.client;

import java.util.List;
import java.util.UUID;

import uni.nbu.logistic.company.model.LoginRequest;
import uni.nbu.logistic.company.model.User;

public interface ClientService {

    User create(User user);

    List<String> getAll();

    User update(UUID id, User user);

    void delete(UUID id);

    void login(LoginRequest loginRequest);

    boolean isLoggedIn(UUID originId);

}
