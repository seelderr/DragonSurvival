package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BallLightningEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LightningBallModel extends GeoModel<BallLightningEntity> {
	private ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/lightning_texture.png");

	@Override
	public ResourceLocation getModelResource(BallLightningEntity dragon){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_lightning_ball.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BallLightningEntity dragon){
		return currentTexture;
	}

	public void setCurrentTexture(ResourceLocation currentTexture){
		this.currentTexture = currentTexture;
	}

	@Override
	public ResourceLocation getAnimationResource(BallLightningEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/lightning_ball.animation.json");
	}
}