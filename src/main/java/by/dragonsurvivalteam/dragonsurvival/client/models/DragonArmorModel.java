package by.dragonsurvivalteam.dragonsurvival.client.models;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class DragonArmorModel extends GeoModel<DragonEntity> {
	private ResourceLocation armorTexture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/armor/empty_armor.png");

	public DragonArmorModel(final DragonModel model) {
		Collection<GeoBone> armorBones = model.getAnimationProcessor().getRegisteredBones();
		Collection<GeoBone> dragonBones = getAnimationProcessor().getRegisteredBones();

		for(GeoBone armorBone : armorBones) {
			for (GeoBone dragonBone : dragonBones) {
				if (armorBone.getName().equals(dragonBone.getName())) {
					dragonBone.getChildBones().add(armorBone);
					break;
				}
			}
		}
	}

	@Override
	public ResourceLocation getModelResource(final DragonEntity ignored) {
		return ResourceLocation.fromNamespaceAndPath(MODID, "geo/dragon_armor_model.geo.json");
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
		return ResourceLocation.fromNamespaceAndPath(MODID, "animations/dragon.animation.json");
	}
}