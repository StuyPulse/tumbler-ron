package com.stuypulse.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Joules;
import static edu.wpi.first.units.Units.KilogramSquareMeters;
import static edu.wpi.first.units.Units.Ohms;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Energy;
import edu.wpi.first.units.measure.MomentOfInertia;
import edu.wpi.first.units.measure.Resistance;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Voltage;

public interface IntakeConstants {
    public interface IntakeSettings {
        Voltage INTAKE_VOLTAGE = Volts.of(6.0);
        Voltage OUTTAKE_VOLTAGE = Volts.of(-6.0);

        Time CONNECTED_DEBOUNCE = Seconds.of(0.5);

        Current CURRENT_LIMIT = Amps.of(60);
    }

    public interface IntakeSimulation {
        double GEAR_RATIO = 1.0;
        MomentOfInertia MOI = KilogramSquareMeters.of(0.002);

        Temperature ROOM_TEMP = Celsius.of(25.0);
        Resistance WINDING_RESISTANCE = Ohms.of(DCMotor.getNeoVortex(1).rOhms);
        Energy THERMAL_CAPACITANCE = Joules.of(180.0); // Joules per degree celsius
        Temperature THERMAL_RESISTANCE = Celsius.of(1.8); // Degree celsius per watt
    }

    public interface IntakeMotorConfig {
        SparkBaseConfig leaderMotorConfig = new SparkFlexConfig()
            .idleMode(IdleMode.kCoast)
            .smartCurrentLimit((int) IntakeSettings.CURRENT_LIMIT.in(Amps));

        SparkBaseConfig followerMotorConfig = new SparkFlexConfig()
            .apply(IntakeMotorConfig.leaderMotorConfig)
            .follow(IntakeMotorIds.LEADER_MOTOR);
    }

    public interface IntakeMotorIds {
        // TODO: get actual
        int LEADER_MOTOR = 20;
        int FOLLOWER_MOTOR = 21;
    }
}
