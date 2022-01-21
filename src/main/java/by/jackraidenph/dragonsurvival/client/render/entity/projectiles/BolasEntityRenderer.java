package by.jackraidenph.dragonsurvival.client.render.entity.projectiles;

import by.jackraidenph.dragonsurvival.common.entity.projectiles.BolasEntity;
import by.jackraidenph.dragonsurvival.common.items.DSItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class BolasEntityRenderer extends EntityRenderer<BolasEntity>
{
	public BolasEntityRenderer(Context p_174198_)
	{
		super(p_174198_);
	}
	
	public void render(BolasEntity p_225623_1_, float p_225623_2_, float p_225623_3_, PoseStack stack, MultiBufferSource p_225623_5_, int p_225623_6_) {
		if (p_225623_1_.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(p_225623_1_) < 12.25D)) {
			stack.pushPose();
			stack.translate(0F, -0.2F, 0F);
			stack.scale(2.0F, 2.0F, 2.0F);
			stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
			stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(DSItems.huntingNet), TransformType.GROUND, p_225623_6_, OverlayTexture.NO_OVERLAY, stack, p_225623_5_, 0);
			stack.popPose();
			super.render(p_225623_1_, p_225623_2_, p_225623_3_, stack, p_225623_5_, p_225623_6_);
		}
		
		
		super.render(p_225623_1_, p_225623_2_, p_225623_3_, stack, p_225623_5_, p_225623_6_);
	}
	
	public ResourceLocation getTextureLocation(BolasEntity pEntity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
