package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IWorldReader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( EntityRendererManager.class )
public class MixinEntityRenderManager{
	@Shadow
	@Final
	private static RenderType SHADOW_RENDER_TYPE;

	@Inject( at = @At( "HEAD" ), method = "renderShadow", cancellable = true )
	private static void renderShadow(MatrixStack p_229096_0_, IRenderTypeBuffer p_229096_1_, Entity p_229096_2_, float p_229096_3_, float p_229096_4_, IWorldReader p_229096_5_, float p_229096_6_, CallbackInfo ci){
		if(!DragonUtils.isDragon(p_229096_2_)){
			return;
		}
		if(ci.isCancelled()){
			return;
		}

		float f = p_229096_6_;
		if(p_229096_2_ instanceof MobEntity){
			MobEntity mobentity = (MobEntity)p_229096_2_;
			if(mobentity.isBaby()){
				f = p_229096_6_ * 0.5F;
			}
		}

		Vector3f lookVector = DragonUtils.getCameraOffset(p_229096_2_);

		p_229096_0_.pushPose();
		p_229096_0_.translate(-lookVector.x(), -lookVector.y(), -lookVector.z());

		double d2 = MathHelper.lerp(p_229096_4_, p_229096_2_.xOld, p_229096_2_.getX());
		double d0 = MathHelper.lerp(p_229096_4_, p_229096_2_.yOld, p_229096_2_.getY());
		double d1 = MathHelper.lerp(p_229096_4_, p_229096_2_.zOld, p_229096_2_.getZ());
		int i = MathHelper.floor(d2 - (double)f);
		int j = MathHelper.floor(d2 + (double)f);
		int k = MathHelper.floor(d0 - (double)f);
		int l = MathHelper.floor(d0);
		int i1 = MathHelper.floor(d1 - (double)f);
		int j1 = MathHelper.floor(d1 + (double)f);
		MatrixStack.Entry matrixstack$entry = p_229096_0_.last();
		IVertexBuilder ivertexbuilder = p_229096_1_.getBuffer(SHADOW_RENDER_TYPE);
		for(BlockPos blockpos : BlockPos.betweenClosed(new BlockPos(i, k, i1), new BlockPos(j, l, j1))){
			renderBlockShadow(matrixstack$entry, ivertexbuilder, p_229096_5_, blockpos, d2, d0, d1, f, p_229096_3_);
		}
		p_229096_0_.popPose();

		ci.cancel();
	}

	@Shadow
	private static void renderBlockShadow(MatrixStack.Entry p_229092_0_, IVertexBuilder p_229092_1_, IWorldReader p_229092_2_, BlockPos p_229092_3_, double p_229092_4_, double p_229092_6_, double p_229092_8_, float p_229092_10_, float p_229092_11_){
	}

	@Inject( at = @At( "HEAD" ), method = "renderHitbox", cancellable = true )
	private void renderHitbox(MatrixStack pMatrixStack, IVertexBuilder pBuffer, Entity pEntity, float pPartialTicks, CallbackInfo callbackInfo){
		if(pEntity instanceof DragonHitBox){
			callbackInfo.cancel();

			DragonHitBox hitBox = (DragonHitBox)pEntity;
			if(hitBox.player == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()){
				return;
			}

			this.renderBox(pMatrixStack, pBuffer, pEntity, 1.0F, 1.0F, 1.0F);

			for(net.minecraftforge.entity.PartEntity<?> enderdragonpartentity : hitBox.getParts()){
				pMatrixStack.pushPose();
				pMatrixStack.translate(enderdragonpartentity.getX() - pEntity.getX(), enderdragonpartentity.getY() - pEntity.getY(), enderdragonpartentity.getZ() - pEntity.getZ());
				this.renderBox(pMatrixStack, pBuffer, enderdragonpartentity, 0.25F, 1.0F, 0.0F);
				pMatrixStack.popPose();
			}
		}
	}

	@Shadow
	private void renderBox(MatrixStack p_229094_1_, IVertexBuilder p_229094_2_, Entity p_229094_3_, float p_229094_4_, float p_229094_5_, float p_229094_6_){}
}