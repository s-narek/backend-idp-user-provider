package ru.tecforce.identity.user.agreement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "agreement_type")
@Getter
@Setter
@NoArgsConstructor
public class AgreementType {

    @Id
    Integer id;
    String code;
    String description;
}
