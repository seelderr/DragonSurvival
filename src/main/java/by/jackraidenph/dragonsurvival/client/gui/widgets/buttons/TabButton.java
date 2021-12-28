package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.gui.settings.SettingsSideScreen;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.client.gui.DragonScreen;
import by.jackraidenph.dragonsurvival.client.gui.SkinsScreen;
import by.jackraidenph.dragonsurvival.client.gui.AbilityScreen;
import by.jackraidenph.dragonsurvival.client.handlers.ClientEvents;
import by.jackraidenph.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.container.OpenInventory;
import by.jackraidenph.dragonsurvival.network.container.OpenDragonInventory;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.Arrays;

public class TabButton extends Button
{
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
		Minecraft.getInstance().getTextureManager().bind(ClientMagicHUDHandler.widgetTextures);
		
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
		
		if(isHovered()){
			GuiUtils.drawHoveringText(stack, Arrays.asList(new TranslationTextComponent("ds.gui.tab_button." + index)), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
		}
	}
	
	public boolean isCurrent(){
		switch(index){
			case 0:
				return parent instanceof DragonScreen || parent instanceof InventoryScreen;
			
			case 1:
				return parent instanceof AbilityScreen;
			
			case 3:
				return parent instanceof SkinsScreen;
			
			case 4:
				return parent instanceof SettingsSideScreen;
		}
		return false;
	}
	
	@Override
	public void onPress()
	{
		if(!isCurrent()){
			switch(index){
				case 0:
					if(parent instanceof AbilityScreen){
						if(((AbilityScreen)parent).sourceScreen != null){
							if(((AbilityScreen)parent).sourceScreen instanceof InventoryScreen){
								Minecraft.getInstance().setScreen(new InventoryScreen(Minecraft.getInstance().player));
								NetworkHandler.CHANNEL.sendToServer(new OpenInventory());
								break;
							}else if(((AbilityScreen)parent).sourceScreen instanceof DragonScreen){
								ClientEvents.mouseX = Minecraft.getInstance().mouseHandler.xpos();
								ClientEvents.mouseY = Minecraft.getInstance().mouseHandler.ypos();
								NetworkHandler.CHANNEL.sendToServer(new OpenDragonInventory());
								break;
							}
						}
					}else if(parent instanceof SkinsScreen){
						if(((SkinsScreen)parent).sourceScreen != null){
							if(((SkinsScreen)parent).sourceScreen instanceof InventoryScreen){
								Minecraft.getInstance().setScreen(new InventoryScreen(Minecraft.getInstance().player));
								NetworkHandler.CHANNEL.sendToServer(new OpenInventory());
								break;
							}else if(((SkinsScreen)parent).sourceScreen instanceof DragonScreen){
								ClientEvents.mouseX = Minecraft.getInstance().mouseHandler.xpos();
								ClientEvents.mouseY = Minecraft.getInstance().mouseHandler.ypos();
								NetworkHandler.CHANNEL.sendToServer(new OpenDragonInventory());
								break;
							}
						}
					}
					
					if(ConfigHandler.CLIENT.dragonInventory.get()){
						ClientEvents.mouseX = Minecraft.getInstance().mouseHandler.xpos();
						ClientEvents.mouseY = Minecraft.getInstance().mouseHandler.ypos();
						NetworkHandler.CHANNEL.sendToServer(new OpenDragonInventory());
					}else {
						Minecraft.getInstance().setScreen(new InventoryScreen(Minecraft.getInstance().player));
						NetworkHandler.CHANNEL.sendToServer(new OpenInventory());
					}
					break;
				
				case 1:
					Minecraft.getInstance().setScreen(new AbilityScreen(parent));
					break;
				
				case 3:
					Minecraft.getInstance().setScreen(new SkinsScreen(parent));
					break;
				
				case 4:
					Minecraft.getInstance().setScreen(new SettingsSideScreen(parent, Minecraft.getInstance().options, new TranslationTextComponent("ds.gui.tab_button.4")));
					break;
			}
		}
	}
}
