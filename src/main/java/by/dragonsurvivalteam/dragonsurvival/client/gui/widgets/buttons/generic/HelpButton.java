package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class HelpButton extends ExtendedButton {
    private static final ResourceLocation DEFAULT_HELP_BUTTON = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/help_button.png");

    private final String text;
    private final Holder<DragonType> type;
    private final ResourceLocation main;
    private final ResourceLocation hover;

    private boolean usesVanillaTooltip;

    public HelpButton(int x, int y, int sizeX, int sizeY, String text) {
        this(DragonStateProvider.getData(Minecraft.getInstance().player).getDragonType(), x, y, sizeX, sizeY, text, null, null);
    }

    public HelpButton(int x, int y, int sizeX, int sizeY, String text, ResourceLocation main, ResourceLocation hover) {
        this(DragonStateProvider.getData(Minecraft.getInstance().player).getDragonType(), x, y, sizeX, sizeY, text, main, hover);
    }

    // This is needed for the DragonScreen, as otherwise we'll get cut out by the scissoring used for the rendering of the player entity in the window
    public HelpButton(int x, int y, int sizeX, int sizeY, String text, boolean usesVanillaTooltip) {
        this(DragonStateProvider.getData(Minecraft.getInstance().player).getDragonType(), x, y, sizeX, sizeY, text, null, null);
        if (usesVanillaTooltip) {
            setTooltip(Tooltip.create(Component.translatable(text)));
        }
        this.usesVanillaTooltip = usesVanillaTooltip;
    }

    public HelpButton(Holder<DragonType> type, int x, int y, int sizeX, int sizeY, String text, ResourceLocation main, ResourceLocation hover) {
        super(x, y, sizeX, sizeY, Component.empty(), action -> { /* Nothing to do */ });
        this.text = text;
        this.type = type;
        this.main = main;
        this.hover = hover;
    }

    @Override
    public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (isHovered() && !usesVanillaTooltip) {
            // Render the tooltip manually since minecraft's tooltip positioner often fails with this button type
            guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(Component.translatable(text)), mouseX, mouseY);
        }

        // FIXME :: We don't use the supplied size at all here. Figure out how to use it correctly.
        if(main == null || hover == null) {
            ResourceLocation texture;
            if (type != null) {
                texture = type.value().miscResources().helpButton();
            } else {
                // Can occur when the altar is entered as a human
                texture = DEFAULT_HELP_BUTTON;
            }

            if(!isHovered()) {
                guiGraphics.blit(texture, getX(), getY(), 0, 1, 9, 9, 20, 11);
            } else {
                guiGraphics.blit(texture, getX() - 1, getY() - 1, 9, 0, 11, 11, 20, 11);
            }
        } else {
            if(!isHovered()) {
                guiGraphics.blit(main, getX(), getY(), 1, 0, 9, 9, 20, 11);
            } else {
                guiGraphics.blit(hover, getX(), getY(), 0, 10, 11, 11, 20, 11);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
}