package ru.tecforce.identity.user.agreement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tecforce.identity.user.entity.User;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
