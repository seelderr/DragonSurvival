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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.Arrays;

public class TabButton extends Button{
	private final int index;
	private final Screen parent;

	public TabButton(int x, int y, int index, Screen parent){
		super(x, y, 28, 32, null, (button) -> {});
		this.index = index;
		this.parent = parent;
	}

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