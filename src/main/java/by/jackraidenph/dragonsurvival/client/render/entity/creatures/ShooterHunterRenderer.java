package by.jackraidenph.dragonsurvival.client.render.entity.creatures;

import by.jackraidenph.dragonsurvival.client.models.HunterModel;
import by.jackraidenph.dragonsurvival.common.entity.creatures.ShooterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;


public class ShooterHunterRenderer extends MobRenderer<ShooterEntity, HunterModel<ShooterEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("dragonsurvival", "textures/dragon_hunter.png");

    public ShooterHunterRenderer(EntityRendererProvider.Context rendererManager) {
        super(rendererManager, new HunterModel(rendererManager.bakeLayer(ModelLayers.EVOKER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, rendererManager.getModelSet()));
    }

    public ResourceLocation getTextureLocation(ShooterEntity p_110775_1_) {
        return TEXTURE;
    }

    protected void scale(ShooterEntity shooter, PoseStack PoseStack , float p_225620_3_) {
        float f = 0.9375F;
        PoseStack .scale(f, f, f);
    }
}
