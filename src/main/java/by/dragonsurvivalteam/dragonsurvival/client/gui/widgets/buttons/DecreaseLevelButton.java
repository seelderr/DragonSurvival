package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;


import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.ArrowButton;
import by.dragonsurvivalteam.dragonsurvival.client.util.TooltipRendering;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncSkillLevelChangeCost;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class DecreaseLevelButton extends ArrowButton{
	private final int slot;
	private PassiveDragonAbility ability;
	private AbstractDragonType type;

	public DecreaseLevelButton(int x, int y, int slot){

		super(x, y, 15, 17, false, button -> {});
		this.slot = slot;

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			type = cap.getType();
		});
	}

	@Override
	public void onPress(){
		super.onPress();

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			ability = cap.getMagicData().getPassiveAbilityFromSlot(slot);

			if(ability != null){
				if(ability.level-1 >= ability.getMinLevel()){
					DragonAbilities.setAbilityLevel(Minecraft.getInstance().player, ability.getClass(),ability.getLevel() - 1);
					NetworkHandler.CHANNEL.sendToServer(new SyncSkillLevelChangeCost(ability.getLevel() - 1, ability.getName(), -1));
				}
			}
		});
	}

	@Override
	public void renderToolTip(PoseStack stack, int mouseX, int mouseY){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			ability = cap.getMagicData().getPassiveAbilityFromSlot(slot);

			if(ability != null){
				if(ability.getLevel() > ability.getMinLevel())
					TooltipRendering.drawHoveringText(stack, Component.translatable("ds.skill.level.down", (int)Math.max(1, ability.getLevelCost() * 0.8F)), mouseX, mouseY);
			}
		});
	}
}