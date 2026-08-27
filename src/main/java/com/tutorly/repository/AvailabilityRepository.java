package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.Availability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityRepository {

    public int create(
            Availability availability
    ) throws SQLException {

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
                    Time.valueOf(
                            availability.getStartTime()
                    )
            );

            statement.setTime(
                    4,
                    Time.valueOf(
                            availability.getEndTime()
                    )
            );

            statement.setString(
                    5,
                    availability.getStatus()
            );

            statement.executeUpdate();

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {

                    int availabilityId =
                            keys.getInt(1);

                    availability.setAvailabilityId(
                            availabilityId
                    );

                    return availabilityId;
                }
            }
        }

        throw new SQLException(
                "Failed to create availability."
        );
    }

    public List<Availability> findByTutorId(
            int tutorId
    ) throws SQLException {

        String sql = """
                SELECT
                    availability_id,
                    tutor_id,
                    day_of_week,
                    start_time,
                    end_time,
                    status
                FROM availability
                WHERE tutor_id = ?
                ORDER BY
                    FIELD(
                        day_of_week,
                        'Monday',
                        'Tuesday',
                        'Wednesday',
                        'Thursday',
                        'Friday',
                        'Saturday',
                        'Sunday'
                    ),
                    start_time
                """;

        List<Availability> availabilityList =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, tutorId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                while (rs.next()) {

                    availabilityList.add(
                            map(rs)
                    );
                }
            }
        }

        return availabilityList;
    }

    public boolean isAvailable(
            int tutorId,
            LocalDate date,
            LocalTime time,
            int duration
    ) throws SQLException {

        if (tutorId <= 0 ||
                date == null ||
                time == null ||
                duration <= 0) {

            return false;
        }

        String dayOfWeek =
                convertDay(date.getDayOfWeek());

        LocalTime requestedEnd =
                time.plusMinutes(duration);

        /*
         * Prevent bookings from crossing midnight.
         */
        if (requestedEnd.isBefore(time)) {
            return false;
        }

        String sql = """
                SELECT COUNT(*)
                FROM availability
                WHERE tutor_id = ?
                  AND day_of_week = ?
                  AND status = 'Available'
                  AND start_time <= ?
                  AND end_time >= ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    tutorId
            );

            statement.setString(
                    2,
                    dayOfWeek
            );

            statement.setTime(
                    3,
                    Time.valueOf(time)
            );

            statement.setTime(
                    4,
                    Time.valueOf(requestedEnd)
            );

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean delete(
            int availabilityId
    ) throws SQLException {

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

            statement.setInt(
                    1,
                    availabilityId
            );

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

            statement.setString(
                    1,
                    status
            );

            statement.setInt(
                    2,
                    availabilityId
            );

            return statement.executeUpdate() > 0;
        }
    }

    private Availability map(
            ResultSet rs
    ) throws SQLException {

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

        Time start =
                rs.getTime("start_time");

        if (start != null) {
            availability.setStartTime(
                    start.toLocalTime()
            );
        }

        Time end =
                rs.getTime("end_time");

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

    private String convertDay(
            DayOfWeek day
    ) {

        return switch (day) {

            case MONDAY ->
                    "Monday";

            case TUESDAY ->
                    "Tuesday";

            case WEDNESDAY ->
                    "Wednesday";

            case THURSDAY ->
                    "Thursday";

            case FRIDAY ->
                    "Friday";

            case SATURDAY ->
                    "Saturday";

            case SUNDAY ->
                    "Sunday";
        };
    }
}
