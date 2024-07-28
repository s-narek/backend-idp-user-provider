package ru.tecforce.identity.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "phone")
@Getter
@Setter
@NoArgsConstructor
public class Phone {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;
    String phone;
    @Enumerated(EnumType.STRING)
    PhoneType type;
}
