/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot.commands.swerve;

import static edu.wpi.first.units.Units.RadiansPerSecond;

import org.littletonrobotics.junction.Logger;

import com.stuypulse.robot.Robot;

import com.stuypulse.robot.constants.DriverConstants.DriveConstraints;
import com.stuypulse.robot.constants.DriverConstants.Driver.Drive;
import com.stuypulse.robot.constants.DriverConstants.Driver.Turn;

import com.stuypulse.robot.subsystems.swerve.Swerve;
import com.stuypulse.robot.util.swerve.DriveInputProcessor;
import com.stuypulse.robot.util.swerve.DriveTurnInputProcessor;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class SwerveDriveDrive extends Command {

    private final Swerve swerve;

    private final DriveInputProcessor speed;

    private final DriveTurnInputProcessor turn;

    public SwerveDriveDrive(CommandXboxController driver) {
        swerve = Swerve.getInstance();
        this.speed = new DriveInputProcessor(
                driver,
                Drive.DEADBAND,
                Drive.POWER,
                DriveConstraints.MAX_VELOCITY,
                DriveConstraints.MAX_ACCEL,
                Drive.RC);
        turn = new DriveTurnInputProcessor(
                driver,
                Turn.DEADBAND,
                Turn.POWER,
                DriveConstraints.MAX_ANGULAR_VEL, Turn.RC);
        addRequirements(swerve);
    }

    @Override
    public void execute() {
        if (swerve.shouldIgnoreJoysticks) {
            return;
        }

        speed.update();
        turn.update();

        ChassisSpeeds speeds = new ChassisSpeeds(speed.get().getX(), speed.get().getY(), -turn.get().in(RadiansPerSecond));

        boolean isFlipped = !Robot.isBlue();

        ChassisSpeeds robotRelative = ChassisSpeeds.fromFieldRelativeSpeeds(speeds,
                        isFlipped
                                ? swerve.getRotation().plus(new Rotation2d(Math.PI))
                                : swerve.getRotation());

        Logger.recordOutput("Drive/ChassisSpeeds/FromDriveInputProcessor", robotRelative);
        swerve.runVelocity(robotRelative);
    }
}
