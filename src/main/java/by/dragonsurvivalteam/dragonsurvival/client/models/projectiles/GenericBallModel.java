package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericBallEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericBallModel extends GeoModel<GenericBallEntity> {
    @Override
    public ResourceLocation getModelResource(GenericBallEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(animatable.getGeoLocation().getNamespace(), "geo/projectiles/" + animatable.getGeoLocation().getPath() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GenericBallEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(animatable.getTextureLocation().getNamespace(), "textures/entity/projectiles/" + animatable.getTextureLocation().getPath() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GenericBallEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(animatable.getAnimLocation().getNamespace(), "animations/projectiles/" + animatable.getAnimLocation().getPath() + ".animation.json");
    }
}
