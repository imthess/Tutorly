package com.tutorly.patterns.proxy.demo;

import com.tutorly.model.Tutor;
import com.tutorly.model.Student;
import com.tutorly.patterns.proxy.VideoProxy;

public class ProxyDemo {

    public static void main(String[] args) {

        System.out.println("===== TUTOR PROXY =====");

        Tutor tutor =
                new Tutor(
                        "Test Tutor",
                        "tutor@test.com",
                        "password",
                        "01700000000"
                );

        VideoProxy tutorProxy =
                new VideoProxy(tutor);

        tutorProxy.startVideo();

        System.out.println(
                "Video running: " +
                tutorProxy.isRunning()
        );

        tutorProxy.stopVideo();


        System.out.println();
        System.out.println("===== STUDENT PROXY =====");

        Student student =
                new Student(
                        "Test Student",
                        "student@test.com",
                        "password",
                        "01800000000"
                );

        VideoProxy studentProxy =
                new VideoProxy(student);

        studentProxy.startVideo();

        System.out.println(
                "Video running: " +
                studentProxy.isRunning()
        );
    }
}
