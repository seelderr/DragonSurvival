package by.dragonsurvivalteam.dragonsurvival.client.models.creatures;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.KnightEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class KnightModel extends AnimatedGeoModel<KnightEntity>{
	@Override
	public ResourceLocation getModelLocation(KnightEntity object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/horseback_rider.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(KnightEntity object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "textures/riders/dragon_knight_black.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(KnightEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/dragon_knight.animations.json");
	}
}