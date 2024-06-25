package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.GeoBone;


@Mixin( LevelRenderer.class )
public abstract class MixinWorldRenderer{
	@Shadow
	@Final
	private RenderBuffers renderBuffers;

	@Shadow protected abstract void renderEntity(Entity pEntity, double pCamX, double pCamY, double pCamZ, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource);

	@Inject( method = "renderLevel", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 0 ) )
	public void render(DeltaTracker deltaTracker, boolean p_109603_, Camera camera, GameRenderer renderer, LightTexture lightTex, Matrix4f matrix1, Matrix4f matrix2, CallbackInfo ci, @Local PoseStack poseStack){
		if(camera.isDetached()){
			return;
		}
		if(!ClientDragonRenderer.renderInFirstPerson){
			return;
		}
		if(!DragonStateProvider.isDragon(camera.getEntity())){
			return;
		}

		Vec3 vec3d = camera.getPosition();
		double d = vec3d.x();
		double e = vec3d.y();
		double f = vec3d.z();

		final GeoBone neckandHead = ClientDragonRenderer.dragonModel.getAnimationProcessor().getBone("Neck");
		final GeoBone armorNeck = ClientDragonRenderer.dragonArmorModel.getAnimationProcessor().getBone("Neck");

		if(neckandHead != null){
			neckandHead.setHidden(true);
		}
		if(armorNeck != null){
			armorNeck.setHidden(true);
		}

		EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
		boolean shouldRender = entityrenderermanager.shouldRenderHitBoxes();

		MultiBufferSource immediate = renderBuffers.bufferSource();
		entityrenderermanager.setRenderHitBoxes(false);
		renderEntity(camera.getEntity(), d, e, f, deltaTracker.getGameTimeDeltaPartialTick(false), poseStack, immediate);
		entityrenderermanager.setRenderHitBoxes(shouldRender);
		if(neckandHead != null){
			neckandHead.setHidden(false);
		}
		if(armorNeck != null){
			armorNeck.setHidden(false);
		}
	}
}