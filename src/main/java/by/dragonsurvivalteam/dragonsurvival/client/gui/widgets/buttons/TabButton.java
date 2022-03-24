<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/TabButton.java
package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.gui.AbilityScreen;
import by.jackraidenph.dragonsurvival.client.gui.DragonScreen;
import by.jackraidenph.dragonsurvival.client.gui.SkinsScreen;
import by.jackraidenph.dragonsurvival.client.gui.utils.TooltipProvider;
import by.jackraidenph.dragonsurvival.client.handlers.ClientEvents;
import by.jackraidenph.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.container.OpenDragonInventory;
import by.jackraidenph.dragonsurvival.network.container.OpenInventory;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
=======
package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.AbilityScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.SkinsScreen;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonInventory;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenInventory;
import com.mojang.blaze3d.matrix.MatrixStack;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/TabButton.java
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/TabButton.java
public class TabButton extends Button implements TooltipProvider
{
	private int index;
	private Screen parent;
	
	public TabButton(int x, int y, int index, Screen parent)
	{
=======
public class TabButton extends Button{
	private final int index;
	private final Screen parent;

	public TabButton(int x, int y, int index, Screen parent){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/TabButton.java
		super(x, y, 28, 32, null, (button) -> {});
		this.index = index;
		this.parent = parent;
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/TabButton.java
	
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
	
=======

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/TabButton.java
	@Override
	public void onPress(){
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
					}else{
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/TabButton.java
	
	@Override
	public List<Component> getTooltip()
	{
		return List.of(new TranslatableComponent("ds.gui.tab_button." + index));
	}
}
=======

	@Override
	public void renderButton(MatrixStack stack, int mouseX, int mouseY, float p_230431_4_){
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
		}
		return false;
	}
}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/TabButton.java
