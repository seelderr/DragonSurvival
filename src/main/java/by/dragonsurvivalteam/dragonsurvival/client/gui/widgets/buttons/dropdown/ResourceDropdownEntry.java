package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.client.util.TooltipRendering;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ResourceDropdownEntry extends DropdownEntry{
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
	public List<? extends GuiEventListener> children(){
		return button != null ? ImmutableList.of(button) : new ArrayList<>();
	}

	@Override
	public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks){
		if(button == null){
			if(list != null)
				button = new ExtendedButton(list.getLeft() + 3, 0, list.getWidth() - 12, pHeight, null, null){
					private int tick = 0;

					@Override
					public TextComponent getMessage(){
						return (TextComponent)TextComponent.EMPTY;
					}

					@Override
					public void onPress(){
						if(!source.isFocused()){
							return;
						}
						setter.accept(entry);
					}

					@Override
					public int getBlitOffset(){
						return 10;
					}

					@Override
					public void renderButton(PoseStack mStack, int mouseX, int mouseY, float partial){
						if(!source.isFocused()){
							return;
						}
						if(y + height > list.getBottom() - 3 || y < list.getTop() + 3){
							return;
						}

						if(entry != null){
							if(tick >= 1){
								entry.tick();
								tick = 0;
							}else{
								tick++;
							}

							mStack.pushPose();
							mStack.translate(0, 0, 200);

							int color = new Color(0.1F, 0.1F, 0.1F, 1F).getRGB();

							if(num % 2 == 0){
								color = new Color(0.2F, 0.2F, 0.2F, 1F).getRGB();
							}

							if(isHovered){
								color = new Color(color).brighter().getRGB();
							}

							Gui.fill(mStack, x, y, x + width, y + height, color);

							String text = entry.id;
							Minecraft.getInstance().font.drawShadow(mStack, new TextComponent( Minecraft.getInstance().font.substrByWidth(new TextComponent(text), width - 20).getString()), x + 25, y + 5, DyeColor.WHITE.getTextColor());

							if(!entry.isEmpty()){
								ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
								itemRenderer.blitOffset = 100;
								itemRenderer.renderAndDecorateItem(entry.getDisplayItem(), x + 3, y + 1);

								if(entry.tag){
									mStack.translate(0, 0, 200);
									Minecraft.getInstance().font.drawShadow(mStack, new TextComponent("#"), x + 14, y + 10, DyeColor.WHITE.getTextColor());
									mStack.translate(0, 0, -200);
								}

								itemRenderer.blitOffset = 0;

								if(isHovered){
									List<Component> lines = entry.getDisplayItem().getTooltipLines(Minecraft.getInstance().player, Default.NORMAL);
									TooltipRendering.drawHoveringText(mStack, lines, mouseX, mouseY);
								}
							}

							mStack.popPose();
						}
					}
				};
		}else{
			button.y = pTop;
			button.visible = source.visible;
			button.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);
		}
	}

	@Override
	public List<? extends NarratableEntry> narratables(){
		return Collections.emptyList();
	}
}