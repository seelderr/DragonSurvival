package by.dragonsurvivalteam.dragonsurvival.client.gui.hud;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.CaveDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.ForestDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.SeaDragonType;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.ContrastShowerAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.LightInDarknessAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive.WaterAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class DragonPenaltyHUD {
    public static final ResourceLocation DRAGON_HUD = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/dragon_hud.png");

    public static void renderDragonPenaltyHUD(final DragonStateHandler handler, final Gui gui, final GuiGraphics guiGraphics) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null || !Minecraft.getInstance().gameMode.canHurtPlayer()) {
            return;
        }

        int rightHeight;

        if (handler.getType() instanceof SeaDragonType seaDragonType) {
            if (seaDragonType.timeWithoutWater > 0 && ServerConfig.penaltiesEnabled && ServerConfig.seaTicksWithoutWater != 0) {
                RenderSystem.enableBlend();

                rightHeight = gui.rightHeight;
                gui.rightHeight += 10;

                int maxTimeWithoutWater = ServerConfig.seaTicksWithoutWater;
                WaterAbility waterAbility = DragonAbilities.getSelfAbility(localPlayer, WaterAbility.class);

                if (waterAbility != null) {
                    maxTimeWithoutWater += Functions.secondsToTicks(waterAbility.getDuration());
                }

                double timeWithoutWater = maxTimeWithoutWater - seaDragonType.timeWithoutWater;
                boolean flag = false;

                if (timeWithoutWater < 0) {
                    flag = true;
                    timeWithoutWater = Math.abs(timeWithoutWater);
                }

                final int left = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 91;
                final int top = Minecraft.getInstance().getWindow().getGuiScaledHeight() - rightHeight;
                final int full = flag ? Mth.floor(timeWithoutWater * 10.0D / maxTimeWithoutWater) : Mth.ceil((timeWithoutWater - 2) * 10.0D / maxTimeWithoutWater);
                final int partial = Mth.ceil(timeWithoutWater * 10.0D / maxTimeWithoutWater) - full;

                for (int i = 0; i < full + partial; ++i) {
                    guiGraphics.blit(DRAGON_HUD, left - i * 8 - 9, top, flag ? 18 : i < full ? 0 : 9, 36, 9, 9);
                }

                RenderSystem.disableBlend();
            }
        } else if (handler.getType() instanceof CaveDragonType caveDragonType) {
            if (caveDragonType.timeInRain > 0 && ServerConfig.penaltiesEnabled && ServerConfig.caveRainDamage != 0.0) {
                RenderSystem.enableBlend();

                rightHeight = gui.rightHeight;
                gui.rightHeight += 10;

                ContrastShowerAbility contrastShower = DragonAbilities.getSelfAbility(localPlayer, ContrastShowerAbility.class);
                int maxRainTime = 0;

                if (contrastShower != null) {
                    maxRainTime += Functions.secondsToTicks(contrastShower.getDuration());
                }

                final int timeInRain = maxRainTime - Math.min(caveDragonType.timeInRain, maxRainTime);

                final int left = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 91;
                final int top = Minecraft.getInstance().getWindow().getGuiScaledHeight() - rightHeight;
                final int full = Mth.ceil((double) (timeInRain - 2) * 10.0D / maxRainTime);
                final int partial = Mth.ceil((double) timeInRain * 10.0D / maxRainTime) - full;

                for (int i = 0; i < full + partial; ++i) {
                    guiGraphics.blit(DRAGON_HUD, left - i * 8 - 9, top, i < full ? 0 : 9, 54, 9, 9);
                }

                RenderSystem.disableBlend();
            }

            if (caveDragonType.lavaAirSupply < ServerConfig.caveLavaSwimmingTicks && ServerConfig.bonusesEnabled && ServerConfig.caveLavaSwimmingTicks != 0 && ServerConfig.caveLavaSwimming) {
                RenderSystem.enableBlend();

                rightHeight = gui.rightHeight;
                gui.rightHeight += 10;

                int left = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 91;
                int top = Minecraft.getInstance().getWindow().getGuiScaledHeight() - rightHeight;
                int full = Mth.ceil((double) (caveDragonType.lavaAirSupply - 2) * 10.0D / ServerConfig.caveLavaSwimmingTicks);
                int partial = Mth.ceil((double) caveDragonType.lavaAirSupply * 10.0D / ServerConfig.caveLavaSwimmingTicks) - full;

                for (int i = 0; i < full + partial; ++i) {
                    guiGraphics.blit(DRAGON_HUD, left - i * 8 - 9, top, i < full ? 0 : 9, 27, 9, 9);
                }

                RenderSystem.disableBlend();
            }
        } else if (handler.getType() instanceof ForestDragonType forestDragonType) {
            if (forestDragonType.timeInDarkness > 0 && ServerConfig.penaltiesEnabled && ServerConfig.forestStressTicks != 0 && !localPlayer.hasEffect(DSEffects.STRESS)) {
                RenderSystem.enableBlend();

                rightHeight = gui.rightHeight;
                gui.rightHeight += 10;

                int maxTimeInDarkness = ServerConfig.forestStressTicks;
                LightInDarknessAbility lightInDarkness = DragonAbilities.getSelfAbility(localPlayer, LightInDarknessAbility.class);

                if (lightInDarkness != null) {
                    maxTimeInDarkness += Functions.secondsToTicks(lightInDarkness.getDuration());
                }

                final int timeInDarkness = maxTimeInDarkness - Math.min(forestDragonType.timeInDarkness, maxTimeInDarkness);

                final int left = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 91;
                final int top = Minecraft.getInstance().getWindow().getGuiScaledHeight() - rightHeight;
                final int full = Mth.ceil((double) (timeInDarkness - 2) * 10.0D / maxTimeInDarkness);
                final int partial = Mth.ceil((double) timeInDarkness * 10.0D / maxTimeInDarkness) - full;

                for (int i = 0; i < full + partial; ++i) {
                    guiGraphics.blit(DRAGON_HUD, left - i * 8 - 9, top, i < full ? 0 : 9, 45, 9, 9);
                }

                RenderSystem.disableBlend();
            }
        }
    }
}
