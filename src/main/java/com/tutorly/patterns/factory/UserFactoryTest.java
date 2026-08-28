package com.tutorly.patterns.factory;

import com.tutorly.model.User;

public class UserFactoryTest {

    public static void main(String[] args) {

        System.out.println("===== FACTORY DESIGN PATTERN TEST =====");


        // Test 1: Create Student
        System.out.println("\n--- Creating Student ---");

        User student = UserFactory.createUser(
                "student",
                "Rahim Ahmed",
                "rahim@gmail.com",
                "password123",
                "01711111111"
        );

        System.out.println("User created successfully!");
        System.out.println("Class Type: "
                + student.getClass().getSimpleName());
        System.out.println("Name: "
                + student.getFullName());
        System.out.println("Email: "
                + student.getEmail());
        System.out.println("Role: "
                + student.getRole());


        // Test 2: Create Tutor
        System.out.println("\n--- Creating Tutor ---");

        User tutor = UserFactory.createUser(
                "tutor",
                "Karim Hasan",
                "karim@gmail.com",
                "password456",
                "01822222222"
        );

        System.out.println("User created successfully!");
        System.out.println("Class Type: "
                + tutor.getClass().getSimpleName());
        System.out.println("Name: "
                + tutor.getFullName());
        System.out.println("Email: "
                + tutor.getEmail());
        System.out.println("Role: "
                + tutor.getRole());


        // Test 3: Create Admin
        System.out.println("\n--- Creating Admin ---");

        User admin = UserFactory.createUser(
                "admin",
                "Admin User",
                "admin@tutorly.com",
                "admin123",
                "01933333333"
        );

        System.out.println("User created successfully!");
        System.out.println("Class Type: "
                + admin.getClass().getSimpleName());
        System.out.println("Name: "
                + admin.getFullName());
        System.out.println("Email: "
                + admin.getEmail());
        System.out.println("Role: "
                + admin.getRole());


        // Test 4: Invalid Role
        System.out.println("\n--- Testing Invalid Role ---");

        try {

            User invalidUser = UserFactory.createUser(
                    "guest",
                    "Test User",
                    "test@gmail.com",
                    "test123",
                    "01644444444"
            );

            System.out.println(invalidUser);

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Expected Error: "
                            + e.getMessage()
            );
        }


        // Test 5: Null Role
        System.out.println("\n--- Testing Null Role ---");

        try {

            User nullRoleUser = UserFactory.createUser(
                    null,
                    "Test User",
                    "test@gmail.com",
                    "test123",
                    "01655555555"
            );

            System.out.println(nullRoleUser);

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Expected Error: "
                            + e.getMessage()
            );
        }


        System.out.println(
                "\n===== ALL FACTORY TESTS COMPLETED ====="
        );
    }
}