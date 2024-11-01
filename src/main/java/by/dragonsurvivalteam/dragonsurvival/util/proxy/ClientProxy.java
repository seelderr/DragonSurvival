package by.dragonsurvivalteam.dragonsurvival.util.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ClientProxy implements Proxy {
    @Override
    public @Nullable Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public @Nullable Level getLocalLevel() {
        Player player = Minecraft.getInstance().player;
        return player != null ? player.level() : null;
    }
}
