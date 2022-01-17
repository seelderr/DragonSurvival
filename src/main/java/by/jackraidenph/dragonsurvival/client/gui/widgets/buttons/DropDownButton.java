package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownEntry;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownValueEntry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.lwjgl.opengl.GL11;

import java.util.Locale;
import java.util.function.Consumer;

public class DropDownButton extends ExtendedButton
{
	public String current;
	private String[] values;
	public Consumer<String> setter;
	
	private boolean toggled;
	private static final int maxItems = 4;
	
	private DropdownList list;
	private Widget renderButton;
	
	private ITextComponent message;
	
	public DropDownButton(int x, int y, int xSize, int ySize, String current, String[] values, Consumer<String> setter)
	{
		super(x, y, xSize, ySize, null, null);
		this.values = values;
		this.setter = setter;
		this.current = current;
		updateMessage();
	}
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		
		if(toggled && (!visible || (!isMouseOver(p_230430_2_, p_230430_3_) && !list.isMouseOver(p_230430_2_, p_230430_3_)))){
			toggled = false;
			Screen screen = Minecraft.getInstance().screen;
			screen.children.removeIf((s) -> s == list);
			screen.buttons.removeIf((s) -> s == renderButton);
		}
		
		
		if(toggled && list != null){
			list.reposition(x, y + height, width, (int)(Math.max(1, Math.min(values.length, maxItems)) * (height * 1.5f)));
		}
	}
	
	public void updateMessage(){
		if(current != null) {
			message = new StringTextComponent(current.substring(0, 1).toUpperCase(Locale.ROOT) + current.substring(1).toLowerCase(Locale.ROOT));
		}
	}
	
	@Override
	public ITextComponent getMessage()
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
				screen.children.add(0, list);
				screen.children.add(list);
				
				for (IGuiEventListener child : screen.children) {
					if(child instanceof AbstractList){
						if(((AbstractList)child).renderTopAndBottom){
							hasBorder = true;
							break;
						}
					}
				}
				
			}else{
				screen.children.add(list);
			}
			
			boolean finalHasBorder = hasBorder;
			renderButton = new ExtendedButton(0, 0, 0, 0, StringTextComponent.EMPTY, null){
				@Override
				public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
				{
					this.active = this.visible = false;
					list.visible = DropDownButton.this.visible;
					
					if(finalHasBorder){
						GL11.glScissor(0,
						               (int)(32 * Minecraft.getInstance().getWindow().getGuiScale()),
						               Minecraft.getInstance().getWindow().getScreenWidth(),
						               Minecraft.getInstance().getWindow().getScreenHeight() - (int)((32) * Minecraft.getInstance().getWindow().getGuiScale())*2);
						GL11.glEnable(GL11.GL_SCISSOR_TEST);
					}
					
					if(list.visible) {
						list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
					}
					
					if(finalHasBorder){
						GL11.glDisable(GL11.GL_SCISSOR_TEST);
					}
				}
			};
			screen.buttons.add(renderButton);
		}else{
			screen.children.removeIf((s) -> s == list);
			screen.buttons.removeIf((s) -> s == renderButton);
		}
		
		toggled = !toggled;
		updateMessage();
	}
}
