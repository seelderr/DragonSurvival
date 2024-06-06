package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerGameMode.class)
public interface AccessorServerPlayerGameMode {
    @Accessor("isDestroyingBlock")
    boolean getIsDestroyingBlock();
}
