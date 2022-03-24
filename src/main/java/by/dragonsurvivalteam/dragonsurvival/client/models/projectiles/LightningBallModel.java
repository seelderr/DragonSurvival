package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/models/projectiles/LightningBallModel.java
import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.entity.projectiles.BallLightningEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LightningBallModel extends AnimatedGeoModel<BallLightningEntity>
{
	private ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/lightning_texture.png");
	
=======
import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BallLightningEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LightningBallModel extends AnimatedGeoModel<BallLightningEntity>{
	private ResourceLocation currentTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/lightning_texture.png");

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/models/projectiles/LightningBallModel.java
	@Override
	public ResourceLocation getModelLocation(BallLightningEntity dragonEntity){
		return new ResourceLocation(DragonSurvivalMod.MODID, "geo/dragon_lightning_ball.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(BallLightningEntity dragonEntity){
		return currentTexture;
	}

	public void setCurrentTexture(ResourceLocation currentTexture){
		this.currentTexture = currentTexture;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(BallLightningEntity animatable){
		return new ResourceLocation(DragonSurvivalMod.MODID, "animations/lightning_ball.animation.json");
	}
}