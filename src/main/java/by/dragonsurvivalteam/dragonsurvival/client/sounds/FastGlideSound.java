package by.dragonsurvivalteam.dragonsurvival.client.sounds;

import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.util.Mth;


public class FastGlideSound extends ElytraOnPlayerSoundInstance {
    private final LocalPlayer player;
    private int time;

    public FastGlideSound(LocalPlayer p_i47113_1_) {
        super(p_i47113_1_);
        player = p_i47113_1_;
        looping = true;
        delay = 0;
        volume = 0.1F;
    }

    @Override
    public void tick() {
        ++time;
        if (!player.isRemoved() && (time <= 20 || ServerFlightHandler.isGliding(player))) {
            x = (float) player.getX();
            y = (float) player.getY();
            z = (float) player.getZ();
            float f = (float) player.getDeltaMovement().lengthSqr();
            if ((double) f >= 1.0E-7D) {
                volume = Mth.clamp(f / 4.0F, 0.0F, 1.0F);
            } else {
                volume = 0.0F;
            }

            if (time < 20) {
                volume = 0.0F;
            } else if (time < 40) {
                volume = (float) ((double) volume * ((double) (time - 20) / 20.0D));
            }

            float f1 = 0.8F;
            if (volume > 0.8F) {
                pitch = 1.0F + (volume - 0.8F);
            } else {
                pitch = 1.0F;
            }
        } else {
            stop();
        }
    }
}