package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.core.processor.IBone;

@Mixin( WorldRenderer.class )
public class MixinWorldRenderer{
	@Shadow
	@Final
	private RenderTypeBuffers renderBuffers;

	@Inject( method = "renderLevel", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;checkPoseStack(Lcom/mojang/blaze3d/matrix/MatrixStack;)V", ordinal = 0 ) )
	public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, net.minecraft.client.renderer.ActiveRenderInfo camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info){
		if(camera.isDetached()){
			return;
		}
		if(!ConfigHandler.CLIENT.renderInFirstPerson.get()){
			return;
		}
		if(!DragonUtils.isDragon(camera.getEntity())){
			return;
		}

		Vector3d vec3d = camera.getPosition();
		double d = vec3d.x();
		double e = vec3d.y();
		double f = vec3d.z();

		final IBone neckandHead = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("Neck");
		final IBone armorNeck = ClientDragonRender.dragonArmorModel.getAnimationProcessor().getBone("Neck");

		if(neckandHead != null){
			neckandHead.setHidden(true);
		}
		if(armorNeck != null){
			armorNeck.setHidden(true);
		}

		EntityRendererManager entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
		boolean shouldRender = entityrenderermanager.shouldRenderHitBoxes();

		IRenderTypeBuffer.Impl immediate = this.renderBuffers.bufferSource();
		entityrenderermanager.setRenderHitBoxes(false);
		this.renderEntity(camera.getEntity(), d, e, f, tickDelta, matrices, immediate);
		entityrenderermanager.setRenderHitBoxes(shouldRender);
		if(neckandHead != null){
			neckandHead.setHidden(false);
		}
		if(armorNeck != null){
			armorNeck.setHidden(false);
		}
	}

	@Shadow
	private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, IRenderTypeBuffer vertexConsumers){

	}
}