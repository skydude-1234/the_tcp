package com.skydude.the_tcp.entity.living.TCP_BOSS;

import com.skydude.the_tcp.entity.living.ai.TCP_entity_melee_goal;
import com.skydude.the_tcp.entity.living.ai.TCP_entity_build_path_goal;
import mod.azure.azurelib.common.ai.pathing.AzureNavigation;
import mod.azure.azurelib.common.util.MoveAnalysis;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.Objects;

public class TCP_entity extends Monster  {
    private static final EntityDataAccessor<Integer> CRIT_PARTICLE_TARGET_ID = SynchedEntityData.defineId(
            TCP_entity.class,
            EntityDataSerializers.INT
    );
    private static final Vector3f WHITE_PARTICLE = new Vector3f(1.0F, 1.0F, 1.0F);
    private static final Vector3f BLUE_PARTICLE = new Vector3f(0.31F, 0.91F, 1.0F);
    private static final Vector3f RED_PARTICLE = new Vector3f(1.0F, 0.0F, 0.0F);
    private static final double HEAD_OUTLINE_BACK_OFFSET = -0.14D;

    public static int default_attack_damage_delay_tick = 10;
    public static int default_attack_animation_length_tick = 20;
    public String attack_name = "attack";
    private boolean hasPlayedSpawnAnimation;
    public int phase = 1;
    public int attackrange = 9;
    public boolean onstart = false;
    // This is your class where you will setup the AzCommands/Animations you wish to play
    public final TCP_entityDispatcher dispatcher;

    public final MoveAnalysis moveAnalysis;
    private TCP_entity_melee_goal meleeGoal;

    public TCP_entity(EntityType<? extends TCP_entity> type, Level level) {
        super(type, level);
        // Create the instance of the class here to use later.
        this.dispatcher = new TCP_entityDispatcher(this);
        this.moveAnalysis = new MoveAnalysis(this);
        this.setPersistenceRequired();
        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_SWORD));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new TCP_entity_build_path_goal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.meleeGoal = new TCP_entity_melee_goal(
                this,
                1.2,
                getAttackReachSqr(this),
                default_attack_animation_length_tick,
                default_attack_damage_delay_tick,
                () -> {
                    dispatcher.attack(this);
                    swing(InteractionHand.MAIN_HAND);
                }){};
        this.goalSelector.addGoal(1, this.meleeGoal);
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 10.0F));
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

            if (this.level() instanceof ServerLevel serverLevel) {
                spawnPhaseParticles(serverLevel);
            }
        }


    }

    public double getAttackReachSqr(LivingEntity entity) {
        return attackrange;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt) {
            float healthPercent = this.getHealth() / this.getMaxHealth();

            if(healthPercent <= 0.4){
                setPhase(4);
            } else if(healthPercent <= 0.6) {
                setPhase(3);
            } else if(healthPercent <= 0.8) {
                setPhase(2);
            }
        }
        return hurt;
    }
    public void setPhase(int phase) {
        this.phase = phase;

        if(phase >= 2){
            this.meleeGoal.setAnimationTicks(11, 6);
        }
        if(phase >= 3){
            this.attack_name = "critattack";
        }
        if(phase >= 4){
            this.attackrange = 16;
        }
    }

    private void spawnPhaseParticles(ServerLevel serverLevel) {
        if (phase <= 1) {
            return;
        }

        Vector3f color;
        if(phase == 2){
            color = WHITE_PARTICLE;
        } else if (phase == 3){
            color = BLUE_PARTICLE;
        } else {
            color = RED_PARTICLE;
        }


        DustParticleOptions particleOptions = new DustParticleOptions(color, 0.75F);
        int pointsPerLine = 3;

        spawnOutlineSegment(serverLevel, particleOptions, -0.26D, 2.05D, 0.26D, 2.05D, HEAD_OUTLINE_BACK_OFFSET, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, -0.30D, 1.72D, -0.30D, 2.05D, HEAD_OUTLINE_BACK_OFFSET, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, 0.30D, 1.72D, 0.30D, 2.05D, HEAD_OUTLINE_BACK_OFFSET, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, -0.20D, 1.66D, 0.20D, 1.66D, HEAD_OUTLINE_BACK_OFFSET, pointsPerLine);

        spawnOutlineSegment(serverLevel, particleOptions, -0.44D, 1.48D, -0.24D, 1.52D, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, 0.24D, 1.52D, 0.44D, 1.48D, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, -0.44D, 1.48D, -0.42D, 0.88D, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, 0.44D, 1.48D, 0.42D, 0.88D, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, -0.48D, 0.78D, -0.36D, 0.78D, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, 0.36D, 0.78D, 0.48D, 0.78D, pointsPerLine);

        spawnOutlineSegment(serverLevel, particleOptions, -0.22D, 1.42D, -0.18D, 0.76D, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, 0.22D, 1.42D, 0.18D, 0.76D, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, -0.18D, 0.76D, 0.18D, 0.76D, pointsPerLine);

        spawnOutlineSegment(serverLevel, particleOptions, -0.17D, 0.72D, -0.17D, 0.18D, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, 0.17D, 0.72D, 0.17D, 0.18D, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, -0.06D, 0.70D, -0.06D, 0.24D, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, 0.06D, 0.70D, 0.06D, 0.24D, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, -0.22D, 0.14D, -0.08D, 0.14D, pointsPerLine);
        spawnOutlineSegment(serverLevel, particleOptions, 0.08D, 0.14D, 0.22D, 0.14D, pointsPerLine);
    }

    private void spawnOutlineSegment(ServerLevel serverLevel, DustParticleOptions particleOptions, double startX, double startY, double endX, double endY, int points) {
        spawnOutlineSegment(serverLevel, particleOptions, startX, startY, endX, endY, 0.0D, points);
    }

    private void spawnOutlineSegment(ServerLevel serverLevel, DustParticleOptions particleOptions, double startX, double startY, double endX, double endY, double localZ, int points) {
        for (int i = 0; i < points; i++) {
            double progress = points == 1 ? 0.0D : (double) i / (points - 1);
            double localX = startX + (endX - startX) * progress;
            double yOffset = startY + (endY - startY) * progress;
            spawnOutlineParticle(serverLevel, particleOptions, localX, yOffset, localZ);
        }
    }

    private void spawnOutlineParticle(ServerLevel serverLevel, DustParticleOptions particleOptions, double localX, double yOffset, double localZ) {
        double yaw = Math.toRadians(getYRot());
        double cos = Math.cos(yaw);
        double sin = Math.sin(yaw);
        double x = getX() + localX * cos - localZ * sin;
        double z = getZ() + localX * sin + localZ * cos;

        serverLevel.sendParticles(particleOptions, x, getY() + yOffset, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
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
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
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
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.MOVEMENT_SPEED, .2);
    }
}
