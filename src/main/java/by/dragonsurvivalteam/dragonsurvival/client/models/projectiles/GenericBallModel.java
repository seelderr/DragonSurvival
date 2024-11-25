package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericBallEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericBallModel extends GeoModel<GenericBallEntity> {
    @Override
    public ResourceLocation getModelResource(GenericBallEntity animatable) {
        return animatable.modelResourceLocation;
    }

    @Override
    public ResourceLocation getTextureResource(GenericBallEntity animatable) {
        return animatable.textureResourceLocation;
    }

    @Override
    public ResourceLocation getAnimationResource(GenericBallEntity animatable) {
        return animatable.animationResourceLocation;
    }
}
