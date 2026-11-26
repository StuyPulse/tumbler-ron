package com.stuypulse.robot.subsystems.intake;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Celsius;
import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Volts;

import org.littletonrobotics.junction.AutoLog;
import org.littletonrobotics.junction.AutoLogOutput;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;

public interface IntakeIO {
    @AutoLog
    public class IntakeIOInputs {
        public boolean connected = false;
        public Current supplyCurrent = Amps.zero();
        public Current statorCurrent = Amps.zero();
        public Temperature temperature = Celsius.zero();
        public Angle position = Degrees.zero();
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
