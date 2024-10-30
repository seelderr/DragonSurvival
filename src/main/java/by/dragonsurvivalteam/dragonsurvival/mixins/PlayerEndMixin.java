package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Undoes the switch from {@link PlayerStartMixin}
 */
@Mixin(value = Player.class, /* Make sure it happens at the end */ priority = 10_000)
public class PlayerEndMixin {
    @Inject(method = "attack", at = @At("RETURN"))
    public void switchEnd(CallbackInfo callback) {
        Player player = (Player) (Object) this;

        if (!DragonStateProvider.isDragon(player)) {
            return;
        }

        DragonStateHandler handler = DragonStateProvider.getData(player);

        if (handler.switchedWeapon) {
            ItemStack originalMainHand = handler.storedMainHandWeapon;
            ItemStack originalToolSlot = player.getItemInHand(InteractionHand.MAIN_HAND);

            player.setItemInHand(InteractionHand.MAIN_HAND, originalMainHand);

            handler.getClawToolData().getClawsInventory().setItem(0, originalToolSlot);
            handler.storedMainHandWeapon = ItemStack.EMPTY;
            handler.switchedWeapon = false;
        }
    }
}
