package by.jackraidenph.dragonsurvival.client.render.item;

import by.jackraidenph.dragonsurvival.client.render.blocks.HelmetEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class HelmetStackTileEntityRenderer extends BlockEntityWithoutLevelRenderer
{
    public HelmetStackTileEntityRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet)
    {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }
    
    @Override
    public void renderByItem(ItemStack pStack, ItemTransforms.TransformType pTransformType, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pTransformType == TransformType.GUI) {
            pPoseStack.translate(0.5, -0.15, 0);
            pPoseStack.mulPose(Vector3f.XP.rotationDegrees(45));
            pPoseStack.mulPose(Vector3f.YP.rotationDegrees(135));
        }
        BlockItem blockItem = (BlockItem) pStack.getItem();
        HelmetEntityRenderer.renderHelmet(null, 0, blockItem.getBlock(), 0, pPoseStack, pBuffer, pPackedLight);
    }
}
