package by.jackraidenph.dragonsurvival.mixins;

import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.core.processor.IBone;

@Mixin( LevelRenderer.class )
public class MixinWorldRenderer
{
	@Shadow
	@Final
	private RenderBuffers renderBuffers;
	
	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 0))	public void render(PoseStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera,
			GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
		if(camera.isDetached()) return;
		if(!ConfigHandler.CLIENT.renderInFirstPerson.get()) return;
		if(!DragonStateProvider.isDragon(camera.getEntity())) return;
		
		Vec3 vec3d = camera.getPosition();
		double d = vec3d.x();
		double e = vec3d.y();
		double f = vec3d.z();
		
		final IBone neckandHead = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("Neck");
		final IBone armorNeck = ClientDragonRender.dragonArmorModel.getAnimationProcessor().getBone("Neck");
		
		if (neckandHead != null) neckandHead.setHidden(true);
		if (armorNeck != null) armorNeck.setHidden(true);
		
		EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
		boolean shouldRender = entityrenderermanager.shouldRenderHitBoxes();
		
		MultiBufferSource immediate = this.renderBuffers.bufferSource();
		entityrenderermanager.setRenderHitBoxes(false);
		this.renderEntity(camera.getEntity(), d, e, f, tickDelta, matrices, immediate);
		entityrenderermanager.setRenderHitBoxes(shouldRender);
		if (neckandHead != null) neckandHead.setHidden(false);
		if (armorNeck != null) armorNeck.setHidden(false);
	}
	
	@Shadow
	private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta,
			PoseStack matrices, MultiBufferSource vertexConsumers) {
		
	}
}
