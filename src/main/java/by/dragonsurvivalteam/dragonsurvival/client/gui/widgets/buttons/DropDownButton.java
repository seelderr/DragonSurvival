package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownEntry;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownValueEntry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;


public class DropDownButton extends ExtendedButton implements TooltipAccessor
{
	public String current;
	private String[] values;
	public Consumer<String> setter;
	
	private boolean toggled;
	private static final int maxItems = 4;
	
	private DropdownList list;
	private AbstractWidget renderButton;
	public List<FormattedCharSequence> tooltip;
	
	private TextComponent message;
	
	public DropDownButton(int x, int y, int xSize, int ySize, String current, String[] values, Consumer<String> setter)
	{
		super(x, y, xSize, ySize, null, null);
		this.values = values;
		this.setter = setter;
		this.current = current;
		updateMessage();
	}
	
	@Override
	public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		
		if(toggled && (!visible || (!isMouseOver(p_230430_2_, p_230430_3_) && !list.isMouseOver(p_230430_2_, p_230430_3_)))){
			toggled = false;
			Screen screen = Minecraft.getInstance().screen;
			screen.children.removeIf((s) -> s == list);
			screen.children.removeIf((s) -> s == renderButton);
			screen.renderables.removeIf((s) -> s == list);
			screen.renderables.removeIf((s) -> s == renderButton);
			//screen.buttons.removeIf((s) -> s == renderButton);
		}
		
		
		if(toggled && list != null){
			list.reposition(x, y + height, width, (int)(Math.max(1, Math.min(values.length, maxItems)) * (height * 1.5f)));
		}
	}
	
	public void updateMessage(){
		if(current != null) {
			message = new TextComponent(current.substring(0, 1).toUpperCase(Locale.ROOT) + current.substring(1).toLowerCase(Locale.ROOT));
		}
	}
	
	@Override
	public TextComponent getMessage()
	{
		return message;
	}
	
	public DropdownEntry createEntry(int pos, String val){
		return new DropdownValueEntry(this, pos, val, setter);
	}
	
	@Override
	public void onPress()
	{
		Screen screen = Minecraft.getInstance().screen;
		
		if(!toggled){
			list = new DropdownList(x, y + height, width, (int)(Math.max(1, Math.min(values.length, maxItems)) * (height * 1.5f)), 19);
			
			for (int i = 0; i < values.length; i++) {
				String val = values[i];
				list.addEntry(createEntry(i, val));
			}
			
			boolean hasBorder = false;
			if(screen.children.size() > 0){
				screen.renderables.add(0, list);
				screen.renderables.add(list);
				screen.children.add(0, list);
				screen.children.add(list);
				
				for (GuiEventListener child : screen.children) {
					if(child instanceof ContainerObjectSelectionList){
						if(((ContainerObjectSelectionList)child).renderTopAndBottom){
							hasBorder = true;
							break;
						}
					}
				}
				
			}else{
				screen.children.add(list);
				screen.renderables.add(list);
			}
			
			boolean finalHasBorder = hasBorder;
			renderButton = new ExtendedButton(0, 0, 0, 0, TextComponent.EMPTY, null){
				@Override
				public void render(PoseStack  p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
				{
					this.active = this.visible = false;
					list.visible = DropDownButton.this.visible;
					
					if(finalHasBorder){
						RenderSystem.enableScissor(0,
						               (int)(32 * Minecraft.getInstance().getWindow().getGuiScale()),
						               Minecraft.getInstance().getWindow().getScreenWidth(),
						               Minecraft.getInstance().getWindow().getScreenHeight() - (int)((32) * Minecraft.getInstance().getWindow().getGuiScale())*2);
					}
					
					if(list.visible) {
						list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
					}
					
					if(finalHasBorder){
						RenderSystem.disableScissor();
					}
				}
			};
			screen.children.add(renderButton);
			screen.renderables.add(renderButton);
		}else{
			screen.children.removeIf((s) -> s == list);
			screen.children.removeIf((s) -> s == renderButton);
			screen.renderables.removeIf((s) -> s == list);
			screen.renderables.removeIf((s) -> s == renderButton);
		}
		
		toggled = !toggled;
		updateMessage();
	}
	
	@Override
	public List<FormattedCharSequence> getTooltip()
	{
		return tooltip;
	}
}
