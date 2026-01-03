package com.leclowndu93150.villager_placement.api;

import com.leclowndu93150.villager_placement.ai.ReturnToIdlePositionBehavior;
import com.leclowndu93150.villager_placement.ai.StayAtIdlePositionBehavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.npc.Villager;

/**
 * API for Villager Placement mod.
 * Use this to integrate idle position behaviors into other mods.
 *
 * Example usage in Liberty Villagers:
 * <pre>
 * if (ModList.get().isLoaded("villager_placement")) {
 *     // Add to idle package tasks
 *     tasks.add(Pair.of(0, VillagerPlacementAPI.createStayAtIdleBehavior()));
 *
 *     // Add to work/meet package tasks
 *     tasks.add(Pair.of(1, VillagerPlacementAPI.createReturnToIdleBehavior()));
 * }
 * </pre>
 */
public final class VillagerPlacementAPI {

    private VillagerPlacementAPI() {}

    /**
     * Creates a behavior that keeps villagers at their assigned idle position during IDLE activity.
     * Should be added to the idle package with high priority (e.g., priority 0).
     *
     * @return A new StayAtIdlePositionBehavior instance
     */
    public static BehaviorControl<Villager> createStayAtIdleBehavior() {
        return new StayAtIdlePositionBehavior();
    }

    /**
     * Creates a behavior that makes villagers return to their idle position after work/meetings.
     * Should be added to work and meet packages with high priority (e.g., priority 1).
     *
     * @return A new ReturnToIdlePositionBehavior instance
     */
    public static BehaviorControl<Villager> createReturnToIdleBehavior() {
        return new ReturnToIdlePositionBehavior();
    }

    /**
     * Checks if the Villager Placement mod is properly initialized.
     *
     * @return true if the mod is ready to use
     */
    public static boolean isAvailable() {
        return true;
    }
}
