package com.stuypulse.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.units.measure.Voltage;

public interface IntakeConstants {
    public interface IntakeSettings {
        Voltage INTAKE_VOLTAGE = Volts.of(6.0);
        Voltage OUTTAKE_VOLTAGE = Volts.of(-6.0);
    }
}
