package by.dragonsurvivalteam.dragonsurvival.client.models.creatures;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.PrincessHorseEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import software.bernie.geckolib.model.GeoModel;

public class PrincessHorseModel extends GeoModel<PrincessHorseEntity> {
	@Override
	public ResourceLocation getModelResource(PrincessHorseEntity object){
		return ResourceLocation.fromNamespaceAndPath(MODID, "geo/horseback_rider.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PrincessHorseEntity object){
		DyeColor dyeColor = DyeColor.byId(object.getColor());
		switch(dyeColor){
			case RED:
				return ResourceLocation.fromNamespaceAndPath(MODID, "textures/riders/princess_red.png");
			case BLUE:
				return ResourceLocation.fromNamespaceAndPath(MODID, "textures/riders/princess_blue.png");
			case PURPLE:
				return ResourceLocation.fromNamespaceAndPath(MODID, "textures/riders/princess_purple.png");
			case WHITE:
				return ResourceLocation.fromNamespaceAndPath(MODID, "textures/riders/princess_white.png");
			case YELLOW:
				return ResourceLocation.fromNamespaceAndPath(MODID, "textures/riders/princess_yellow.png");
			case BLACK:
				return ResourceLocation.fromNamespaceAndPath(MODID, "textures/riders/princess_black.png");
		}
		return null;
	}

	@Override
	public ResourceLocation getAnimationResource(PrincessHorseEntity animatable){
		return ResourceLocation.fromNamespaceAndPath(MODID, "animations/horseback_rider.animation.json");
	}
}