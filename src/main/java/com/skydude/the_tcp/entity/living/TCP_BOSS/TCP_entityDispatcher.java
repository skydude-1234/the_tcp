package com.skydude.the_tcp.entity.living.TCP_BOSS;

import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;

public class TCP_entityDispatcher {
    public String lastattackname = "attack";
    private static final AzCommand IDLE_COMMAND = AzCommand.create(
            "base_controller",
            "idle",
            AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK_COMMAND = AzCommand.create(
            "base_controller",
            "walk",
            AzPlayBehaviors.LOOP
    );
    private static final AzCommand ATTACK_COMMAND = AzCommand.create(
            "attack_controller",
            "attack",
            AzPlayBehaviors.PLAY_ONCE
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
    public void attack(TCP_entity entity) {
        String attackname = entity.attack_name;
        ATTACK_COMMAND.sendForEntity(tcpentity);
        lastattackname = "attack";
    }

    public void run() {
        RUN_COMMAND.sendForEntity(tcpentity);
    }

    public void startFloat() {
        STARTFLOAT.sendForEntity(tcpentity);
    }
}
