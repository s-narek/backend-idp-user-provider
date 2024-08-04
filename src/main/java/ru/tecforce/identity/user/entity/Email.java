package ru.tecforce.identity.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity(name = "email")
@Getter
@Setter
@NoArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;
    String email;
    @Enumerated(EnumType.STRING)
    EmailType type;
}
