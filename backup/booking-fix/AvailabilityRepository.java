package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.Availability;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityRepository {

    public List<Availability> findByTutorId(int tutorId)
            throws SQLException {

        String sql = """
                SELECT availability_id,
                       tutor_id,
                       day_of_week,
                       start_time,
                       end_time,
                       status
                FROM availability
                WHERE tutor_id = ?
                ORDER BY
                    FIELD(day_of_week,
                    'Monday','Tuesday','Wednesday',
                    'Thursday','Friday','Saturday','Sunday'),
                    start_time
                """;

        List<Availability> result = new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, tutorId);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    result.add(map(rs));
                }
            }
        }

        return result;
    }

    public int create(Availability availability)
            throws SQLException {

        String sql = """
                INSERT INTO availability
                (
                    tutor_id,
                    day_of_week,
                    start_time,
                    end_time,
                    status
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setInt(
                    1,
                    availability.getTutorId()
            );

            statement.setString(
                    2,
                    availability.getDayOfWeek()
            );

            statement.setTime(
                    3,
                    Time.valueOf(availability.getStartTime())
            );

            statement.setTime(
                    4,
                    Time.valueOf(availability.getEndTime())
            );

            statement.setString(
                    5,
                    availability.getStatus()
            );

            statement.executeUpdate();

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {
                    int id = keys.getInt(1);
                    availability.setAvailabilityId(id);
                    return id;
                }
            }
        }

        throw new SQLException(
                "Failed to create availability."
        );
    }

    public boolean delete(int availabilityId)
            throws SQLException {

        String sql = """
                DELETE FROM availability
                WHERE availability_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, availabilityId);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(
            int availabilityId,
            String status
    ) throws SQLException {

        String sql = """
                UPDATE availability
                SET status = ?
                WHERE availability_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, status);
            statement.setInt(2, availabilityId);

            return statement.executeUpdate() > 0;
        }
    }

    private Availability map(ResultSet rs)
            throws SQLException {

        Availability availability =
                new Availability();

        availability.setAvailabilityId(
                rs.getInt("availability_id")
        );

        availability.setTutorId(
                rs.getInt("tutor_id")
        );

        availability.setDayOfWeek(
                rs.getString("day_of_week")
        );

        Time start = rs.getTime("start_time");

        if (start != null) {
            availability.setStartTime(
                    start.toLocalTime()
            );
        }

        Time end = rs.getTime("end_time");

        if (end != null) {
            availability.setEndTime(
                    end.toLocalTime()
            );
        }

        availability.setStatus(
                rs.getString("status")
        );

        return availability;
    }
}
