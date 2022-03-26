package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.DropDownButton;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ColoredDropdownValueEntry extends by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownEntry{
	private final int num;
	private final String value;
	private final Consumer<String> setter;
	private final DropDownButton source;
	private final TextComponent message;
	private ExtendedButton button;

	public ColoredDropdownValueEntry(DropDownButton source, int num, String value, Consumer<String> setter){
		this.num = num;
		this.value = value;
		this.setter = setter;
		this.source = source;
		message = new TextComponent(value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1).toLowerCase(Locale.ROOT));
	}

	/*
	private static ResourceLocation color1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/color_0.png");
	private static ResourceLocation color2 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/color_1.png");
	private static ResourceLocation color3 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/color_2.png");
	private Color c1 = new Color((int)(Math.random() * 0x1000000));
	private Color c2 = new Color((int)(Math.random() * 0x1000000));
	private Color c3 = new Color((int)(Math.random() * 0x1000000));
	 */

	@Override
	public List<? extends GuiEventListener> children(){
		return ImmutableList.of(button);
	}

	@Override
	public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks){
		if(button == null){
			if(list != null){
				button = new ExtendedButton(list.getLeft() + 3, 0, list.getWidth() - 12, pHeight + 1, null, null){
					@Override
					public TextComponent getMessage(){
						return message;
					}

					@Override
					public void onPress(){
						source.current = value;
						source.onPress();
						setter.accept(value);
					}
					/*
					@Override
					public void renderButton(PoseStack  mStack, int mouseX, int mouseY, float partial)
					{
						RenderSystem.pushMatrix();
						RenderSystem.enableAlphaTest();
						RenderSystem.enableBlend();
						RenderSystem.defaultBlendFunc();
						RenderSystem.defaultAlphaFunc();

						RenderSystem.setShaderTexture(0,color1);
						GL11.glColor3f(c1.getRed() / 255f, c1.getGreen() / 255f, c1.getBlue() / 255f);
						blit(mStack, x, y, 0, 0, width, height, width, height);

						RenderSystem.setShaderTexture(0,color2);
						GL11.glColor3f(c2.getRed() / 255f, c2.getGreen() / 255f, c2.getBlue() / 255f);
						blit(mStack, x, y, 0, 0, width, height, width, height);

						RenderSystem.setShaderTexture(0,color3);
						GL11.glColor3f(c3.getRed() / 255f, c3.getGreen() / 255f, c3.getBlue() / 255f);
						blit(mStack, x, y, 0, 0, width, height, width, height);
						RenderSystem.popMatrix();

						TextComponent buttonText = this.getMessage();
						int strWidth = Minecraft.getInstance().font.width(buttonText);
						int ellipsisWidth = Minecraft.getInstance().font.width("...");

						if (strWidth > width - 6 && strWidth > ellipsisWidth)
							buttonText = new TextComponent(Minecraft.getInstance().font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString() + "...");

						drawCenteredString(mStack, Minecraft.getInstance().font, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, getFGColor());
					}
					*/

					@Override
					public int getBlitOffset(){
						return 10;
					}
				};
			}
		}else{
			button.y = pTop;
			button.visible = source.visible;
			button.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);
		}
	}

	@Override
	public List<? extends NarratableEntry> narratables(){
		return null;
	}
}