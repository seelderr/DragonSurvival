package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;


import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.FireBallEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FireballModel extends GeoModel<FireBallEntity> {
	private final ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/fireball_texture.png");

	@Override
	public ResourceLocation getModelResource(FireBallEntity dragonEntity) {
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_fireball.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FireBallEntity dragonEntity) {
		return currentTexture;
	}

	@Override
	public ResourceLocation getAnimationResource(FireBallEntity animatable) {
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/fireball.animation.json");
	}
}