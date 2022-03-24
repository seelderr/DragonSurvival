package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.DragonAltarGUI;
import by.jackraidenph.dragonsurvival.client.gui.DragonCustomizationScreen;
import by.jackraidenph.dragonsurvival.client.gui.utils.TooltipProvider;
import by.jackraidenph.dragonsurvival.client.handlers.ClientEvents;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.handlers.DragonFoodHandler;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.RequestClientData;
import by.jackraidenph.dragonsurvival.network.entity.player.SynchronizeDragonCap;
import by.jackraidenph.dragonsurvival.network.flight.SyncSpinStatus;
import by.jackraidenph.dragonsurvival.network.status.SyncAltarCooldown;
import by.jackraidenph.dragonsurvival.util.Functions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AltarTypeButton extends Button implements TooltipProvider
{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_altar_icons.png");
	
	public DragonType type;
	private DragonAltarGUI gui;
	private boolean atTheTopOrBottom;

	public AltarTypeButton(DragonAltarGUI gui, DragonType type, int x, int y)
	{
		super(x, y, 49, 147, null, null);
		this.gui = gui;
		this.type = type;
		
	}
	
	@Override
	public void renderButton(PoseStack mStack, int mouseX, int mouseY, float p_230431_4_)
	{
		atTheTopOrBottom = (mouseY > y + 6 && mouseY < y + 26) || (mouseY > y + 133 && mouseY < y + 153);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		
		fill(mStack, x-1, y-1, x + width+1, y + height+1, new Color(0.5f, 0.5f, 0.5f).getRGB());
		blit(mStack, x, y, (type.ordinal() * 49), isHovered ? 0 : 147, 49, 147, 512, 512);
	}
	
	@Override
	public List<Component> getTooltip()
	{
		if(atTheTopOrBottom){
			return altarDragonInfoLocalized((type == DragonType.NONE ? "human" : type.name().toLowerCase() + "_dragon"), type == DragonType.NONE ? Collections.emptyList() : DragonFoodHandler.getSafeEdibleFoods(type));
		}
		return List.of();
	}
	
	private ArrayList<Component> altarDragonInfoLocalized(String dragonType, List<Item> foodList) {
		ArrayList<Component> info = new ArrayList<>();
		Component foodInfo = TextComponent.EMPTY;
		
		if (Screen.hasShiftDown()) {
			if (!Objects.equals(dragonType, "human")) {
				String food = "";
				for (Item item : foodList) {
					food += (item.getName(new ItemStack(item)).getString() + "; ");
				}
				foodInfo = Component.nullToEmpty(food);
			}
		} else {
			foodInfo = new TranslatableComponent("ds.hold_shift.for_food");
		}
		
		TranslatableComponent textComponent = new TranslatableComponent("ds.altar_dragon_info." + dragonType, foodInfo.getString());
		String text = textComponent.getString();
		for (String s : text.split("\n")) {
			info.add(new TextComponent(s));
		}
		return info;
	}
	
	@Override
	public void onPress()
	{
		initiateDragonForm(type);
	}
	
	private void initiateDragonForm(DragonType type)
	{
		LocalPlayer player = Minecraft.getInstance().player;
		
		if (player == null) return;
		
		if(type == DragonType.NONE){
			Minecraft.getInstance().player.sendMessage(new TranslatableComponent("ds.choice_human"), Minecraft.getInstance().player.getUUID());
			
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				player.level.playSound(player, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1, 0.7f);
				
				cap.setType(type);
				
				if (type == DragonType.NONE) {
					cap.setSize(20F);
					cap.setHasWings(false);
				}
				
				cap.setIsHiding(false);
				cap.getMovementData().spinLearned = false;
				
				NetworkHandler.CHANNEL.sendToServer(new SyncAltarCooldown(Minecraft.getInstance().player.getId(), Functions.secondsToTicks(ConfigHandler.SERVER.altarUsageCooldown.get())));
				NetworkHandler.CHANNEL.sendToServer(new SynchronizeDragonCap(player.getId(), cap.isHiding(), cap.getType(), cap.getSize(), cap.hasWings(), ConfigHandler.SERVER.caveLavaSwimmingTicks.get(), 0));
				NetworkHandler.CHANNEL.sendToServer(new SyncSpinStatus(Minecraft.getInstance().player.getId(), cap.getMovementData().spinAttack, cap.getMovementData().spinCooldown, cap.getMovementData().spinLearned));
				ClientEvents.sendClientData(new RequestClientData(cap.getType(), cap.getLevel()));
			});
			player.closeContainer();
		}else{
			Minecraft.getInstance().setScreen(new DragonCustomizationScreen(gui, type));
		}
	}
}
