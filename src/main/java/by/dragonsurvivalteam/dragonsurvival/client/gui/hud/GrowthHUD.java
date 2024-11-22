package by.dragonsurvivalteam.dragonsurvival.client.gui.hud;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.util.RenderingUtils;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonGrowthHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonStage;
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
import java.util.function.BiFunction;
import java.util.function.Function;

/** HUD that is shown when the dragon is holding an item that can change its growth */
public class GrowthHUD {
    public static final Function<Integer, String> CIRCLE_BASE = number -> "textures/gui/growth/circle_" + number + ".png";
    public static final Function<String, String> CIRCLE_TEXTURE = dragonType -> "textures/gui/growth/circle_" + dragonType + ".png";
    public static final BiFunction<String, ResourceLocation, String> ICON = (dragonType, dragonLevel) -> "textures/gui/growth/" + dragonType + "/" + dragonLevel.getPath() + ".png";

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

        double growth = DragonGrowthHandler.getGrowth(handler.getLevel(), stack.getItem());
        double newSize = DragonStage.getBoundedSize(handler.getSize() + growth);

        if (handler.getSize() == newSize) {
            return;
        }

        Holder<DragonStage> dragonLevel = handler.getLevel();
        float progress = (float) dragonLevel.value().getProgress(handler.getSize());
        float nextProgress = (float) dragonLevel.value().getProgress(handler.getSize() + growth);

        progress = Math.min(1, progress);
        nextProgress = Math.min(1, nextProgress);

        int radius = 17;
        int thickness = 5;
        int circleX = width / 2 - radius;
        int circleY = height - 90;

        circleX += growthXOffset;
        circleY += growthYOffset;

        RenderSystem.setShaderColor(BRIGHTER_COLOR.getRed() / 255f, BRIGHTER_COLOR.getBlue() / 255f, BRIGHTER_COLOR.getGreen() / 255f, 1);
        RenderingUtils.drawSmoothCircle(guiGraphics, circleX + radius, circleY + radius, radius, 6, 1, 0);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1.0f);

        if (nextProgress >= progress) {
            double perSide = 1.0 / 6.0;
            int number = 1;

            if (nextProgress < progress + perSide) {
                nextProgress = (float) (progress + perSide);
                number = 2;
            }

            RenderSystem.setShaderTexture(0, getOrCreate(CIRCLE_BASE.apply(number)));
            drawCircle(handler, guiGraphics, progress, nextProgress, radius, circleX, circleY);
        } else if (growth < 0) {
            RenderSystem.setShaderTexture(0, getOrCreate(CIRCLE_BASE.apply(3)));
            drawCircle(handler, guiGraphics, nextProgress, progress, radius, circleX, circleY);
        }

        RenderSystem.setShaderColor(COLOR.getRed() / 255f, COLOR.getBlue() / 255f, COLOR.getGreen() / 255f, 1);
        RenderingUtils.drawSmoothCircle(guiGraphics, circleX + radius, circleY + radius, radius - thickness, 6, 1, 0);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 300);
        ResourceLocation levelLocation = Objects.requireNonNull(dragonLevel.getKey()).location();
        guiGraphics.blit(getOrCreate(levelLocation.getNamespace(), ICON.apply(handler.getTypeNameLowerCase(), levelLocation)), circleX + 6, circleY + 6, 0, 0, 20, 20, 20, 20);
        guiGraphics.pose().popPose();
    }

    private static void drawCircle(DragonStateHandler handler, @NotNull GuiGraphics graphics, float progress, float nextProgress, int radius, int circleX, int circleY) {
        RenderingUtils.drawTexturedCircle(graphics, circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, nextProgress, -0.5);
        RenderSystem.setShaderTexture(0, getOrCreate(CIRCLE_TEXTURE.apply(handler.getTypeNameLowerCase())));
        RenderingUtils.drawTexturedCircle(graphics, circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, 6, progress, -0.5);
    }

    public static ResourceLocation getOrCreate(final String path) {
        return getOrCreate(DragonSurvival.MODID, path);
    }

    public static ResourceLocation getOrCreate(final String namespace, final String path) {
        return CACHE.computeIfAbsent(path, key -> ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}