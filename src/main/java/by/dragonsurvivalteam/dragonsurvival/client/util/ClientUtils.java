package by.dragonsurvivalteam.dragonsurvival.client.util;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class ClientUtils {
    public static boolean hasLavaVision() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return false;
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null) {
            return player.hasEffect(DSEffects.LAVA_VISION);
        }

        return false;
    }

    public static boolean hasWaterVision() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return false;
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null) {
            return player.hasEffect(DSEffects.WATER_VISION);
        }

        return false;
    }
}
