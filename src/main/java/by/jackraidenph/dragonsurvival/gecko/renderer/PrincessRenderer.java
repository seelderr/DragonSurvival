package by.jackraidenph.dragonsurvival.gecko.renderer;

import by.jackraidenph.dragonsurvival.gecko.entity.Princess;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class PrincessRenderer extends GeoEntityRenderer<Princess> {
    public PrincessRenderer(EntityRendererManager renderManager, AnimatedGeoModel<Princess> modelProvider) {
        super(renderManager, modelProvider);
    }
}
