package by.dragonsurvivalteam.dragonsurvival.util.proxy.sound;

import by.dragonsurvivalteam.dragonsurvival.client.sounds.FollowEntitySound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class TickableSoundInstanceClientProxy implements TickableSoundInstanceProxy {
    TickableSoundInstance soundInstance;

    @Override
    public void queueTickingSound(SoundEvent soundEvent, SoundSource soundSource, Entity entity) {
        TickableSoundInstance sound = new FollowEntitySound(soundEvent, soundSource, entity);
        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        soundInstance = sound;
    }

    @Override
    public void stopTickingSound() {
        if (soundInstance != null) {
            Minecraft.getInstance().getSoundManager().stop(soundInstance);
            soundInstance = null;
        }
    }
}
