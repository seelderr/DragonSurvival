package by.jackraidenph.dragonsurvival.gecko.renderer.Dragon;

import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.gecko.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.ClientDragonRender;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {
	public ResourceLocation glowTexture = null;
	
	public DragonRenderer(EntityRendererManager renderManager, AnimatedGeoModel<DragonEntity> modelProvider) {
        super(renderManager, modelProvider);
		this.addLayer(new DragonGlowLayerRenderer(this));
		this.addLayer(new DragonArmorRenderLayer(this));
		this.addLayer(new ClawsAndTeethRenderLayer(this));
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
