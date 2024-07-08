package by.dragonsurvivalteam.dragonsurvival.client.models.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.SpearmanEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SpearmanModel extends GeoModel<SpearmanEntity> {
    @Override
    public ResourceLocation getModelResource(SpearmanEntity object){
        return ResourceLocation.fromNamespaceAndPath(MODID, "geo/hunter_spearman.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpearmanEntity object){
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/hunters/spearman.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpearmanEntity animatable){
        return ResourceLocation.fromNamespaceAndPath(MODID, "animations/hunter_spearman.animation.json");
    }
}
