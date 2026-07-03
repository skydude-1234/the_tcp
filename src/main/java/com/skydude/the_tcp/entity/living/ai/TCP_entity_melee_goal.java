package com.skydude.the_tcp.entity.living.ai;

import com.skydude.the_tcp.entity.living.TCP_BOSS.TCP_entity;
import net.minecraft.world.entity.PathfinderMob;

import java.util.Objects;

public class TCP_entity_melee_goal extends AnimatedSyncMeleeAttackGoal{
    public TCP_entity entity;
    public TCP_entity_melee_goal(PathfinderMob entity, double speedModifier, double attackreachsqr, int attackAnimationTicks, int attackDamageTick, Runnable attackAnimation) {
        super(entity, speedModifier, attackreachsqr, attackAnimationTicks, attackDamageTick, attackAnimation);
        this.entity = (TCP_entity) entity;
    }

    @Override
    public void customattack(){
        if(Objects.equals(entity.attack_name, "attack")){
            entity.attack_name = "attack2";
        }
    }
}
