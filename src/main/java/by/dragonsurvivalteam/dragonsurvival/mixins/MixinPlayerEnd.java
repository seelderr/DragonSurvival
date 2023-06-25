package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolActions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.IS_BETTERCOMBAT_LOADED;

@Mixin(value = Player.class, priority = 10000) // To make sure it's the last call in the method
public class MixinPlayerEnd {
    /** Put the switched-out items (dragon claw tool and main hand item) back to their original places */
    @Inject(method = "attack", at = @At("RETURN"))
    public void switchEnd(Entity target, CallbackInfo ci) {
        Object self = this;
        Player player = (Player) self;

        if (!DragonUtils.isDragon(player)) {
            return;
        }

        DragonStateHandler handler = DragonUtils.getHandler(player);

        if (handler.switchedItems) {
            ItemStack originalMainHand = handler.storedMainHand;
            ItemStack originalToolSlot = player.getItemInHand(InteractionHand.MAIN_HAND);

            player.setItemInHand(InteractionHand.MAIN_HAND, originalMainHand);

            handler.getClawToolData().getClawsInventory().setItem(0, originalToolSlot);
            handler.storedMainHand = ItemStack.EMPTY; // There is no real need to reset it here but doesn't hurt to do it
            handler.switchedItems = false;
        }
    }

    /** Re-enable sweeping for dragons when `Better Combat` is installed since it currently does not work with the dragon claw slot */
    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 3)
    private boolean reEnableSweeping(boolean value) {
        if (!IS_BETTERCOMBAT_LOADED) {
            return value;
        }

        Object self = this;
        Player player = (Player) self;
        DragonStateHandler handler = DragonUtils.getHandler(player);

        // Only need to overwrite the value if the items were switched out (= dragon claw sword was used)
        if (handler.switchedItems) {
            return player.getMainHandItem().canPerformAction(ToolActions.SWORD_SWEEP);
        }

        return value;
    }
}
