package by.dragonsurvivalteam.dragonsurvival.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.DeltaTracker$Timer")
public interface TestMixin {
    @Accessor("msPerTick")
    float getMsPerTick();
}
