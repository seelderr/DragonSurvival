package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BallLightning;
import by.dragonsurvivalteam.dragonsurvival.common.items.DSItems;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

@OnlyIn( Dist.CLIENT )
public class BallLightningRenderer extends GeoProjectilesRenderer<BallLightning>{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/lightning_texture.png");


	public BallLightningRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<BallLightning> modelProvider){


	public BallLightningRenderer(EntityRendererManager renderManager, AnimatedGeoModel < BallLightning > modelProvider) {
			super(renderManager, modelProvider);
		}

		protected int getBlockLightLevel (BallLightning p_225624_1_, BlockPos p_225624_2_){
			return 15;
		}


		public void render (BallLightning p_225623_1_,float p_225623_2_, float p_225623_3_, PoseStack stack, MultiBufferSource p_225623_5_,int p_225623_6_){
			if(p_225623_1_.tickCount >= 2 || !(this.entityRenderDispatcher.camera.get().distanceToSqr(p_225623_1_) < 12.25D)){


				public void render (BallLightning p_225623_1_,float p_225623_2_, float p_225623_3_, PoseStack stack, MultiBufferSource p_225623_5_,int p_225623_6_){
					if(p_225623_1_.tickCount >= 2 || !(this.entityRenderDispatcher.camera.get().distanceToSqr(p_225623_1_) < 12.25D)){
						stack.pushPose();
						stack.translate(0F, -0.2F, 0F);
						stack.scale(2.0F, 2.0F, 2.0F);
						stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
						stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
						Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(DSItems.lightningTextureItem), TransformType.GROUND, p_225623_6_, OverlayTexture.NO_OVERLAY, stack, p_225623_5_, 0);
						stack.popPose();
						super.render(p_225623_1_, p_225623_2_, p_225623_3_, stack, p_225623_5_, p_225623_6_);
					}


					super.render(p_225623_1_, p_225623_2_, p_225623_3_, stack, p_225623_5_, p_225623_6_);
				}

				public ResourceLocation getTextureLocation (BallLightning p_110775_1_){
					return TEXTURE_LOCATION;
				}
			}