<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinFirstPersonRenderer.java
package by.jackraidenph.dragonsurvival.mixins;

import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.handlers.DragonFoodHandler;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( ItemInHandRenderer.class)
public abstract class MixinFirstPersonRenderer {
=======
package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( FirstPersonRenderer.class )
public abstract class MixinFirstPersonRenderer{
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinFirstPersonRenderer.java

	@Shadow
	@Final
	public Minecraft minecraft;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinFirstPersonRenderer.java
	@Inject(at = @At(value = "HEAD"), method = "applyEatTransform", cancellable = true, expect = 1)
	public void applyDragonEatTransform(PoseStack p_228398_1_, float p_228398_2_, HumanoidArm p_228398_3_, ItemStack p_228398_4_, CallbackInfo ci) {
=======
	@Inject( at = @At( value = "HEAD" ), method = "applyEatTransform", cancellable = true, expect = 1 )
	public void applyDragonEatTransform(MatrixStack p_228398_1_, float p_228398_2_, HandSide p_228398_3_, ItemStack p_228398_4_, CallbackInfo ci){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinFirstPersonRenderer.java
		DragonStateProvider.getCap(minecraft.player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				float f = (float)this.minecraft.player.getUseItemRemainingTicks() - p_228398_2_ + 1.0F;
				float f1 = f / (float)DragonFoodHandler.getUseDuration(p_228398_4_, dragonStateHandler.getType());
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinFirstPersonRenderer.java
				if (f1 < 0.8F) {
					float f2 = Mth.abs(Mth.cos(f / 4.0F * (float)Math.PI) * 0.1F);
					p_228398_1_.translate(0.0D, (double)f2, 0.0D);
				}
			
				float f3 = 1.0F - (float)Math.pow((double)f1, 27.0D);
				int i = p_228398_3_ == HumanoidArm.RIGHT ? 1 : -1;
				p_228398_1_.translate((double)(f3 * 0.6F * (float)i), (double)(f3 * -0.5F), (double)(f3 * 0.0F));
=======
				if(f1 < 0.8F){
					float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float)Math.PI) * 0.1F);
					p_228398_1_.translate(0.0D, f2, 0.0D);
				}

				float f3 = 1.0F - (float)Math.pow(f1, 27.0D);
				int i = p_228398_3_ == HandSide.RIGHT ? 1 : -1;
				p_228398_1_.translate(f3 * 0.6F * (float)i, f3 * -0.5F, f3 * 0.0F);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinFirstPersonRenderer.java
				p_228398_1_.mulPose(Vector3f.YP.rotationDegrees((float)i * f3 * 90.0F));
				p_228398_1_.mulPose(Vector3f.XP.rotationDegrees(f3 * 10.0F));
				p_228398_1_.mulPose(Vector3f.ZP.rotationDegrees((float)i * f3 * 30.0F));

				ci.cancel();
			}
		});
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinFirstPersonRenderer.java
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FirstPersonRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/matrix/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", ordinal = 2), method = "renderArmWithItem", cancellable = true)
	public void renderDragonArmWithItem(AbstractClientPlayer p_228405_1_, float p_228405_2_, float p_228405_3_, HumanoidArm p_228405_4_, float p_228405_5_, ItemStack p_228405_6_, float p_228405_7_, PoseStack  p_228405_8_, MultiBufferSource p_228405_9_, int p_228405_10_, CallbackInfo ci) {
		DragonStateProvider.getCap(minecraft.player).ifPresent(dragonStateHandler -> {
			if (dragonStateHandler.isDragon() && DragonFoodHandler.isDragonEdible(p_228405_6_.getItem(), dragonStateHandler.getType()))
				this.applyEatTransform(p_228405_8_, p_228405_2_, p_228405_4_ == HumanoidArm.RIGHT ? p_228405_1_.getMainArm() : p_228405_1_.getMainArm().getOpposite(), p_228405_6_);
		});
	}

	@Inject(at = @At(value = "HEAD"), method = "renderPlayerArm", cancellable = true)
	private void renderDragonArm(PoseStack  p_228401_1_, MultiBufferSource p_228401_2_, int p_228401_3_, float p_228401_4_, float p_228401_5_, HumanoidArm p_228401_6_, CallbackInfo ci){
		if (DragonUtils.isDragon(minecraft.player))
=======

	@Inject( at = @At( value = "INVOKE", target = "Lnet/minecraft/client/renderer/FirstPersonRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/util/HandSide;F)V", ordinal = 2 ), method = "renderArmWithItem", cancellable = true )
	public void renderDragonArmWithItem(AbstractClientPlayerEntity p_228405_1_, float p_228405_2_, float p_228405_3_, Hand p_228405_4_, float p_228405_5_, ItemStack p_228405_6_, float p_228405_7_, MatrixStack p_228405_8_, IRenderTypeBuffer p_228405_9_, int p_228405_10_, CallbackInfo ci){
		DragonStateProvider.getCap(minecraft.player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon() && DragonFoodHandler.isDragonEdible(p_228405_6_.getItem(), dragonStateHandler.getType())){
				this.applyEatTransform(p_228405_8_, p_228405_2_, p_228405_4_ == Hand.MAIN_HAND ? p_228405_1_.getMainArm() : p_228405_1_.getMainArm().getOpposite(), p_228405_6_);
			}
		});
	}

	@Shadow
	public void applyEatTransform(MatrixStack p_228405_8_, float p_228405_2_, HandSide handSide, ItemStack p_228405_6_){
		throw new IllegalStateException("Mixin failed to shadow applyEatTransform()");
	}

	@Inject( at = @At( value = "HEAD" ), method = "renderPlayerArm", cancellable = true )
	private void renderDragonArm(MatrixStack p_228401_1_, IRenderTypeBuffer p_228401_2_, int p_228401_3_, float p_228401_4_, float p_228401_5_, HandSide p_228401_6_, CallbackInfo ci){
		if(DragonUtils.isDragon(minecraft.player)){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinFirstPersonRenderer.java
			ci.cancel();
		}
	}

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinFirstPersonRenderer.java
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FirstPersonRenderer;renderMapHand(Lcom/mojang/blaze3d/matrix/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/HumanoidArm;)V"), method = "renderTwoHandedMap")
	private void removeTwoHandsMapForDragon(ItemInHandRenderer firstPersonRenderer, PoseStack  p_228403_1_, MultiBufferSource p_228403_2_, int p_228403_3_, HumanoidArm p_228403_4_){
		if (!DragonUtils.isDragon(minecraft.player))
=======
	@Redirect( at = @At( value = "INVOKE", target = "Lnet/minecraft/client/renderer/FirstPersonRenderer;renderMapHand(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/util/HandSide;)V" ), method = "renderTwoHandedMap" )
	private void removeTwoHandsMapForDragon(FirstPersonRenderer firstPersonRenderer, MatrixStack p_228403_1_, IRenderTypeBuffer p_228403_2_, int p_228403_3_, HandSide p_228403_4_){
		if(!DragonUtils.isDragon(minecraft.player)){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinFirstPersonRenderer.java
			this.renderMapHand(p_228403_1_, p_228403_2_, p_228403_3_, p_228403_4_);
		}
	}

	@Shadow
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinFirstPersonRenderer.java
	public void renderMapHand(PoseStack  p_228403_1_, MultiBufferSource p_228403_2_, int p_228403_3_, HumanoidArm p_228403_4_) {
		throw new IllegalStateException("Mixin failed to shadow renderMapHand()");
	}

	@Shadow
	public void applyEatTransform(PoseStack  p_228405_8_, float p_228405_2_, HumanoidArm handSide, ItemStack p_228405_6_) {
		throw new IllegalStateException("Mixin failed to shadow applyEatTransform()");
	}
	
	
	
}
=======
	public void renderMapHand(MatrixStack p_228403_1_, IRenderTypeBuffer p_228403_2_, int p_228403_3_, HandSide p_228403_4_){
		throw new IllegalStateException("Mixin failed to shadow renderMapHand()");
	}
}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinFirstPersonRenderer.java
