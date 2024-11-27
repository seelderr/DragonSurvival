package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class GenericArrowRenderer extends ArrowRenderer<GenericArrowEntity> {
    public GenericArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(GenericArrowEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/projectiles/"+entity.getResourceLocation().getPath()+".png");
    }
}
