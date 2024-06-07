package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
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
	public void render(float pPartialTick, long pNanoTime, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pProjectionMatrix, Matrix4f pFrustrumMatrix, CallbackInfo ci, @Local PoseStack poseStack){
		if(pCamera.isDetached()){
			return;
		}
		if(!ClientDragonRender.renderInFirstPerson){
			return;
		}
		if(!DragonStateProvider.isDragon(pCamera.getEntity())){
			return;
		}

		Vec3 vec3d = pCamera.getPosition();
		double d = vec3d.x();
		double e = vec3d.y();
		double f = vec3d.z();

		final GeoBone neckandHead = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("Neck");
		final GeoBone armorNeck = ClientDragonRender.dragonArmorModel.getAnimationProcessor().getBone("Neck");

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
		renderEntity(pCamera.getEntity(), d, e, f, pPartialTick, poseStack, immediate);
		entityrenderermanager.setRenderHitBoxes(shouldRender);
		if(neckandHead != null){
			neckandHead.setHidden(false);
		}
		if(armorNeck != null){
			armorNeck.setHidden(false);
		}
	}
}