package ru.tecforce.identity.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tecforce.identity.user.agreement.entity.UserAgreement;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "user")
@Getter
@Setter
@NoArgsConstructor
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    UUID id;
    UUID externalId;
    @Column(name = "first_name")
    String firstName;
    @Column(name = "middle_name")
    String middleName;
    @Column(name = "last_name")
    String lastName;
    @Column(name = "personnel_number")
    String personnelNumber;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Phone> phones = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Email> emails = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<UserAgreement> agreements = new ArrayList<>();
}
