package by.jackraidenph.dragonsurvival.client.render.entity.creatures;

import by.jackraidenph.dragonsurvival.client.models.HunterModel;
import by.jackraidenph.dragonsurvival.common.entity.creatures.SquireEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;


public class SquireHunterRenderer extends MobRenderer<SquireEntity, HunterModel<SquireEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("dragonsurvival", "textures/dragon_squire.png");

    public SquireHunterRenderer(EntityRendererProvider.Context rendererManager) {
        super(rendererManager, new HunterModel(rendererManager.bakeLayer(ModelLayers.EVOKER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, rendererManager.getModelSet()));
    }

    public ResourceLocation getTextureLocation(SquireEntity squireHunter) {
        return TEXTURE;
    }

    protected void scale(SquireEntity squire, PoseStack PoseStack , float p_225620_3_) {
        float f = 0.9375F;
        PoseStack .scale(f, f, f);
    }
}
