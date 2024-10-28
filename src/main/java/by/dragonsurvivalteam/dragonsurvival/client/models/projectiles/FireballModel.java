package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;


import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.FireBallEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class FireballModel extends GeoModel<FireBallEntity> {
    private final ResourceLocation currentTexture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/fireball_texture.png");

    @Override
    public ResourceLocation getModelResource(FireBallEntity dragonEntity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "geo/dragon_fireball.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireBallEntity dragonEntity) {
        return currentTexture;
    }

    @Override
    public ResourceLocation getAnimationResource(FireBallEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "animations/fireball.animation.json");
    }
}