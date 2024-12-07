package by.dragonsurvivalteam.dragonsurvival.util.proxy.sound;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public interface SoundManagerProxy {
    default void playSoundAtEyeLevel(SoundEvent event, SoundSource source, Player player) {};
}
