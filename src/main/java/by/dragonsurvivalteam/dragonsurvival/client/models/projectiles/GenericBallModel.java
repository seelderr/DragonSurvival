package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericBallEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericBallModel extends GeoModel<GenericBallEntity> {
    @Override
    public ResourceLocation getModelResource(GenericBallEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(animatable.getResourceLocation().getNamespace(), "geo/projectiles/" + animatable.getResourceLocation().getPath() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GenericBallEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(animatable.getResourceLocation().getNamespace(), "textures/entity/projectiles/" + animatable.getResourceLocation().getPath() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GenericBallEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(animatable.getResourceLocation().getNamespace(), "animations/projectiles/" + animatable.getResourceLocation().getPath() + ".animation.json");
    }
}
