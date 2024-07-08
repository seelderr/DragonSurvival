package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BallLightningEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class LightningBallModel extends GeoModel<BallLightningEntity> {
	private ResourceLocation currentTexture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/lightning_texture.png");

	@Override
	public ResourceLocation getModelResource(BallLightningEntity dragon){
		return ResourceLocation.fromNamespaceAndPath(MODID, "geo/dragon_lightning_ball.geo.json");
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
		return ResourceLocation.fromNamespaceAndPath(MODID, "animations/lightning_ball.animation.json");
	}
}