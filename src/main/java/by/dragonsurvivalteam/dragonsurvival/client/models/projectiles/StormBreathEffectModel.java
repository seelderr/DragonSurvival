package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.StormBreathEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class StormBreathEffectModel extends AnimatedGeoModel<StormBreathEntity>{
	private ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/storms_breath.png");

	@Override
	public ResourceLocation getModelLocation(StormBreathEntity dragonEntity){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/storms_breath.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(StormBreathEntity dragonEntity){
		return currentTexture;
	}

	public void setCurrentTexture(ResourceLocation currentTexture){
		this.currentTexture = currentTexture;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(StormBreathEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/storms_breath.animations.json");
	}
}