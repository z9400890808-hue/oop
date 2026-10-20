package roadaccident.dao;

import roadaccident.model.AccidentAlert;
import roadaccident.model.Hospital;
import roadaccident.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AmbulanceDriverDAO {

    public List<AccidentAlert> getAccidentsByZone(
            String zone) throws SQLException {

        if (zone == null || zone.isBlank()) {
            throw new IllegalArgumentException(
                    "A zone is required to load accident reports."
            );
        }

        List<AccidentAlert> accidents =
                new ArrayList<>();

        String sql = """
            SELECT
                accident.accident_id,
                accident.location,
                accident.reporter_phone,
                accident.severity,
                accident.victim_count,
                accident.description,
                accident.status,
                dispatch.pickup_time
            FROM accident
            LEFT JOIN ambulance_dispatch dispatch
                ON dispatch.accident_id = accident.accident_id
            WHERE UPPER(TRIM(accident.location)) = ?
            ORDER BY accident.created_at DESC, accident.accident_id DESC
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, zone.trim() + " ZONE");

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {
                    accidents.add(
                            new AccidentAlert(
                                    result.getInt(
                                            "accident_id"
                                    ),
                                    result.getString(
                                            "location"
                                    ),
                                    result.getString(
                                            "reporter_phone"
                                    ),
                                    zone.trim(),
                                    result.getString(
                                            "severity"
                                    ),
                                    result.getInt(
                                            "victim_count"
                                    ),
                                    result.getString(
                                            "description"
                                    ),
                                    result.getString(
                                            "status"
                                    ),
                                    result.getString(
                                            "pickup_time"
                                    )
                            )
                    );
                }
            }
        }

        return accidents;
    }

    public List<Hospital> getHospitalsByZone(
            String zone) throws SQLException {

        if (zone == null || zone.isBlank()) {
            throw new IllegalArgumentException(
                    "A zone is required to load hospitals."
            );
        }

        List<Hospital> hospitals =
                new ArrayList<>();

        String sql = """
            SELECT
                hospital_id,
                hospital_name,
                zone,
                available_beds,
                phone_number
            FROM hospitals
            WHERE UPPER(TRIM(REPLACE(UPPER(zone), ' ZONE', ''))) = ?
            AND available_beds > 0
            ORDER BY available_beds DESC,
                     hospital_name ASC
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, zone.trim());

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {
                    hospitals.add(
                            new Hospital(
                                    result.getInt(
                                            "hospital_id"
                                    ),
                                    result.getString(
                                            "hospital_name"
                                    ),
                                    result.getString(
                                            "zone"
                                    ),
                                    result.getInt(
                                            "available_beds"
                                    ),
                                    result.getString(
                                            "phone_number"
                                    )
                            )
                    );
                }
            }
        }

        return hospitals;
    }

    public Timestamp recordPickupAndNotifyHospital(
            int accidentId,
            int driverUserId,
            int hospitalId) throws SQLException {

        String hospitalSql = """
            SELECT
                hospital_name,
                available_beds,
                zone
            FROM hospitals
            WHERE hospital_id = ?
            FOR UPDATE
            """;

        String accidentSql = """
            SELECT location, status
            FROM accident
            WHERE accident_id = ?
            FOR UPDATE
            """;

        String pickupTimeSql = """
            SELECT CURRENT_TIMESTAMP
            """;

        String dispatchSql = """
            INSERT INTO ambulance_dispatch (
                accident_id,
                driver_user_id,
                hospital_id,
                pickup_time,
                dispatch_status
            )
            VALUES (?, ?, ?, ?, 'VICTIM_PICKED_UP')
            """;

        String updateAccidentSql = """
            UPDATE accident
            SET status = 'VICTIM_PICKED_UP'
            WHERE accident_id = ?
            """;

        String updateHospitalSql = """
            UPDATE hospitals
            SET available_beds = available_beds - 1
            WHERE hospital_id = ?
            AND available_beds > 0
            """;

        String notificationSql = """
            INSERT INTO notifications (
                accident_id,
                hospital_id,
                message,
                notification_type,
                notification_status,
                created_at
            )
            VALUES (?, ?, ?, 'HOSPITAL_ALERT', 'SENT', NOW())
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                String hospitalName;
                String hospitalZone;

                try (PreparedStatement hospitalStatement =
                             connection.prepareStatement(hospitalSql)) {
                    hospitalStatement.setInt(1, hospitalId);

                    try (ResultSet hospitalResult =
                                 hospitalStatement.executeQuery()) {
                        if (!hospitalResult.next()) {
                            throw new SQLException(
                                    "Hospital not found."
                            );
                        }

                        int availableBeds =
                                hospitalResult.getInt("available_beds");
                        if (availableBeds <= 0) {
                            throw new SQLException(
                                    "No beds available in the selected hospital."
                            );
                        }

                        hospitalName =
                                hospitalResult.getString("hospital_name");
                        hospitalZone =
                                hospitalResult.getString("zone");
                    }
                }

                String accidentLocation;
                String accidentStatus;
                try (PreparedStatement accidentStatement =
                             connection.prepareStatement(accidentSql)) {
                    accidentStatement.setInt(1, accidentId);

                    try (ResultSet accidentResult =
                                 accidentStatement.executeQuery()) {
                        if (!accidentResult.next()) {
                            throw new SQLException(
                                    "Accident record not found."
                            );
                        }
                        accidentLocation =
                                accidentResult.getString("location");
                        accidentStatus =
                                accidentResult.getString("status");
                    }
                }

                if ("VICTIM_PICKED_UP".equalsIgnoreCase(accidentStatus)) {
                    throw new SQLException(
                            "A driver has already picked up this accident."
                    );
                }

                if (!accidentLocation.trim().equalsIgnoreCase(
                        hospitalZone.trim() + " ZONE"
                )) {
                    throw new SQLException(
                            "Select a hospital in the accident's zone."
                    );
                }

                Timestamp pickupTime;
                try (PreparedStatement pickupTimeStatement =
                             connection.prepareStatement(pickupTimeSql);
                     ResultSet pickupTimeResult =
                             pickupTimeStatement.executeQuery()) {
                    if (!pickupTimeResult.next()) {
                        throw new SQLException(
                                "Could not determine the pickup time."
                        );
                    }
                    pickupTime =
                            pickupTimeResult.getTimestamp(1);
                }

                try (PreparedStatement dispatchStatement =
                             connection.prepareStatement(dispatchSql)) {
                    dispatchStatement.setInt(1, accidentId);
                    dispatchStatement.setInt(2, driverUserId);
                    dispatchStatement.setInt(3, hospitalId);
                    dispatchStatement.setTimestamp(4, pickupTime);

                    dispatchStatement.executeUpdate();
                }

                try (PreparedStatement accidentUpdate =
                             connection.prepareStatement(updateAccidentSql)) {
                    accidentUpdate.setInt(1, accidentId);

                    if (accidentUpdate.executeUpdate() == 0) {
                        throw new SQLException(
                                "Accident status could not be updated."
                        );
                    }
                }

                try (PreparedStatement hospitalUpdate =
                             connection.prepareStatement(updateHospitalSql)) {
                    hospitalUpdate.setInt(1, hospitalId);

                    if (hospitalUpdate.executeUpdate() == 0) {
                        throw new SQLException(
                                "Bed could not be reserved."
                        );
                    }
                }

                String message =
                        "Emergency patient pickup notification. "
                            + "Accident ID: ACC-"
                            + accidentId
                            + ". Ambulance driver has picked up "
                            + "the victim and is heading to "
                            + hospitalName
                            + ". Pickup time: "
                            + pickupTime;

                try (PreparedStatement notificationStatement =
                             connection.prepareStatement(notificationSql)) {
                    notificationStatement.setInt(1, accidentId);
                    notificationStatement.setInt(2, hospitalId);
                    notificationStatement.setString(3, message);

                    notificationStatement.executeUpdate();
                }

                connection.commit();

                return pickupTime;

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

}