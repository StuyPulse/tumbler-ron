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
import edu.wpi.first.units.measure.MutAngularVelocity;
import edu.wpi.first.units.measure.MutCurrent;
import edu.wpi.first.units.measure.MutTemperature;
import edu.wpi.first.units.measure.MutVoltage;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class IntakeIOReal implements IntakeIO {
    protected SparkFlex rollerMotorLeader;
    protected SparkFlex rollerMotorFollower;

    private final Debouncer leaderConnectedDebouncer;
    private final Debouncer followerConnectedDebouncer;

    private final MutCurrent mutStatorCurrent = Amps.mutable(0);
    private final MutCurrent mutSupplyCurrent = Amps.mutable(0);
    private final MutTemperature mutTemperature = Celsius.mutable(0);
    private final MutVoltage mutAppliedVoltage = Volts.mutable(0);
    private final MutAngularVelocity mutVelocity = RPM.mutable(0);

    protected BooleanSupplier leaderConnected;
    protected BooleanSupplier followerConnected;
    protected DoubleSupplier statorCurrent;
    protected DoubleSupplier supplyCurrent;
    protected DoubleSupplier temperature;
    protected DoubleSupplier appliedVoltage;
    protected DoubleSupplier velocity;

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

        this.leaderConnectedDebouncer = new Debouncer(IntakeSettings.CONNECTED_DEBOUNCE.in(Seconds));
        this.followerConnectedDebouncer = new Debouncer(IntakeSettings.CONNECTED_DEBOUNCE.in(Seconds));

        this.leaderConnected = () -> leaderConnectedDebouncer.calculate(rollerMotorLeader.getLastError() == REVLibError.kOk);
        this.followerConnected = () -> followerConnectedDebouncer.calculate(rollerMotorFollower.getLastError() == REVLibError.kOk);
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
        mutStatorCurrent.mut_setMagnitude(statorCurrent.getAsDouble());
        mutSupplyCurrent.mut_setMagnitude(supplyCurrent.getAsDouble());
        mutTemperature.mut_setMagnitude(temperature.getAsDouble());
        mutAppliedVoltage.mut_setMagnitude(appliedVoltage.getAsDouble());
        mutVelocity.mut_setMagnitude(velocity.getAsDouble());

        inputs.statorCurrent = mutStatorCurrent;
        inputs.supplyCurrent = mutSupplyCurrent;
        inputs.temperature = mutTemperature;
        inputs.appliedVoltage = mutAppliedVoltage;
        inputs.velocity = mutVelocity;

        inputs.leaderConnected = leaderConnected.getAsBoolean();
        inputs.followerConnected = followerConnected.getAsBoolean();
    }

    @Override
    public void applyOutputs(IntakeIOOutputs outputs) {
        switch (outputs.outputMode) {
            case STOP -> rollerMotorLeader.stopMotor();
            case VOLTAGE -> rollerMotorLeader.setVoltage(outputs.targetVoltage);
        }
    }
}
