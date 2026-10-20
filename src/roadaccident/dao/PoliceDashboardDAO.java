package roadaccident.dao;

import roadaccident.model.PoliceIncident;
import roadaccident.model.TimelineEvent;
import roadaccident.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PoliceDashboardDAO {

    public List<PoliceIncident> getIncidentsByZone(String zone)
            throws SQLException {
        if (zone == null || zone.isBlank()) {
            throw new IllegalArgumentException(
                    "The police account does not have a zone."
            );
        }

        String sql = """
            SELECT
                a.accident_id,
                a.reporter_name,
                a.reporter_phone,
                a.accident_date,
                a.accident_time,
                a.location,
                a.accident_type,
                a.severity,
                a.victim_count,
                a.description,
                a.status,
                a.created_at AS reported_at,
                d.pickup_time,
                driver.full_name AS driver_name,
                driver.ambulance_number,
                h.hospital_name,
                h.phone_number AS hospital_phone
            FROM accident a
            LEFT JOIN ambulance_dispatch d
                ON d.accident_id = a.accident_id
            LEFT JOIN users driver
                ON driver.user_id = d.driver_user_id
            LEFT JOIN hospitals h
                ON h.hospital_id = d.hospital_id
            WHERE UPPER(TRIM(a.location)) = ?
            ORDER BY a.created_at DESC, a.accident_id DESC
            """;

        List<PoliceIncident> incidents = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, zone.trim() + " ZONE");

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    incidents.add(new PoliceIncident(
                            result.getInt("accident_id"),
                            result.getString("reporter_name"),
                            result.getString("reporter_phone"),
                            result.getString("accident_date"),
                            result.getString("accident_time"),
                            result.getString("location"),
                            result.getString("accident_type"),
                            result.getString("severity"),
                            result.getInt("victim_count"),
                            result.getString("description"),
                            result.getString("status"),
                            result.getString("reported_at"),
                            result.getString("pickup_time"),
                            result.getString("driver_name"),
                            result.getString("ambulance_number"),
                            result.getString("hospital_name"),
                            result.getString("hospital_phone")
                    ));
                }
            }
        }
        return incidents;
    }

    public List<TimelineEvent> getTimeline(int accidentId)
            throws SQLException {
        String sql = """
            SELECT event_time, event_description
            FROM (
                SELECT
                    a.created_at AS event_time,
                    CONCAT(
                        'Accident report submitted by ',
                        a.reporter_name,
                        ' (',
                        a.severity,
                        ', ',
                        a.victim_count,
                        ' victim(s))'
                    ) AS event_description
                FROM accident a
                WHERE a.accident_id = ?

                UNION ALL

                SELECT
                    d.pickup_time AS event_time,
                    CONCAT(
                        'Victim picked up by ',
                        COALESCE(u.full_name, 'ambulance driver'),
                        '; destination: ',
                        COALESCE(h.hospital_name, 'not recorded')
                    ) AS event_description
                FROM ambulance_dispatch d
                LEFT JOIN users u ON u.user_id = d.driver_user_id
                LEFT JOIN hospitals h ON h.hospital_id = d.hospital_id
                WHERE d.accident_id = ?

                UNION ALL

                SELECT
                    n.created_at AS event_time,
                    CONCAT('Hospital notified: ', n.message)
                        AS event_description
                FROM notifications n
                WHERE n.accident_id = ?

                UNION ALL

                SELECT
                    r.informed_at AS event_time,
                    CONCAT(
                        'Relative informed: ',
                        r.relative_name,
                        ' (',
                        r.contact_method,
                        ')'
                    ) AS event_description
                FROM relative_contact_log r
                WHERE r.accident_id = ?
            ) timeline
            ORDER BY event_time
            """;

        List<TimelineEvent> events = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int parameter = 1; parameter <= 4; parameter++) {
                statement.setInt(parameter, accidentId);
            }

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    events.add(new TimelineEvent(
                            result.getString("event_time"),
                            result.getString("event_description")
                    ));
                }
            }
        }
        return events;
    }

    public void recordRelativeInformed(
            int accidentId,
            int policeUserId,
            String relativeName,
            String relativePhone,
            String contactMethod) throws SQLException {
        String sql = """
            INSERT INTO relative_contact_log (
                accident_id,
                police_user_id,
                relative_name,
                relative_phone,
                contact_method,
                informed_at
            )
            VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accidentId);
            statement.setInt(2, policeUserId);
            statement.setString(3, relativeName.trim());
            statement.setString(4, relativePhone.trim());
            statement.setString(5, contactMethod);
            statement.executeUpdate();
        }
    }

}
