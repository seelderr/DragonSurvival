package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.StormBreathEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT) // FIXME :: Unused?
public class StormBreathRender extends GeoEntityRenderer<StormBreathEntity> {
	public StormBreathRender(final EntityRendererProvider.Context context, final GeoModel<StormBreathEntity> model) {
		super(context, model);
	}
}