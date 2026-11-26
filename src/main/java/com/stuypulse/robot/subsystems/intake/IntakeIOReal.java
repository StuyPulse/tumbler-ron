package com.stuypulse.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import java.util.function.Supplier;

import com.revrobotics.PersistMode;
import com.revrobotics.REVLibError;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import com.stuypulse.robot.subsystems.intake.IntakeConstants.IntakeMotorConfig;
import com.stuypulse.robot.subsystems.intake.IntakeConstants.IntakeMotorIds;
import com.stuypulse.robot.subsystems.intake.IntakeConstants.IntakeSettings;

import edu.wpi.first.math.filter.Debouncer;

public class IntakeIOReal implements IntakeIO {
    protected SparkFlex rollerMotorLeader;
    protected SparkFlex rollerMotorFollower;

    private Debouncer leaderConnectedDebouncer = new Debouncer(IntakeSettings.CONNECTED_DEBOUNCE.in(Seconds));
    private Debouncer followerConnectedDebouncer = new Debouncer(IntakeSettings.CONNECTED_DEBOUNCE.in(Seconds));

    private Supplier<Double> statorCurrent;
    private Supplier<Double> supplyCurrent;
    private Supplier<Double> temperature;
    private Supplier<Double> appliedVoltage;
    private Supplier<Double> velocity;

    public IntakeIOReal() {
        this.rollerMotorLeader = new SparkFlex(IntakeMotorIds.LEADER_MOTOR, MotorType.kBrushless);
        this.rollerMotorFollower = new SparkFlex(IntakeMotorIds.FOLLOWER_MOTOR, MotorType.kBrushless);

        rollerMotorLeader.configure(IntakeMotorConfig.leaderMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        rollerMotorFollower.configure(IntakeMotorConfig.followerMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        this.statorCurrent = () -> rollerMotorLeader.getOutputCurrent();
        this.supplyCurrent = () -> statorCurrent.get() * Math.abs(rollerMotorLeader.getAppliedOutput());
        this.temperature = () -> rollerMotorLeader.getMotorTemperature();
        this.appliedVoltage = () -> rollerMotorLeader.getBusVoltage();
        this.velocity = () -> rollerMotorLeader.getEncoder().getVelocity();
    }

    @Override
    public void updateInputs(IntakeIOInputs inputs) {
        inputs.statorCurrent = Amps.of(statorCurrent.get());
        inputs.supplyCurrent = Amps.of(supplyCurrent.get());
        inputs.temperature = Celsius.of(temperature.get());
        inputs.appliedVoltage = Volts.of(appliedVoltage.get());
        inputs.velocity = RPM.of(velocity.get());

        inputs.leaderConnected = leaderConnectedDebouncer.calculate(rollerMotorLeader.getLastError() == REVLibError.kOk);
        inputs.followerConnected = followerConnectedDebouncer.calculate(rollerMotorFollower.getLastError() == REVLibError.kOk);
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
