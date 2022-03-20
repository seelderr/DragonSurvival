package by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.client.models.HunterModel;
import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.ShooterEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;

public class ShooterHunterRenderer extends MobRenderer<ShooterEntity, HunterModel<ShooterEntity>>{
	private static final ResourceLocation TEXTURE = new ResourceLocation("dragonsurvival", "textures/dragon_hunter.png");

	public ShooterHunterRenderer(EntityRendererManager rendererManager){
		super(rendererManager, new HunterModel<>(0.0F, 0.0F, 64, 64), 0.5F);
		addLayer(new HeadLayer<>(this));
		addLayer(new HeldItemLayer<>(this));
	}

	public ResourceLocation getTextureLocation(ShooterEntity p_110775_1_){
		return TEXTURE;
	}

	protected void scale(ShooterEntity shooter, MatrixStack matrixStack, float p_225620_3_){
		float f = 0.9375F;
		matrixStack.scale(f, f, f);
	}
}