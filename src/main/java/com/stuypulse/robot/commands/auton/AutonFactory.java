/************************ PROJECT ZIXI ************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved.*/
/* This work is licensed under the terms of the MIT license.  */
/**************************************************************/
package com.stuypulse.robot.commands.auton;

import com.stuypulse.robot.subsystems.swerve.Swerve;

/** Class that stores all auton commands */
public class AutonFactory {
    /** Add robot subsystems into this constructor for easy access when making autons */
    private final Swerve swerve;
    
    public AutonFactory(Swerve swerve) {
        this.swerve = swerve;
    }

    public Auton doNothingAuton() {
        return new Auton() {};
    }
}
