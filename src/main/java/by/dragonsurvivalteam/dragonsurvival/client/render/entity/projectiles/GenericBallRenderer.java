package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.GenericBallEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class GenericBallRenderer extends GeoEntityRenderer<GenericBallEntity> {
    // TODO: Make this dynamically work correctly

    public GenericBallRenderer(EntityRendererProvider.Context renderManager, GeoModel<GenericBallEntity> model) {
        super(renderManager, model);
    }

    @Override
    protected int getBlockLightLevel(@NotNull final GenericBallEntity entity, @NotNull final BlockPos position) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull final GenericBallEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/lightning_texture.png");
        //return ResourceLocation.fromNamespaceAndPath(DragonSurvival.MODID, "textures/entity/" + animatable.getName() + ".png");
    }
}
