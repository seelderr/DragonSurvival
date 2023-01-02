package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.FireBallEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

@OnlyIn( Dist.CLIENT )
public class FireBallRenderer extends GeoProjectilesRenderer<FireBallEntity>{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/fireball_texture.png");

	public FireBallRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<FireBallEntity> modelProvider){
		super(renderManager, modelProvider);
	}

	@Override
	protected int getBlockLightLevel(FireBallEntity p_225624_1_, BlockPos p_225624_2_){
		return 15;
	}

	@Override
	public void render(FireBallEntity p_225623_1_, float p_225623_2_, float p_225623_3_, PoseStack stack, MultiBufferSource p_225623_5_, int p_225623_6_){
		super.render(p_225623_1_, p_225623_2_, p_225623_3_, stack, p_225623_5_, p_225623_6_);
	}

	@Override
	public ResourceLocation getTextureLocation(FireBallEntity p_110775_1_){
		return TEXTURE_LOCATION;
	}
}