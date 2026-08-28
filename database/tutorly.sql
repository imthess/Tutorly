-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Aug 28, 2026 at 07:00 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tutorly`
--

-- --------------------------------------------------------

--
-- Table structure for table `availability`
--

CREATE TABLE `availability` (
                                `availability_id` int(11) NOT NULL,
                                `tutor_id` int(11) DEFAULT NULL,
                                `subject_id` int(11) NOT NULL,
                                `day_of_week` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') DEFAULT NULL,
                                `start_time` time DEFAULT NULL,
                                `end_time` time DEFAULT NULL,
                                `description` varchar(500) DEFAULT NULL,
                                `status` enum('Available','Unavailable') DEFAULT 'Available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `availability`
--

INSERT INTO `availability` (`availability_id`, `tutor_id`, `subject_id`, `day_of_week`, `start_time`, `end_time`, `description`, `status`) VALUES
                                                                                                                                               (39, 5, 20, 'Monday', '08:00:00', '09:00:00', 'qqq', 'Available'),
                                                                                                                                               (40, 7, 20, 'Monday', '11:00:00', '13:00:00', 'new', 'Available');

-- --------------------------------------------------------

--
-- Table structure for table `bookings`
--

CREATE TABLE `bookings` (
                            `booking_id` int(11) NOT NULL,
                            `student_id` int(11) DEFAULT NULL,
                            `tutor_id` int(11) DEFAULT NULL,
                            `subject_id` int(11) DEFAULT NULL,
                            `booking_date` date DEFAULT NULL,
                            `booking_time` time DEFAULT NULL,
                            `duration` int(11) DEFAULT NULL,
                            `status` enum('Pending','Accepted','Rejected','Completed','Cancelled') DEFAULT 'Pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exams`
--

CREATE TABLE `exams` (
                         `exam_id` int(11) NOT NULL,
                         `tutor_id` int(11) DEFAULT NULL,
                         `subject_id` int(11) DEFAULT NULL,
                         `title` varchar(100) DEFAULT NULL,
                         `exam_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exam_results`
--

CREATE TABLE `exam_results` (
                                `result_id` int(11) NOT NULL,
                                `exam_id` int(11) DEFAULT NULL,
                                `student_id` int(11) DEFAULT NULL,
                                `marks` decimal(5,2) DEFAULT NULL,
                                `grade` varchar(5) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `learning_materials`
--

CREATE TABLE `learning_materials` (
                                      `material_id` int(11) NOT NULL,
                                      `tutor_id` int(11) DEFAULT NULL,
                                      `title` varchar(200) DEFAULT NULL,
                                      `file_path` varchar(255) DEFAULT NULL,
                                      `upload_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
                                 `notification_id` int(11) NOT NULL,
                                 `user_id` int(11) DEFAULT NULL,
                                 `message` text DEFAULT NULL,
                                 `notification_type` enum('Booking','Payment','Class','Result') DEFAULT NULL,
                                 `is_read` tinyint(1) DEFAULT 0,
                                 `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `online_classes`
--

CREATE TABLE `online_classes` (
                                  `class_id` int(11) NOT NULL,
                                  `booking_id` int(11) DEFAULT NULL,
                                  `meeting_link` varchar(255) DEFAULT NULL,
                                  `start_time` datetime DEFAULT NULL,
                                  `end_time` datetime DEFAULT NULL,
                                  `status` enum('Scheduled','Completed','Cancelled') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
                            `payment_id` int(11) NOT NULL,
                            `booking_id` int(11) DEFAULT NULL,
                            `amount` decimal(10,2) DEFAULT NULL,
                            `payment_method` enum('Bkash','Nagad','Rocket','Card') DEFAULT NULL,
                            `payment_status` enum('Pending','Paid','Failed','Refunded') DEFAULT 'Pending',
                            `payment_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
                           `review_id` int(11) NOT NULL,
                           `booking_id` int(11) DEFAULT NULL,
                           `student_id` int(11) DEFAULT NULL,
                           `tutor_id` int(11) DEFAULT NULL,
                           `rating` int(11) DEFAULT NULL CHECK (`rating` between 1 and 5),
                           `comment` text DEFAULT NULL,
                           `review_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
                            `student_id` int(11) NOT NULL,
                            `user_id` int(11) DEFAULT NULL,
                            `education` varchar(100) DEFAULT NULL,
                            `institute` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`student_id`, `user_id`, `education`, `institute`) VALUES
                                                                               (1, 4, 'ZA', 'ZA'),
                                                                               (2, 5, 'aa', 'aa'),
                                                                               (3, 7, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `subjects`
--

CREATE TABLE `subjects` (
                            `subject_id` int(11) NOT NULL,
                            `tutor_id` int(11) DEFAULT NULL,
                            `subject_name` varchar(150) NOT NULL,
                            `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `subjects`
--

INSERT INTO `subjects` (`subject_id`, `tutor_id`, `subject_name`, `description`) VALUES
                                                                                     (11, NULL, 'Mathematics', 'Covers fundamental algebra, calculus, and geometry concepts.'),
                                                                                     (12, NULL, 'Physics', 'Introduction to mechanics, thermodynamics, and electromagnetism.'),
                                                                                     (13, NULL, 'Chemistry', 'Study of organic, inorganic, and physical chemical properties.'),
                                                                                     (14, NULL, 'Biology', 'Explores cell biology, genetics, and evolutionary science.'),
                                                                                     (15, NULL, 'English Literature', 'Critical analysis of classic and modern literary works.'),
                                                                                     (16, NULL, 'Computer Science', 'Fundamentals of programming, data structures, and algorithms.'),
                                                                                     (17, NULL, 'World History', 'Overview of major global historical events and civilizations.'),
                                                                                     (18, NULL, 'Economics', 'Principles of microeconomics, macroeconomics, and market systems.'),
                                                                                     (19, NULL, 'Psychology', 'Introduction to human behavior, cognition, and mental processes.'),
                                                                                     (20, NULL, 'Art & Design', 'Exploration of visual arts, color theory, and digital design techniques.');

-- --------------------------------------------------------

--
-- Table structure for table `tutors`
--

CREATE TABLE `tutors` (
                          `tutor_id` int(11) NOT NULL,
                          `user_id` int(11) DEFAULT NULL,
                          `qualifications` text DEFAULT NULL,
                          `experience` int(11) DEFAULT NULL,
                          `hourly_rate` decimal(8,2) DEFAULT NULL,
                          `bio` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tutors`
--

INSERT INTO `tutors` (`tutor_id`, `user_id`, `qualifications`, `experience`, `hourly_rate`, `bio`) VALUES
                                                                                                       (1, 1, 'phd in f1', 10, 100000.00, '4 times WC'),
                                                                                                       (2, 3, 'az', 10, 10.00, 'azazaz'),
                                                                                                       (3, 6, NULL, NULL, NULL, NULL),
                                                                                                       (4, 8, NULL, NULL, NULL, NULL),
                                                                                                       (5, 9, 'q', 10, 100.00, 'q'),
                                                                                                       (6, 10, NULL, NULL, NULL, NULL),
                                                                                                       (7, 11, 'e', 10, 10.00, 'w');

-- --------------------------------------------------------

--
-- Table structure for table `tutor_subjects`
--

CREATE TABLE `tutor_subjects` (
                                  `tutor_id` int(11) NOT NULL,
                                  `subject_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
                         `user_id` int(11) NOT NULL,
                         `full_name` varchar(100) NOT NULL,
                         `email` varchar(100) NOT NULL,
                         `password` varchar(255) NOT NULL,
                         `phone` varchar(20) DEFAULT NULL,
                         `role` enum('student','tutor','admin') NOT NULL,
                         `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `full_name`, `email`, `password`, `phone`, `role`, `created_at`) VALUES
                                                                                                     (1, 'Max Verstappen', 'maxverstappen@gmail.com', 'maxverstappen', '01234567890', 'tutor', '2026-08-13 15:08:10'),
                                                                                                     (2, 'Hans Hass', 'hanshass@gmail.com', 'hanshass', '11111111111', 'student', '2026-08-13 15:25:48'),
                                                                                                     (3, 'AZ', 'az@az.com', 'azazaz', '11111111111', 'tutor', '2026-08-14 13:21:14'),
                                                                                                     (4, 'ZA', 'za@za.com', 'zazaza', '11111111111', 'student', '2026-08-14 13:22:19'),
                                                                                                     (5, 'a', 'a@aa.aa', 'aaaaaa', '11111111111', 'student', '2026-08-15 10:30:34'),
                                                                                                     (6, 'b', 'b@bb.bb', 'bbbbbb', '22222222222', 'tutor', '2026-08-15 11:21:35'),
                                                                                                     (7, 's', 's@s.s', 'ssssss', '33333333333', 'student', '2026-08-15 11:25:10'),
                                                                                                     (8, 't', 't@t.t', 'tttttt', '01111111111', 'tutor', '2026-08-21 05:47:18'),
                                                                                                     (9, 'q', 'q@q.q', 'qqqqqq', '01111111111', 'tutor', '2026-08-21 06:17:33'),
                                                                                                     (10, 'w', 'w@w.w', 'wwwwww', '11111111111', 'tutor', '2026-08-27 14:23:25'),
                                                                                                     (11, 'e', 'e@e.e', 'eeeeee', '33333333333', 'tutor', '2026-08-28 10:30:41');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `availability`
--
ALTER TABLE `availability`
    ADD PRIMARY KEY (`availability_id`),
  ADD KEY `tutor_id` (`tutor_id`),
  ADD KEY `fk_availability_tutor_subject` (`tutor_id`,`subject_id`),
  ADD KEY `fk_availability_subjects_direct` (`subject_id`);

--
-- Indexes for table `bookings`
--
ALTER TABLE `bookings`
    ADD PRIMARY KEY (`booking_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `tutor_id` (`tutor_id`),
  ADD KEY `subject_id` (`subject_id`);

--
-- Indexes for table `exams`
--
ALTER TABLE `exams`
    ADD PRIMARY KEY (`exam_id`),
  ADD KEY `tutor_id` (`tutor_id`),
  ADD KEY `subject_id` (`subject_id`);

--
-- Indexes for table `exam_results`
--
ALTER TABLE `exam_results`
    ADD PRIMARY KEY (`result_id`),
  ADD KEY `exam_id` (`exam_id`),
  ADD KEY `student_id` (`student_id`);

--
-- Indexes for table `learning_materials`
--
ALTER TABLE `learning_materials`
    ADD PRIMARY KEY (`material_id`),
  ADD KEY `tutor_id` (`tutor_id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
    ADD PRIMARY KEY (`notification_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `online_classes`
--
ALTER TABLE `online_classes`
    ADD PRIMARY KEY (`class_id`),
  ADD KEY `booking_id` (`booking_id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
    ADD PRIMARY KEY (`payment_id`),
  ADD UNIQUE KEY `booking_id` (`booking_id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
    ADD PRIMARY KEY (`review_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `tutor_id` (`tutor_id`),
  ADD KEY `booking_id` (`booking_id`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
    ADD PRIMARY KEY (`student_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `subjects`
--
ALTER TABLE `subjects`
    ADD PRIMARY KEY (`subject_id`),
  ADD UNIQUE KEY `tutor_id` (`tutor_id`,`subject_name`);

--
-- Indexes for table `tutors`
--
ALTER TABLE `tutors`
    ADD PRIMARY KEY (`tutor_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `tutor_subjects`
--
ALTER TABLE `tutor_subjects`
    ADD PRIMARY KEY (`tutor_id`,`subject_id`),
  ADD UNIQUE KEY `uq_tutor_subject` (`tutor_id`,`subject_id`),
  ADD KEY `subject_id` (`subject_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
    ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `availability`
--
ALTER TABLE `availability`
    MODIFY `availability_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
    MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `exams`
--
ALTER TABLE `exams`
    MODIFY `exam_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `exam_results`
--
ALTER TABLE `exam_results`
    MODIFY `result_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `learning_materials`
--
ALTER TABLE `learning_materials`
    MODIFY `material_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
    MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `online_classes`
--
ALTER TABLE `online_classes`
    MODIFY `class_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
    MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
    MODIFY `review_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
    MODIFY `student_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `subjects`
--
ALTER TABLE `subjects`
    MODIFY `subject_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `tutors`
--
ALTER TABLE `tutors`
    MODIFY `tutor_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
    MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `availability`
--
ALTER TABLE `availability`
    ADD CONSTRAINT `availability_ibfk_1` FOREIGN KEY (`tutor_id`) REFERENCES `tutors` (`tutor_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_availability_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`),
  ADD CONSTRAINT `fk_availability_subject_2` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`) ON UPDATE CASCADE,
                                                                                                                                                                                                                                        ADD CONSTRAINT `fk_availability_subjects_direct` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `bookings`
--
ALTER TABLE `bookings`
    ADD CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  ADD CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`tutor_id`) REFERENCES `tutors` (`tutor_id`),
  ADD CONSTRAINT `bookings_ibfk_3` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`);

--
-- Constraints for table `exams`
--
ALTER TABLE `exams`
    ADD CONSTRAINT `exams_ibfk_1` FOREIGN KEY (`tutor_id`) REFERENCES `tutors` (`tutor_id`),
  ADD CONSTRAINT `exams_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`);

--
-- Constraints for table `exam_results`
--
ALTER TABLE `exam_results`
    ADD CONSTRAINT `exam_results_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`exam_id`),
  ADD CONSTRAINT `exam_results_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`);

--
-- Constraints for table `learning_materials`
--
ALTER TABLE `learning_materials`
    ADD CONSTRAINT `learning_materials_ibfk_1` FOREIGN KEY (`tutor_id`) REFERENCES `tutors` (`tutor_id`);

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
    ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `online_classes`
--
ALTER TABLE `online_classes`
    ADD CONSTRAINT `online_classes_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`);

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
    ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`) ON DELETE CASCADE;

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
    ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  ADD CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`tutor_id`) REFERENCES `tutors` (`tutor_id`),
  ADD CONSTRAINT `reviews_ibfk_3` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`);

--
-- Constraints for table `students`
--
ALTER TABLE `students`
    ADD CONSTRAINT `students_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `subjects`
--
ALTER TABLE `subjects`
    ADD CONSTRAINT `subjects_ibfk_1` FOREIGN KEY (`tutor_id`) REFERENCES `tutors` (`tutor_id`) ON DELETE CASCADE;

--
-- Constraints for table `tutors`
--
ALTER TABLE `tutors`
    ADD CONSTRAINT `tutors_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `tutor_subjects`
--
ALTER TABLE `tutor_subjects`
    ADD CONSTRAINT `tutor_subjects_ibfk_1` FOREIGN KEY (`tutor_id`) REFERENCES `tutors` (`tutor_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `tutor_subjects_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;