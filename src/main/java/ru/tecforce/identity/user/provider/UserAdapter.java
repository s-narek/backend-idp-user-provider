package ru.tecforce.identity.user.provider;

import org.jboss.logging.Logger;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import ru.tecforce.identity.user.entity.Email;
import ru.tecforce.identity.user.entity.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class UserAdapter extends AbstractUserAdapterFederatedStorage {
    private static final Logger log = Logger.getLogger(UserAdapter.class);
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

    public String getPassword() {
        // TODO Уточнить про пароль
        // return entity.getPassword();
        return "";
    }

    public void setPassword(String password) {
        log.info("$ "+ "setPassword() called with: password = [" + password + "]");
        // TODO Уточнить про пароль
        // entity.setPassword(password);
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
        return entity.getEmails().get(0).getEmail();
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        if (name.equals("firstName")) {
            entity.setFirstName(value);
        } else if (name.equals("lastName")) {
            entity.setLastName(value);
        } else {
            super.setSingleAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        if (name.equals("firstName")) {
            entity.setFirstName(null);
        } else if (name.equals("lastName")) {
            entity.setLastName(null);
        } else {
            super.removeAttribute(name);
        }
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        if (name.equals("firstName")) {
            entity.setFirstName(values.get(0));
        } else if (name.equals("lastName")) {
            entity.setLastName(values.get(0));
        } else {
            super.setAttribute(name, values);
        }
    }

    @Override
    public String getFirstAttribute(String name) {
        if (name.equals("firstName")) {
            return entity.getFirstName();
        } if (name.equals("lastName")) {
            return entity.getLastName();
        } else {
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
        return all;
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        if (name.equals("firstName")) {
            List<String> firstName = new LinkedList<>();
            firstName.add(entity.getFirstName());
            return firstName.stream();
        } else if (name.equals("lastName")) {
            List<String> lastName = new LinkedList<>();
            lastName.add(entity.getLastName());
            return lastName.stream();
        } else {
            return super.getAttributeStream(name);
        }
    }
}
