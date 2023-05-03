package by.dragonsurvivalteam.dragonsurvival.client.models.creatures;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.PrinceHorseEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PrinceModel extends AnimatedGeoModel<PrinceHorseEntity>{
	@Override
	public ResourceLocation getModelResource(PrinceHorseEntity object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/horseback_rider.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PrinceHorseEntity object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "textures/riders/dragon_prince.png");
	}

	@Override
	public ResourceLocation getAnimationResource(PrinceHorseEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/horseback_rider.animations.json");
	}
}