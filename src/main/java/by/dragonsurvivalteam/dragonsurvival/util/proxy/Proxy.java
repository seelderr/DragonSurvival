package by.dragonsurvivalteam.dragonsurvival.util.proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface Proxy {
    default @Nullable Player getLocalPlayer() {
        return null;
    }

    default @Nullable Level getLocalLevel() {
        return null;
    }
}
