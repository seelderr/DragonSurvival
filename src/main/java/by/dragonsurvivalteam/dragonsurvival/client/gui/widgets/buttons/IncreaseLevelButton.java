<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/IncreaseLevelButton.java
package by.jackraidenph.dragonsurvival.client.gui.widgets.buttons;

import by.jackraidenph.dragonsurvival.client.gui.AbilityScreen;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.common.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.magic.ChangeSkillLevel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
=======
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/IncreaseLevelButton.java

import java.util.ArrayList;
import java.util.Arrays;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/IncreaseLevelButton.java
public class IncreaseLevelButton extends ArrowButton
{
	private int slot;
	
	private DragonAbility ability;
	private DragonType type;
	private AbilityScreen screen;
	
	public int skillCost = 0;
	
	public IncreaseLevelButton(int x, int y, int slot, AbilityScreen screen)
	{
=======
public class IncreaseLevelButton extends ArrowButton{
	public int skillCost = 0;
	private final int slot;
	private DragonAbility ability;
	private DragonType type;
	private final AbilityScreen screen;

	public IncreaseLevelButton(int x, int y, int slot, AbilityScreen screen){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/IncreaseLevelButton.java
		super(x, y, 15, 17, true, (button) -> {});
		this.slot = slot;
		this.screen = screen;

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			type = cap.getType();
		});
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/IncreaseLevelButton.java
	
	@Override
	public void onPress()
	{
=======

	@Override
	public void onPress(){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/IncreaseLevelButton.java
		super.onPress();

		ability = DragonAbilities.PASSIVE_ABILITIES.get(type).get(slot);

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			if(ability != null){
				if(cap.getMagic().getAbilityLevel(ability) + 1 <= ability.getMaxLevel()){

					if(ability != null){
						PassiveDragonAbility currentAbility = (PassiveDragonAbility)cap.getMagic().getAbility(ability);

						if(ability != null && currentAbility == null){
							currentAbility = (PassiveDragonAbility)ability;
						}

						if(currentAbility != null && currentAbility.getLevel() < currentAbility.getMaxLevel()){
							PassiveDragonAbility newActivty = currentAbility.createInstance();
							newActivty.setLevel(currentAbility.getLevel() + 1);

							if(Minecraft.getInstance().player.experienceLevel >= newActivty.getLevelCost() || Minecraft.getInstance().player.isCreative()){
								NetworkHandler.CHANNEL.sendToServer(new ChangeSkillLevel(cap.getMagic().getAbilityLevel(ability) + 1, ability.getId(), 1));
							}
						}else{
							skillCost = -1;
						}
					}
				}
			}
		});
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/IncreaseLevelButton.java
	public void renderToolTip(PoseStack stack, int mouseX, int mouseY)
	{
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			ability = DragonAbilities.PASSIVE_ABILITIES.get(type).get(slot);
			ChatFormatting format = cap.getType() == DragonType.CAVE ? ChatFormatting.DARK_RED : cap.getType() == DragonType.SEA ? ChatFormatting.AQUA : cap.getType() == DragonType.FOREST ? ChatFormatting.GREEN : ChatFormatting.WHITE;
			ArrayList<Component> description = new ArrayList<>(Arrays.asList(new TranslatableComponent("ds.skill.level.up", skillCost).withStyle(format)));
			
			if(ability != null) {
=======
	public void renderToolTip(MatrixStack stack, int mouseX, int mouseY){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			ability = DragonAbilities.PASSIVE_ABILITIES.get(type).get(slot);
			TextFormatting format = cap.getType() == DragonType.CAVE ? TextFormatting.DARK_RED : cap.getType() == DragonType.SEA ? TextFormatting.AQUA : cap.getType() == DragonType.FOREST ? TextFormatting.GREEN : TextFormatting.WHITE;
			ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(new TranslationTextComponent("ds.skill.level.up", skillCost).withStyle(format)));

			if(ability != null){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/IncreaseLevelButton.java
				PassiveDragonAbility currentAbility = (PassiveDragonAbility)cap.getMagic().getAbility(ability);

				if(ability != null && currentAbility == null){
					currentAbility = (PassiveDragonAbility)ability;
				}

				if(ability.getLevelUpInfo().size() > 0){
					description.add(new TextComponent(""));
					description.addAll(ability.getLevelUpInfo());
				}


				if(currentAbility != null && currentAbility.getLevel() < currentAbility.getMaxLevel()){
					PassiveDragonAbility newActivty = currentAbility.createInstance();
					newActivty.setLevel(currentAbility.getLevel() + 1);
					skillCost = newActivty.getLevelCost();
					screen.renderComponentTooltip(stack, description, mouseX, mouseY);
				}
			}
		});
	}
}