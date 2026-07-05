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

        if(this.entity.getHealth() < 20) {
            this.entity.attack_name = "critattack";
            setAnimationTicks(10, 10);
        }



        this.attackAnimation.run();
    }

    public void setAnimationTicks(int duration, int damagedelay){
        this.attackAnimationDurationTicks = duration;
        this.attackDamageTick = damagedelay;
    }
}
