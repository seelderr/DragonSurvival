package by.jackraidenph.dragonsurvival.magic.entity.models;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.magic.entity.FireBallEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FireballModel extends AnimatedGeoModel<FireBallEntity>
{
	private  ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/fireball_texture.png");
	
	@Override
	public ResourceLocation getModelLocation(FireBallEntity dragonEntity) {
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_ball.geo.json");
	}
	
	public void setCurrentTexture(ResourceLocation currentTexture) {
		this.currentTexture = currentTexture;
	}
	
	@Override
	public ResourceLocation getTextureLocation(FireBallEntity dragonEntity) {
		return currentTexture;
	}
	
	@Override
	public ResourceLocation getAnimationFileLocation(FireBallEntity animatable) {
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/fireball.animation.json");
	}
}