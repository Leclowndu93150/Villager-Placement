package com.leclowndu93150.villager_placement.client;

import com.leclowndu93150.villager_placement.VillagerPlacement;
import com.leclowndu93150.villager_placement.item.VillagerWandItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

/**
 * Renders a highlight box around the block selected with the Villager Wand.
 * The box is only visible when holding the wand with a position selected.
 */
@EventBusSubscriber(modid = VillagerPlacement.MODID, value = Dist.CLIENT)
public class WandBlockHighlightRenderer {

    private static final float LINE_WIDTH = 3.0F;

    private static final float R = 0.2F;
    private static final float G = 0.9F;
    private static final float B = 0.3F;
    private static final float A = 1.0F;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        BlockPos selectedPos = null;
        if (mainHand.getItem() instanceof VillagerWandItem) {
            selectedPos = VillagerWandItem.getSelectedPosition(mainHand);
        } else if (offHand.getItem() instanceof VillagerWandItem) {
            selectedPos = VillagerWandItem.getSelectedPosition(offHand);
        }

        if (selectedPos == null) return;

        renderBlockOutline(event.getPoseStack(), event.getCamera(), selectedPos);
    }

    private static void renderBlockOutline(PoseStack poseStack, Camera camera, BlockPos pos) {
        Vec3 cameraPos = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(
                pos.getX() - cameraPos.x,
                pos.getY() - cameraPos.y,
                pos.getZ() - cameraPos.z
        );

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.lineWidth(LINE_WIDTH);

        Matrix4f mat = poseStack.last().pose();

        float minX = -0.002F;
        float minY = -0.002F;
        float minZ = -0.002F;
        float maxX = 1.002F;
        float maxY = 1.002F;
        float maxZ = 1.002F;

        int ri = (int) (R * 255);
        int gi = (int) (G * 255);
        int bi = (int) (B * 255);
        int ai = (int) (A * 255);

        BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        buf.addVertex(mat, minX, minY, minZ).setColor(ri, gi, bi, ai);
        buf.addVertex(mat, maxX, minY, minZ).setColor(ri, gi, bi, ai);

        buf.addVertex(mat, maxX, minY, minZ).setColor(ri, gi, bi, ai);
        buf.addVertex(mat, maxX, minY, maxZ).setColor(ri, gi, bi, ai);

        buf.addVertex(mat, maxX, minY, maxZ).setColor(ri, gi, bi, ai);
        buf.addVertex(mat, minX, minY, maxZ).setColor(ri, gi, bi, ai);

        buf.addVertex(mat, minX, minY, maxZ).setColor(ri, gi, bi, ai);
        buf.addVertex(mat, minX, minY, minZ).setColor(ri, gi, bi, ai);

        buf.addVertex(mat, minX, maxY, minZ).setColor(ri, gi, bi, ai);
        buf.addVertex(mat, maxX, maxY, minZ).setColor(ri, gi, bi, ai);

        buf.addVertex(mat, maxX, maxY, minZ).setColor(ri, gi, bi, ai);
        buf.addVertex(mat, maxX, maxY, maxZ).setColor(ri, gi, bi, ai);

        buf.addVertex(mat, maxX, maxY, maxZ).setColor(ri, gi, bi, ai);
        buf.addVertex(mat, minX, maxY, maxZ).setColor(ri, gi, bi, ai);

        buf.addVertex(mat, minX, maxY, maxZ).setColor(ri, gi, bi, ai);
        buf.addVertex(mat, minX, maxY, minZ).setColor(ri, gi, bi, ai);

        buf.addVertex(mat, minX, minY, minZ).setColor(ri, gi, bi, ai);
        buf.addVertex(mat, minX, maxY, minZ).setColor(ri, gi, bi, ai);

        buf.addVertex(mat, maxX, minY, minZ).setColor(ri, gi, bi, ai);
        buf.addVertex(mat, maxX, maxY, minZ).setColor(ri, gi, bi, ai);

        buf.addVertex(mat, maxX, minY, maxZ).setColor(ri, gi, bi, ai);
        buf.addVertex(mat, maxX, maxY, maxZ).setColor(ri, gi, bi, ai);

        buf.addVertex(mat, minX, minY, maxZ).setColor(ri, gi, bi, ai);
        buf.addVertex(mat, minX, maxY, maxZ).setColor(ri, gi, bi, ai);

        BufferUploader.drawWithShader(buf.buildOrThrow());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }
}
