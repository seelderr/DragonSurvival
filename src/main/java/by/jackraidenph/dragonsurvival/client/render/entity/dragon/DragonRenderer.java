package by.jackraidenph.dragonsurvival.client.render.entity.dragon;

import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;
import java.awt.*;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {
	public ResourceLocation glowTexture = null;
	
	public DragonRenderer(EntityRendererManager renderManager, AnimatedGeoModel<DragonEntity> modelProvider) {
        super(renderManager, modelProvider);
		this.addLayer(new DragonGlowLayerRenderer(this));
		this.addLayer(new ClawsAndTeethRenderLayer(this));
	}
	
	public Color renderColor = new Color(255, 255, 255);
	
	@Override
	public Color getRenderColor(DragonEntity animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn)
	{
		return renderColor;
	}
	
	@Override
	public void render(DragonEntity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		PlayerEntity player = entity.getPlayer();
		DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
		
		boolean hasWings = true;
		
		if(handler != null){
			hasWings = handler.hasWings();
		}
		
		final IBone leftwing = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("WingLeft");
		final IBone rightWing = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("WingRight");
		
		if (leftwing != null) {
			leftwing.setHidden(!hasWings);
		}
		
		if (rightWing != null) {
			rightWing.setHidden(!hasWings);
		}
		
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}
}
