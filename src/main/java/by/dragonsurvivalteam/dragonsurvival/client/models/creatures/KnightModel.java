package by.dragonsurvivalteam.dragonsurvival.client.models.creatures;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.KnightEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KnightModel extends GeoModel<KnightEntity> {
	@Override
	public ResourceLocation getModelResource(KnightEntity object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/horseback_rider.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(KnightEntity object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "textures/riders/dragon_knight_black.png");
	}

	@Override
	public ResourceLocation getAnimationResource(KnightEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/knight.animation.json");
	}
}