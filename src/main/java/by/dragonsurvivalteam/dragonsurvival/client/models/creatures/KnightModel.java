package by.dragonsurvivalteam.dragonsurvival.client.models.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.KnightEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class KnightModel extends GeoModel<KnightEntity> {
	@Override
	public ResourceLocation getModelResource(KnightEntity object){
		return ResourceLocation.fromNamespaceAndPath(MODID, "geo/horseback_rider.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(KnightEntity object){
		return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/hunters/knight_on_horse.png");
	}

	@Override
	public ResourceLocation getAnimationResource(KnightEntity animatable){
		return ResourceLocation.fromNamespaceAndPath(MODID, "animations/hunter_knight.animation.json");
	}
}