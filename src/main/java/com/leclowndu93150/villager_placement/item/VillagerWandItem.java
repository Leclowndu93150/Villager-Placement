package com.leclowndu93150.villager_placement.item;

import com.leclowndu93150.villager_placement.data.VillagerPlacementData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class VillagerWandItem extends Item {

    public VillagerWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            BlockState state = level.getBlockState(pos);

            if (!state.isSolid()) {
                player.displayClientMessage(
                        Component.translatable("villager_placement.wand.invalid_block").withStyle(ChatFormatting.RED),
                        true
                );
                return InteractionResult.FAIL;
            }

            setSelectedPosition(stack, pos);

            player.displayClientMessage(
                    Component.translatable("villager_placement.wand.position_set", pos.getX(), pos.getY(), pos.getZ())
                            .withStyle(ChatFormatting.GREEN),
                    true
            );
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Shift+right-click in air clears the wand selection
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                if (getSelectedPosition(stack) != null) {
                    clearSelectedPosition(stack);
                    player.displayClientMessage(
                            Component.translatable("villager_placement.wand.selection_cleared").withStyle(ChatFormatting.YELLOW),
                            true
                    );
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!(entity instanceof Villager villager)) {
            return InteractionResult.PASS;
        }

        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        Level level = player.level();

        if (!level.isClientSide) {
            BlockPos selectedPos = getSelectedPosition(stack);

            // If wand has no position, free the villager
            if (selectedPos == null) {
                if (VillagerPlacementData.hasIdlePosition(villager)) {
                    VillagerPlacementData.clearIdlePosition(villager);
                    player.displayClientMessage(
                            Component.translatable("villager_placement.wand.villager_freed").withStyle(ChatFormatting.YELLOW),
                            true
                    );
                } else {
                    player.displayClientMessage(
                            Component.translatable("villager_placement.wand.no_position").withStyle(ChatFormatting.RED),
                            true
                    );
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            // Assign villager to position
            VillagerPlacementData.setIdlePosition(villager, selectedPos);

            ItemStack handStack = player.getItemInHand(hand);
            clearSelectedPosition(handStack);

            player.displayClientMessage(
                    Component.translatable("villager_placement.wand.villager_assigned", selectedPos.getX(), selectedPos.getY(), selectedPos.getZ())
                            .withStyle(ChatFormatting.GREEN),
                    true
            );
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        BlockPos pos = getSelectedPosition(stack);
        if (pos != null) {
            tooltipComponents.add(
                    Component.translatable("villager_placement.wand.tooltip.selected", pos.getX(), pos.getY(), pos.getZ())
                            .withStyle(ChatFormatting.GOLD)
            );
        } else {
            tooltipComponents.add(
                    Component.translatable("villager_placement.wand.tooltip.no_selection")
                            .withStyle(ChatFormatting.GRAY)
            );
        }

        // Show instructions only when holding shift
        if (tooltipFlag.hasShiftDown()) {
            tooltipComponents.add(Component.empty());
            tooltipComponents.add(
                    Component.translatable("villager_placement.wand.tooltip.instruction1")
                            .withStyle(ChatFormatting.YELLOW)
            );
            tooltipComponents.add(
                    Component.translatable("villager_placement.wand.tooltip.instruction2")
                            .withStyle(ChatFormatting.YELLOW)
            );
            tooltipComponents.add(
                    Component.translatable("villager_placement.wand.tooltip.instruction3")
                            .withStyle(ChatFormatting.YELLOW)
            );
            tooltipComponents.add(
                    Component.translatable("villager_placement.wand.tooltip.instruction4")
                            .withStyle(ChatFormatting.YELLOW)
            );
            tooltipComponents.add(Component.empty());
            tooltipComponents.add(
                    Component.translatable("villager_placement.wand.tooltip.note")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
            );
        } else {
            tooltipComponents.add(
                    Component.translatable("villager_placement.wand.tooltip.hold_shift")
                            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
            );
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getSelectedPosition(stack) != null;
    }

    public static void setSelectedPosition(ItemStack stack, BlockPos pos) {
        stack.set(ModDataComponents.SELECTED_POSITION.get(), pos);
    }

    public static BlockPos getSelectedPosition(ItemStack stack) {
        return stack.get(ModDataComponents.SELECTED_POSITION.get());
    }

    public static void clearSelectedPosition(ItemStack stack) {
        stack.remove(ModDataComponents.SELECTED_POSITION.get());
    }
}
