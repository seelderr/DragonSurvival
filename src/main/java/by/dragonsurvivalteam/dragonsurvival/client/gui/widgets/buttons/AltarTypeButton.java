package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonAltarGUI;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncAltarCooldown;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.CompleteDataSync;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AltarTypeButton extends Button{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_altar_icons.png");

	public DragonType type;
	DragonAltarGUI gui;

	public AltarTypeButton(DragonAltarGUI gui, DragonType type, int x, int y){
		super(x, y, 49, 147, null, null);
		this.gui = gui;
		this.type = type;
	}

	@Override
	public void onPress(){
		initiateDragonForm(type);
	}

	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float p_230431_4_){
		final boolean atTheTopOrBottom = (mouseY > y + 6 && mouseY < y + 26) || (mouseY > y + 133 && mouseY < y + 153);

		Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);

		fill(mStack, x - 1, y - 1, x + width + 1, y + height + 1, new Color(0.5f, 0.5f, 0.5f).getRGB());
		blit(mStack, x, y, (type.ordinal() * 49), isHovered ? 0 : 147, 49, 147, 512, 512);

		if(isHovered && atTheTopOrBottom){
			gui.renderWrappedToolTip(mStack, altarDragonInfoLocalized((type == DragonType.NONE ? "human" : type.name().toLowerCase() + "_dragon"), type == DragonType.NONE ? Collections.emptyList() : DragonFoodHandler.getSafeEdibleFoods(type)), mouseX, mouseY, Minecraft.getInstance().font);
		}
	}

	private ArrayList<ITextComponent> altarDragonInfoLocalized(String dragonType, List<Item> foodList){
		ArrayList<ITextComponent> info = new ArrayList<ITextComponent>();
		ITextComponent foodInfo = StringTextComponent.EMPTY;

		if(Screen.hasShiftDown()){
			if(!Objects.equals(dragonType, "human")){
				String food = "";
				for(Item item : foodList){
					food += (item.getName(new ItemStack(item)).getString() + "; ");
				}
				foodInfo = ITextComponent.nullToEmpty(food);
			}
		}else{
			foodInfo = new TranslationTextComponent("ds.hold_shift.for_food");
		}

		TranslationTextComponent textComponent = new TranslationTextComponent("ds.altar_dragon_info." + dragonType, foodInfo.getString());
		String text = textComponent.getString();
		for(String s : text.split("\n")){
			info.add(new StringTextComponent(s));
		}
		return info;
	}

	private void initiateDragonForm(DragonType type){
		ClientPlayerEntity player = Minecraft.getInstance().player;

		if(player == null){
			return;
		}

		if(type == DragonType.NONE){
			Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("ds.choice_human"), Minecraft.getInstance().player.getUUID());

			DragonStateProvider.getCap(player).ifPresent(cap -> {
				player.level.playSound(player, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 1, 0.7f);

				cap.setType(type);

				if(type == DragonType.NONE){
					cap.setSize(20F);
					cap.setHasWings(false);
				}

				cap.setIsHiding(false);
				cap.getMovementData().spinLearned = false;

				NetworkHandler.CHANNEL.sendToServer(new CompleteDataSync(Minecraft.getInstance().player.getId(), cap.writeNBT()));
				NetworkHandler.CHANNEL.sendToServer(new SyncAltarCooldown(Minecraft.getInstance().player.getId(), Functions.secondsToTicks(ConfigHandler.SERVER.altarUsageCooldown.get())));
				NetworkHandler.CHANNEL.sendToServer(new SyncSpinStatus(Minecraft.getInstance().player.getId(), cap.getMovementData().spinAttack, cap.getMovementData().spinCooldown, cap.getMovementData().spinLearned));
				ClientEvents.sendClientData(new RequestClientData(cap.getType(), cap.getLevel()));
			});
			player.closeContainer();
		}else{
			Minecraft.getInstance().setScreen(new DragonEditorScreen(gui, type));
		}
	}
}