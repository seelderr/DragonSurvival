package by.jackraidenph.dragonsurvival.magic.entity.models;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.magic.entity.BallLightningEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LightningBallModel extends AnimatedGeoModel<BallLightningEntity>
{
	private  ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/lightning_texture.png");
	
	@Override
	public ResourceLocation getModelLocation(BallLightningEntity dragonEntity) {
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_ball.geo.json");
	}
	
	public void setCurrentTexture(ResourceLocation currentTexture) {
		this.currentTexture = currentTexture;
	}
	
	@Override
	public ResourceLocation getTextureLocation(BallLightningEntity dragonEntity) {
		return currentTexture;
	}
	
	@Override
	public ResourceLocation getAnimationFileLocation(BallLightningEntity animatable) {
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/lightning_ball.animation.json");
	}
}