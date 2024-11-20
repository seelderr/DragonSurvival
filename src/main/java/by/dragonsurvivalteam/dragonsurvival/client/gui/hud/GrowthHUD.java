package by.dragonsurvivalteam.dragonsurvival.client.gui.hud;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.util.RenderingUtils;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonGrowthHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

/** HUD that is shown when the dragon is holding an item that can change its growth */
public class GrowthHUD {
    private static final HashMap<String, ResourceLocation> CACHE = new HashMap<>();
    private static final Color COLOR = new Color(99, 99, 99);
    private static final Color BRIGHTER_COLOR = COLOR.brighter();

    @ConfigRange(min = -1000, max = 1000)
    @Translation(key = "growth_x_offset", type = Translation.Type.CONFIGURATION, comments = "Offset for the x position of the item growth icon")
    @ConfigOption(side = ConfigSide.CLIENT, category = {"ui", "growth"}, key = "growth_x_offset")
    public static Integer growthXOffset = 0;

    @ConfigRange(min = -1000, max = 1000)
    @Translation(key = "growth_y_offset", type = Translation.Type.CONFIGURATION, comments = "Offset for the y position of the item growth icon")
    @ConfigOption(side = ConfigSide.CLIENT, category = {"ui", "growth"}, key = "growth_y_offset")
    public static Integer growthYOffset = 0;

    public static void renderGrowthHUD(final DragonStateHandler handler, @NotNull final GuiGraphics guiGraphics, int width, int height) {
        Player localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null || localPlayer.isSpectator()) {
            return;
        }

        ItemStack stack = localPlayer.getMainHandItem();

        if (stack.isEmpty()) {
            return;
        }

        double change = DragonGrowthHandler.getIncrement(stack.getItem(), handler.getLevel());

        if (change < 0) {
            DragonLevel min = DragonLevel.getLevel(localPlayer.registryAccess(), 0).value();
            double difference = Math.min(0, handler.getSize() - min.sizeRange().min());
            change = Math.max(change, -difference);
        } else if (change > 0) {
            DragonLevel max = DragonLevel.getLevel(localPlayer.registryAccess(), Double.MAX_VALUE).value();
            double difference = Math.max(0, max.sizeRange().max() - handler.getSize());
            change = Math.min(change, difference);
        }

        if (change == 0) {
            return;
        }

        Holder<DragonLevel> dragonLevel = Objects.requireNonNull(handler.getLevel());

        float currentSize = (float) handler.getSize();
        float nextSize = (float) (handler.getSize() + change);

        float progress = (float) ((currentSize - dragonLevel.value().sizeRange().min()) / (dragonLevel.value().sizeRange().max() - dragonLevel.value().sizeRange().min()));
        float nextProgress = (float) ((nextSize - dragonLevel.value().sizeRange().min()) / (dragonLevel.value().sizeRange().max() - dragonLevel.value().sizeRange().min()));

        progress = Math.min(1, progress);
        nextProgress = Math.min(1, nextProgress);

        int radius = 17;
        int thickness = 5;
        int circleX = width / 2 - radius;
        int circleY = height - 90;

        circleX += growthXOffset;
        circleY += growthYOffset;

        RenderSystem.setShaderColor(0f, 0f, 0f, 1f);

        RenderSystem.setShaderColor(BRIGHTER_COLOR.getRed() / 255.0f, BRIGHTER_COLOR.getBlue() / 255.0f, BRIGHTER_COLOR.getGreen() / 255.0f, 1.0f);
        RenderingUtils.drawSmoothCircle(guiGraphics, circleX + radius, circleY + radius, radius, 6, 1, 0);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1.0f);

        if (nextProgress > progress) {
            int num = 1;
            double perSide = 1.0 / 6.0;

            if (nextProgress < progress + perSide) {
                nextProgress = (float) (progress + perSide);
                num = 2;
            }

            RenderSystem.setShaderTexture(0, getOrCreate("textures/gui/growth/circle_" + num + ".png"));
            RenderingUtils.drawTexturedCircle(guiGraphics, circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, nextProgress, -0.5);

            RenderSystem.setShaderTexture(0, getOrCreate("textures/gui/growth/circle_" + handler.getTypeNameLowerCase() + ".png"));
            RenderingUtils.drawTexturedCircle(guiGraphics, circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, progress, -0.5);
        } else if (change < 0) {
            RenderSystem.setShaderTexture(0, getOrCreate("textures/gui/growth/circle_3.png"));
            RenderingUtils.drawTexturedCircle(guiGraphics, circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, progress, -0.5);

            RenderSystem.setShaderTexture(0, getOrCreate("textures/gui/growth/circle_" + handler.getTypeNameLowerCase() + ".png"));
            RenderingUtils.drawTexturedCircle(guiGraphics, circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, nextProgress, -0.5);
        }

        RenderSystem.setShaderColor(COLOR.getRed() / 255.0f, COLOR.getBlue() / 255.0f, COLOR.getGreen() / 255.0f, 1.0f);
        RenderingUtils.drawSmoothCircle(guiGraphics, circleX + radius, circleY + radius, radius - thickness, 6, 1, 0);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1.0f);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 300);
        //noinspection DataFlowIssue -> key is present
        ResourceLocation levelLocation = dragonLevel.getKey().location();
        guiGraphics.blit(getOrCreate(levelLocation.getNamespace(), "textures/gui/growth/" + handler.getTypeNameLowerCase() + "/" + levelLocation.getPath() + ".png"), circleX + 6, circleY + 6, 0, 0, 20, 20, 20, 20);
        guiGraphics.pose().popPose();
    }

    private static ResourceLocation getOrCreate(final String path) {
        return getOrCreate(DragonSurvival.MODID, path);
    }

    private static ResourceLocation getOrCreate(final String namespace, final String path) {
        return CACHE.computeIfAbsent(path, key -> ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}