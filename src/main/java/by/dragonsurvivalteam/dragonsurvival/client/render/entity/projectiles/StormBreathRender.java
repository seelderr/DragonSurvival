package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/render/entity/projectiles/StormBreathRender.java
import by.jackraidenph.dragonsurvival.common.entity.projectiles.StormBreathEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
=======
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.StormBreathEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/render/entity/projectiles/StormBreathRender.java
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/render/entity/projectiles/StormBreathRender.java
@OnlyIn( Dist.CLIENT)
public class StormBreathRender extends GeoProjectilesRenderer<StormBreathEntity>
{
	public StormBreathRender(EntityRendererProvider.Context renderManager, AnimatedGeoModel<StormBreathEntity> modelProvider)
	{
=======
@OnlyIn( Dist.CLIENT )
public class StormBreathRender extends GeoProjectilesRenderer<StormBreathEntity>{
	public StormBreathRender(EntityRendererManager renderManager, AnimatedGeoModel<StormBreathEntity> modelProvider){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/render/entity/projectiles/StormBreathRender.java
		super(renderManager, modelProvider);
	}
}