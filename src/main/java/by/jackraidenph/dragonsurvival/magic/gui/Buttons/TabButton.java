package by.jackraidenph.dragonsurvival.magic.gui.Buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.gui.DragonScreen;
import by.jackraidenph.dragonsurvival.magic.gui.AbilityScreen;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class TabButton extends Button
{
	public static final ResourceLocation buttonTexture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/buttons.png");
	private int index;
	private Screen parent;
	
	public TabButton(int x, int y, int index, Screen parent)
	{
		super(x, y, 28, 32, null, (button) -> {});
		this.index = index;
		this.parent = parent;
	}
	
	@Override
	public void renderButton(MatrixStack stack, int mouseX, int mouseY, float p_230431_4_)
	{
		Minecraft.getInstance().getTextureManager().bind(buttonTexture);
		
		if(isCurrent()){
			blit(stack, x, y, index == 0 ? 0 : 28, 0, 28, 32);
		}else{
			if(isHovered()){
				blit(stack, x, y, 84, 0, 28, 32);
				
			}else{
				blit(stack, x, y, 56, 0, 28, 32);
			}
		}
		
		
		if(isHovered() || isCurrent()){
			blit(stack, x + 2, y + 2 + (isCurrent() ? 2 : 0), (index * 24), 67, 24, 24);
		}else{
			blit(stack, x + 2, y + 2 + (isCurrent() ? 2 : 0), (index * 24), 41, 24, 24);
		}
	}
	
	public boolean isCurrent(){
		switch(index){
			case 0:
				return parent instanceof DragonScreen || parent instanceof InventoryScreen;
			
			case 1:
				return parent instanceof AbilityScreen;
		}
		return false;
	}
	
	@Override
	public void onPress()
	{
		if(!isCurrent()){
			switch(index){
				case 0:
					Minecraft.getInstance().setScreen(new InventoryScreen(Minecraft.getInstance().player));
					//DragonSurvivalMod.CHANNEL.sendToServer(new OpenDragonInventory());
				break;
				
				case 1:
					Minecraft.getInstance().setScreen(new AbilityScreen());
					break;
			}
		}
	}
}
