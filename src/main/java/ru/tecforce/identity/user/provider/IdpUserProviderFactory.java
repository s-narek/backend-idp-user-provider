package ru.tecforce.identity.user.provider;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class IdpUserProviderFactory implements UserStorageProviderFactory<IdpUserProvider> {
    public static final String PROVIDER_ID = "custom_user_provider";

    @Override
    public IdpUserProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new IdpUserProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "User Provider";
    }

    @Override
    public void close() {
    }
}
