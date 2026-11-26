/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot;

import com.stuypulse.robot.commands.auton.Auton;
import com.stuypulse.robot.constants.GlobalSettings;
import com.stuypulse.robot.util.PeriodicSubsystem;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import java.util.function.Consumer;
import org.littletonrobotics.junction.AutoLogOutput;
import org.littletonrobotics.junction.LogFileUtil;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGReader;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;

/**
 *
 *
 * <h2>Robot Class</h2>
 *
 * This is the main class for robot code, instantiated in {@link com.stuypulse.robot.Main} It
 * extends TimedRobot, meaning that the methods in this class are called automatically during
 * specific states of the robot.
 *
 * <p>This robot is structured using the <a href=
 * "https://docs.wpilib.org/en/stable/docs/software/commandbased/what-is-command-based.html">CommandBased</a>
 * framework.
 */
public class Robot extends LoggedRobot {
    private RobotContainer robot;
    private Auton auto = null;
    private boolean wasAutonomous = false;

    /**
     * Checks the alliance the robot is on
     *
     * @return true if the robot is on the blue alliance, false if the robot is on the red alliance
     */
    @AutoLogOutput(key = "Alliance/isBlue")
    public static boolean isBlue() {
        if (DriverStation.getAlliance().isPresent()) {
            return DriverStation.getAlliance().get() == Alliance.Blue;
        } else {
            DriverStation.reportWarning("Alliance not present", true);
            return true;
        }
    }

    /************************/
    /*** ROBOT SCHEDULING ***/
    /************************/
    @Override
    public void robotInit() {
        robot = new RobotContainer();

        switch (GlobalSettings.CURRENT_MODE) {
            case REAL -> {
                Logger.addDataReceiver(new WPILOGWriter());
                Logger.addDataReceiver(new NT4Publisher());
            }

            case SIM -> Logger.addDataReceiver(new NT4Publisher());

            case REPLAY -> {
                setUseTiming(false); // Run as fast as possible
                String logPath = LogFileUtil.findReplayLog();
                Logger.setReplaySource(new WPILOGReader(logPath));
                Logger.addDataReceiver(
                        new WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "replay")));
            }
        }

        Logger.start();
    }

    /**
     * This method runs when the robot connects to Driver Station.
     *
     * <p>It is used to update the robot's current alliance.
     */
    @Override
    public void driverStationConnected() {}

    /** This function is called every 20ms, regardless of the robot mode. */
    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
        PeriodicSubsystem.runAllPeriodicAfterScheduler();

        boolean isAutonomous = DriverStation.isAutonomous();
        if (isAutonomous && !wasAutonomous) {
            autonomousSelected();
        }

        if (!isAutonomous && wasAutonomous) {
            autonomousClosed();
        }

        wasAutonomous = isAutonomous;
    }

    /******************/
    /*** SIMULATION ***/
    /******************/

    /** This method is called when the robot first starts in simulation mode. */
    @Override
    public void simulationInit() {}

    /** This method is called every 20ms during simulation mode. */
    @Override
    public void simulationPeriodic() {}

    /*********************/
    /*** DISABLED MODE ***/
    /*********************/

    /** This method is called each time when the robot is disabled. */
    @Override
    public void disabledInit() {}

    /***********************/
    /*** AUTONOMOUS MODE ***/
    /***********************/

    private void ifAutonNotNull(Consumer<Auton> consumer) {
        if (auto != null) {
            consumer.accept(auto);
        }
    }

    private void setAuto(Auton newAuto) {
        ifAutonNotNull(Auton::clearPaths);
        auto = newAuto;
        ifAutonNotNull(Auton::displayPaths);
    }

    /** This method is called at the start of autonomous mode. */
    @Override
    public void autonomousInit() {
        if (!wasAutonomous) {
            autonomousSelected();
            wasAutonomous = true;
        }

        ifAutonNotNull(Auton::clearPaths);
        auto = robot.getAutonomousCommand();
        ifAutonNotNull(a -> CommandScheduler.getInstance().schedule(a.getCommand()));
    }

    /** This method is called every 20ms during autonomous mode. */
    @Override
    public void autonomousPeriodic() {}

    /** This method is called when autonomous mode ends. */
    @Override
    public void autonomousExit() {
        ifAutonNotNull(a -> CommandScheduler.getInstance().cancel(a.getCommand()));
    }

    public void autonomousSelected() {
        setAuto(robot.getAutonomousCommand());
        robot.onAutonChanged(this::setAuto);
    }

    public void autonomousClosed() {
        ifAutonNotNull(a -> CommandScheduler.getInstance().cancel(a.getCommand()));
        ifAutonNotNull(Auton::clearPaths);

        robot.onAutonChanged(null);
    }

    /*******************/
    /*** TELEOP MODE ***/
    /*******************/

    /** This method is called at the start of teleop mode. */
    @Override
    public void teleopInit() {}

    /** This method is called every 20ms in teleop mode. */
    @Override
    public void teleopPeriodic() {}

    /** This method is called when teleop mode ends. */
    @Override
    public void teleopExit() {}

    /*****************/
    /*** TEST MODE ***/
    /*****************/

    /** This method is called at the start of test mode. */
    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    /** This method is called every 20ms in test mode. */
    @Override
    public void testPeriodic() {}

    /** This method is called when test mode ends. */
    @Override
    public void testExit() {}
}
