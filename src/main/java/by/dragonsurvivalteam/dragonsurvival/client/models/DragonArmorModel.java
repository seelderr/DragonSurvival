package by.dragonsurvivalteam.dragonsurvival.client.models;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.List;

public class DragonArmorModel extends AnimatedGeoModel<DragonEntity>{

	private ResourceLocation armorTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/armor/empty_armor.png");

	@SuppressWarnings( "unchecked" )
	public DragonArmorModel(DragonModel dragonModel){
		List<IBone> armorBones = dragonModel.getAnimationProcessor().getModelRendererList();
		List<IBone> dragonBones = this.getAnimationProcessor().getModelRendererList();
		for(IBone armorBone : armorBones){
			GeoBone armorGeoBone = (GeoBone)armorBone;
			for(IBone dragonBone : dragonBones){
				GeoBone dragonGeobone = (GeoBone)dragonBone;
				if(armorGeoBone.name.equals(dragonGeobone.name)){
					dragonGeobone.childBones.add(armorGeoBone);
					break;
				}
			}
		}
	}

	@Override
	public ResourceLocation getModelLocation(DragonEntity object){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_armor_model.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(DragonEntity object){
		return armorTexture;
	}

	public void setArmorTexture(ResourceLocation armorTexture){
		this.armorTexture = armorTexture;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(DragonEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/dragon.animations.json");
	}
}