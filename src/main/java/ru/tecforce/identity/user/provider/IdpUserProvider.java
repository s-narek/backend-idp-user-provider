package ru.tecforce.identity.user.provider;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import ru.tecforce.identity.user.agreement.entity.AgreementType;
import ru.tecforce.identity.user.agreement.entity.UserAgreement;
import ru.tecforce.identity.user.agreement.entity.UserAgreementValue;
import ru.tecforce.identity.user.entity.Email;
import ru.tecforce.identity.user.entity.EmailType;
import ru.tecforce.identity.user.entity.Phone;
import ru.tecforce.identity.user.entity.PhoneType;
import ru.tecforce.identity.user.entity.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class IdpUserProvider
        implements
        UserStorageProvider,
        UserRegistrationProvider,
        UserLookupProvider,
        UserQueryProvider {

    public static final String ID_COLUMN = "id";
    public static final String EXTERNAL_ID_COLUMN = "external_id";
    public static final String FIRST_NAME_COLUMN = "first_name";
    public static final String MIDDLE_NAME_COLUMN = "middle_name";
    public static final String LAST_NAME_COLUMN = "last_name";
    public static final String PERSONNEL_NUMBER_COLUMN = "personnel_number";
    public static final String PHONE_COLUMN = "phone";
    public static final String EMAIL_COLUMN = "email";
    public static final String TYPE_COLUMN = "type";
    public static final String TYPE_ID_COLUMN = "type_id";
    public static final String VALUE_COLUMN = "value";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String CODE_COLUMN = "code";

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    protected ComponentModel model;
    protected KeycloakSession session;


    public IdpUserProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.* FROM \"user\" u";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                User user = new User();
                user.setId((UUID) resultSet.getObject(ID_COLUMN));
                user.setExternalId((UUID) resultSet.getObject(EXTERNAL_ID_COLUMN));
                user.setFirstName(resultSet.getString(FIRST_NAME_COLUMN));
                user.setMiddleName(resultSet.getString(MIDDLE_NAME_COLUMN));
                user.setLastName(resultSet.getString(LAST_NAME_COLUMN));
                user.setPersonnelNumber(resultSet.getString(PERSONNEL_NUMBER_COLUMN));

                user.getPhones().addAll(getPhone(user));
                user.getEmails().addAll(getEmail(user));
                user.getAgreements().addAll(getAgreements(user));

                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<UserModel> userModels = new LinkedList<>();
        for (User entity : users) userModels.add(new UserAdapter(session, realm, model, entity));

        return userModels.stream();
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        User newUser = new User();
        newUser.setFirstName(username);

        String insertQuery = "INSERT INTO \"user\" (first_name) VALUES (?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, newUser.getFirstName());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {

                    newUser.setId((UUID) generatedKeys.getObject(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return new UserAdapter(session, realm, model, newUser);
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        String result = getUserId(id);

        User user = new User();
        String query = String.format("SELECT u.* FROM \"user\" u WHERE id = '%s'", result);

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                user.setId((UUID) resultSet.getObject(ID_COLUMN));
                user.setExternalId((UUID) resultSet.getObject(EXTERNAL_ID_COLUMN));
                user.setFirstName(resultSet.getString(FIRST_NAME_COLUMN));
                user.setMiddleName(resultSet.getString(MIDDLE_NAME_COLUMN));
                user.setLastName(resultSet.getString(LAST_NAME_COLUMN));
                user.setPersonnelNumber(resultSet.getString(PERSONNEL_NUMBER_COLUMN));

                user.getPhones().addAll(getPhone(user));
                user.getEmails().addAll(getEmail(user));
                user.getAgreements().addAll(getAgreements(user));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new UserAdapter(session, realm, model, user);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        UUID userId = UUID.fromString(getUserId(user.getId()));
        String query = "DELETE FROM \"user\" WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setObject(1, userId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void preRemove(RealmModel realm) {
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
    }

    @Override
    public void close() {
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        return null;
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        return null;
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

    private List<Phone> getPhone(User user) throws SQLException {
        String phonesQuery = "SELECT * FROM phone WHERE user_id = ?";
        List<Phone> phones = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement phonesStmt = connection.prepareStatement(phonesQuery)) {
            phonesStmt.setObject(1, UUID.fromString(user.getId().toString()));
            try (ResultSet phonesRs = phonesStmt.executeQuery()) {
                while (phonesRs.next()) {
                    Phone phone = new Phone();
                    phone.setId((UUID) phonesRs.getObject(ID_COLUMN));
                    phone.setPhone(phonesRs.getString(PHONE_COLUMN));
                    phone.setType(PhoneType.valueOf(phonesRs.getString(TYPE_COLUMN)));
                    phone.setUser(user);
                    phones.add(phone);
                }
            }
        }
        return phones;
    }

    private List<Email> getEmail(User user) throws SQLException {
        String emailsQuery = "SELECT * FROM email WHERE user_id = ?";
        List<Email> emails = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement emailsStmt = connection.prepareStatement(emailsQuery)) {
            emailsStmt.setObject(1, UUID.fromString(user.getId().toString()));
            try (ResultSet emailsRs = emailsStmt.executeQuery()) {
                while (emailsRs.next()) {
                    Email email = new Email();
                    email.setId((UUID) emailsRs.getObject(ID_COLUMN));
                    email.setEmail(emailsRs.getString(EMAIL_COLUMN));
                    email.setType(EmailType.valueOf(emailsRs.getString(TYPE_COLUMN)));
                    email.setUser(user);
                    emails.add(email);
                }
            }
        }
        return emails;
    }

    private List<UserAgreement> getAgreements(User user) throws SQLException {
        String agreementsQuery = "SELECT * FROM user_agreement WHERE user_id = ?";
        List<UserAgreement> userAgreements = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement agreementsStmt = connection.prepareStatement(agreementsQuery)) {
            agreementsStmt.setObject(1, UUID.fromString(user.getId().toString()));
            try (ResultSet resultSet = agreementsStmt.executeQuery()) {
                while (resultSet.next()) {
                    UserAgreement userAgreement = new UserAgreement();
                    userAgreement.setId((UUID) resultSet.getObject(ID_COLUMN));
                    userAgreement.setValue(UserAgreementValue.valueOf(resultSet.getString(VALUE_COLUMN)));
                    userAgreement.setType(getAgreementType(resultSet.getString(TYPE_ID_COLUMN)));
                    userAgreement.setUser(user);
                    userAgreements.add(userAgreement);
                }
            }
        }
        return userAgreements;
    }

    private AgreementType getAgreementType(String typeId) throws SQLException {
        String agreementsQuery = "SELECT * FROM agreement_type WHERE id = ?";
        AgreementType agreementType = new AgreementType();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement agreementsStmt = connection.prepareStatement(agreementsQuery)) {
            agreementsStmt.setObject(1, Integer.valueOf(typeId));
            try (ResultSet agreementsRs = agreementsStmt.executeQuery()) {
                while (agreementsRs.next()) {
                    agreementType.setId(agreementsRs.getInt(ID_COLUMN));
                    agreementType.setCode(agreementsRs.getString(CODE_COLUMN));
                    agreementType.setDescription(agreementsRs.getString(DESCRIPTION_COLUMN));
                }
            }
        }
        return agreementType;
    }

    private String getUserId(String id) {
        String[] parts = id.split(":");
        String result = "";
        if (parts.length > 1) {
            return parts[2];
        }
        return result;
    }
}
