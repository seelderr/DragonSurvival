package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.AbilityScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.SkinsScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.utils.TooltipRender;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.dragonsurvivalteam.dragonsurvival.client.util.TooltipRendering;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonInventory;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenInventory;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;


public class TabButton extends Button implements TooltipRender {
	private final Screen parent;
	private final TabType tabType;

	public enum TabType {
		INVENTORY,
		ABILITY,
		GITHUB_REMINDER,
		SKINS
	}

	public TabButton(int x, int y, TabType tabType, Screen parent){

		super(x, y, 28, 32, null, button -> {});
		this.tabType = tabType;
		this.parent = parent;
	}

	private void setInventoryScreen(Screen sourceScreen) {
		if (sourceScreen instanceof InventoryScreen) {
			Minecraft.getInstance().setScreen(new InventoryScreen(Minecraft.getInstance().player));
			NetworkHandler.CHANNEL.sendToServer(new OpenInventory());
		} else if (sourceScreen instanceof DragonScreen) {
			NetworkHandler.CHANNEL.sendToServer(new OpenDragonInventory());
		}
	}

	@Override
	public void onPress(){
		if(!isCurrent())
			switch(tabType){
				case INVENTORY -> {
					if(parent instanceof AbilityScreen){
						if(((AbilityScreen)parent).sourceScreen != null){
							setInventoryScreen(((AbilityScreen)parent).sourceScreen);
						}
					} else if(parent instanceof SkinsScreen){
						if(((SkinsScreen)parent).sourceScreen != null){
							setInventoryScreen(((SkinsScreen)parent).sourceScreen);
						}
					} else if(ClientEvents.dragonInventory){
						NetworkHandler.CHANNEL.sendToServer(new OpenDragonInventory());
					} else {
						Minecraft.getInstance().setScreen(new InventoryScreen(Minecraft.getInstance().player));
						NetworkHandler.CHANNEL.sendToServer(new OpenInventory());
					}
				}
				case ABILITY -> Minecraft.getInstance().setScreen(new AbilityScreen(parent));
				case SKINS -> Minecraft.getInstance().setScreen(new SkinsScreen(parent));
			}
	}

	public boolean isCurrent(){
		return switch(tabType){
			case INVENTORY -> parent instanceof DragonScreen || parent instanceof InventoryScreen;
			case ABILITY -> parent instanceof AbilityScreen;
			case SKINS -> parent instanceof SkinsScreen;
			default -> false;
		};
	}

	@Override
	public void renderButton(PoseStack stack, int mouseX, int mouseY, float p_230431_4_){
		RenderSystem.setShaderTexture(0, ClientMagicHUDHandler.widgetTextures);

		if(isCurrent())
			blit(stack, x, y, tabType.ordinal() == 0 ? 0 : 28, 0, 28, 32);
		else if(isHovered){
			blit(stack, x, y, 84, 0, 28, 32);
		}else{
			blit(stack, x, y, 56, 0, 28, 32);
		}


		if(isHovered || isCurrent())
			blit(stack, x + 2, y + 2 + (isCurrent() ? 2 : 0), tabType.ordinal() * 24, 67, 24, 24);
		else
			blit(stack, x + 2, y + 2 + (isCurrent() ? 2 : 0), tabType.ordinal() * 24, 41, 24, 24);
	}

	@Override
	public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY){
		super.renderToolTip(pPoseStack, pMouseX, pMouseY);
		TooltipRendering.drawHoveringText(pPoseStack, Component.translatable("ds.gui.tab_button." + tabType.ordinal()), pMouseX, pMouseY);
	}
}