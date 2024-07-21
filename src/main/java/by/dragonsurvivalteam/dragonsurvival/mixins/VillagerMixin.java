package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class VillagerMixin {
    @Inject(method = "startTrading", at = @At("HEAD"), cancellable = true)
    private void preventTradingWithMarkedPlayers(Player pPlayer, CallbackInfo ci) {
        if(pPlayer.hasEffect(DSEffects.ROYAL_CHASE)) {
            ci.cancel();
        }
    }
}
