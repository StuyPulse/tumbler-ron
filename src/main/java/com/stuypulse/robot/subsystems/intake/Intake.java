package com.stuypulse.robot.subsystems.intake;

import org.littletonrobotics.junction.AutoLogOutput;
import org.littletonrobotics.junction.Logger;

import com.stuypulse.robot.constants.GlobalSettings;
import com.stuypulse.robot.subsystems.intake.IntakeConstants.IntakeSettings;
import com.stuypulse.robot.subsystems.intake.IntakeIO.IntakeIOOutputMode;
import com.stuypulse.robot.subsystems.intake.IntakeIO.IntakeIOOutputs;
import com.stuypulse.robot.util.PeriodicSubsystem;

import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj2.command.Command;

public class Intake extends PeriodicSubsystem {
    private static final Intake instance;

    static {
        switch (GlobalSettings.CURRENT_MODE) {
            case REAL -> instance = new Intake(new IntakeIOReal());
            case SIM -> instance = new Intake(new IntakeIOSim());
            default -> instance = new Intake(new IntakeIO() {
            });
        }
    }

    public static Intake getInstance() {
        return instance;
    }

    private IntakeIO io;
    private IntakeIOInputsAutoLogged inputs;
    private IntakeIOOutputs outputs;
    @AutoLogOutput(key = "Intake/State")
    private IntakeState state;

    public enum IntakeState {
        STOP,
        INTAKE,
        OUTTAKE
    }

    public Intake(IntakeIO io) {
        this.io = io;
        this.inputs = new IntakeIOInputsAutoLogged();
        this.outputs = new IntakeIOOutputs();
        this.state = IntakeState.STOP;
    }

    private void setState(IntakeState state) {
        this.state = state;
    }

    public IntakeState getState() {
        return state;
    }

    private void runIntakeStop() {
        outputs.outputMode = IntakeIOOutputMode.STOP;
    }

    private void runIntakeVoltage(Voltage voltage) {
        outputs.outputMode = IntakeIOOutputMode.VOLTAGE;
        outputs.targetVoltage = voltage;
    }

    @Override
    public void periodic() {
        io.updateInputs(inputs);
        Logger.processInputs("Intake", inputs);

        if (!GlobalSettings.EnabledSubsystems.INTAKE.get()) {
            runIntakeStop();
            return;
        }

        switch (state) {
            case STOP -> runIntakeStop();
            case INTAKE -> runIntakeVoltage(IntakeSettings.INTAKE_VOLTAGE);
            case OUTTAKE -> runIntakeVoltage(IntakeSettings.OUTTAKE_VOLTAGE);
        }
    }

    @Override
    public void periodicAfterScheduler() {
        io.applyOutputs(outputs);
    }

    public Command stop() {
        return run(() -> setState(IntakeState.STOP));
    }

    public Command intake() {
        return run(() -> setState(IntakeState.INTAKE));
    }

    public Command outtake() {
        return run(() -> setState(IntakeState.OUTTAKE));
    }
}
