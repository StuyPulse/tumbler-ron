/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot;

import com.stuypulse.robot.commands.swerve.SwerveDriveDrive;
// import com.stuypulse.robot.commands.auton.defensive.LBDisrupt;
// import com.stuypulse.robot.commands.auton.defensive.LBFerry;
// import com.stuypulse.robot.commands.auton.defensive.RBDisrupt;
// import com.stuypulse.robot.commands.auton.defensive.RBFerry;
// import com.stuypulse.robot.commands.auton.shooting.FrontHubShootPreloads;
// import com.stuypulse.robot.commands.auton.shooting.LBDumpy;
// import com.stuypulse.robot.commands.auton.shooting.RBDumpy;
// import com.stuypulse.robot.commands.auton.structures.CenterDepot;
import com.stuypulse.robot.commands.swerve.SwerveDriveResetRotation;
import com.stuypulse.robot.commands.swerve.SwerveDriveXMode;
import com.stuypulse.robot.commands.swerve.SwerveIgnoreJoysticks;
import com.stuypulse.robot.commands.swerve.SwerveResetPose;
import com.stuypulse.robot.commands.swerve.SwerveUseJoysticks;
import com.stuypulse.robot.commands.swerve.driveAligned.SwerveDriveAlignToFerryZone;
import com.stuypulse.robot.commands.swerve.driveAligned.SwerveDriveAlignToHub;
import com.stuypulse.robot.constants.Field;
import com.stuypulse.robot.constants.Ports;
import com.stuypulse.robot.subsystems.swerve.Swerve;
import com.stuypulse.robot.subsystems.swerve.SwerveConstants;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

/**
 * <h2>Robot Container Class</h2>
 * 
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {

    // Gamepads
    public final CommandXboxController driver = new CommandXboxController(Ports.Gamepad.DRIVER);

    // Subsystem
    private final Swerve swerve = Swerve.getInstance();

    // Autons

    // Robot container

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        // swerve.configureAutoBuilder(); // done in swerve constructor
        configureDefaultCommands();
        configureButtonBindings();
        configureAutons();
        SmartDashboard.putData("Field", Field.FIELD2D);

        // COMMENT OUT BEFORE MATCHES
        configureSysID();
    }

    /** ************ */
    /** DEFAULTS ** */
    /** ************ */
    private void configureDefaultCommands() {
        swerve.setDefaultCommand(new SwerveDriveDrive(driver));
    }

    /***************/
    /*** BUTTONS ***/
    /***************/

    /**
     * This method is used to configure button bindings for controlling the robot.
   */
    private void configureButtonBindings() {
      
    }

    /**************/
    /*** AUTONS ***/
    /**************/

    /**
     * This method is used to configure the autonomous commands.
     */
     public void configureAutons() {
        // SHOOT

        // AutonConfig centerDepot = new AutonConfig("Center Depot", CenterDepot::new,
        //     "Hub to Depot",
        //     "Tower shoot");
        // centerDepot.register(autonChooser);

        // AutonConfig frontHubShootPreloads = new AutonConfig("Front Hub Shoot Preloads", FrontHubShootPreloads::new,
        //     "Front Hub Shoot Preloads"
        // );
        // frontHubShootPreloads.register(autonChooser);

        // AutonConfig LB_Dumpy = new AutonConfig("LB Dumpy", LBDumpy::new,
        //     "LB to N Dumpy",
        //     "LB Intake Dumpy",
        //     "LB Backsweep Dumpy",
        //     "LB Shoot Dumpy",
        //     "LB Shoot to Depot");
        // LB_Dumpy.register(autonChooser);

        // AutonConfig RB_Dumpy = new AutonConfig("RB Dumpy", RBDumpy::new, 
        //     "RB to N Dumpy",
        //     "RB Intake Dumpy",
        //     "RB Backsweep Dumpy",
        //     "RB Shoot Dumpy"
        // );
        // RB_Dumpy.register(autonChooser);

        // // FERRY
        // AutonConfig LB_Ferry = new AutonConfig("LB Disrupt", LBFerry::new, 
        //     "LB to N Ferry",
        //     "N to LT Ferry",
        //     "LT Hub Ferry",
        //     "N to Depot Ferry"
        // );
        // LB_Ferry.register(autonChooser);

        // AutonConfig RB_Ferry = new AutonConfig("LB Disrupt", RBFerry::new, 
        //     "RB to N Ferry",
        //     "N to RT Ferry",
        //     "RT Hub Ferry",
        //     "N to Outpost Ferry"
        // );
        // RB_Ferry.register(autonChooser);

        // // DISRUPT
        // AutonConfig LB_Disrupt = new AutonConfig("LB Disrupt", LBDisrupt::new, 
        //     "LB to CN Disrupt",
        //     "LN Disrupt Circle",
        //     "LN Disrupt Circle",
        //     "LB Disrupt Return"
        // );
        // LB_Disrupt.register(autonChooser);

        // AutonConfig RB_Disrupt = new AutonConfig("RB Disrupt", RBDisrupt::new, 
        //     "RB to CN Disrupt",
        //     "RN Circle Disrupt",
        //     "RN Circle Disrupt",
        //     "RB Disrupt Return"
        // );
        // RB_Disrupt.register(autonChooser);

        // BLineConfig LB_DUMPY_BLINE = new BLineConfig("LB Dumpy Bline", LBDumpyBLine::new,
        // "dumpy-lb-to-nz",
        //     "dumpy-lb-nz-intake",
        //     "dumpy-lb-backsweep",
        //     "dumpy-lb-shoot"
        // );
        // LB_DUMPY_BLINE.register(autonChooser);

        // BLineConfig RB_DUMPY_BLINE = new BLineConfig("RB Dumpy Bline", RBDumpyBLine::new,
        //     "dumpy-rb-to-nz",
        //     "dumpy-rb-nz-intake",
        //     "dumpy-rb-backsweep",
        //     "dumpy-rb-shoot"
        // );
        // RB_DUMPY_BLINE.register(autonChooser);

        // BLineConfig FRONT_HUB_SHOOT_BLINE = new BLineConfig("Front Hub Shoot Bline", FrontHubShootBLine::new,
        //     "front-hub-shoot"
        // );
        // FRONT_HUB_SHOOT_BLINE.register(autonChooser);

        // BLineConfig CENTER_DEPOT_BLINE = new BLineConfig("Center Depot Bline", CenterDepotBLine::new,
        //     "hub-to-depot",
        //     "tower-shoot"
        // );
        // CENTER_DEPOT_BLINE.register(autonChooser);

        // BLineConfig OUTPOST_ONLY_BLINE = new BLineConfig("Outpost Only Bline", OutpostOnlyBLine::new,
        //     "outpost"
        // );
        // OUTPOST_ONLY_BLINE.register(autonChooser);

        // BLineConfig TWO_METER_TEST_BLINE = new BLineConfig("Two Meter Test Bline", TwoMeterTestBLine::new,
        //     "two-meter"
        // );
        // TWO_METER_TEST_BLINE.register(autonChooser);

        

        // // autonChooser.addOption("SysID Module Translation Dynamic Forwards",
        // // swerve.sysIdDynamic(Direction.kForward));
        // // autonChooser.addOption("SysID Module Translation Dynamic Backwards",
        // // swerve.sysIdDynamic(Direction.kReverse));
        // // autonChooser.addOption("SysID Module Translation Quasi Forwards",
        // // swerve.sysIdQuasistatic(Direction.kForward));
        // // autonChooser.addOption("SysID Module Translation Quasi Backwards",
        // // swerve.sysIdQuasistatic(Direction.kReverse));
        // // autonChooser.addOption("SysID Rotation Translation Dynamic Forwards",
        // // swerve.sysidRotationDynamic(Direction.kForward));
        // // autonChooser.addOption("SysID Rotation Translation Dynamic Backwards",
        // // swerve.sysidRotationDynamic(Direction.kReverse));
        // // autonChooser.addOption("SysID Rotation Translation Quasi Forwards",
        // // swerve.sysidRotationQuasiStatic(Direction.kForward));
        // // autonChooser.addOption("SysID Rotation Translation Quasi Backwards",
        // // swerve.sysidRotationQuasiStatic(Direction.kReverse));
        // SmartDashboard.putData("Autonomous", autonChooser);
    }
    
    public void configureSysID() {
        // autonChooser.addOption("SysID Module Translation Dynamic Forwards",
        // swerve.sysIdDynamic(Direction.kForward));
        // autonChooser.addOption("SysID Module Translation Dynamic Backwards",
        // swerve.sysIdDynamic(Direction.kReverse));
        // autonChooser.addOption("SysID Module Translation Quasi Forwards",
        // swerve.sysIdQuasistatic(Direction.kForward));
        // autonChooser.addOption("SysID Module Translation Quasi Backwards",
        // swerve.sysIdQuasistatic(Direction.kReverse));

        // autonChooser.addOption("Swerve FF Characterization", swerve.feedforwardCharacterization());
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     * @return The command to run in autonomous
     */
    // public BLineAuton getAutonomousCommand() {
        // return autonChooser.getSelected();
    // }
}