package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.DragonAltarGUI;
import by.jackraidenph.dragonsurvival.client.gui.DragonCustomizationScreen;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.handlers.DragonFoodHandler;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.RequestClientData;
import by.jackraidenph.dragonsurvival.network.SynchronizationController;
import by.jackraidenph.dragonsurvival.network.entity.player.SynchronizeDragonCap;
import by.jackraidenph.dragonsurvival.network.flight.SyncSpinStatus;
import by.jackraidenph.dragonsurvival.network.status.SyncAltarCooldown;
import by.jackraidenph.dragonsurvival.util.Functions;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AltarTypeButton extends Button
{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_altar_icons.png");
	
	public DragonType type;
	DragonAltarGUI gui;
	
	public AltarTypeButton(DragonAltarGUI gui, DragonType type, int x, int y)
	{
		super(x, y, 49, 147, null, null);
		this.gui = gui;
		this.type = type;
	}
	
	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float p_230431_4_)
	{
		final boolean atTheTopOrBottom = (mouseY > y + 6 && mouseY < y + 26) || (mouseY > y + 133 && mouseY < y + 153);
		
		Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);
		
		fill(mStack, x-1, y-1, x + width+1, y + height+1, new Color(0.5f, 0.5f, 0.5f).getRGB());
		blit(mStack, x, y, (type.ordinal() * 49), isHovered ? 0 : 147, 49, 147, 512, 512);
		
		if (isHovered && atTheTopOrBottom) {
			gui.renderWrappedToolTip(mStack, altarDragonInfoLocalized((type == DragonType.NONE ? "human" : type.name().toLowerCase() + "_dragon"), type == DragonType.NONE ? Collections.emptyList() : DragonFoodHandler.getSafeEdibleFoods(type)), mouseX, mouseY, Minecraft.getInstance().font);
		}
	}
	
	private ArrayList<ITextComponent> altarDragonInfoLocalized(String dragonType, List<Item> foodList) {
		ArrayList<ITextComponent> info = new ArrayList<ITextComponent>();
		ITextComponent foodInfo = StringTextComponent.EMPTY;
		
		if (gui.hasShiftDown()) {
			if (!Objects.equals(dragonType, "human")) {
				String food = "";
				for (Item item : foodList) {
					food += (item.getName(new ItemStack(item)).getString() + "; ");
				}
				foodInfo = ITextComponent.nullToEmpty(food);
			}
		} else {
			foodInfo = new TranslationTextComponent("ds.hold_shift.for_food");
		}
		
		TranslationTextComponent textComponent = new TranslationTextComponent("ds.altar_dragon_info." + dragonType, foodInfo.getString());
		String text = textComponent.getString();
		for (String s : text.split("\n")) {
			info.add(new StringTextComponent(s));
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
		ClientPlayerEntity player = Minecraft.getInstance().player;
		
		if (player == null) return;
		
		if(type == DragonType.NONE){
			Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("ds.choice_human"), Minecraft.getInstance().player.getUUID());
			
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				player.level.playSound(player, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 1, 0.7f);
				
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
				SynchronizationController.sendClientData(new RequestClientData(cap.getType(), cap.getLevel()));
			});
			player.closeContainer();
		}else{
			Minecraft.getInstance().setScreen(new DragonCustomizationScreen(gui, type));
		}
	}
}
