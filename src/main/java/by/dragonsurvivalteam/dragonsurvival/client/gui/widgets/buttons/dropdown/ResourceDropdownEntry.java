package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.DyeColor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ResourceDropdownEntry extends DropdownEntry{
	private final int num;
	private final ResourceEntry entry;
	private final Consumer<ResourceEntry> setter;
	private ExtendedButton button;
	private final ResourceTextField source;

	public ResourceDropdownEntry(ResourceTextField source, int num, ResourceEntry entry, Consumer<ResourceEntry> setter){
		this.num = num;
		this.entry = entry;
		this.setter = setter;
		this.source = source;
	}

	@Override
	public List<? extends IGuiEventListener> children(){
		return button != null ? ImmutableList.of(button) : new ArrayList<>();
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks){
		if(button == null){
			if(list != null){
				button = new ExtendedButton(list.getLeft() + 3, 0, list.getWidth() - 12, pHeight, null, null){
					private int tick = 0;

					@Override
					public ITextComponent getMessage(){
						return StringTextComponent.EMPTY;
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
					public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
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

							AbstractGui.fill(mStack, x, y, x + width, y + height, color);

							String text = entry.id;
							Minecraft.getInstance().font.drawShadow(mStack, new StringTextComponent(Minecraft.getInstance().font.substrByWidth(new StringTextComponent(text), width - 20).getString()), x + 25, y + 5, DyeColor.WHITE.getTextColor());

							if(!entry.isEmpty()){
								ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
								itemRenderer.blitOffset = 100;
								itemRenderer.renderAndDecorateItem(entry.getDisplayItem(), x + 3, y + 1);
								itemRenderer.blitOffset = 0;

								if(isHovered){
									List<ITextComponent> lines = entry.getDisplayItem().getTooltipLines(Minecraft.getInstance().player, TooltipFlags.NORMAL);
									GuiUtils.drawHoveringText(mStack, lines, mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
								}
							}

							mStack.popPose();
						}
					}
				};
			}
		}else{
			button.y = pTop;
			button.visible = source.visible;
			button.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		}
	}
}