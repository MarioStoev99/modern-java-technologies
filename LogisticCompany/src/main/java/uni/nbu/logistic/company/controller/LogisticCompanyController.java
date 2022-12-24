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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import uni.nbu.logistic.company.constants.Constants;
import uni.nbu.logistic.company.model.Parcel;
import uni.nbu.logistic.company.service.client.ClientService;
import uni.nbu.logistic.company.service.parcel.ParcelService;

@RestController
@RequestMapping(Constants.LOGISTIC_API)
@AllArgsConstructor
public class LogisticCompanyController {

    private final ParcelService parcelService;
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Parcel> sendParcel(@Valid @RequestBody Parcel parcel) {
        return ResponseEntity.ok(parcelService.create(parcel));
    }

    @GetMapping
    public ResponseEntity<List<Parcel>> getAll(@Parameter(description = "Client identifier", required = true)
                                               @RequestHeader(Constants.ORIGIN_ID_HEADER) UUID originId) {
        if (!clientService.isLoggedIn(originId)) {
            throw new IllegalStateException("Not logged in!");
        }
        List<Parcel> parcels = parcelService.getAll(originId);
        return ResponseEntity.ok(parcels);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Parcel> update(@NotNull UUID id, @Valid @RequestBody Parcel parcel) {
        return ResponseEntity.of(Optional.of(parcelService.update(id, parcel)));
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> delete(@NotNull UUID id) {
        parcelService.delete(id);
        return ResponseEntity.of(Optional.of(HttpStatus.OK));
    }

}
