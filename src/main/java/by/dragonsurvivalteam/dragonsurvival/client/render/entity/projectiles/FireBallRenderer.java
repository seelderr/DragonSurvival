package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.FireBall;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

@OnlyIn( Dist.CLIENT )
public class FireBallRenderer extends GeoProjectilesRenderer<FireBall>{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/fireball_texture.png");


	public FireBallRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<FireBall> modelProvider){


	public FireBallRenderer(EntityRendererManager renderManager, AnimatedGeoModel < FireBall > modelProvider) {
			super(renderManager, modelProvider);
		}

		protected int getBlockLightLevel (FireBall p_225624_1_, BlockPos p_225624_2_){
			return 15;
		}


		public void render (FireBall p_225623_1_,float p_225623_2_, float p_225623_3_, PoseStack stack, MultiBufferSource p_225623_5_,int p_225623_6_){


			public void render (FireBall p_225623_1_,float p_225623_2_, float p_225623_3_, PoseStack stack, MultiBufferSource p_225623_5_,int p_225623_6_){
				super.render(p_225623_1_, p_225623_2_, p_225623_3_, stack, p_225623_5_, p_225623_6_);
			}

			public ResourceLocation getTextureLocation (FireBall p_110775_1_){
				return TEXTURE_LOCATION;
			}
		}