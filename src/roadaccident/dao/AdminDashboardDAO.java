package roadaccident.dao;

import roadaccident.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class AdminDashboardDAO {

    public int getCountByStatus(String status)
            throws SQLException {

        String sql = """
            SELECT COUNT(*) AS total
            FROM users
            WHERE verification_status = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, status);

            try (ResultSet result =
                         statement.executeQuery()) {

                if (result.next()) {
                    return result.getInt("total");
                }
            }
        }

        return 0;
    }

    public int getCountByRole(String role)
            throws SQLException {

        String sql = """
            SELECT COUNT(*) AS total
            FROM users
            WHERE UPPER(REPLACE(role, ' ', '_')) = ?
            AND verification_status = 'APPROVED'
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, normalizeRole(role));

            try (ResultSet result =
                         statement.executeQuery()) {

                if (result.next()) {
                    return result.getInt("total");
                }
            }
        }

        return 0;
    }

    public ResultSet getPendingUsers(
            Connection connection) throws SQLException {

        String sql = """
            SELECT
                user_id,
                full_name,
                phone_number,
                role,
                zone,
                driving_licence_number,
                ambulance_number,
                hospital_name,
                designation,
                police_station,
                badge_number,
                created_at
            FROM users
            WHERE verification_status = 'PENDING'
            ORDER BY created_at DESC
            """;

        PreparedStatement statement =
                connection.prepareStatement(sql);

        return statement.executeQuery();
    }

    public ResultSet getApprovedUsersByRole(
            Connection connection,
            String role) throws SQLException {

        String sql = """
            SELECT
                user_id,
                full_name,
                phone_number,
                role,
                zone,
                driving_licence_number,
                ambulance_number,
                hospital_name,
                designation,
                police_station,
                badge_number,
                created_at
            FROM users
            WHERE verification_status = 'APPROVED'
            AND UPPER(REPLACE(role, ' ', '_')) = ?
            ORDER BY full_name ASC
            """;

        PreparedStatement statement =
                connection.prepareStatement(sql);
        statement.setString(1, normalizeRole(role));
        return statement.executeQuery();
    }

    private String normalizeRole(String role) {
        return role == null
                ? null
                : role.trim()
                        .toUpperCase(Locale.ROOT)
                        .replace(' ', '_');
    }

    public boolean updateRegistrationStatus(
            int userId,
            String status) throws SQLException {

        String sql = """
            UPDATE users
            SET verification_status = ?
            WHERE user_id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, userId);

            int updatedRows =
                    statement.executeUpdate();

            return updatedRows > 0;
        }
    }
}