package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BallLightningEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LightningBallModel extends AnimatedGeoModel<BallLightningEntity>{
	private ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/lightning_texture.png");

	@Override
	public ResourceLocation getModelLocation(BallLightningEntity dragonEntity){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_lightning_ball.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(BallLightningEntity dragonEntity){
		return currentTexture;
	}

	public void setCurrentTexture(ResourceLocation currentTexture){
		this.currentTexture = currentTexture;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(BallLightningEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/lightning_ball.animation.json");
	}
}