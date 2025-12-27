package com.leclowndu93150.villager_placement.data;

import com.leclowndu93150.villager_placement.VillagerPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.Villager;

import javax.annotation.Nullable;

/**
 * Handles storing and retrieving idle position data for villagers.
 * The data is stored directly on the villager's persistent NBT data.
 */
public class VillagerPlacementData {

    private static final String IDLE_POS_TAG = VillagerPlacement.MODID + "_idle_pos";
    private static final String IDLE_POS_X = "x";
    private static final String IDLE_POS_Y = "y";
    private static final String IDLE_POS_Z = "z";

    /**
     * Sets the idle position for a villager.
     * The villager will return to this position when idle.
     */
    public static void setIdlePosition(Villager villager, BlockPos pos) {
        CompoundTag persistentData = villager.getPersistentData();
        CompoundTag idleTag = new CompoundTag();
        idleTag.putInt(IDLE_POS_X, pos.getX());
        idleTag.putInt(IDLE_POS_Y, pos.getY());
        idleTag.putInt(IDLE_POS_Z, pos.getZ());
        persistentData.put(IDLE_POS_TAG, idleTag);
    }

    /**
     * Gets the idle position for a villager, if set.
     */
    @Nullable
    public static BlockPos getIdlePosition(Villager villager) {
        CompoundTag persistentData = villager.getPersistentData();
        if (!persistentData.contains(IDLE_POS_TAG)) {
            return null;
        }
        CompoundTag idleTag = persistentData.getCompound(IDLE_POS_TAG);
        return new BlockPos(
                idleTag.getInt(IDLE_POS_X),
                idleTag.getInt(IDLE_POS_Y),
                idleTag.getInt(IDLE_POS_Z)
        );
    }

    /**
     * Checks if a villager has an idle position assigned.
     */
    public static boolean hasIdlePosition(Villager villager) {
        return villager.getPersistentData().contains(IDLE_POS_TAG);
    }

    /**
     * Clears the idle position for a villager.
     */
    public static void clearIdlePosition(Villager villager) {
        villager.getPersistentData().remove(IDLE_POS_TAG);
    }
}
