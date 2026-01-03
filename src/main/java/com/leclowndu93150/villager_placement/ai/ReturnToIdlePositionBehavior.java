package com.leclowndu93150.villager_placement.ai;

import com.google.common.collect.ImmutableMap;
import com.leclowndu93150.villager_placement.Config;
import com.leclowndu93150.villager_placement.data.VillagerPlacementData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class ReturnToIdlePositionBehavior extends Behavior<Villager> {

    private static final double JOB_SITE_DISTANCE = 1.73;
    private static final long WORK_COOLDOWN = 20;

    public ReturnToIdlePositionBehavior() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.JOB_SITE, MemoryStatus.REGISTERED,
                MemoryModuleType.LAST_WORKED_AT_POI, MemoryStatus.REGISTERED
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

        Optional<GlobalPos> jobSite = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isPresent()) {
            if (villager.shouldRestock()) {
                return false;
            }

            Optional<Long> lastWorked = villager.getBrain().getMemory(MemoryModuleType.LAST_WORKED_AT_POI);
            if (lastWorked.isEmpty()) {
                return false;
            }

            long timeSinceWork = level.getGameTime() - lastWorked.get();
            if (timeSinceWork < WORK_COOLDOWN) {
                return false;
            }
        }

        BlockPos standPos = idlePos.above();
        Vec3 standVec = Vec3.atBottomCenterOf(standPos);
        double dist = villager.position().distanceTo(standVec);

        return dist <= Config.maxDistance;
    }

    @Override
    protected void start(ServerLevel level, Villager villager, long gameTime) {
        navigateToIdlePosition(villager);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Villager villager, long gameTime) {
        if (!VillagerPlacementData.hasIdlePosition(villager)) {
            return false;
        }

        Optional<GlobalPos> jobSite = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isPresent()) {
            if (villager.shouldRestock()) {
                return false;
            }

            BlockPos jobPos = jobSite.get().pos();
            double distToJob = villager.position().distanceTo(Vec3.atCenterOf(jobPos));

            if (distToJob < JOB_SITE_DISTANCE) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected void tick(ServerLevel level, Villager villager, long gameTime) {
        BlockPos idlePos = VillagerPlacementData.getIdlePosition(villager);
        if (idlePos == null) return;

        BlockPos standPos = idlePos.above();
        Vec3 standVec = Vec3.atBottomCenterOf(standPos);
        double dist = villager.position().distanceTo(standVec);

        if (dist >= Config.arrivalThreshold) {
            Optional<WalkTarget> currentTarget = villager.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
            if (currentTarget.isEmpty() || !isTargetingIdlePosition(currentTarget.get(), standVec)) {
                navigateToIdlePosition(villager);
            }
        } else {
            villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
    }

    private boolean isTargetingIdlePosition(WalkTarget target, Vec3 idleVec) {
        Vec3 targetPos = target.getTarget().currentPosition();
        return targetPos.distanceTo(idleVec) < 1.0;
    }

    private void navigateToIdlePosition(Villager villager) {
        BlockPos idlePos = VillagerPlacementData.getIdlePosition(villager);
        if (idlePos != null) {
            BlockPos standPos = idlePos.above();
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                new WalkTarget(Vec3.atBottomCenterOf(standPos), Config.walkSpeed, 0));
        }
    }
}
