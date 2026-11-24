/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot.commands.swerve;

import org.littletonrobotics.junction.Logger;

import com.stuypulse.robot.constants.DriverConstants.DriveConstraints;
import com.stuypulse.robot.constants.DriverConstants.Driver.Drive;
import com.stuypulse.robot.util.swerve.DriveInputProcessor;

import com.stuypulse.robot.Robot;

import com.stuypulse.robot.subsystems.swerve.Swerve;
import com.stuypulse.robot.subsystems.swerve.SwerveConstants.SwerveGains;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class SwerveDriveRotate extends Command {

    private final Swerve swerve;

    private Rotation2d rotation;

    private final DriveInputProcessor speed;

    private final PIDController headingController;

    public SwerveDriveRotate(CommandXboxController driver, Rotation2d rotation) {
        this.swerve = Swerve.getInstance();
        this.rotation = rotation;
        this.speed = new DriveInputProcessor(
                driver,
                Drive.DEADBAND,
                Drive.POWER,
                DriveConstraints.MAX_VELOCITY,
                DriveConstraints.MAX_ACCEL,
                Drive.RC);
        addRequirements(swerve);

        headingController = new PIDController(
                SwerveGains.akP,
                SwerveGains.akI,
                SwerveGains.akD);
        headingController.enableContinuousInput(-Math.PI, Math.PI);
    }

    @Override
    public void initialize() {
        headingController.reset();
    }

    @Override
    public void execute() {
        speed.update();

        Translation2d velocity = speed.get();

        double omega = headingController.calculate(
                swerve.getRotation().getRadians(),
                rotation.getRadians()); // rotation.get?

        ChassisSpeeds speeds = new ChassisSpeeds(velocity.getX(), velocity.getY(), omega);

        boolean isFlipped = !Robot.isBlue();
        swerve.runVelocity(
                ChassisSpeeds.fromFieldRelativeSpeeds(speeds,
                        isFlipped
                                ? swerve.getRotation().plus(new Rotation2d(Math.PI))
                                : swerve.getRotation()));

        Logger.recordOutput(
                "Swerve/Angle Minus Target Angle",
                swerve.getPose().getRotation().minus(rotation).getDegrees());
        Logger.recordOutput("Swerve/Facing Angle", swerve.getPose().getRotation().getDegrees());
    }
}
