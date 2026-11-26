/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;
import org.littletonrobotics.junction.AutoLog;
import org.littletonrobotics.junction.AutoLogOutput;

public interface IntakeIO {
    @AutoLog
    public class IntakeIOInputs {
        public boolean leaderConnected = false;
        public boolean followerConnected = false;

        public Current supplyCurrent = Amps.zero();
        public Current statorCurrent = Amps.zero();
        public Temperature temperature = Celsius.zero();
        public Voltage appliedVoltage = Volts.zero();
        public AngularVelocity velocity = RPM.zero();
    }

    public enum IntakeIOOutputMode {
        STOP,
        VOLTAGE
    }

    public class IntakeIOOutputs {
        @AutoLogOutput(key = "Intake/Output Mode")
        public IntakeIOOutputMode outputMode = IntakeIOOutputMode.STOP;

        @AutoLogOutput(key = "Intake/Target Voltage")
        public Voltage targetVoltage = Volts.zero();
    }

    public default void updateInputs(IntakeIOInputs inputs) {}

    public default void applyOutputs(IntakeIOOutputs outputs) {}
}
