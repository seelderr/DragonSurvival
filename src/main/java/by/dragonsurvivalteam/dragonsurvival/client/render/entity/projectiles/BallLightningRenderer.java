//package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;
//
//import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BallLightningEntity;
//import net.minecraft.client.renderer.entity.EntityRendererProvider;
//import net.minecraft.core.BlockPos;
//import net.minecraft.resources.ResourceLocation;
//import org.jetbrains.annotations.NotNull;
//import software.bernie.geckolib.model.GeoModel;
//import software.bernie.geckolib.renderer.GeoEntityRenderer;
//
//import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;
//
//public class BallLightningRenderer extends GeoEntityRenderer<BallLightningEntity> {
//    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/lightning_texture.png");
//
//    public BallLightningRenderer(EntityRendererProvider.Context renderManager, GeoModel<BallLightningEntity> modelProvider) {
//        super(renderManager, modelProvider);
//    }
//
//    @Override
//    protected int getBlockLightLevel(@NotNull final BallLightningEntity entity, @NotNull final BlockPos position) {
//        return 15;
//    }
//
//    @Override
//    public @NotNull ResourceLocation getTextureLocation(@NotNull final BallLightningEntity entity) {
//        return TEXTURE_LOCATION;
//    }
//}