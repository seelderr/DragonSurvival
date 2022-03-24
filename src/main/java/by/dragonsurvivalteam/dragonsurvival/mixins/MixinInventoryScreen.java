package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin( InventoryScreen.class )
public abstract class MixinInventoryScreen extends DisplayEffectsScreen<PlayerContainer> implements IRecipeShownListener{
	public MixinInventoryScreen(PlayerContainer p_i51091_1_, PlayerInventory p_i51091_2_, ITextComponent p_i51091_3_){
		super(p_i51091_1_, p_i51091_2_, p_i51091_3_);
	}

	@Redirect( method = "Lnet/minecraft/client/gui/screen/inventory/InventoryScreen;renderEntityInInventory(IIIFFLnet/minecraft/entity/LivingEntity;)V", at = @At( value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;runAsFancy(Ljava/lang/Runnable;)V" ) )
	private static void dragonScreenEntityRender(Runnable p_runAsFancy_0_){
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if(DragonStateProvider.getCap(player).isPresent() && DragonUtils.getHandler(player).isDragon()){
			DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
				double bodyYaw = dragonStateHandler.getMovementData().bodyYaw;
				double headYaw = dragonStateHandler.getMovementData().headYaw;
				double headPitch = dragonStateHandler.getMovementData().headPitch;

				double lastBodyYaw = dragonStateHandler.getMovementData().bodyYawLastTick;
				double lastHeadYaw = dragonStateHandler.getMovementData().headYawLastTick;
				double lastHeadPitch = dragonStateHandler.getMovementData().headPitchLastTick;

				dragonStateHandler.getMovementData().bodyYaw = player.yBodyRot;
				dragonStateHandler.getMovementData().headYaw = 0;
				dragonStateHandler.getMovementData().headPitch = 0;

				dragonStateHandler.getMovementData().bodyYawLastTick = player.yBodyRot;
				dragonStateHandler.getMovementData().headYawLastTick = player.yHeadRot;
				dragonStateHandler.getMovementData().headPitchLastTick = player.xRot;

				RenderSystem.runAsFancy(p_runAsFancy_0_);

				dragonStateHandler.getMovementData().bodyYaw = bodyYaw;
				dragonStateHandler.getMovementData().headYaw = headYaw;
				dragonStateHandler.getMovementData().headPitch = headPitch;

				dragonStateHandler.getMovementData().bodyYawLastTick = lastBodyYaw;
				dragonStateHandler.getMovementData().headYawLastTick = lastHeadYaw;
				dragonStateHandler.getMovementData().headPitchLastTick = lastHeadPitch;
			});
		}else{
			RenderSystem.runAsFancy(p_runAsFancy_0_);
		}
	}
}