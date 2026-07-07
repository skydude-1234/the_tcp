package com.skydude.the_tcp.entity.living.ai;

import com.skydude.the_tcp.entity.living.TCP_BOSS.TCP_entity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;

public class TCP_entity_build_path_goal extends Goal {
    private static final int STUCK_TICKS_BEFORE_BUILDING = 1;
    private static final double BUILD_SPEED = 300.0D;
    private static final double MIN_MOVEMENT_SQR = 0.0025D;
    private static final double UPWARD_BUILD_HEIGHT_DIFFERENCE = 2.5D;

    private final TCP_entity entity;
    private int stuckTicks;
    private double lastX;
    private double lastZ;
    private int blocksPerTick = 1;

    public TCP_entity_build_path_goal(TCP_entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return getTargetToReach() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        resetStuckCheck();
    }

    @Override
    public void stop() {
        stuckTicks = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = getTargetToReach();
        if (target == null || !(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (entity.phase == 4) {
            blocksPerTick = 4;
        }

        if (shouldTower(target) && placeTowerBlock(serverLevel, target)) {
            afterBuilding(target);
            return;
        }

        if (isMakingProgress()) {
            resetStuckCheck();
            return;
        }

        stuckTicks++;
        if (stuckTicks >= STUCK_TICKS_BEFORE_BUILDING && buildPath(serverLevel, target, blocksPerTick) > 0) {
            afterBuilding(target);
        }
    }

    private void afterBuilding(LivingEntity target) {
        resetStuckCheck();
        entity.getNavigation().moveTo(target, BUILD_SPEED);
    }

    private boolean isMakingProgress() {
        double movedX = entity.getX() - lastX;
        double movedZ = entity.getZ() - lastZ;
        return movedX * movedX + movedZ * movedZ > MIN_MOVEMENT_SQR;
    }

    private void resetStuckCheck() {
        stuckTicks = 0;
        lastX = entity.getX();
        lastZ = entity.getZ();
    }

    private LivingEntity getTargetToReach() {
        LivingEntity target = entity.getTarget();
        if (target == null || !target.isAlive() || entity.distanceToSqr(target) <= entity.getAttackReachSqr(target)){
            return null;
        }
        if (!(entity.level() instanceof ServerLevel serverLevel) || !EventHooks.canEntityGrief(serverLevel, entity)) {
            return null;
        }

        return target;
    }

    private int buildPath(ServerLevel serverLevel, LivingEntity target, int maxBlocks) {
        if (shouldTower(target)) {
            return placeTowerBlock(serverLevel, target) ? 1 : 0;
        }

        int placedBlocks = 0;
        for (int i = 0; i < maxBlocks; i++) {
            double forwardOffset = entity.phase == 4 ? 1.8D + i : 1.2D;
            int upwardOffset = target.getY() - entity.getY() > UPWARD_BUILD_HEIGHT_DIFFERENCE ? i : 0;

            if (placeBridgeOrStairBlock(serverLevel, target, forwardOffset, upwardOffset)) {
                placedBlocks++;
            }
        }

        return placedBlocks;
    }

    private boolean shouldTower(LivingEntity target) {
        return target.getY() - entity.getY() > UPWARD_BUILD_HEIGHT_DIFFERENCE;
    }

    private boolean placeTowerBlock(ServerLevel serverLevel, LivingEntity target) {
        if (entity.getY() >= target.getY()) {
            return false;
        }

        entity.getJumpControl().jump();

        BlockPos underFeet = BlockPos.containing(entity.getX(), entity.getBoundingBox().minY - 0.05D, entity.getZ());
        return canPlaceTowerBlock(serverLevel, underFeet) && placeWool(serverLevel, underFeet);
    }

    private boolean placeBridgeOrStairBlock(ServerLevel serverLevel, LivingEntity target, double forwardOffset, int upwardOffset) {
        double x = target.getX() - entity.getX();
        double z = target.getZ() - entity.getZ();
        double horizontalDistance = Math.sqrt(x * x + z * z);
        if (horizontalDistance <= 0.5) {
            return false;
        }

        double xStep = x / horizontalDistance;
        double zStep = z / horizontalDistance;
        BlockPos feetAhead = BlockPos.containing(
                entity.getX() + xStep * forwardOffset,
                entity.getY() + upwardOffset,
                entity.getZ() + zStep * forwardOffset
        );

        if (target.getY() - entity.getY() > UPWARD_BUILD_HEIGHT_DIFFERENCE && placeStairBlock(serverLevel, feetAhead)) {
            return true;
        }

        return placeBridgeBlock(serverLevel, feetAhead);
    }

    private boolean placeStairBlock(ServerLevel serverLevel, BlockPos feetAhead) {
        return canPlacePathBlock(serverLevel, feetAhead)
                && canPassThrough(serverLevel, feetAhead.above())
                && canPassThrough(serverLevel, feetAhead.above(2))
                && placeWool(serverLevel, feetAhead);
    }

    private boolean placeBridgeBlock(ServerLevel serverLevel, BlockPos feetAhead) {
        BlockPos supportBlock = feetAhead.below();
        return canPlacePathBlock(serverLevel, supportBlock)
                && canPassThrough(serverLevel, feetAhead)
                && canPassThrough(serverLevel, feetAhead.above())
                && placeWool(serverLevel, supportBlock);
    }

    private boolean placeWool(ServerLevel serverLevel, BlockPos pos) {
        return serverLevel.setBlockAndUpdate(pos, Blocks.WHITE_WOOL.defaultBlockState());
    }

    private boolean canPlacePathBlock(ServerLevel serverLevel, BlockPos pos) {
        return serverLevel.getBlockState(pos).canBeReplaced() && !isInsideEntity(pos);
    }

    private boolean canPassThrough(ServerLevel serverLevel, BlockPos pos) {
        return serverLevel.getBlockState(pos).canBeReplaced();
    }

    private boolean canPlaceTowerBlock(ServerLevel serverLevel, BlockPos pos) {
        return serverLevel.getBlockState(pos).canBeReplaced() && !isInsideEntity(pos);
    }

    private boolean isInsideEntity(BlockPos pos) {
        return new AABB(pos).intersects(entity.getBoundingBox());
    }
}
