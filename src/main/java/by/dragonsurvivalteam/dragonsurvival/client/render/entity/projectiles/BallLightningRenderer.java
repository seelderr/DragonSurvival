package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BallLightningEntity;
import by.dragonsurvivalteam.dragonsurvival.common.items.DSItems;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

@OnlyIn( Dist.CLIENT )
public class BallLightningRenderer extends GeoProjectilesRenderer<BallLightningEntity>{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/lightning_texture.png");

	public BallLightningRenderer(EntityRendererManager renderManager, AnimatedGeoModel<BallLightningEntity> modelProvider){
		super(renderManager, modelProvider);
	}

	protected int getBlockLightLevel(BallLightningEntity p_225624_1_, BlockPos p_225624_2_){
		return 15;
	}

	public void render(BallLightningEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack stack, IRenderTypeBuffer p_225623_5_, int p_225623_6_){
		if(p_225623_1_.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(p_225623_1_) < 12.25D)){
			stack.pushPose();
			stack.translate(0F, -0.2F, 0F);
			stack.scale(2.0F, 2.0F, 2.0F);
			stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
			stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(DSItems.lightningTextureItem), TransformType.GROUND, p_225623_6_, OverlayTexture.NO_OVERLAY, stack, p_225623_5_);
			stack.popPose();
			super.render(p_225623_1_, p_225623_2_, p_225623_3_, stack, p_225623_5_, p_225623_6_);
		}


		super.render(p_225623_1_, p_225623_2_, p_225623_3_, stack, p_225623_5_, p_225623_6_);
	}

	public ResourceLocation getTextureLocation(BallLightningEntity p_110775_1_){
		return TEXTURE_LOCATION;
	}
}