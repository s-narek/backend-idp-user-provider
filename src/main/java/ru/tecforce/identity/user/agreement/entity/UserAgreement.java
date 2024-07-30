package ru.tecforce.identity.user.agreement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tecforce.identity.user.entity.User;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "user_agreement")
@Getter
@Setter
@NoArgsConstructor
public class UserAgreement {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;
    @Enumerated(EnumType.STRING)
    UserAgreementValue value;
    @ManyToOne
    @JoinColumn(name = "type_id")
    AgreementType type;
}
