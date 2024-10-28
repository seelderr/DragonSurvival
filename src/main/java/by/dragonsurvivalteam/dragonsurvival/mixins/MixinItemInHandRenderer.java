package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class MixinItemInHandRenderer {

	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyExpressionValue(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;"))
	private UseAnim dragonRenderArmWithItem(UseAnim original, AbstractClientPlayer pPlayer, float pPartialTicks, float pPitch, InteractionHand pHand, float pSwingProgress, ItemStack pStack, float pEquippedProgress, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight) {
		if (DragonStateProvider.isDragon(pPlayer)) {
			DragonStateHandler handler = DragonStateProvider.getData(pPlayer);
			return DragonFoodHandler.isEdible(pStack, handler.getType()) ? UseAnim.EAT : original;
		}

		return original;
	}

	@ModifyExpressionValue(method = "applyEatTransform", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration(Lnet/minecraft/world/entity/LivingEntity;)I"))
	private int dragonUseDuration(int original, @Local(argsOnly = true) ItemStack stack) {
		if (DragonStateProvider.isDragon(minecraft.player)) {
			return DragonFoodHandler.getUseDuration(stack, minecraft.player);
		}

		return original;
	}

	@Inject(at = @At(value = "HEAD"), method = "renderPlayerArm", cancellable = true)
	private void hideArmsForDragon(PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, float pEquippedProgress, float pSwingProgress, HumanoidArm pSide, CallbackInfo ci) {
		if (DragonStateProvider.isDragon(minecraft.player)) {
			ci.cancel();
		}
	}

	@ModifyExpressionValue(method = "renderTwoHandedMap", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isInvisible()Z"))
	private boolean hideArmsForDragonTwoHandedMap(boolean original) {
		return DragonStateProvider.isDragon(minecraft.player) || original;
	}
}