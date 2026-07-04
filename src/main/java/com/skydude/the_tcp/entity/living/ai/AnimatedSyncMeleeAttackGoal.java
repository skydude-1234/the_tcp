package com.skydude.the_tcp.entity.living.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Objects;

public class AnimatedSyncMeleeAttackGoal extends Goal {
    protected final PathfinderMob entity;
    public final double speedModifier;

    public  int attackAnimationDurationTicks;
    public  int attackDamageTick;

    public final Runnable attackAnimation;
    public LivingEntity attackTarget;
    public int attackAnimationTick;
    public boolean attackDamageDone;
    public final double attackreachsqr;


    public AnimatedSyncMeleeAttackGoal(
            PathfinderMob entity,
            double speedModifier,
            double attackreachsqr,
            int attackAnimationDurationTicks,
            int attackDamageTick,
            Runnable attackAnimation
    ) {
        if (attackAnimationDurationTicks <= 0) {
            throw new IllegalArgumentException("Attack animation length must be greater than 0 ticks");
        }
        if (attackDamageTick < 0 || attackDamageTick > attackAnimationDurationTicks) {
            throw new IllegalArgumentException("Attack damage tick must be between 0 and the animation length");
        }

        this.entity = entity;
        this.speedModifier = speedModifier;
        this.attackreachsqr = attackreachsqr;
        this.attackAnimationDurationTicks = attackAnimationDurationTicks;
        this.attackDamageTick = attackDamageTick;
        this.attackAnimation = Objects.requireNonNull(attackAnimation);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.entity.getTarget();
        return this.isAttackAnimationPlaying() || target != null && target.isAlive();
    }

    @Override
    public void stop() {
      //  this.entity.getNavigation().stop();
  //      this.clearAttackAnimation();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public  boolean canHitAttackTarget(LivingEntity target) {
        return target.isAlive()
                && entity.hasLineOfSight(target)
                && entity.distanceToSqr(target) <= attackreachsqr;
    }
    @Override
    public void tick() {
        if (this.isAttackAnimationPlaying()) {
            this.tickAttackAnimation();
            return;
        }

        LivingEntity target = this.entity.getTarget();
        if (target == null) {
            return;
        }

        this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.entity.getNavigation().moveTo(target, this.speedModifier);

        if (!canHitAttackTarget(target)) {
            this.entity.getNavigation().moveTo(target, this.speedModifier);
            return;
        }

        this.startAttackAnimation(target);
    }

    public boolean isAttackAnimationPlaying() {
        return this.attackTarget != null;
    }

    protected void startAttackAnimation(LivingEntity target) {
        this.attackTarget = target;
        this.attackAnimationTick = 0;
        this.attackDamageDone = false;
        this.attackAnimation.run();
    }

    protected void tickAttackAnimation() {
        if (this.attackTarget != null && this.attackTarget.isAlive()) {
            this.entity.getLookControl().setLookAt(this.attackTarget, 30.0F, 30.0F);
            this.entity.getNavigation().moveTo(this.attackTarget, this.speedModifier);
        }

        this.attackAnimationTick++;

        if (!this.attackDamageDone && this.attackAnimationTick >= this.attackDamageTick) {
            this.doAttackDamage();
        }

        if (this.attackAnimationTick >= this.attackAnimationDurationTicks) {
            this.clearAttackAnimation();
        }
    }

    protected void doAttackDamage() {
        if (this.attackTarget != null && this.canHitAttackTarget(this.attackTarget)) {
            this.entity.doHurtTarget(this.attackTarget);
        }

        this.attackDamageDone = true;
        customattack();
    }

    protected void clearAttackAnimation() {
        this.attackTarget = null;
        this.attackAnimationTick = 0;
        this.attackDamageDone = false;
    }

    public void customattack(){

    }
}
