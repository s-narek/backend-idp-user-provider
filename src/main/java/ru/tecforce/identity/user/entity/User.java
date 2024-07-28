package ru.tecforce.identity.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import ru.tecforce.identity.user.agreement.entity.UserAgreement;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getUserByUsername",
                query = "SELECT u FROM User u WHERE u.firstName = :firstName"
        ),
        @NamedQuery(
                name = "getUserByPersonnelNumber",
                query = "SELECT u FROM User u WHERE u.personnelNumber = :personnelNumber"
        ),
        @NamedQuery(
                name = "getUserCount",
                query = "SELECT COUNT(u) FROM User u"
        ),
        @NamedQuery(
                name = "getAllUsers",
                query = "SELECT u FROM User u"
        ),
        @NamedQuery(
                name = "searchForUser",
                query = "SELECT u FROM User u WHERE " +
                        "(LOWER(u.firstName) LIKE :search OR LOWER(u.lastName) LIKE :search) " +
                        "ORDER BY u.firstName"
        )
})
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    UUID id;
    UUID externalId;
    String firstName;
    String middleName;
    String lastName;
    String personnelNumber;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Phone> phones = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Email> emails = new ArrayList<>();
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    List<UserAgreement> agreements = new ArrayList<>();
}
