package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.ArrowButton;
import by.dragonsurvivalteam.dragonsurvival.client.util.TooltipRendering;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncSkillLevelChangeCost;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.Arrays;


public class IncreaseLevelButton extends ArrowButton{
	private final int slot;
	public int skillCost = 0;
	private PassiveDragonAbility ability;
	private DragonType type;

	public IncreaseLevelButton(int x, int y, int slot){

		super(x, y, 15, 17, true, button -> {});
		this.slot = slot;

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			type = cap.getType();
		});
	}


	@Override
	public void onPress(){
		super.onPress();

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			ability = cap.getMagic().getPassiveAbilityFromSlot(slot);

			if(ability != null){
				if(ability.getLevel() + 1 <= ability.getMaxLevel()){
					try{
						PassiveDragonAbility newActivty = ability.getClass().newInstance();
						newActivty.setLevel(ability.getLevel() + 1);

						if(Minecraft.getInstance().player.experienceLevel >= newActivty.getLevelCost() || Minecraft.getInstance().player.isCreative()){
							NetworkHandler.CHANNEL.sendToServer(new SyncSkillLevelChangeCost(ability.getLevel() + 1, ability.getName(), 1));
							DragonAbilities.setAbilityLevel(Minecraft.getInstance().player, ability.getClass(), ability.getLevel() + 1);
							newActivty.setLevel(ability.getLevel() + 2);
							lastStore = newActivty;
						}

					}catch(InstantiationException | IllegalAccessException e){
						throw new RuntimeException(e);
					}
				}
			}
		});
	}


	private PassiveDragonAbility lastStore = null;
	@Override
	public void renderToolTip(PoseStack stack, int mouseX, int mouseY){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			ability = cap.getMagic().getPassiveAbilityFromSlot(slot);

			ChatFormatting format = cap.getType() == DragonType.CAVE ? ChatFormatting.DARK_RED : cap.getType() == DragonType.SEA ? ChatFormatting.AQUA : cap.getType() == DragonType.FOREST ? ChatFormatting.GREEN : ChatFormatting.WHITE;
			ArrayList<Component> description = new ArrayList<>(Arrays.asList(new TranslatableComponent("ds.skill.level.up", skillCost).withStyle(format)));

			if(ability != null){
				if(ability.getLevelUpInfo().size() > 0){
					description.add(new TextComponent(""));
					description.addAll(ability.getLevelUpInfo());
				}

				if(ability.getLevel() < ability.getMaxLevel()){
					try{
						PassiveDragonAbility newActivty = lastStore != null && lastStore.getClass() == ability.getClass() ? lastStore : (PassiveDragonAbility)ability.getClass().newInstance();
						newActivty.setLevel(ability.getLevel() + 1);
						lastStore = newActivty;

						skillCost = newActivty.getLevelCost();
						TooltipRendering.drawHoveringText(stack, description, mouseX, mouseY);
					}catch(InstantiationException | IllegalAccessException e){
						throw new RuntimeException(e);
					}
				}
			}
		});
	}
}