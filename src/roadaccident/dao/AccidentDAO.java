package roadaccident.dao;

import roadaccident.model.Accident;
import roadaccident.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccidentDAO {

    // Save accident
    public static int save(Accident accident) throws SQLException {

        String sql =
                "INSERT INTO accident "
                + "(reporter_name, reporter_phone, accident_date, "
                + "accident_time, location, accident_type, severity, "
                + "victim_count, description, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            ps.setString(1, accident.getName());
            ps.setString(2, accident.getPhone());
            ps.setString(3, accident.getDate());
            ps.setString(4, accident.getTime());
            ps.setString(5, accident.getLocation());
            ps.setString(6, accident.getAccidentType());
            ps.setString(7, accident.getSeverity());
            ps.setInt(8, accident.getNumberOfVictims());
            ps.setString(9, accident.getDescription());

            // Initial status
            ps.setString(10, "Reported");

            ps.executeUpdate();

            // Get generated accident ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException(
                "The accident report was inserted, but no generated ID was returned."
        );
    }


    // Get accident using ID
    public static Accident getAccidentById(int id) throws SQLException {

        String sql =
                "SELECT * FROM accident "
                + "WHERE accident_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new Accident(
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
                            result.getString("status")
                    );
                }
            }
        }
        return null;
    }
}