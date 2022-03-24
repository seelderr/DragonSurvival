package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/models/projectiles/StormBreathEffectModel.java
import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.entity.projectiles.StormBreathEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class StormBreathEffectModel extends AnimatedGeoModel<StormBreathEntity>
{
	private ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/storms_breath.png");
	
=======
import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.StormBreathEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class StormBreathEffectModel extends AnimatedGeoModel<StormBreathEntity>{
	private ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/storms_breath.png");

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/models/projectiles/StormBreathEffectModel.java
	@Override
	public ResourceLocation getModelLocation(StormBreathEntity dragonEntity){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/storms_breath.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(StormBreathEntity dragonEntity){
		return currentTexture;
	}

	public void setCurrentTexture(ResourceLocation currentTexture){
		this.currentTexture = currentTexture;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(StormBreathEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/storms_breath.animations.json");
	}
}