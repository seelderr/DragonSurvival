package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/DecreaseLevelButton.java
import by.jackraidenph.dragonsurvival.client.gui.AbilityScreen;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.common.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.magic.ChangeSkillLevel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Arrays;

public class DecreaseLevelButton extends ArrowButton
{
	private int slot;
	
	private DragonAbility ability;
	private DragonType type;
	private AbilityScreen screen;
	
	public DecreaseLevelButton(int x, int y, int slot, AbilityScreen screen)
	{
=======
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
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/DecreaseLevelButton.java
		super(x, y, 15, 17, false, (button) -> {});
		this.slot = slot;
		this.screen = screen;

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			type = cap.getType();
		});
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/DecreaseLevelButton.java
	
	@Override
	public void onPress()
	{
=======

	@Override
	public void onPress(){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/DecreaseLevelButton.java
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/DecreaseLevelButton.java
	public void renderToolTip(PoseStack stack, int mouseX, int mouseY)
	{
=======
	public void renderToolTip(MatrixStack stack, int mouseX, int mouseY){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/DecreaseLevelButton.java
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			ability = DragonAbilities.PASSIVE_ABILITIES.get(type).get(slot);

			if(ability != null){
				PassiveDragonAbility currentAbility = (PassiveDragonAbility)cap.getMagic().getAbility(ability);

				if(ability != null && currentAbility == null){
					currentAbility = (PassiveDragonAbility)ability;
				}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/DecreaseLevelButton.java
				
				if(currentAbility != null && currentAbility.getLevel() > currentAbility.getMinLevel()) {
					screen.renderComponentTooltip(stack, Arrays.asList(new TranslatableComponent("ds.skill.level.down", (int)Math.max(1, currentAbility.getLevelCost() * 0.8F))), mouseX, mouseY);
=======

				if(currentAbility != null && currentAbility.getLevel() > currentAbility.getMinLevel()){
					GuiUtils.drawHoveringText(stack, Arrays.asList(new TranslationTextComponent("ds.skill.level.down", (int)Math.max(1, currentAbility.getLevelCost() * 0.8F))), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/DecreaseLevelButton.java
				}
			}
		});
	}
}