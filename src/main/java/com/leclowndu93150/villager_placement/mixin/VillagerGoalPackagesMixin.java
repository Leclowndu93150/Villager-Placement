package com.leclowndu93150.villager_placement.mixin;

import com.google.common.collect.ImmutableList;
import com.leclowndu93150.villager_placement.VillagerPlacement;
import com.leclowndu93150.villager_placement.ai.StayAtIdlePositionBehavior;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(VillagerGoalPackages.class)
public class VillagerGoalPackagesMixin {

    @Inject(
            method = "getIdlePackage",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void villagerplacement$injectIdlePositionBehavior(
            VillagerProfession profession,
            float speed,
            CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>>> cir
    ) {

        ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> original = cir.getReturnValue();

        List<Pair<Integer, ? extends BehaviorControl<? super Villager>>> modifiedList = new ArrayList<>(original);

        modifiedList.add(0, Pair.of(0, new StayAtIdlePositionBehavior()));

        cir.setReturnValue(ImmutableList.copyOf(modifiedList));
    }
}
