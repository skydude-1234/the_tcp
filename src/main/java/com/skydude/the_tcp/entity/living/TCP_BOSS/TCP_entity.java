package com.skydude.the_tcp.entity.living.TCP_BOSS;

import com.skydude.the_tcp.entity.living.ai.TCP_entity_melee_goal;
import mod.azure.azurelib.common.ai.pathing.AzureNavigation;
import mod.azure.azurelib.common.util.MoveAnalysis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class TCP_entity extends Monster  {
    private static final EntityDataAccessor<Integer> CRIT_PARTICLE_TARGET_ID = SynchedEntityData.defineId(
            TCP_entity.class,
            EntityDataSerializers.INT
    );

    public static int attack_damage_delay_tick = 10;
    public static int attack_animation_length_tick = 20;
    public String attack_name = "attack";
    private boolean hasPlayedSpawnAnimation;
    public int phase = 1;
    public int attackrange = 4;
    public boolean onstart = false;
    // This is your class where you will setup the AzCommands/Animations you wish to play
    public final TCP_entityDispatcher dispatcher;

    public final MoveAnalysis moveAnalysis;
    public TCP_entity(EntityType<? extends TCP_entity> type, Level level) {
        super(type, level);
        // Create the instance of the class here to use later.
        this.dispatcher = new TCP_entityDispatcher(this);
        this.moveAnalysis = new MoveAnalysis(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(1, new TCP_entity_melee_goal(
                this,
                1.2,
                getAttackReachSqr(this),
                attack_animation_length_tick,
                attack_damage_delay_tick,
                () -> {
                    dispatcher.attack(this);
                    swing(InteractionHand.MAIN_HAND);
                }){}
        );

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, true));
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this));

        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D) {

        });

    }
    @Override
    protected PathNavigation createNavigation(Level pLevel) {return new AzureNavigation(this, pLevel);
    }


    @Override
    public void tick() {
        super.tick();
        moveAnalysis.update();
        if(!onstart){
            onstart = true;

        }
        if (!this.level().isClientSide) {
            if (!moveAnalysis.isMoving()){
                // If the entity is standing still on the ground, play the idle loop
                this.dispatcher.idle();
            } else {
                dispatcher.walk();
            }
        }


    }

    public double getAttackReachSqr(LivingEntity entity) {
        return attackrange;
    }
    // horrendous override
    @Override
    public boolean doHurtTarget(Entity p_21372_) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if(Objects.equals(this.attack_name, "critattack")){
            f = (float) (f * 1.5);
        }
        DamageSource damagesource = this.damageSources().mobAttack(this);
        if (this.level() instanceof ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), p_21372_, damagesource, f);
        }

        boolean flag = p_21372_.hurt(damagesource, f);
        if (flag) {
            float f1 = this.getKnockback(p_21372_, damagesource);
            if (f1 > 0.0F && p_21372_ instanceof LivingEntity livingentity) {
                livingentity.knockback(
                        (double)(f1 * 0.5F),
                        (double) Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                        (double)(-Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)))
                );
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (this.level() instanceof ServerLevel serverlevel1) {
                EnchantmentHelper.doPostAttackEffects(serverlevel1, p_21372_, damagesource);
            }

            this.setLastHurtMob(p_21372_);
            this.playAttackSound();
        }

        return flag;
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CRIT_PARTICLE_TARGET_ID, 0);
    }

    public void setCritParticleTarget(LivingEntity target) {
        this.entityData.set(CRIT_PARTICLE_TARGET_ID, target == null ? 0 : target.getId());
    }

    public int getCritParticleTargetId() {
        return this.entityData.get(CRIT_PARTICLE_TARGET_ID);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("HasPlayedSpawnAnimation", hasPlayedSpawnAnimation);
        compound.putInt("phase", phase);
        compound.putInt("attackrange", attackrange);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        hasPlayedSpawnAnimation = compound.getBoolean("HasPlayedSpawnAnimation");
        phase = compound.getInt("phase");
        if (compound.contains("attackrange")) {
            attackrange = compound.getInt("attackrange");
        }
    }


    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.MOVEMENT_SPEED, .2);
    }
}
