/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Joules;
import static edu.wpi.first.units.Units.KilogramSquareMeters;
import static edu.wpi.first.units.Units.Ohms;
import static edu.wpi.first.units.Units.Seconds;

import com.revrobotics.sim.SparkFlexSim;
import com.stuypulse.robot.constants.GlobalSettings;
import com.stuypulse.robot.subsystems.intake.IntakeConstants.IntakeSimulation;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.simulation.FlywheelSim;

public class IntakeIOSim extends IntakeIOReal {
    private SparkFlexSim rollerMotorLeader;
    private FlywheelSim leaderSim;

    private SparkFlexSim rollerMotorFollower;
    private FlywheelSim followerSim;

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

        super.leaderConnected = () -> true;
        super.followerConnected = () -> true;
        super.statorCurrent = () -> rollerMotorLeader.getMotorCurrent();
        super.supplyCurrent =
                () -> statorCurrent.getAsDouble() * Math.abs(rollerMotorLeader.getAppliedOutput());
        super.temperature = () -> simulatedTemperatureCelsius;
        super.appliedVoltage =
                () -> rollerMotorLeader.getBusVoltage() * rollerMotorLeader.getAppliedOutput();
        super.velocity = () -> rollerMotorLeader.getVelocity();
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

        super.updateInputs(inputs);
    }

    @Override
    public void applyOutputs(IntakeIOOutputs outputs) {
        switch (outputs.outputMode) {
            case STOP -> super.rollerMotorLeader.stopMotor();
            case VOLTAGE -> super.rollerMotorLeader.setVoltage(outputs.targetVoltage);
        }
    }
}
