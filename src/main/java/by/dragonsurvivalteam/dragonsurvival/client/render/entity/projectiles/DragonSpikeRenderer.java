package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.DragonSpikeEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;


public class DragonSpikeRenderer extends ArrowRenderer<DragonSpikeEntity> {
    public DragonSpikeRenderer(EntityRendererProvider.Context p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Override
    public ResourceLocation getTextureLocation(DragonSpikeEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/dragon_spike_" + entity.getArrow_level() + ".png");
    }
}