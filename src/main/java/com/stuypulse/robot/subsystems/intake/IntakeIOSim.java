/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Joules;
import static edu.wpi.first.units.Units.KilogramSquareMeters;
import static edu.wpi.first.units.Units.Ohms;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import com.revrobotics.sim.SparkFlexSim;
import com.stuypulse.robot.constants.GlobalSettings;
import com.stuypulse.robot.subsystems.intake.IntakeConstants.IntakeSimulation;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.simulation.FlywheelSim;
import java.util.function.DoubleSupplier;
import org.littletonrobotics.junction.Logger;

public class IntakeIOSim extends IntakeIOReal {
    private SparkFlexSim rollerMotorLeader;
    private FlywheelSim leaderSim;

    private SparkFlexSim rollerMotorFollower;
    private FlywheelSim followerSim;

    private DoubleSupplier statorCurrent;
    private DoubleSupplier supplyCurrent;
    private DoubleSupplier appliedVoltage;
    private DoubleSupplier velocity;

    private double simulatedTemperatureCelsius;

    public IntakeIOSim() {
        super();
        this.rollerMotorLeader = new SparkFlexSim(super.rollerMotorLeader, DCMotor.getNeoVortex(1));
        this.leaderSim =
                new FlywheelSim(
                        LinearSystemId.createFlywheelSystem(
                                DCMotor.getNeoVortex(1),
                                IntakeSimulation.MOI.in(KilogramSquareMeters),
                                IntakeSimulation.GEAR_RATIO),
                        DCMotor.getNeoVortex(1));
        this.rollerMotorFollower =
                new SparkFlexSim(super.rollerMotorFollower, DCMotor.getNeoVortex(1));
        this.followerSim =
                new FlywheelSim(
                        LinearSystemId.createFlywheelSystem(
                                DCMotor.getNeoVortex(1),
                                IntakeSimulation.MOI.in(KilogramSquareMeters),
                                IntakeSimulation.GEAR_RATIO),
                        DCMotor.getNeoVortex(1));

        this.rollerMotorLeader.enable();
        this.rollerMotorFollower.enable();

        simulatedTemperatureCelsius = IntakeSimulation.ROOM_TEMP.in(Celsius);

        this.statorCurrent = () -> rollerMotorLeader.getMotorCurrent();
        this.supplyCurrent =
                () -> statorCurrent.getAsDouble() * Math.abs(rollerMotorLeader.getAppliedOutput());
        this.appliedVoltage =
                () -> rollerMotorLeader.getBusVoltage() * rollerMotorLeader.getAppliedOutput();
        this.velocity = () -> rollerMotorLeader.getVelocity();
    }

    private void setSimulatedTemperature() {
        double currentDrawAmps = rollerMotorLeader.getMotorCurrent();

        double heatGeneratedWatts =
                Math.pow(currentDrawAmps, 2) * IntakeSimulation.WINDING_RESISTANCE.in(Ohms);
        double heatDissipatedWatts =
                (simulatedTemperatureCelsius - IntakeSimulation.ROOM_TEMP.in(Celsius))
                        / IntakeSimulation.THERMAL_RESISTANCE.in(Celsius);

        double netPowerWatts = heatGeneratedWatts - heatDissipatedWatts;
        simulatedTemperatureCelsius +=
                (netPowerWatts / IntakeSimulation.THERMAL_CAPACITANCE.in(Joules))
                        * GlobalSettings.DT.in(Seconds);
    }

    @Override
    public void updateInputs(IntakeIOInputs inputs) {
        inputs.leaderConnected = true;
        inputs.followerConnected = true;

        double batteryVoltage = RobotController.getBatteryVoltage();
        double DT = GlobalSettings.DT.in(Seconds);

        leaderSim.setInput(rollerMotorLeader.getAppliedOutput() * batteryVoltage);
        leaderSim.update(DT);
        rollerMotorLeader.iterate(leaderSim.getAngularVelocityRPM(), batteryVoltage, DT);

        rollerMotorFollower.setAppliedOutput(rollerMotorLeader.getAppliedOutput());

        followerSim.setInput(rollerMotorFollower.getAppliedOutput() * batteryVoltage);
        followerSim.update(DT);
        rollerMotorFollower.iterate(followerSim.getAngularVelocityRPM(), batteryVoltage, DT);

        setSimulatedTemperature();

        inputs.statorCurrent = Amps.of(statorCurrent.getAsDouble());
        inputs.supplyCurrent = Amps.of(supplyCurrent.getAsDouble());
        inputs.temperature = Celsius.of(simulatedTemperatureCelsius);
        inputs.appliedVoltage = Volts.of(appliedVoltage.getAsDouble());
        inputs.velocity = RPM.of(velocity.getAsDouble());

        Logger.recordOutput(
                "Intake/FollowerVelocity",
                RPM.of(rollerMotorFollower.getVelocity()).in(RadiansPerSecond));
    }

    @Override
    public void applyOutputs(IntakeIOOutputs outputs) {
        switch (outputs.outputMode) {
            case STOP -> super.rollerMotorLeader.stopMotor();
            case VOLTAGE -> super.rollerMotorLeader.setVoltage(outputs.targetVoltage);
        }
    }
}
