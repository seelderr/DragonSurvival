package by.dragonsurvivalteam.dragonsurvival.client.gui.hud;

import by.dragonsurvivalteam.dragonsurvival.registry.attachments.PenaltySupply;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.GlStateBackup;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class DragonPenaltyHUD {
    public static void renderDragonPenaltyHUD(final Gui gui, final GuiGraphics guiGraphics) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null || !Minecraft.getInstance().gameMode.canHurtPlayer()) {
            return;
        }

        int rightHeight;

        PenaltySupply penaltySupply = PenaltySupply.getData(localPlayer);
        GlStateBackup backup = new GlStateBackup();
        RenderSystem.backupGlState(backup);
        for(String supplyType : penaltySupply.getSupplyTypes()) {
            float supplyPercentage = penaltySupply.getPercentage(supplyType);
            boolean hasSupply = penaltySupply.hasSupply(supplyType);
            if(hasSupply && supplyPercentage < 1) {
                RenderSystem.enableBlend();

                rightHeight = gui.rightHeight;
                gui.rightHeight += 10;


                // See renderAirLevel in vanilla to understand this value
                final float vanillaSupplyPercentageOffset = (float) 2 / 360;

                final int left = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 91;
                int top =  Minecraft.getInstance().getWindow().getGuiScaledHeight() - rightHeight;
                int full = Mth.ceil((supplyPercentage - vanillaSupplyPercentageOffset) * 10.0);
                int partial = Mth.ceil(supplyPercentage * 10.0D) - full;
                RenderSystem.enableBlend();

                ResourceLocation supplyIcon = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/supply_icons/" + supplyType + ".png");
                for (int i = 0; i < full + partial; ++i) {
                    guiGraphics.blit(supplyIcon, left - i * 8 - 9, top, 9, 9,  i < full ? 0 : 9, 0, 9, 9, 18, 9);
                }

                RenderSystem.disableBlend();
            }
        }
        RenderSystem.restoreGlState(backup);
    }
}
