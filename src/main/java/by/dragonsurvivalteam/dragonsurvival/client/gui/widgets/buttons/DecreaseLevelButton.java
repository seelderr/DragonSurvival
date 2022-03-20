package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.AbilityScreen;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.magic.ChangeSkillLevel;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.Arrays;

public class DecreaseLevelButton extends ArrowButton{
	private final int slot;

	private DragonAbility ability;
	private DragonType type;
	private final AbilityScreen screen;

	public DecreaseLevelButton(int x, int y, int slot, AbilityScreen screen){
		super(x, y, 15, 17, false, (button) -> {});
		this.slot = slot;
		this.screen = screen;

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			type = cap.getType();
		});
	}

	@Override
	public void onPress(){
		super.onPress();

		ability = DragonAbilities.PASSIVE_ABILITIES.get(type).get(slot);

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			if(ability != null){
				if(cap.getMagic().getAbility(ability) != null && cap.getMagic().getAbilityLevel(ability) > ability.getMinLevel()){
					NetworkHandler.CHANNEL.sendToServer(new ChangeSkillLevel(cap.getMagic().getAbilityLevel(ability) - 1, ability.getId(), -1));
				}
			}
		});
	}

	@Override
	public void renderToolTip(MatrixStack stack, int mouseX, int mouseY){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			ability = DragonAbilities.PASSIVE_ABILITIES.get(type).get(slot);

			if(ability != null){
				PassiveDragonAbility currentAbility = (PassiveDragonAbility)cap.getMagic().getAbility(ability);

				if(ability != null && currentAbility == null){
					currentAbility = (PassiveDragonAbility)ability;
				}

				if(currentAbility != null && currentAbility.getLevel() > currentAbility.getMinLevel()){
					GuiUtils.drawHoveringText(stack, Arrays.asList(new TranslationTextComponent("ds.skill.level.down", (int)Math.max(1, currentAbility.getLevelCost() * 0.8F))), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}
		});
	}
}