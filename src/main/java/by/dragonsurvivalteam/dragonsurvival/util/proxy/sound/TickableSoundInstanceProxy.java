package by.dragonsurvivalteam.dragonsurvival.util.proxy.sound;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public interface TickableSoundInstanceProxy {
    default void queueTickingSound(SoundEvent soundEvent, SoundSource soundSource, Entity entity) {};
    default void stopTickingSound() {};
}
