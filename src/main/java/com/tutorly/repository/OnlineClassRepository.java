package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.OnlineClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class OnlineClassRepository {

    public int create(OnlineClass onlineClass)
            throws SQLException {

        String sql = """
                INSERT INTO online_classes
                (
                    booking_id,
                    meeting_link,
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
                                java.sql.Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setInt(
                    1,
                    onlineClass.getBookingId()
            );

            statement.setString(
                    2,
                    onlineClass.getMeetingLink()
            );

            statement.setTimestamp(
                    3,
                    onlineClass.getStartTime() == null
                            ? null
                            : Timestamp.valueOf(
                            onlineClass.getStartTime()
                    )
            );

            statement.setTimestamp(
                    4,
                    onlineClass.getEndTime() == null
                            ? null
                            : Timestamp.valueOf(
                            onlineClass.getEndTime()
                    )
            );

            statement.setString(
                    5,
                    onlineClass.getStatus()
            );

            statement.executeUpdate();

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {

                    int classId = keys.getInt(1);

                    onlineClass.setClassId(classId);

                    return classId;
                }
            }
        }

        throw new SQLException(
                "Failed to create online class."
        );
    }

    public OnlineClass findById(
            int classId
    ) throws SQLException {

        String sql = """
                SELECT
                    class_id,
                    booking_id,
                    meeting_link,
                    start_time,
                    end_time,
                    status
                FROM online_classes
                WHERE class_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, classId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                return mapOnlineClass(resultSet);
            }
        }
    }

    public OnlineClass findByBookingId(
            int bookingId
    ) throws SQLException {

        String sql = """
                SELECT
                    class_id,
                    booking_id,
                    meeting_link,
                    start_time,
                    end_time,
                    status
                FROM online_classes
                WHERE booking_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, bookingId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                return mapOnlineClass(resultSet);
            }
        }
    }

    public boolean updateStatus(
            int classId,
            String status
    ) throws SQLException {

        String sql = """
                UPDATE online_classes
                SET status = ?
                WHERE class_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, status);
            statement.setInt(2, classId);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateClass(
            OnlineClass onlineClass
    ) throws SQLException {

        String sql = """
            UPDATE online_classes
            SET meeting_link = ?,
                start_time = ?,
                end_time = ?,
                status = ?
            WHERE class_id = ?
            """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    onlineClass.getMeetingLink()
            );

            if (onlineClass.getStartTime() != null) {
                statement.setTimestamp(
                        2,
                        java.sql.Timestamp.valueOf(
                                onlineClass.getStartTime()
                        )
                );
            } else {
                statement.setNull(
                        2,
                        java.sql.Types.TIMESTAMP
                );
            }

            if (onlineClass.getEndTime() != null) {
                statement.setTimestamp(
                        3,
                        java.sql.Timestamp.valueOf(
                                onlineClass.getEndTime()
                        )
                );
            } else {
                statement.setNull(
                        3,
                        java.sql.Types.TIMESTAMP
                );
            }

            statement.setString(
                    4,
                    onlineClass.getStatus()
            );

            statement.setInt(
                    5,
                    onlineClass.getClassId()
            );

            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(
            int classId
    ) throws SQLException {

        String sql = """
                DELETE FROM online_classes
                WHERE class_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, classId);

            return statement.executeUpdate() > 0;
        }
    }

    private OnlineClass mapOnlineClass(
            ResultSet resultSet
    ) throws SQLException {

        OnlineClass onlineClass =
                new OnlineClass();

        onlineClass.setClassId(
                resultSet.getInt("class_id")
        );

        onlineClass.setBookingId(
                resultSet.getInt("booking_id")
        );

        onlineClass.setMeetingLink(
                resultSet.getString("meeting_link")
        );

        Timestamp startTime =
                resultSet.getTimestamp("start_time");

        if (startTime != null) {
            onlineClass.setStartTime(
                    startTime.toLocalDateTime()
            );
        }

        Timestamp endTime =
                resultSet.getTimestamp("end_time");

        if (endTime != null) {
            onlineClass.setEndTime(
                    endTime.toLocalDateTime()
            );
        }

        onlineClass.setStatus(
                resultSet.getString("status")
        );

        return onlineClass;
    }
}