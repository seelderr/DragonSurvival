package by.dragonsurvivalteam.dragonsurvival.client.models.creatures;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.PrinceHorseEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PrinceModel extends GeoModel<PrinceHorseEntity> {
	@Override
	public ResourceLocation getModelResource(final PrinceHorseEntity ignored) {
		return ResourceLocation.fromNamespaceAndPath(MODID, "geo/horseback_rider.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(final PrinceHorseEntity ignored) {
		return ResourceLocation.fromNamespaceAndPath(MODID, "textures/riders/dragon_prince.png");
	}

	@Override
	public ResourceLocation getAnimationResource(final PrinceHorseEntity ignored) {
		return ResourceLocation.fromNamespaceAndPath(MODID, "animations/horseback_rider.animation.json");
	}
}