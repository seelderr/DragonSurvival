package by.jackraidenph.dragonsurvival.mixins;

import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( EntityRenderDispatcher.class )
public class MixinEntityRenderManager
{
	@Shadow
	@Final
	private static RenderType SHADOW_RENDER_TYPE;
	
	@Inject( at = @At("HEAD"), method = "renderShadow", cancellable = true)
	private static void renderShadow(PoseStack p_229096_0_, MultiBufferSource p_229096_1_, Entity p_229096_2_, float p_229096_3_, float p_229096_4_, LevelReader p_229096_5_, float p_229096_6_, CallbackInfo ci) {
		if(!DragonStateProvider.isDragon(p_229096_2_)) return;
		if(ci.isCancelled()) return;
		
		float f = p_229096_6_;
		if (p_229096_2_ instanceof Mob) {
			Mob mobentity = (Mob)p_229096_2_;
			if (mobentity.isBaby()) {
				f = p_229096_6_ * 0.5F;
			}
		}
		
		Vector3f lookVector = DragonStateProvider.getCameraOffset(p_229096_2_);
		
		p_229096_0_.pushPose();
		p_229096_0_.translate(-lookVector.x(), -lookVector.y(), -lookVector.z());
		
		double d2 = Mth.lerp((double)p_229096_4_, p_229096_2_.xOld, p_229096_2_.getX());
		double d0 = Mth.lerp((double)p_229096_4_, p_229096_2_.yOld, p_229096_2_.getY());
		double d1 = Mth.lerp((double)p_229096_4_, p_229096_2_.zOld, p_229096_2_.getZ());
		int i = Mth.floor(d2 - (double)f);
		int j = Mth.floor(d2 + (double)f);
		int k = Mth.floor(d0 - (double)f);
		int l = Mth.floor(d0);
		int i1 = Mth.floor(d1 - (double)f);
		int j1 = Mth.floor(d1 + (double)f);
		PoseStack.Pose $entry = p_229096_0_.last();
		VertexConsumer ivertexbuilder = p_229096_1_.getBuffer(SHADOW_RENDER_TYPE);
		for(BlockPos blockpos : BlockPos.betweenClosed(new BlockPos(i, k, i1), new BlockPos(j, l, j1))) {
			renderBlockShadow($entry, ivertexbuilder, p_229096_5_, blockpos, d2, d0, d1, f, p_229096_3_);
		}
		p_229096_0_.popPose();
		
		ci.cancel();
	}
	
	@Inject( at = @At("HEAD"), method = "renderHitbox", cancellable = true)
	private static void renderHitbox(PoseStack pPoseStack , VertexConsumer pBuffer, Entity pEntity, float pPartialTicks, CallbackInfo callbackInfo) {
		if (pEntity instanceof DragonHitBox) {
			callbackInfo.cancel();
			
			DragonHitBox hitBox = (DragonHitBox)pEntity;
			if(hitBox.player == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()){
				return;
			}
			
			for(net.minecraftforge.entity.PartEntity<?> enderdragonpartentity : hitBox.getParts()) {
				pPoseStack .pushPose();
				pPoseStack .translate(enderdragonpartentity.getX() - pEntity.getX(), enderdragonpartentity.getY() - pEntity.getY(), enderdragonpartentity.getZ() - pEntity.getZ());
				LevelRenderer.renderLineBox(pPoseStack, pBuffer, enderdragonpartentity.getBoundingBox().move(-enderdragonpartentity.getX(), -enderdragonpartentity.getY(), -enderdragonpartentity.getZ()), 0.25F, 1.0F, 0.0F, 1.0F);
				pPoseStack .popPose();
			}
		}
	}
	
	@Shadow
	private static void renderBlockShadow(PoseStack.Pose p_229092_0_, VertexConsumer p_229092_1_, LevelReader p_229092_2_, BlockPos p_229092_3_, double p_229092_4_, double p_229092_6_, double p_229092_8_, float p_229092_10_, float p_229092_11_)
	{
	}
}
