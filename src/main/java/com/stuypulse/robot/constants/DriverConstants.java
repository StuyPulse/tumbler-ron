
package com.stuypulse.robot.constants;

import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.units.measure.LinearVelocity;

public interface DriverConstants {
    public interface Driver {
        int DRIVER_INDEX = 0;
        double BUZZ_TIME = 1.0;
        double BUZZ_INTENSITY = 1.0;

        public interface Drive {
            double DEADBAND = 0.05;
            double RC = 0.05;
            double POWER = 2.0;
        }

        public interface Turn {
            double DEADBAND = 0.07;
            double RC = 0.05;
            double POWER = 2.0;
        }
    }

    public interface DriveConstraints {
        LinearVelocity MAX_VELOCITY = MetersPerSecond.of(4.3);

        AngularVelocity MAX_ANGULAR_VEL = DegreesPerSecond.of(400.0);

        LinearAcceleration MAX_ACCEL = MetersPerSecondPerSecond.of(20.0);
    }
}