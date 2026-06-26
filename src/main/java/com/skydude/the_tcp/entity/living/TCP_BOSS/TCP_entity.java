package com.skydude.the_tcp.entity.living.TCP_BOSS;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class TCP_entity extends Monster  {
    private boolean hasPlayedSpawnAnimation;

    public TCP_entity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide() && !hasPlayedSpawnAnimation) {
            hasPlayedSpawnAnimation = true;
            new TCP_entityDispatcher(this).startFloat();
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
