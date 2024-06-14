package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.StormBreathEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT) // TODO :: Unused?
public class StormBreathRender extends GeoEntityRenderer<StormBreathEntity> {
	public StormBreathRender(final EntityRendererProvider.Context context, final GeoModel<StormBreathEntity> model) {
		super(context, model);
	}
}