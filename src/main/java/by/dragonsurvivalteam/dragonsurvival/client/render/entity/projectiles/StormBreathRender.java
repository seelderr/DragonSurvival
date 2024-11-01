package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.StormBreathEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// FIXME :: unused
public class StormBreathRender extends GeoEntityRenderer<StormBreathEntity> {
    public StormBreathRender(final EntityRendererProvider.Context context, final GeoModel<StormBreathEntity> model) {
        super(context, model);
    }
}