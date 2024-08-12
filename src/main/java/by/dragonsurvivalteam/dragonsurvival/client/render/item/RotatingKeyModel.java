package by.dragonsurvivalteam.dragonsurvival.client.render.item;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.res;

import by.dragonsurvivalteam.dragonsurvival.common.items.RotatingKeyItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RotatingKeyModel extends GeoModel<RotatingKeyItem> {
    @Override
    public ResourceLocation getModelResource(RotatingKeyItem object) {
        return object.model;
    }

    @Override
    public ResourceLocation getTextureResource(RotatingKeyItem object) {
        return object.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(RotatingKeyItem object) {
        return res("animations/key.animation.json");
    }

}