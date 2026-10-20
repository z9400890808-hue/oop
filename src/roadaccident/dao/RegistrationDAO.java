package roadaccident.dao;

import roadaccident.model.Registration;
import roadaccident.util.DatabaseConnection;
import roadaccident.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationDAO {

    public boolean phoneNumberExists(String phoneNumber)
            throws SQLException {

        String normalizedPhoneNumber = normalizeText(phoneNumber);
        if (normalizedPhoneNumber == null) {
            return false;
        }

        String sql = """
            SELECT user_id
            FROM users
            WHERE phone_number = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, normalizedPhoneNumber);

            try (ResultSet result =
                         statement.executeQuery()) {

                return result.next();
            }
        }
    }

    public boolean register(Registration registration)
            throws SQLException {

        if (registration == null) {
            return false;
        }

        String phoneNumber = normalizeText(registration.getPhoneNumber());
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }

        if (phoneNumberExists(phoneNumber)) {
            return false;
        }

       String sql = """
    INSERT INTO users (
        full_name,
        phone_number,
        password_hash,
        role,
        zone,
        driving_licence_number,
        ambulance_number,
        hospital_name,
        designation,
        police_station,
        badge_number,
        verification_status
    )
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')
    """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

           
            String passwordHash =
                    registration.getPassword();

            statement.setString(
                    1,
                    registration.getFullName()
            );

            statement.setString(
                    2,
                    registration.getPhoneNumber()
            );

            statement.setString(
                    3,
                    passwordHash
            );

            statement.setString(
                    4,
                    registration.getRole()
            );

            statement.setString(
                    5,
                    registration.getZone()
            );

            statement.setString(
                    6,
                    emptyToNull(
                            registration
                                    .getDrivingLicenceNumber()
                    )
            );

            statement.setString(
                    7,
                    emptyToNull(
                            registration
                                    .getAmbulanceNumber()
                    )
            );

            statement.setString(
                    8,
                    emptyToNull(
                            registration
                                    .getHospitalName()
                    )
            );

            statement.setString(
                    9,
                    emptyToNull(
                            registration
                                    .getDesignation()
                    )
            );

            statement.setString(
                    10,
                    emptyToNull(
                            registration
                                    .getPoliceStation()
                    )
            );

            statement.setString(
                    11,
                    emptyToNull(
                            registration
                                    .getBadgeNumber()
                    )
            );

            int rowsInserted =
                    statement.executeUpdate();

            return rowsInserted > 0;
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String emptyToNull(String value) {
        return normalizeText(value);
    }

    public User login(
        String phoneNumber,
        String password) throws SQLException {

    String sql = """
        SELECT
            user_id,
            full_name,
            phone_number,
            password_hash,
            role,
            zone,
            verification_status,
            hospital_name
        FROM users
        WHERE phone_number = ?
        AND verification_status = 'APPROVED'
        """;

    try (Connection connection =
                 DatabaseConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setString(1, phoneNumber);

        try (ResultSet result =
                     statement.executeQuery()) {

            if (!result.next()) {
                return null;
            }

            String storedPassword =
                    result.getString("password_hash");

        
            if (!password.equals(storedPassword)) {
                return null;
            }

            return new User(
                    result.getInt("user_id"),
                    result.getString("full_name"),
                    result.getString("phone_number"),
                    result.getString("role"),
                    result.getString("zone"),
                    result.getString("verification_status"),
                    result.getString("hospital_name")
            );
        }
    }
}
}
