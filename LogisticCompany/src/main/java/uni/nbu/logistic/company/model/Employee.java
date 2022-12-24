package uni.nbu.logistic.company.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "employee")
public class Employee {

    @Id
    @NonNull
    private UUID id;

    @NonNull
    private String username;

    @NonNull
    @Column(name = "company_name")
    private String companyName;

    @NonNull
    private UserRole role;

}
