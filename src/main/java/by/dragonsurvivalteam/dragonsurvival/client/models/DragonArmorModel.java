package by.dragonsurvivalteam.dragonsurvival.client.models;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.Collection;

public class DragonArmorModel extends GeoModel<DragonEntity> {
	private ResourceLocation armorTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/armor/empty_armor.png");

	public DragonArmorModel(final DragonModel model) {
		Collection<CoreGeoBone> armorBones = model.getAnimationProcessor().getRegisteredBones();
		Collection<CoreGeoBone> dragonBones = getAnimationProcessor().getRegisteredBones();

		for(CoreGeoBone armorBone : armorBones) {
			GeoBone armorGeoBone = (GeoBone) armorBone;

			for (CoreGeoBone dragonBone : dragonBones) {
				GeoBone dragonGeobone = (GeoBone) dragonBone;

				if (armorGeoBone.getName().equals(dragonGeobone.getName())) {
					dragonGeobone.getChildBones().add(armorGeoBone);
					break;
				}
			}
		}
	}

	@Override
	public ResourceLocation getModelResource(final DragonEntity ignored) {
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_armor_model.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(final DragonEntity ignored) {
		return armorTexture;
	}

	public void setArmorTexture(final ResourceLocation armorTexture) {
		this.armorTexture = armorTexture;
	}

	@Override
	public ResourceLocation getAnimationResource(final DragonEntity animatable) {
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/dragon.animations.json");
	}
}