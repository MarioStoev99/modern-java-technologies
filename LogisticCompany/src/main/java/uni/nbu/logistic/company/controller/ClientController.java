package uni.nbu.logistic.company.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.AllArgsConstructor;
import uni.nbu.logistic.company.constants.Constants;
import uni.nbu.logistic.company.model.LoginRequest;
import uni.nbu.logistic.company.model.User;
import uni.nbu.logistic.company.service.client.ClientService;

@RestController
@RequestMapping(Constants.CLIENT_API)
@AllArgsConstructor
public class ClientController {

    private ClientService registrationService;

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return ResponseEntity.of(Optional.of(registrationService.create(user)));
    }

    @GetMapping
    public ResponseEntity<List<String>> getAll() {
        return ResponseEntity.of(Optional.of(registrationService.getAll()));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<HttpStatus> login(@Valid @RequestBody LoginRequest loginRequest) {
        registrationService.login(loginRequest);
        return ResponseEntity.of(Optional.of(HttpStatus.OK));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<User> update(@NotNull UUID id, @Valid @RequestBody User user) {
        return ResponseEntity.of(Optional.of(registrationService.update(id, user)));
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> delete(@NotNull UUID id) {
        registrationService.delete(id);
        return ResponseEntity.of(Optional.of(HttpStatus.OK));
    }
}
