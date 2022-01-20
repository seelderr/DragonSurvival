package by.jackraidenph.dragonsurvival.client.render.entity.projectiles;

import by.jackraidenph.dragonsurvival.common.entity.projectiles.StormBreathEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

@OnlyIn( Dist.CLIENT)
public class StormBreathRender extends GeoProjectilesRenderer<StormBreathEntity>
{
	public StormBreathRender(EntityRendererProvider.Context renderManager, AnimatedGeoModel<StormBreathEntity> modelProvider)
	{
		super(renderManager, modelProvider);
	}
}
