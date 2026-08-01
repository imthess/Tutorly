CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),
                       role ENUM('student','tutor','admin') NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE students (
                          student_id INT AUTO_INCREMENT PRIMARY KEY,
                          user_id INT UNIQUE,
                          education VARCHAR(100),
                          institute VARCHAR(100),

                          FOREIGN KEY (user_id)
                              REFERENCES users(user_id)
                              ON DELETE CASCADE
);
CREATE TABLE tutors (
                        tutor_id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT UNIQUE,

                        qualifications TEXT,
                        experience INT,
                        hourly_rate DECIMAL(8,2),
                        bio TEXT,

                        FOREIGN KEY (user_id)
                            REFERENCES users(user_id)
                            ON DELETE CASCADE
);
CREATE TABLE subjects (
                          subject_id INT AUTO_INCREMENT PRIMARY KEY,
                          subject_name VARCHAR(100) UNIQUE
);
CREATE TABLE tutor_subjects (

                                tutor_id INT,
                                subject_id INT,

                                PRIMARY KEY(tutor_id,subject_id),

                                FOREIGN KEY (tutor_id)
                                    REFERENCES tutors(tutor_id)
                                    ON DELETE CASCADE,

                                FOREIGN KEY (subject_id)
                                    REFERENCES subjects(subject_id)
                                    ON DELETE CASCADE
);
CREATE TABLE availability (

                              availability_id INT AUTO_INCREMENT PRIMARY KEY,

                              tutor_id INT,

                              day_of_week ENUM('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'),

                              start_time TIME,

                              end_time TIME,

                              status ENUM('Available','Unavailable') DEFAULT 'Available',

                              FOREIGN KEY (tutor_id)
                                  REFERENCES tutors(tutor_id)
                                  ON DELETE CASCADE
);
CREATE TABLE bookings (

                          booking_id INT AUTO_INCREMENT PRIMARY KEY,

                          student_id INT,

                          tutor_id INT,

                          subject_id INT,

                          booking_date DATE,

                          booking_time TIME,

                          duration INT,

                          status ENUM('Pending','Accepted','Rejected','Completed','Cancelled')
    DEFAULT 'Pending',

                          FOREIGN KEY(student_id)
                              REFERENCES students(student_id),

                          FOREIGN KEY(tutor_id)
                              REFERENCES tutors(tutor_id),

                          FOREIGN KEY(subject_id)
                              REFERENCES subjects(subject_id)
);
CREATE TABLE payments (

                          payment_id INT AUTO_INCREMENT PRIMARY KEY,

                          booking_id INT UNIQUE,

                          amount DECIMAL(10,2),

                          payment_method ENUM('Bkash','Nagad','Rocket','Card'),

                          payment_status ENUM('Pending','Paid','Failed','Refunded')
    DEFAULT 'Pending',

                          payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          FOREIGN KEY (booking_id)
                              REFERENCES bookings(booking_id)
                              ON DELETE CASCADE
);
CREATE TABLE reviews (

                         review_id INT AUTO_INCREMENT PRIMARY KEY,

                         booking_id INT,

                         student_id INT,

                         tutor_id INT,

                         rating INT CHECK(rating BETWEEN 1 AND 5),

                         comment TEXT,

                         review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                         FOREIGN KEY(student_id)
                             REFERENCES students(student_id),

                         FOREIGN KEY(tutor_id)
                             REFERENCES tutors(tutor_id),

                         FOREIGN KEY(booking_id)
                             REFERENCES bookings(booking_id)
);
CREATE TABLE learning_materials (

                                    material_id INT AUTO_INCREMENT PRIMARY KEY,

                                    tutor_id INT,

                                    title VARCHAR(200),

                                    file_path VARCHAR(255),

                                    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                    FOREIGN KEY (tutor_id)
                                        REFERENCES tutors(tutor_id)
);
CREATE TABLE online_classes (

                                class_id INT AUTO_INCREMENT PRIMARY KEY,

                                booking_id INT,

                                meeting_link VARCHAR(255),

                                start_time DATETIME,

                                end_time DATETIME,

                                status ENUM('Scheduled','Completed','Cancelled'),

                                FOREIGN KEY (booking_id)
                                    REFERENCES bookings(booking_id)
);
CREATE TABLE exams (

                       exam_id INT AUTO_INCREMENT PRIMARY KEY,

                       tutor_id INT,

                       subject_id INT,

                       title VARCHAR(100),

                       exam_date DATE,

                       FOREIGN KEY(tutor_id)
                           REFERENCES tutors(tutor_id),

                       FOREIGN KEY(subject_id)
                           REFERENCES subjects(subject_id)
);
CREATE TABLE exam_results (

                              result_id INT AUTO_INCREMENT PRIMARY KEY,

                              exam_id INT,

                              student_id INT,

                              marks DECIMAL(5,2),

                              grade VARCHAR(5),

                              FOREIGN KEY(exam_id)
                                  REFERENCES exams(exam_id),

                              FOREIGN KEY(student_id)
                                  REFERENCES students(student_id)
);
CREATE TABLE notifications (

                               notification_id INT AUTO_INCREMENT PRIMARY KEY,

                               user_id INT,

                               message TEXT,

                               notification_type ENUM('Booking','Payment','Class','Result'),

                               is_read BOOLEAN DEFAULT FALSE,

                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                               FOREIGN KEY(user_id)
                                   REFERENCES users(user_id)
                                   ON DELETE CASCADE
);
INSERT INTO subjects(subject_name)
VALUES
    ('Mathematics'),
    ('Physics'),
    ('Chemistry'),
    ('Biology'),
    ('English'),
    ('Geography'),
    ('History'),
    ('Computer');
