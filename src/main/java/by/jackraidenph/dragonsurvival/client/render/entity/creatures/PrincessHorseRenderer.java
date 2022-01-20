package by.jackraidenph.dragonsurvival.client.render.entity.creatures;

import by.jackraidenph.dragonsurvival.common.entity.creatures.PrincesHorseEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class PrincessHorseRenderer extends GeoEntityRenderer<PrincesHorseEntity> {
    public PrincessHorseRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<PrincesHorseEntity> modelProvider) {
        super(renderManager, modelProvider);
    }
}
