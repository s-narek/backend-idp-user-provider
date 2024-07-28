package ru.tecforce.identity.user.provider;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class CustomUserProviderFactory implements UserStorageProviderFactory<CustomUserProvider> {
    public static final String PROVIDER_ID = "custom_user_provider";

    @Override
    public CustomUserProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new CustomUserProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "Peanuts User Provider";
    }

    @Override
    public void close() {
    }
}
