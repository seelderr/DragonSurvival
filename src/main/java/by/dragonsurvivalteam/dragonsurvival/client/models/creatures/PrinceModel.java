package by.dragonsurvivalteam.dragonsurvival.client.models.creatures;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.PrinceHorseEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PrinceModel extends AnimatedGeoModel<PrinceHorseEntity>{
	@Override
	public ResourceLocation getModelLocation(PrinceHorseEntity object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/horseback_rider.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(PrinceHorseEntity object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "textures/riders/dragon_prince.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(PrinceHorseEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/horseback_rider.animations.json");
	}
}