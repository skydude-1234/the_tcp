package com.skydude.the_tcp.item;

import com.skydude.the_tcp.init.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class InfamousWoolItem extends Item {
    public InfamousWoolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        BlockPos summonPos = context.getClickedPos().relative(context.getClickedFace());
        Player player = context.getPlayer();

        playBedwarsEffects(serverLevel, summonPos);

      EntityRegistry.TCP_ENTITY.get().spawn(serverLevel, entity -> {
                    if (player == null) return;
                    // face the player
                    float yaw = Mth.wrapDegrees(player.getYRot() + 180.0F);
                    entity.setYRot(yaw);
                    entity.yHeadRot = yaw;
                    entity.yBodyRot = yaw;
                },
                summonPos, MobSpawnType.MOB_SUMMONED,
                // shouldoffset parameters
                false, false
        );
        // don't consume the item if in creative mode
        if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.CONSUME;
    }

    private static void playBedwarsEffects(ServerLevel level, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.7;
        double z = pos.getZ() + 0.5;
        BlockParticleOption redWool = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.RED_WOOL.defaultBlockState());

        level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.PLAYERS, 1.2F, 0.8F);
        level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.8F, 1.25F);
        level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.9F, 1.1F);

        level.sendParticles(redWool, x, y, z, 55, 0.65, 0.35, 0.65, 0.08);
        level.sendParticles(ParticleTypes.FIREWORK, x, y + 0.5, z, 35, 0.55, 0.85, 0.55, 0.06);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y + 0.25, z, 16, 0.45, 0.45, 0.45, 0.03);
    }
}
