package com.leclowndu93150.villager_placement.ai;

import com.google.common.collect.ImmutableMap;
import com.leclowndu93150.villager_placement.data.VillagerPlacementData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

/**
 * A behavior that makes villagers go to their assigned idle position and STAY there.
 * This behavior runs continuously during IDLE activity, blocking other idle behaviors
 * like random strolling, interacting with cats, jumping on beds, etc.
 *
 * The villager can still do important things because those run in other activities:
 * - REST: Sleeping
 * - WORK: Working at job site, restocking
 * - PANIC: Fleeing from danger
 * - RAID/HIDE: Hiding from raids
 * - MEET: Going to meeting point (bell)
 */
public class StayAtIdlePositionBehavior extends Behavior<Villager> {

    private static final float WALK_SPEED = 0.5F;
    private static final int MAX_DIST_FROM_POSITION = 48;
    private static final double ARRIVAL_THRESHOLD = 0.75;

    public StayAtIdlePositionBehavior() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED
        ), 1200);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Villager villager) {
        if (!VillagerPlacementData.hasIdlePosition(villager)) {
            return false;
        }

        BlockPos idlePos = VillagerPlacementData.getIdlePosition(villager);
        if (idlePos == null) {
            return false;
        }

        BlockPos standPos = idlePos.above();
        Vec3 standVec = Vec3.atBottomCenterOf(standPos);
        double dist = villager.position().distanceTo(standVec);

        return !(dist > MAX_DIST_FROM_POSITION);
    }

    @Override
    protected void start(ServerLevel level, Villager villager, long gameTime) {
        navigateToIdlePosition(villager);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Villager villager, long gameTime) {
        return VillagerPlacementData.hasIdlePosition(villager);
    }

    @Override
    protected void tick(ServerLevel level, Villager villager, long gameTime) {
        BlockPos idlePos = VillagerPlacementData.getIdlePosition(villager);
        if (idlePos == null) return;

        BlockPos standPos = idlePos.above();
        Vec3 standVec = Vec3.atBottomCenterOf(standPos);
        double dist = villager.position().distanceTo(standVec);

        if (dist >= ARRIVAL_THRESHOLD && !villager.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
            navigateToIdlePosition(villager);
        }

        if (dist < ARRIVAL_THRESHOLD) {
            villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
    }

    private void navigateToIdlePosition(Villager villager) {
        BlockPos idlePos = VillagerPlacementData.getIdlePosition(villager);
        if (idlePos != null) {
            BlockPos standPos = idlePos.above();
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                new WalkTarget(Vec3.atBottomCenterOf(standPos), WALK_SPEED, 0));
        }
    }
}
