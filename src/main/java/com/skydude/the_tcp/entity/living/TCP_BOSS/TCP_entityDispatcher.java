package com.skydude.the_tcp.entity.living.TCP_BOSS;

import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;

public class TCP_entityDispatcher {
    private static final AzCommand IDLE_COMMAND = AzCommand.create(
            "base_controller",
            "idle",
            AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK_COMMAND = AzCommand.create(
            "base_controller",
            "walking",
            AzPlayBehaviors.LOOP
    );

    private static final AzCommand RUN_COMMAND = AzCommand.create(
            "base_controller",
            "running",
            AzPlayBehaviors.LOOP
    );

    private static final AzCommand STARTFLOAT = AzCommand.create(
            "base_controller",
            "startFloat",
            AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private final TCP_entity tcpentity;

    public TCP_entityDispatcher(TCP_entity animatable) {
        this.tcpentity = animatable;
    }

    public void idle() {
        IDLE_COMMAND.sendForEntity(tcpentity);
    }

    public void walk() {
        WALK_COMMAND.sendForEntity(tcpentity);
    }

    public void run() {
        RUN_COMMAND.sendForEntity(tcpentity);
    }

    public void startFloat() {
        STARTFLOAT.sendForEntity(tcpentity);
    }
}
