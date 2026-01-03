package com.leclowndu93150.villager_placement;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = VillagerPlacement.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue MAX_DISTANCE = BUILDER
            .comment("Maximum distance a villager can be from their idle position before the behavior stops working")
            .defineInRange("maxDistance", 48, 8, 128);

    private static final ModConfigSpec.DoubleValue ARRIVAL_THRESHOLD = BUILDER
            .comment("How close the villager needs to be to their idle position to be considered 'arrived' (in blocks)")
            .defineInRange("arrivalThreshold", 0.75, 0.1, 2.0);

    private static final ModConfigSpec.DoubleValue WALK_SPEED = BUILDER
            .comment("Walking speed modifier when villager returns to their idle position")
            .defineInRange("walkSpeed", 0.5, 0.1, 1.0);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int maxDistance;
    public static double arrivalThreshold;
    public static float walkSpeed;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        maxDistance = MAX_DISTANCE.get();
        arrivalThreshold = ARRIVAL_THRESHOLD.get();
        walkSpeed = WALK_SPEED.get().floatValue();
    }
}
