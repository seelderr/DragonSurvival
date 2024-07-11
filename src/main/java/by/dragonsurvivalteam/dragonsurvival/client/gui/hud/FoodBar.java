package by.dragonsurvivalteam.dragonsurvival.client.gui.hud;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class FoodBar {
    private static final ResourceLocation FOOD_ICONS = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/dragon_hud.png");
    private static final RandomSource RANDOM = RandomSource.create();

    public static boolean render(final Gui gui, final GuiGraphics guiGraphics, int width, int height) {
        Player localPlayer = ClientProxy.getLocalPlayer();

        if (localPlayer == null || !Minecraft.getInstance().gameMode.canHurtPlayer()) {
            return false;
        }

        DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(localPlayer);

        if (!handler.isDragon()) {
            return false;
        }

        Minecraft.getInstance().getProfiler().push("food");
        RenderSystem.enableBlend();

        DragonFoodHandler.rightHeight = gui.rightHeight;
        gui.rightHeight += 10;

        final int left = width / 2 + 91;
        final int top = height - DragonFoodHandler.rightHeight;
        DragonFoodHandler.rightHeight += 10;
        final FoodData food = localPlayer.getFoodData();
        final int type = DragonUtils.isDragonType(handler, DragonTypes.FOREST) ? 0 : DragonUtils.isDragonType(handler, DragonTypes.CAVE) ? 9 : 18;
        final boolean hunger = localPlayer.hasEffect(MobEffects.HUNGER);

        for (int i = 0; i < 10; i++) {
            int icon = i * 2 + 1; // there can be 10 icons (food level maximum is 20)
            int y = top;

            if (food.getSaturationLevel() <= 0 && localPlayer.tickCount % (food.getFoodLevel() * 3 + 1) == 0) {
                // Animate the food icons (moving up / down)
                y = top + RANDOM.nextInt(3) - 1;
            }

            guiGraphics.blit(FOOD_ICONS, left - i * 8 - 9, y, hunger ? 117 : 0, type, 9, 9);

            if (icon < food.getFoodLevel()) {
                guiGraphics.blit(FOOD_ICONS, left - i * 8 - 9, y, hunger ? 72 : 36, type, 9, 9);
            } else if (icon == food.getFoodLevel()) {
                guiGraphics.blit(FOOD_ICONS, left - i * 8 - 9, y, hunger ? 81 : 45, type, 9, 9);
            }
        }

        RenderSystem.disableBlend();
        Minecraft.getInstance().getProfiler().pop();

        return true;
    }
}
