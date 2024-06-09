package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Stack;

@Mixin( InventoryScreen.class )
public abstract class MixinInventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener{
	public MixinInventoryScreen(InventoryMenu p_98701_, Inventory p_98702_, Component p_98703_){
		super(p_98701_, p_98702_, p_98703_);
	}

	@Unique
	private static float dragon_Survival$storedXAngle = 0;
	@Unique
	private static float dragon_Survival$storedYAngle = 0;

	// This is to angle the dragon entity (including its head) to correctly follow the angle specified when rendering.
	@Redirect(method = "renderEntityInInventory", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;runAsFancy(Ljava/lang/Runnable;)V"))
	private static void dragonScreenEntityRender(final Runnable runnable){
		LocalPlayer player = Minecraft.getInstance().player;
		DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);

		if (handler.isDragon()) {
			double bodyYaw = handler.getMovementData().bodyYaw;
			double headYaw = handler.getMovementData().headYaw;
			double headPitch = handler.getMovementData().headPitch;

			double lastBodyYaw = handler.getMovementData().bodyYawLastTick;
			double lastHeadYaw = handler.getMovementData().headYawLastTick;
			double lastHeadPitch = handler.getMovementData().headPitchLastTick;

			handler.getMovementData().bodyYaw = player.yBodyRot;
			handler.getMovementData().headYaw = -Math.toDegrees(dragon_Survival$storedXAngle);
			handler.getMovementData().headPitch = -Math.toDegrees(dragon_Survival$storedYAngle);

			handler.getMovementData().bodyYawLastTick = player.yBodyRot;
			handler.getMovementData().headYawLastTick = -Math.toDegrees(dragon_Survival$storedXAngle);
			handler.getMovementData().headPitchLastTick = -Math.toDegrees(dragon_Survival$storedYAngle);

			RenderSystem.runAsFancy(runnable);

			dragon_Survival$storedXAngle = 0;
			dragon_Survival$storedYAngle = 0;

			handler.getMovementData().bodyYaw = bodyYaw;
			handler.getMovementData().headYaw = headYaw;
			handler.getMovementData().headPitch = headPitch;

			handler.getMovementData().bodyYawLastTick = lastBodyYaw;
			handler.getMovementData().headYawLastTick = lastHeadYaw;
			handler.getMovementData().headPitchLastTick = lastHeadPitch;
		} else {
			RenderSystem.runAsFancy(runnable);
		}
	}

	// If we are a dragon, we don't want to angle the entire entity when rendering it with a follows mouse command (like vanilla does).
	// Instead, we angle just the dragon's head to follow the given angle. So we modify the angles to be zero if we are a dragon and capture them to use them later.
	@ModifyArgs(method = "renderEntityInInventoryFollowsMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventoryFollowsAngle(Lnet/minecraft/client/gui/GuiGraphics;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V"))
	private static void cancelEntityAnglingForDragons(Args args){
		if(DragonStateProvider.isDragon(args.get(9))) {
			dragon_Survival$storedXAngle = args.get(7);
			dragon_Survival$storedYAngle = args.get(8);
			args.set(7, 0.f);
			args.set(8, 0.f);
		}
	}

	@ModifyArgs(method = "renderEntityInInventory", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
	private static void dragonScreenEntityRescaler(Args args) {
		LocalPlayer player = Minecraft.getInstance().player;
		DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);

		if (handler.isDragon()) {
			double size = handler.getSize();
			if(size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE)
			{
				// Scale the matrix back to the MAX_GROWTH_SIZE to prevent the entity from clipping in the inventory panel
				float scale = (float)(ServerConfig.DEFAULT_MAX_GROWTH_SIZE / size);
				args.setAll(scale, scale, scale);
			}
		}
	}
}
