package by.dragonsurvivalteam.dragonsurvival.client.models;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.List;

public class DragonArmorModel extends AnimatedGeoModel<DragonEntity>{

	private ResourceLocation armorTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/armor/empty_armor.png");

	@SuppressWarnings( "unchecked" )
	public DragonArmorModel(DragonModel dragonModel){
		List<CoreGeoBone> armorBones = dragonModel.getAnimationProcessor().getModelRendererList();
		List<CoreGeoBone> dragonBones = getAnimationProcessor().getModelRendererList();
		for(CoreGeoBone armorBone : armorBones){
			GeoBone armorGeoBone = (GeoBone)armorBone;
			for(CoreGeoBone dragonBone : dragonBones){
				GeoBone dragonGeobone = (GeoBone)dragonBone;
				if(armorGeoBone.name.equals(dragonGeobone.name)){
					dragonGeobone.childBones.add(armorGeoBone);
					break;
				}
			}
		}
	}


	@Override
	public ResourceLocation getModelResource(DragonEntity object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_armor_model.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DragonEntity object){
		return armorTexture;
	}

	public void setArmorTexture(ResourceLocation armorTexture){
		this.armorTexture = armorTexture;
	}

	@Override
	public ResourceLocation getAnimationResource(DragonEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/dragon.animations.json");
	}
}