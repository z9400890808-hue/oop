package roadaccident.dao;

import roadaccident.model.Hospital;
import roadaccident.model.HospitalNotification;
import roadaccident.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HospitalStaffDAO {

    public Hospital getHospitalForStaff(
            String hospitalName,
            String zone,
            String phoneNumber) throws SQLException {

        String findSql = """
            SELECT
                hospital_id,
                hospital_name,
                zone,
                available_beds,
                phone_number
            FROM hospitals
            WHERE UPPER(TRIM(hospital_name)) = UPPER(TRIM(?))
            AND UPPER(TRIM(REPLACE(UPPER(zone), ' ZONE', ''))) = ?
            """;

        String insertSql = """
            INSERT INTO hospitals (
                hospital_name,
                zone,
                available_beds,
                phone_number
            )
            VALUES (?, ?, 0, ?)
            """;

        if (zone == null || zone.isBlank()) {
            throw new IllegalArgumentException(
                    "The hospital staff account has no zone."
            );
        }
        String hospitalZone = zone.trim();
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                Hospital hospital = findHospital(
                        connection,
                        findSql,
                        hospitalName,
                        hospitalZone
                );

                if (hospital == null) {
                    try (PreparedStatement statement =
                                 connection.prepareStatement(insertSql)) {
                        statement.setString(1, hospitalName.trim());
                        statement.setString(2, hospitalZone);
                        statement.setString(3, phoneNumber);
                        statement.executeUpdate();
                    }

                    hospital = findHospital(
                            connection,
                            findSql,
                            hospitalName,
                            hospitalZone
                    );
                }

                if (hospital == null) {
                    throw new SQLException(
                            "Could not load or create the hospital record."
                    );
                }

                connection.commit();
                return hospital;
            } catch (SQLException ex) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    ex.addSuppressed(rollbackException);
                }
                throw ex;
            }
        }
    }

    private Hospital findHospital(
            Connection connection,
            String sql,
            String hospitalName,
            String zone) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {
            statement.setString(1, hospitalName);
            statement.setString(2, zone);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                return new Hospital(
                        result.getInt("hospital_id"),
                        result.getString("hospital_name"),
                        result.getString("zone"),
                        result.getInt("available_beds"),
                        result.getString("phone_number")
                );
            }
        }
    }

    public boolean updateAvailableBeds(
            int hospitalId,
            int availableBeds) throws SQLException {

        String sql = """
            UPDATE hospitals
            SET available_beds = ?
            WHERE hospital_id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, availableBeds);
            statement.setInt(2, hospitalId);

            return statement.executeUpdate() > 0;
        }
    }

    public List<HospitalNotification>
            getHospitalNotifications(
                    int hospitalId) throws SQLException {

        List<HospitalNotification> notifications =
                new ArrayList<>();

        String sql = """
            SELECT
                n.notification_id,
                n.accident_id,
                n.message,
                n.notification_status,
                n.created_at,

                a.location,
                a.severity,
                a.victim_count,

                d.pickup_time,

                u.full_name AS driver_name

            FROM notifications n

            JOIN accident a
                ON n.accident_id = a.accident_id

            LEFT JOIN ambulance_dispatch d
                ON d.accident_id = n.accident_id
                AND d.hospital_id = n.hospital_id

            LEFT JOIN users u
                ON d.driver_user_id = u.user_id

            WHERE n.hospital_id = ?

            ORDER BY n.created_at DESC
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, hospitalId);

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {
                    notifications.add(
                            new HospitalNotification(
                                    result.getInt(
                                            "notification_id"
                                    ),
                                    result.getInt(
                                            "accident_id"
                                    ),
                                    result.getString(
                                            "location"
                                    ),
                                    result.getString(
                                            "severity"
                                    ),
                                    result.getInt(
                                            "victim_count"
                                    ),
                                    String.valueOf(
                                            result.getTimestamp(
                                                    "pickup_time"
                                            )
                                    ),
                                    result.getString(
                                            "driver_name"
                                    ),
                                    result.getString(
                                            "message"
                                    ),
                                    result.getString(
                                            "notification_status"
                                    )
                            )
                    );
                }
            }
        }

        return notifications;
    }

    public boolean markNotificationAsRead(
            int notificationId) throws SQLException {

        String sql = """
            UPDATE notifications
            SET notification_status = 'READ'
            WHERE notification_id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, notificationId);

            return statement.executeUpdate() > 0;
        }
    }
}