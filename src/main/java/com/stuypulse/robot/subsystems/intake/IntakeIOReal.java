/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import com.revrobotics.PersistMode;
import com.revrobotics.REVLibError;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.stuypulse.robot.subsystems.intake.IntakeConstants.IntakeMotorConfig;
import com.stuypulse.robot.subsystems.intake.IntakeConstants.IntakeMotorIds;
import com.stuypulse.robot.subsystems.intake.IntakeConstants.IntakeSettings;
import edu.wpi.first.math.filter.Debouncer;
import java.util.function.DoubleSupplier;

public class IntakeIOReal implements IntakeIO {
    protected SparkFlex rollerMotorLeader;
    protected SparkFlex rollerMotorFollower;

    private Debouncer leaderConnectedDebouncer =
            new Debouncer(IntakeSettings.CONNECTED_DEBOUNCE.in(Seconds));
    private Debouncer followerConnectedDebouncer =
            new Debouncer(IntakeSettings.CONNECTED_DEBOUNCE.in(Seconds));

    private DoubleSupplier statorCurrent;
    private DoubleSupplier supplyCurrent;
    private DoubleSupplier temperature;
    private DoubleSupplier appliedVoltage;
    private DoubleSupplier velocity;

    public IntakeIOReal() {
        this.rollerMotorLeader = new SparkFlex(IntakeMotorIds.LEADER_MOTOR, MotorType.kBrushless);
        this.rollerMotorFollower =
                new SparkFlex(IntakeMotorIds.FOLLOWER_MOTOR, MotorType.kBrushless);

        rollerMotorLeader.configure(
                IntakeMotorConfig.leaderMotorConfig,
                ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);
        rollerMotorFollower.configure(
                IntakeMotorConfig.followerMotorConfig,
                ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);

        this.statorCurrent = () -> rollerMotorLeader.getOutputCurrent();
        this.supplyCurrent =
                () -> statorCurrent.getAsDouble() * Math.abs(rollerMotorLeader.getAppliedOutput());
        this.temperature = () -> rollerMotorLeader.getMotorTemperature();
        this.appliedVoltage =
                () -> rollerMotorLeader.getBusVoltage() * rollerMotorLeader.getAppliedOutput();
        this.velocity = () -> rollerMotorLeader.getEncoder().getVelocity();
    }

    @Override
    public void updateInputs(IntakeIOInputs inputs) {
        inputs.statorCurrent = Amps.of(statorCurrent.getAsDouble());
        inputs.supplyCurrent = Amps.of(supplyCurrent.getAsDouble());
        inputs.temperature = Celsius.of(temperature.getAsDouble());
        inputs.appliedVoltage = Volts.of(appliedVoltage.getAsDouble());
        inputs.velocity = RPM.of(velocity.getAsDouble());

        inputs.leaderConnected =
                leaderConnectedDebouncer.calculate(
                        rollerMotorLeader.getLastError() == REVLibError.kOk);
        inputs.followerConnected =
                followerConnectedDebouncer.calculate(
                        rollerMotorFollower.getLastError() == REVLibError.kOk);
    }

    @Override
    public void applyOutputs(IntakeIOOutputs outputs) {
        switch (outputs.outputMode) {
            case STOP -> {
                rollerMotorLeader.stopMotor();
            }
            case VOLTAGE -> {
                rollerMotorLeader.setVoltage(outputs.targetVoltage);
            }
        }
    }
}
