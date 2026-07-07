package com.skydude.the_tcp.entity.living.ai;

import com.skydude.the_tcp.entity.living.TCP_BOSS.TCP_entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

import java.util.Objects;

public class TCP_entity_melee_goal extends AnimatedSyncMeleeAttackGoal{
    public TCP_entity entity;
    public TCP_entity_melee_goal(PathfinderMob entity, double speedModifier, double attackreachsqr, int attackAnimationTicks, int attackDamageTick, Runnable attackAnimation) {
        super(entity, speedModifier, attackreachsqr, attackAnimationTicks, attackDamageTick, attackAnimation);
        this.entity = (TCP_entity) entity;
    }

    @Override
    public boolean canHitAttackTarget(LivingEntity target) {
        return target.isAlive()
                && entity.hasLineOfSight(target)
                && entity.distanceToSqr(target) <= entity.getAttackReachSqr(target);
    }

    @Override
    protected void startAttackAnimation(LivingEntity target) {
        this.attackTarget = target;
        this.attackAnimationTick = 0;
        this.attackDamageDone = false;
        lookAtTarget(target);
        if(Objects.equals(this.entity.attack_name, "critattack") && this.entity.onGround()){
            this.entity.setSprinting(true);
            this.entity.getJumpControl().jump();

            double x = target.getX() - this.entity.getX();
            double z = target.getZ() - this.entity.getZ();
            double distance = Math.sqrt(x * x + z * z);
            if (distance > 1.0E-5D) {
                this.entity.push(x / distance * 0.6D, 0.35D, z / distance * 0.6D);
            }
        }


        this.attackAnimation.run();
    }

    public void setAnimationTicks(int duration, int damagedelay){
        this.attackAnimationDurationTicks = duration;
        this.attackDamageTick = damagedelay;
    }

    @Override
    protected void clearAttackAnimation() {
        super.clearAttackAnimation();
        this.entity.setCritParticleTarget(null);
    }
    @Override
    public void doAttackDamage() {
        if (this.attackTarget != null && this.canHitAttackTarget(this.attackTarget)) {
            this.entity.doHurtTarget(this.attackTarget);
            this.entity.setCritParticleTarget(this.entity.getTarget());
            this.attackDamageDone = true;
        }
    }
}
