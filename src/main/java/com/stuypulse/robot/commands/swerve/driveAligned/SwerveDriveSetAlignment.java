/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot.commands.swerve.driveAligned;

import static edu.wpi.first.units.Units.*;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.littletonrobotics.junction.Logger;

import com.stuypulse.robot.subsystems.swerve.Swerve;

import com.stuypulse.robot.subsystems.swerve.SwerveConstants.SwerveTolerances;
import com.stuypulse.robot.subsystems.swerve.SwerveConstants.SwerveGains;


import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;

public class SwerveDriveSetAlignment extends Command {

    protected static final Swerve swerve;

    protected final BooleanSupplier isAligned;
    protected final Debouncer alignmentDebouncer;

    private final PIDController headingController;

    private Supplier<Pose2d> pose;

    protected SwerveDriveSetAlignment(Supplier<Pose2d> pose) {
        this.isAligned = () -> Math.abs(swerve.getPose().getRotation().minus(getTargetAngle())
                .getDegrees()) < SwerveTolerances.THETA_TOLERANCE.getDegrees();
        this.alignmentDebouncer = new Debouncer(SwerveTolerances.ALIGNMENT_DEBOUNCE.in(Seconds), DebounceType.kBoth);
        this.pose = pose;
        this.headingController = new PIDController(SwerveGains.akP, SwerveGains.akI, SwerveGains.akD);
        this.headingController.enableContinuousInput(-Math.PI, Math.PI);
        addRequirements(swerve);
    }

    static {
        swerve = Swerve.getInstance();
    }

    public Rotation2d getTargetAngle() {
        double atan = Math.atan2(pose.get().getY() - swerve.getPose().getY(), pose.get().getX() - swerve.getPose().getX());
        return new Rotation2d(atan);
    }

    @Override
    public boolean isFinished() {
        return alignmentDebouncer.calculate(isAligned.getAsBoolean());
    }

    @Override
    public void execute() {
        Rotation2d targetAngle = getTargetAngle();

        double omega = headingController.calculate(
                swerve.getRotation().getRadians(),
                targetAngle.getRadians()); // rotation.get?

        ChassisSpeeds speeds = new ChassisSpeeds(0, 0, omega);
        swerve.runVelocity(speeds);
        Logger.recordOutput("SwerveAlign/Target Angle", targetAngle);
        Logger.recordOutput("SwerveAlign/isAligned", isAligned);
    }
}
