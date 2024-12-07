package by.dragonsurvivalteam.dragonsurvival.util.proxy.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SoundManagerClientProxy implements SoundManagerProxy {
    @Override
    public void playSoundAtEyeLevel(SoundEvent event, SoundSource source, Player player) {
        Vec3 pos = player.getEyePosition(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
        SimpleSoundInstance sound = new SimpleSoundInstance(
                event,
                SoundSource.PLAYERS,
                1.0F, 1.0F,
                SoundInstance.createUnseededRandom(),
                pos.x, pos.y, pos.z
        );
        Minecraft.getInstance().getSoundManager().playDelayed(sound, 0);
    }
}
