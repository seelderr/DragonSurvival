package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin( EntityRenderDispatcher.class )
public class MixinEntityRenderDispatcher{
	@Shadow
	@Final
	private static RenderType SHADOW_RENDER_TYPE;

	// TODO: Look into if this usage of pose stack is actually safe or not
	@Inject(at = @At("HEAD"), method = "renderShadow", cancellable = true)
	private static void renderShadow(final PoseStack poseStack, final MultiBufferSource buffer, final Entity entity, float weight, float partialTick, final LevelReader level, float size, final CallbackInfo callback) {
		if(!DragonUtils.isDragon(entity)){
			return;
		}
		if(callback.isCancelled()){
			return;
		}

		float f = size;
		if(entity instanceof Mob mobentity){
			if(mobentity.isBaby()){
				f = size * 0.5F;
			}
		}

		Vector3f lookVector = Functions.getDragonCameraOffset(entity);

		poseStack.pushPose();
		poseStack.translate(-lookVector.x(), -lookVector.y(), -lookVector.z());

		double d2 = Mth.lerp(partialTick, entity.xOld, entity.getX());
		double d0 = Mth.lerp(partialTick, entity.yOld, entity.getY());
		double d1 = Mth.lerp(partialTick, entity.zOld, entity.getZ());
		int i = Mth.floor(d2 - (double)f);
		int j = Mth.floor(d2 + (double)f);
		int k = Mth.floor(d0 - (double)f);
		int l = Mth.floor(d0);
		int i1 = Mth.floor(d1 - (double)f);
		int j1 = Mth.floor(d1 + (double)f);
		PoseStack.Pose $entry = poseStack.last();
		VertexConsumer ivertexbuilder = buffer.getBuffer(SHADOW_RENDER_TYPE);
		for (BlockPos blockpos : BlockPos.betweenClosed(new BlockPos(i, k, i1), new BlockPos(j, l, j1))) {
			ChunkAccess chunkaccess = level.getChunk(blockpos);
			renderBlockShadow($entry, ivertexbuilder, chunkaccess, level, blockpos, d2, d0, d1, f, weight);
		}
		poseStack.popPose();

		callback.cancel();
	}

	@Shadow
	private static void renderBlockShadow(PoseStack.Pose pPose, VertexConsumer pVertexConsumer, ChunkAccess pChunk, LevelReader pLevel, BlockPos pPos, double pX, double pY, double pZ, float pSize, float p_277496_){}
}