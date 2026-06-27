package com.skydude.the_tcp.entity.living.TCP_BOSS;

import mod.azure.azurelib.common.ai.pathing.AzureNavigation;
import mod.azure.azurelib.common.util.MoveAnalysis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class TCP_entity extends Monster  {
    private boolean hasPlayedSpawnAnimation;
    // This is your class where you will setup the AzCommands/Animations you wish to play
    public final TCP_entityDispatcher dispatcher;

    public final MoveAnalysis moveAnalysis;

    public TCP_entity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        // Create the instance of the class here to use later.
        this.dispatcher = new TCP_entityDispatcher(this);
        this.moveAnalysis = new MoveAnalysis(this);

    }
    protected void registerGoals() {
        super.registerGoals();
  //      this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));

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

        if (!level().isClientSide() && !hasPlayedSpawnAnimation) {
            hasPlayedSpawnAnimation = true;

            dispatcher.startFloat();

        }

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("HasPlayedSpawnAnimation", hasPlayedSpawnAnimation);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        hasPlayedSpawnAnimation = compound.getBoolean("HasPlayedSpawnAnimation");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.MOVEMENT_SPEED, .3);
    }
}
