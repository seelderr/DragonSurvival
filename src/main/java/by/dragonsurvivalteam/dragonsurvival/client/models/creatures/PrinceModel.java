package by.dragonsurvivalteam.dragonsurvival.client.models.creatures;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.PrinceHorse;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PrinceModel extends AnimatedGeoModel<PrinceHorse>{
	@Override
	public ResourceLocation getModelLocation(PrinceHorse object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/horseback_rider.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(PrinceHorse object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "textures/riders/dragon_prince.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(PrinceHorse animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/horseback_rider.animations.json");
	}
}