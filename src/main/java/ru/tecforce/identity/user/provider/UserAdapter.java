package ru.tecforce.identity.user.provider;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import ru.tecforce.identity.user.entity.Email;
import ru.tecforce.identity.user.entity.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class UserAdapter extends AbstractUserAdapterFederatedStorage {
    protected User entity;
    protected String keycloakId;

    public UserAdapter(
        KeycloakSession session,
        RealmModel realm,
        ComponentModel model,
        User entity
    ) {
        super(session, realm, model);

        this.entity = entity;
        this.keycloakId = StorageId.keycloakId(model, entity.getId().toString());
    }

    @Override
    public String getUsername() {
        return entity.getFirstName();
    }

    @Override
    public void setUsername(String username) {
        entity.setFirstName(username);
    }

    @Override
    public void setEmail(String email) {
        var emails = new Email();
        emails.setEmail(email);

        entity.setEmails(List.of(emails));
    }

    @Override
    public String getEmail() {
        if (!entity.getEmails().isEmpty()) {
            return entity.getEmails().get(0).getEmail();
        }
        return null;
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        switch (name) {
            case "firstName":
                entity.setFirstName(value);
                break;
            case "lastName":
                entity.setLastName(value);
                break;
            case "middleName":
                entity.setMiddleName(value);
                break;
            case "personnelNumber":
                entity.setPersonnelNumber(value);
                break;
            default:
                super.setSingleAttribute(name, value);
                break;
        }
    }

    @Override
    public void removeAttribute(String name) {
        switch (name) {
            case "firstName":
                entity.setFirstName(null);
                break;
            case "lastName":
                entity.setLastName(null);
                break;
            case "middleName":
                entity.setMiddleName(null);
                break;
            case "personnelNumber":
                entity.setPersonnelNumber(null);
                break;
            default:
                super.removeAttribute(name);
                break;
        }
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        switch (name) {
            case "firstName":
                entity.setFirstName(values.get(0));
                break;
            case "lastName":
                entity.setLastName(values.get(0));
                break;
            case "middleName":
                entity.setMiddleName(values.get(0));
                break;
            case "personnelNumber":
                entity.setPersonnelNumber(values.get(0));
                break;
            default:
                super.setAttribute(name, values);
                break;
        }
    }

    @Override
    public String getFirstAttribute(String name) {
        switch (name) {
            case "firstName":
                return entity.getFirstName();
            case "lastName":
                return entity.getLastName();
            case "middleName":
                return entity.getMiddleName();
            case "personnelNumber":
                return entity.getPersonnelNumber();
            default:
                return super.getFirstAttribute(name);
        }
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attrs = super.getAttributes();
        MultivaluedHashMap<String, String> all = new MultivaluedHashMap<>();
        all.putAll(attrs);
        all.add("firstName", entity.getFirstName());
        all.add("lastName", entity.getLastName());
        all.add("middleName", entity.getMiddleName());
        all.add("personnelNumber", entity.getPersonnelNumber());
        return all;
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        switch (name) {
            case "firstName":
                return Stream.of(entity.getFirstName());
            case "lastName":
                return Stream.of(entity.getLastName());
            case "middleName":
                return Stream.of(entity.getMiddleName());
            case "personnelNumber":
                return Stream.of(entity.getPersonnelNumber());
            default:
                return super.getAttributeStream(name);
        }
    }
}
