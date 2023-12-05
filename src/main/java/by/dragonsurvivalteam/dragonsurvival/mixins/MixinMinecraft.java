package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayerUtils;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/util/profiling/ProfilerFiller.popPush(Ljava/lang/String;)V",
                    shift = At.Shift.AFTER
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;tick()V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;tickEntities()V")
            )
    )
    public void tickDragon(final CallbackInfo callback) {
        // For animation purposes - alternative to MixinGeoModel
//        FakeClientPlayerUtils.FAKE_DRAGONS.forEach((playerId, dragon) -> dragon.tickCount++);
//        ClientDragonRender.playerDragonHashMap.forEach((playerId, dragon) -> dragon.get().tickCount++);
    }
}
