package ru.tecforce.identity.user.provider;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.cache.OnUserCache;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import ru.tecforce.identity.user.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class CustomUserProvider
        implements
        UserStorageProvider,
        UserRegistrationProvider,
        UserLookupProvider,
        CredentialInputUpdater,
        CredentialInputValidator,
        OnUserCache,
        UserQueryProvider {

    private static final Logger logger = Logger.getLogger(CustomUserProvider.class);
    public static final String PASSWORD_CACHE_KEY = UserAdapter.class.getName() + ".password";

    protected EntityManager em;
    protected ComponentModel model;
    protected KeycloakSession session;

    public CustomUserProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
        this.em = session.getProvider(JpaConnectionProvider.class, "user-store").getEntityManager();
    }

    @Override
    public void preRemove(RealmModel realm) {
        logger.info("$ "+ "preRemove() called with: realm = [" + realm + "]");
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
        logger.info("$ "+ "preRemove() called with: realm = [" + realm + "], group = [" + group + "]");
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        logger.info("$ "+ "preRemove() called with: realm = [" + realm + "], role = [" + role + "]");
    }

    @Override
    public void close() {
        logger.info("$ "+ "close() called");
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        logger.info("$ "+ "getUserById() called with: realm = [" + realm + "], id = [" + id + "]");

        String persistenceId = StorageId.externalId(id);
        User entity = em.find(User.class, UUID.fromString(persistenceId));

        if (entity == null) {
            logger.info("could not find user by id: " + id);
            return null;
        }
        return new UserAdapter(session, realm, model, entity);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        logger.info("$ "+ "getUserByUsername() called with: realm = [" + realm + "], username = [" + username + "]");

        TypedQuery<User> query = em.createNamedQuery("getUserByUsername", User.class);
        query.setParameter("firstName", username);
        List<User> result = query.getResultList();

        if (result.isEmpty()) {
            logger.info("could not find username: " + username);
            return null;
        }

        return new UserAdapter(session, realm, model, result.get(0));
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        logger.info("$ "+ "getUserByEmail() called with: realm = [" + realm + "], email = [" + email + "]");

        return null;
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        logger.info("$ "+ "addUser() called with: realm = [" + realm + "], username = [" + username + "]");

        User entity = new User();
        entity.setId(UUID.randomUUID());
        entity.setFirstName(username);
        em.persist(entity);
        return new UserAdapter(session, realm, model, entity);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        logger.info("$ "+ "removeUser() called with: realm = [" + realm + "], user = [" + user + "]");

        String persistenceId = StorageId.externalId(user.getId());
        User entity = em.find(User.class, persistenceId);
        if (entity == null) return false;
        em.remove(entity);
        return true;
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel) {
        return List.of();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realmModel, int i, int i1) {
        return List.of();
    }

    @Override
    public List<UserModel> searchForUser(String s, RealmModel realmModel) {
        return List.of();
    }

    @Override
    public List<UserModel> searchForUser(String s, RealmModel realmModel, int i, int i1) {
        return List.of();
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> map, RealmModel realmModel) {
        return List.of();
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> map, RealmModel realmModel, int i, int i1) {
        return List.of();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realmModel, GroupModel groupModel) {
        return List.of();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realmModel, GroupModel groupModel, int i, int i1) {
        return List.of();
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String s, String s1, RealmModel realmModel) {
        return List.of();
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        logger.info("$ "+ "supportsCredentialType() called with: credentialType = [" + credentialType + "]");

        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        logger.info("$ "+ "updateCredential() called with: realm = [" + realm + "], user = [" + user + "], input = [" + input + "]");

        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;
        UserCredentialModel cred = (UserCredentialModel)input;
        UserAdapter adapter = getUserAdapter(user);
        // TODO Уточнить про пароль
         adapter.setPassword(cred.getValue());

        return true;
    }

    public UserAdapter getUserAdapter(UserModel user) {
        logger.info("$ "+ "getUserAdapter() called with: user = [" + user + "]");

        if (user instanceof CachedUserModel) {
            return (UserAdapter)((CachedUserModel) user).getDelegateForUpdate();
        } else {
            return (UserAdapter) user;
        }
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        logger.info("$ "+ "disableCredentialType() called with: realm = [" + realm + "], user = [" + user + "], credentialType = [" + credentialType + "]");

        if (!supportsCredentialType(credentialType)) return;

        // TODO Уточнить про пароль
         getUserAdapter(user).setPassword(null);
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        logger.info("$ "+ "getDisableableCredentialTypesStream() called with: realm = [" + realm + "], user = [" + user + "]");

        // TODO Уточнить про пароль
        if (getUserAdapter(user).getPassword() != null) {
            Set<String> set = new HashSet<>();
            set.add(PasswordCredentialModel.TYPE);
            return set;
        } else {
            return Set.of();
        }
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        logger.info("$ "+ "isConfiguredFor() called with: realm = [" + realm + "], user = [" + user + "], credentialType = [" + credentialType + "]");

        return supportsCredentialType(credentialType) && getPassword(user) != null;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        logger.info("$ "+ "isValid() called with: realm = [" + realm + "], user = [" + user + "], input = [" + input + "]");

        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;
        UserCredentialModel cred = (UserCredentialModel)input;
        String password = getPassword(user);
        return password != null && password.equals(cred.getValue());
    }

    public String getPassword(UserModel user) {
        logger.info("$ "+ "getPassword() called with: user = [" + user + "]");

        String password = null;
        if (user instanceof CachedUserModel) {
            password = (String)((CachedUserModel)user).getCachedWith().get(PASSWORD_CACHE_KEY);
        } else if (user instanceof UserAdapter) {
            // TODO Уточнить про пароль
             password = ((UserAdapter)user).getPassword();
        }
        return password;
    }

    @Override
    public void onCache(RealmModel realm, CachedUserModel cachedUserModel, UserModel userModel) {
        logger.info("$ "+ "onCache() called with: realm = [" + realm + "], user = [" + cachedUserModel + "], delegate = [" + userModel + "]");
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        logger.info("$ "+ "getUsersCount() called with: realm = [" + realm + "]");

        Object count = em.createNamedQuery("getUserCount")
                .getSingleResult();
        return ((Number)count).intValue();
    }

    @Override
    public Stream<UserModel> getUsersStream(RealmModel realm, Integer firstResult, Integer maxResults) {
        logger.info("$ "+ "getUsersStream() called with: realm = [" + realm + "], firstResult = [" + firstResult + "], maxResults = [" + maxResults + "]");

        TypedQuery<User> query = em.createNamedQuery("getAllUsers", User.class);
        if (firstResult == 1) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }
        List<User> results = query.getResultList();
        List<UserModel> users = new LinkedList<>();
        for (User entity : results) users.add(new UserAdapter(session, realm, model, entity));
        return users.stream();
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        logger.info("$ "+ "searchForUserStream() called with: realm = [" + realm + "], search = [" + search + "], firstResult = [" + firstResult + "], maxResults = [" + maxResults + "]");

        TypedQuery<User> query = em.createNamedQuery("searchForUser", User.class);
        query.setParameter("search", "%" + search.toLowerCase() + "%");
        if (firstResult == -1) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }
        List<User> results = query.getResultList();
        List<UserModel> users = new LinkedList<>();
        for (User entity : results) users.add(new UserAdapter(session, realm, model, entity));
        return users.stream();
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        logger.info("$ "+ "searchForUserStream() called with: realm = [" + realm + "], params = [" + params + "], firstResult = [" + firstResult + "], maxResults = [" + maxResults + "]");
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        logger.info("$ "+ "getGroupMembersStream() called with: realm = [" + realm + "], group = [" + group + "], firstResult = [" + firstResult + "], maxResults = [" + maxResults + "]");
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        logger.info("$ "+ "searchForUserByUserAttributeStream() called with: realm = [" + realm + "], attrName = [" + attrName + "], attrValue = [" + attrValue + "]");
        return Stream.empty();
    }
}
