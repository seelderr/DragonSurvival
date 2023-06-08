package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import squeek.appleskin.helpers.FoodHelper;

@Mixin(FoodHelper.class)
public class MixinFoodHelper {

    // Lsqueek/appleskin/helpers/FoodHelper;isRotten(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Z
    @Inject(at = @At("RETURN"), method = "isRotten", cancellable = true, remap = false)
    private static void isRotten(final ItemStack itemStack, final Player player, final CallbackInfoReturnable<Boolean> callback) {
        if (DragonUtils.isDragon(player)) {
            callback.setReturnValue(!DragonFoodHandler.isDragonEdible(itemStack.getItem(), DragonUtils.getDragonType(player)));
        }
    }
}
