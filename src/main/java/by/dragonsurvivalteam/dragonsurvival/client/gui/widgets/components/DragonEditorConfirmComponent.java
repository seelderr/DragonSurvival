package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.components;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang.LangKey;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DragonEditorConfirmComponent extends AbstractContainerEventHandler implements Renderable {
    @Translation(type = Translation.Type.MISC, comments = "\nWith your current config settings all progress will be lost when changing species.\n\nWould you still like to continue?")
    private final static String CONFIRM_LOSE_ALL = Translation.Type.GUI.wrap("dragon_editor.confirm.all");

    @Translation(type = Translation.Type.MISC, comments = "\nWith your current config settings your growth progress will be lost when changing species or body types.\n\nWould you still like to continue?")
    private final static String CONFIRM_LOSE_GROWTH = Translation.Type.GUI.wrap("dragon_editor.confirm.growth");

    @Translation(type = Translation.Type.MISC, comments = "\nWith your current config settings your ability progress will be lost when changing species.\n\nWould you still like to continue?")
    private final static String CONFIRM_LOSE_ABILITIES = Translation.Type.GUI.wrap("dragon_editor.confirm.abilities");

    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(DragonSurvival.MODID, "textures/gui/dragon_altar_warning.png");

    private final AbstractWidget btn1;
    private final AbstractWidget btn2;
    private final int x;
    private final int y;
    private final int xSize;
    private final int ySize;
    public boolean visible;
    public boolean isBodyTypeChange;

    public DragonEditorConfirmComponent(DragonEditorScreen screen, int x, int y, int xSize, int ySize) {
        this.x = x;
        this.y = y;
        this.xSize = xSize;
        this.ySize = ySize;
        this.isBodyTypeChange = false;

        btn1 = new ExtendedButton(x + 19, y + 133, 41, 21, CommonComponents.GUI_YES, pButton -> {
        }) {
            @Override
            public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, getFGColor());

                if (isHovered()) {
                    guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("ds.gui.dragon_editor.tooltip.done"), mouseX, mouseY);
                }
            }

            @Override
            public void onPress() {
                screen.confirm();
            }
        };

        btn2 = new ExtendedButton(x + 66, y + 133, 41, 21, CommonComponents.GUI_NO, pButton -> {
        }) {
            @Override
            public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, getFGColor());

                if (isHovered) {
                    guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("ds.gui.dragon_editor.tooltip.cancel"), mouseX, mouseY);
                }
            }

            @Override
            public void onPress() {
                screen.confirmation = false;
                screen.showUi = true;
            }
        };
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return ImmutableList.of(btn1, btn2);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        guiGraphics.fillGradient(0, 0, Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), -1072689136, -804253680);

        String key = "";

        if (!ServerConfig.saveAllAbilities && (!ServerConfig.saveGrowthStage && !isBodyTypeChange)) {
            key = CONFIRM_LOSE_ALL;
        } else if ((ServerConfig.saveAllAbilities || isBodyTypeChange) && !ServerConfig.saveGrowthStage) {
            key = CONFIRM_LOSE_GROWTH;
        } else if (!ServerConfig.saveAllAbilities) {
            key = CONFIRM_LOSE_ABILITIES;
        }

        String text = Component.translatable(key).getString();
        guiGraphics.blit(BACKGROUND_TEXTURE, x, y, 0, 0, xSize, ySize);
        TextRenderUtil.drawCenteredScaledTextSplit(guiGraphics, x + xSize / 2, y + 42, 1f, text, DyeColor.WHITE.getTextColor(), xSize - 10, 150);

        btn1.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
        btn2.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
    }
}