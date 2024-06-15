package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.client.models.HunterModel;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.SquireEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class SquireHunterRenderer extends MobRenderer<SquireEntity, HunterModel<SquireEntity>>{
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/dragon_squire.png");

	public SquireHunterRenderer(EntityRendererProvider.Context rendererManager){
		super(rendererManager, new HunterModel(rendererManager.bakeLayer(ModelLayers.EVOKER)), 0.5F);
		addLayer(new CustomHeadLayer<>(this, rendererManager.getModelSet(), rendererManager.getItemInHandRenderer()));
	}

	@Override
	public ResourceLocation getTextureLocation(SquireEntity squireHunter){
		return TEXTURE;
	}

	@Override
	protected void scale(SquireEntity squire, PoseStack matrixStack, float p_225620_3_){
		float f = 0.9375F;
		matrixStack.scale(f, f, f);
	}
}