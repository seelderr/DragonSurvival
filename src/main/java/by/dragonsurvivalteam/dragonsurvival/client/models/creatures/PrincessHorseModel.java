package by.dragonsurvivalteam.dragonsurvival.client.models.creatures;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.PrincessHorseEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import software.bernie.geckolib.model.GeoModel;

public class PrincessHorseModel extends GeoModel<PrincessHorseEntity> {
	@Override
	public ResourceLocation getModelResource(PrincessHorseEntity object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/horseback_rider.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PrincessHorseEntity object){
		DyeColor dyeColor = DyeColor.byId(object.getColor());
		switch(dyeColor){
			case RED:
				return new ResourceLocation(DragonSurvivalMod.MODID, "textures/riders/princess_red.png");
			case BLUE:
				return new ResourceLocation(DragonSurvivalMod.MODID, "textures/riders/princess_blue.png");
			case PURPLE:
				return new ResourceLocation(DragonSurvivalMod.MODID, "textures/riders/princess_purple.png");
			case WHITE:
				return new ResourceLocation(DragonSurvivalMod.MODID, "textures/riders/princess_white.png");
			case YELLOW:
				return new ResourceLocation(DragonSurvivalMod.MODID, "textures/riders/princess_yellow.png");
			case BLACK:
				return new ResourceLocation(DragonSurvivalMod.MODID, "textures/riders/princess_black.png");
		}
		return null;
	}

	@Override
	public ResourceLocation getAnimationResource(PrincessHorseEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/horseback_rider.animation.json");
	}
}