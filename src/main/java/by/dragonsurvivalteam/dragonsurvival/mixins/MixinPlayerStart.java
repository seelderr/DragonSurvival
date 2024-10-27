package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, /* Make sure it happens at the start */ priority = 1)
public class MixinPlayerStart {
    @Inject(method = "attack", at = @At("HEAD"))
    public void switchStart(Entity target, CallbackInfo ci) {
        Object self = this;
        Player player = (Player) self;

        if (!DragonStateProvider.isDragon(player)) {
            return;
        }

        ItemStack toolSlot = ClawToolHandler.getDragonSword(player);
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (toolSlot != ItemStack.EMPTY) {
            player.setItemInHand(InteractionHand.MAIN_HAND, toolSlot);

            DragonStateHandler handler = DragonStateProvider.getData(player);
            handler.getClawToolData().getClawsInventory().setItem(0, ItemStack.EMPTY);
            handler.storedMainHandWeapon = mainHand;
            handler.switchedWeapon = true;
        }
    }
}
