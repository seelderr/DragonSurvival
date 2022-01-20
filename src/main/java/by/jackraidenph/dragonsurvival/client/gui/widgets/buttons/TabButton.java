package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.gui.AbilityScreen;
import by.jackraidenph.dragonsurvival.client.gui.DragonScreen;
import by.jackraidenph.dragonsurvival.client.gui.SkinsScreen;
import by.jackraidenph.dragonsurvival.client.handlers.ClientEvents;
import by.jackraidenph.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.container.OpenDragonInventory;
import by.jackraidenph.dragonsurvival.network.container.OpenInventory;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.TranslatableComponent;

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
	public void renderButton(PoseStack stack, int mouseX, int mouseY, float p_230431_4_)
	{
		RenderSystem.setShaderTexture(0, ClientMagicHUDHandler.widgetTextures);
		
		if(isCurrent()){
			blit(stack, x, y, index == 0 ? 0 : 28, 0, 28, 32);
		}else{
			if(isHovered){
				blit(stack, x, y, 84, 0, 28, 32);
				
			}else{
				blit(stack, x, y, 56, 0, 28, 32);
			}
		}
		
		
		if(isHovered || isCurrent()){
			blit(stack, x + 2, y + 2 + (isCurrent() ? 2 : 0), (index * 24), 67, 24, 24);
		}else{
			blit(stack, x + 2, y + 2 + (isCurrent() ? 2 : 0), (index * 24), 41, 24, 24);
		}
		
		if(isHovered){
			parent.renderComponentTooltip(stack, Arrays.asList(new TranslatableComponent("ds.gui.tab_button." + index)), mouseX, mouseY);
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
			}
		}
	}
}
