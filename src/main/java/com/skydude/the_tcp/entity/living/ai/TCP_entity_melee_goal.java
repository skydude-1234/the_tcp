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
    public void customattack(){
        if(Objects.equals(entity.attack_name, "attack")){
                if(Objects.equals(entity.dispatcher.lastattackname, "attack")){
                    if(entity.hasEffect(MobEffects.DAMAGE_BOOST) ){
                        int amplifier = entity.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier();
                        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30, amplifier + 1));
                        System.out.println("amplifier: " + amplifier);
                    }
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30));
                }
        }
    }
    @Override
    protected void startAttackAnimation(LivingEntity target) {
        this.attackTarget = target;
        this.attackAnimationTick = 0;
        this.attackDamageDone = false;
        this.entity.getLookControl().setLookAt(this.attackTarget, 30.0F, 30.0F);
        var health = this.entity.getHealth();

        if(health <= 5){
            setPhase(4);
        } else if(health <= 10) {
            setPhase(3);
        } else if(health <= 20) {
            setPhase(2);
        }
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

    public void setPhase(int phase) {
        this.entity.phase = phase;
        if(phase == 2){
            setAnimationTicks(11, 6);
        }
        if(phase == 3){
            setPhase(2);
            this.entity.attack_name = "critattack";
        }
        if(phase == 4){
            setPhase(3);
            this.entity.attackrange = 9;
        }
    }
    public void setAnimationTicks(int duration, int damagedelay){
        this.attackAnimationDurationTicks = duration;
        this.attackDamageTick = damagedelay;
    }

    @Override
    protected void clearAttackAnimation() {
        super.clearAttackAnimation();
   //     this.entity.setSprinting(false);
        this.entity.setCritParticleTarget(null);
    }
    @Override
    public void doAttackDamage() {
        if (this.attackTarget != null && this.canHitAttackTarget(this.attackTarget)) {
            this.entity.doHurtTarget(this.attackTarget);
            this.entity.setCritParticleTarget(this.entity.getTarget());
            this.attackDamageDone = true;
        }


        customattack();
    }
}
