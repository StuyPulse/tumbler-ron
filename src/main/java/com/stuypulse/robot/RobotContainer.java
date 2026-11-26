/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot;

import com.stuypulse.robot.commands.auton.Auton;
import com.stuypulse.robot.commands.auton.AutonFactory;
import com.stuypulse.robot.commands.swerve.SwerveDriveDrive;
import com.stuypulse.robot.constants.DriverConstants;
import com.stuypulse.robot.constants.FieldConstants;
import com.stuypulse.robot.subsystems.intake.Intake;
import com.stuypulse.robot.subsystems.swerve.Swerve;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import java.util.function.Consumer;

import org.littletonrobotics.junction.networktables.LoggedDashboardChooser;

/**
 *
 *
 * <h2>Robot Container Class</h2>
 *
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {

    // Gamepads
    public final CommandXboxController driver =
            new CommandXboxController(DriverConstants.Driver.DRIVER_INDEX);

    // Subsystem
    private final Swerve swerve = Swerve.getInstance();
    private final Intake intake = Intake.getInstance();

    // Autons
    private final LoggedDashboardChooser<Auton> autonChooser =
            new LoggedDashboardChooser<>("Auton");

    // Robot container

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        // swerve.configureAutoBuilder(); // done in swerve constructor
        configureDefaultCommands();
        configureButtonBindings();
        configureAutons();
        SmartDashboard.putData("Field", FieldConstants.FIELD_2D);

        // COMMENT OUT BEFORE MATCHES
        configureSysID();
    }

    /** ************ */
    /** DEFAULTS ** */
    /** ************ */
    private void configureDefaultCommands() {
        swerve.setDefaultCommand(new SwerveDriveDrive(driver));
        intake.setDefaultCommand(intake.stop());
    }

    /***************/
    /*** BUTTONS ***/
    /***************/

    /** This method is used to configure button bindings for controlling the robot. */
    private void configureButtonBindings() {
        driver.povUp().onTrue(Commands.runOnce(() -> swerve.resetHeading(Rotation2d.kZero)));

        driver.x().whileTrue(intake.intake());
        driver.y().whileTrue(intake.outtake());
    }

    /**************/
    /*** AUTONS ***/
    /**************/

    /** This method is used to configure the autonomous commands. */
    public void configureAutons() {
        AutonFactory factory = new AutonFactory(swerve);
    }

    public void configureSysID() {}

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return The command to run in autonomous
     */
    public Auton getAutonomousCommand() {
        return autonChooser.get();
    }

    public void onAutonChanged(Consumer<Auton> callback) {
        autonChooser.onChange(callback);
    }
}
