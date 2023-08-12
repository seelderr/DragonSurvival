package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.client.util.TooltipRendering;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ResourceDropdownEntry extends DropdownEntry {
	private final int num;
	private final ResourceEntry entry;
	private final Consumer<ResourceEntry> setter;
	private final ResourceTextField source;
	private ExtendedButton button;

	public ResourceDropdownEntry(ResourceTextField source, int num, ResourceEntry entry, Consumer<ResourceEntry> setter){
		this.num = num;
		this.entry = entry;
		this.setter = setter;
		this.source = source;
	}

	@Override
	public @NotNull List<? extends GuiEventListener> children() {
		return button != null ? ImmutableList.of(button) : new ArrayList<>();
	}

	@Override
    public void render(@NotNull final PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
        if (button == null) {
            button = new ExtendedButton(list.getLeft() + 3, 0, list.getWidth() - 12, height, null, null) {
                private int tick = 0;

                @Override
                public @NotNull Component getMessage() {
                    return Component.empty();
                }

                @Override
                public void onPress() {
                    if (!source.isFocused()) {
                        return;
                    }

                    setter.accept(entry);
                }

                @Override
                public int getBlitOffset() {
                    return 10;
                }

                @Override
                public void renderButton(final PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
                    if (!source.isFocused()) {
                        return;
                    }

                    if (y + height > list.getBottom() - 3 || y < list.getTop() + 3) {
                        return;
                    }

                    // Make sure it gets rendered above the other resource text fields
                    poseStack.pushPose();
                    RenderSystem.enableDepthTest();
                    poseStack.translate(0, 0, 200);

                    if (entry != null) {
                        if (tick >= 1) {
                            entry.tick();
                            tick = 0;
                        } else {
                            tick++;
                        }

                        int color;

                        if (num % 2 == 0) {
                            color = new Color(0.2F, 0.2F, 0.2F, 1F).getRGB();
                        } else {
                             color = new Color(0.1F, 0.1F, 0.1F, 1F).getRGB();
                        }

                        if (isHovered) {
                            color = new Color(color).brighter().getRGB();
                        }

                        // Draws the background per entry // FIXME :: Currently overalys the item tooltip
                        Gui.fill(poseStack, x, y, x + width, y + height, color);

                        String text = entry.id;
                        Minecraft.getInstance().font.drawShadow(poseStack, Component.empty().append(Minecraft.getInstance().font.substrByWidth(Component.empty().append(text), width - 20).getString()), x + 25, y + 5, DyeColor.WHITE.getTextColor());

                        if (!entry.isEmpty()) {
                            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                            itemRenderer.blitOffset = 100;
                            itemRenderer.renderAndDecorateItem(entry.getDisplayItem(), x + 3, y + 1);

                            if (entry.tag) {
                                poseStack.translate(0, 0, 200);
                                Minecraft.getInstance().font.drawShadow(poseStack, Component.empty().append("#"), x + 14, y + 10, DyeColor.WHITE.getTextColor());
                                poseStack.translate(0, 0, -200);
                            }

                            itemRenderer.blitOffset = 0;

                            if (isHovered) {
                                poseStack.translate(0, 0, 200); // TODO
                                List<Component> lines = entry.getDisplayItem().getTooltipLines(Minecraft.getInstance().player, Default.NORMAL);
                                TooltipRendering.drawHoveringText(poseStack, lines, mouseX, mouseY);
                                poseStack.translate(0, 0, -200);
                            }
                        }

                        RenderSystem.disableDepthTest();
                        poseStack.popPose();
                    }
                }
            };
        } else {
            button.y = top;
            button.visible = source.visible;
            button.render(poseStack, mouseX, mouseY, partialTicks);
        }
    }

	@Override
	public @NotNull List<? extends NarratableEntry> narratables(){
		return Collections.emptyList();
	}
}