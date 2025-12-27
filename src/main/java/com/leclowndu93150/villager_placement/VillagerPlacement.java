package com.leclowndu93150.villager_placement;

import com.leclowndu93150.villager_placement.item.ModDataComponents;
import com.leclowndu93150.villager_placement.item.VillagerWandItem;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(VillagerPlacement.MODID)
public class VillagerPlacement {
    public static final String MODID = "villager_placement";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<Item, VillagerWandItem> VILLAGER_WAND = ITEMS.register("villager_wand",
            () -> new VillagerWandItem(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VILLAGER_PLACEMENT_TAB = CREATIVE_MODE_TABS.register("villager_placement_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.villager_placement"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> VILLAGER_WAND.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(VILLAGER_WAND.get());
                    })
                    .build());

    public VillagerPlacement(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModDataComponents.register(modEventBus);
    }
}
