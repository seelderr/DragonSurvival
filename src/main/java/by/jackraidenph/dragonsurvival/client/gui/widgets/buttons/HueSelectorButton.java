package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationObject.Texture;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.DragonCustomizationHandler;
import by.jackraidenph.dragonsurvival.client.gui.DragonCustomizationScreen;
import by.jackraidenph.dragonsurvival.client.gui.HueSelectorComponent;
import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayerUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.HashMap;
import java.util.function.Consumer;

public class HueSelectorButton extends ExtendedButton
{

	public double min, max, current;
	public Consumer<Double> setter;
	
	public boolean toggled;
	
	private HueSelectorComponent component;
	private Widget renderButton;
	
	private DragonCustomizationScreen screen;
	private CustomizationLayer layer;
	public int xSize, ySize;
	
	public HueSelectorButton(DragonCustomizationScreen screen, CustomizationLayer layer, int x, int y, int xSize, int ySize, double min, double max, double current, Consumer<Double> setter)
	{
		super(x, y, xSize, ySize, null, null);
		this.xSize = xSize;
		this.ySize = ySize;
		this.setter = setter;
		this.min = min;
		this.max = max;
		this.current = current;
		this.screen = screen;
		this.layer = layer;
	}
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		
		if(toggled && (!visible || (!isMouseOver(p_230430_2_, p_230430_3_) && !component.isMouseOver(p_230430_2_, p_230430_3_)))){
			toggled = false;
			Screen screen = Minecraft.getInstance().screen;
			screen.children.removeIf((s) -> s == component);
			screen.buttons.removeIf((s) -> s == renderButton);
		}
		
		Texture text = DragonCustomizationHandler.getSkin(FakeClientPlayerUtils.getFakePlayer(0, screen.handler), layer, screen.map.getOrDefault(screen.handler.getLevel(), new HashMap<>()).getOrDefault(layer, null), screen.handler.getType());
		
		if(text != null && text.recolor){
			active = true;
		}else{
			active = false;
		}
	}
	
	@Override
	public ITextComponent getMessage()
	{
		return StringTextComponent.EMPTY;
	}
	
	@Override
	public void onPress()
	{
		Screen screen = Minecraft.getInstance().screen;
		
		if(!toggled){
			component = new HueSelectorComponent(this.screen, x + xSize - 120, y, 120, 30, layer);
			renderButton = new ExtendedButton(0, 0, 0, 0, StringTextComponent.EMPTY, null){
				@Override
				public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
				{
					this.active = this.visible = false;
					component.visible = HueSelectorButton.this.visible;
					
					if(component.visible) {
						component.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
					}
				}
			};
			
			screen.children.add(0, component);
			screen.children.add(component);
			screen.buttons.add(renderButton);
		}else{
			screen.children.removeIf((s) -> s == component);
			screen.buttons.removeIf((s) -> s == renderButton);
		}
		
		toggled = !toggled;
	}
}
