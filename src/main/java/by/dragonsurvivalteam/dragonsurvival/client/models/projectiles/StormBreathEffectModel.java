package by.dragonsurvivalteam.dragonsurvival.client.models.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.StormBreathEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

// TODO :: Unused?
//  Aures: The answer is yes, you can delete it

public class StormBreathEffectModel extends GeoModel<StormBreathEntity> {
    private ResourceLocation currentTexture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/storms_breath.png");

    @Override
    public ResourceLocation getModelResource(final StormBreathEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "geo/storms_breath.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final StormBreathEntity entity) {
        return currentTexture;
    }

    public void setCurrentTexture(final ResourceLocation currentTexture) {
        this.currentTexture = currentTexture;
    }

    @Override
    public ResourceLocation getAnimationResource(final StormBreathEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "animations/storms_breath.animation.json");
    }
}