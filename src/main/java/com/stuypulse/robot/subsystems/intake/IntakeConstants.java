package com.stuypulse.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Voltage;

public interface IntakeConstants {
    public interface IntakeSettings {
        Voltage INTAKE_VOLTAGE = Volts.of(6.0);
        Voltage OUTTAKE_VOLTAGE = Volts.of(-6.0);

        Time CONNECTED_DEBOUNCE = Seconds.of(0.5);

        Current CURRENT_LIMIT = Amps.of(60);
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
        int LEADER_MOTOR = 67;
        int FOLLOWER_MOTOR = 68;
    }
}
