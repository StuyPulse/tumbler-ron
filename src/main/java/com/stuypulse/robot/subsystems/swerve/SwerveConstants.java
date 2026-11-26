/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot.subsystems.swerve;

import static edu.wpi.first.units.Units.*;

import com.pathplanner.lib.config.PIDConstants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.*;
import edu.wpi.first.units.measure.*;

public interface SwerveConstants {
    public interface SwerveGains {
        double akP = 14;
        double akI = 0.0;
        double akD = 0.0;

        PIDConstants XY = new PIDConstants(10.0, 0.0, 0.0);
        PIDConstants THETA = new PIDConstants(10.0, 0.0, 0.0);
    }

    public interface SwerveTolerances {
        Distance X_TOLERANCE = Inches.of(2.0);
        Distance Y_TOLERANCE = Inches.of(2.0);
        Rotation2d THETA_TOLERANCE = Rotation2d.fromDegrees(5);
        Pose2d POSE_TOLERANCE =
                new Pose2d(X_TOLERANCE.in(Meters), Y_TOLERANCE.in(Meters), THETA_TOLERANCE);
        LinearVelocity MAX_VELOCITY_WHEN_ALIGNED = MetersPerSecond.of(0.15);
        Time ALIGNMENT_DEBOUNCE = Seconds.of(0.15);
    }

    public interface SwerveCharacterization {
        Time FF_START_DELAY = Seconds.of(2.0);
        double FF_RAMP_RATE = 0.1;

        double WHEEL_RADIUS_MAX_VELOCITY = 0.25;
        double WHEEL_RADIUS_RAMP_RATE = 0.05;

        Time SYSID_REST = Seconds.of(1.5);

        public interface DriveSysId {
            Velocity<VoltageUnit> RAMP_RATE = Volts.of(1).per(Second);
            Voltage STEP_VOLTAGE = Volts.of(4);
            Time QUASISTATIC_TIMEOUT = Seconds.of(5);
            Time DYNAMIC_TIMEOUT = Seconds.of(2);
        }

        public interface SteerSysId {
            Velocity<VoltageUnit> RAMP_RATE = Volts.of(1).per(Second);
            Voltage STEP_VOLTAGE = Volts.of(7);
            Time QUASISTATIC_TIMEOUT = Seconds.of(6);
            Time DYNAMIC_TIMEOUT = Seconds.of(2);
        }
    }
}
