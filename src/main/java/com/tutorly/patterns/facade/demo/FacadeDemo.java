package com.tutorly.patterns.facade.demo;

import com.tutorly.model.Tutor;
import com.tutorly.patterns.facade.LiveClassFacade;

public class FacadeDemo {

    public static void main(String[] args) {

        Tutor tutor =
                new Tutor(
                        "Demo Tutor",
                        "tutor@tutorly.com",
                        "password",
                        "01700000000"
                );

        LiveClassFacade liveClass =
                new LiveClassFacade(tutor);

        System.out.println("===== STARTING CLASS =====");

        liveClass.startClass();

        System.out.println();

        System.out.println(
                "Class running: " +
                liveClass.isClassRunning()
        );

        System.out.println();

        System.out.println("===== ENDING CLASS =====");

        liveClass.endClass();

        System.out.println();

        System.out.println(
                "Class running: " +
                liveClass.isClassRunning()
        );
    }
}
