/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot.util;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.ArrayList;
import java.util.List;

/**
 * A standard subsystem that includes an extra periodic callback which runs after the command
 * scheduler. Allows outputs to be published after all other periodic code has finished.
 */
public abstract class PeriodicSubsystem extends SubsystemBase {
    private static List<PeriodicSubsystem> instances = new ArrayList<>();

    public PeriodicSubsystem() {
        super();
        instances.add(this);
    }

    public PeriodicSubsystem(String name) {
        super(name);
        instances.add(this);
    }

    /**
     * This method is called periodically after the command scheduler, and should be overriden for
     * applying outputs.
     */
    public void periodicAfterScheduler() {}

    /** Run the "after periodic" methods for all subsystems. */
    public static void runAllPeriodicAfterScheduler() {
        for (PeriodicSubsystem instance : instances) {
            instance.periodicAfterScheduler();
        }
    }
}
