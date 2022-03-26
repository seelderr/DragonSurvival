package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BallLightning;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LightningBallModel extends AnimatedGeoModel<BallLightning>{
	private ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/lightning_texture.png");

	@Override
	public ResourceLocation getModelLocation(BallLightning dragon){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_lightning_ball.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(BallLightning dragon){
		return currentTexture;
	}

	public void setCurrentTexture(ResourceLocation currentTexture){
		this.currentTexture = currentTexture;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(BallLightning animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/lightning_ball.animation.json");
	}
}