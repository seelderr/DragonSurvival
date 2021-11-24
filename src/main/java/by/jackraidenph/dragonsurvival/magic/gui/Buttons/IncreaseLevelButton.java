package by.jackraidenph.dragonsurvival.magic.gui.Buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.magic.Abilities.DragonAbilities;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.PassiveDragonAbility;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.gui.AbilityScreen;
import by.jackraidenph.dragonsurvival.network.magic.ChangeSkillLevel;
import by.jackraidenph.dragonsurvival.util.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.Arrays;

public class IncreaseLevelButton extends Button
{
	private int slot;
	
	private DragonAbility ability;
	private DragonType type;
	private AbilityScreen screen;
	
	public int skillCost = 0;
	
	public IncreaseLevelButton(int x, int y, int slot, AbilityScreen screen)
	{
		super(x, y, 11, 17, null, (button) -> {});
		this.slot = slot;
		this.screen = screen;
		
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			type = cap.getType();
		});
	}
	
	@Override
	public void renderButton(MatrixStack stack, int mouseX, int mouseY, float p_230431_4_)
	{
		Minecraft.getInstance().getTextureManager().bind(TabButton.buttonTexture);
		
		if(isHovered()){
			blit(stack, x, y, 66 / 2, 222 / 2, 11, 17,128, 128);
		}else{
			blit(stack, x, y, 44 / 2, 222 / 2, 11, 17, 128, 128);
		}
	}
	
	@Override
	public void onPress()
	{
		super.onPress();
		
		ability = DragonAbilities.PASSIVE_ABILITIES.get(type).get(slot);
		
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			if(ability != null) {
				if (cap.getAbilityLevel(ability) + 1 <= ability.getMaxLevel()) {
					
					if (ability != null) {
						PassiveDragonAbility currentAbility = (PassiveDragonAbility)cap.getAbility(ability);
						
						if (ability != null && currentAbility == null) {
							currentAbility = (PassiveDragonAbility)ability;
						}
						
						if (currentAbility != null && currentAbility.getLevel() < currentAbility.getMaxLevel()) {
							PassiveDragonAbility newActivty = currentAbility.createInstance();
							newActivty.setLevel(currentAbility.getLevel() + 1);
							
							if(Minecraft.getInstance().player.experienceLevel >= newActivty.getLevelCost()){
								DragonSurvivalMod.CHANNEL.sendToServer(new ChangeSkillLevel(cap.getAbilityLevel(ability) + 1, ability.getId()));
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
	public void renderToolTip(MatrixStack stack, int mouseX, int mouseY)
	{
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			ability = DragonAbilities.PASSIVE_ABILITIES.get(type).get(slot);
			
			if(ability != null) {
				PassiveDragonAbility currentAbility = (PassiveDragonAbility)cap.getAbility(ability);
				
				if(ability != null && currentAbility == null){
					currentAbility = (PassiveDragonAbility)ability;
				}
				
				if(currentAbility != null && currentAbility.getLevel() < currentAbility.getMaxLevel()) {
					PassiveDragonAbility newActivty = currentAbility.createInstance();
					newActivty.setLevel(currentAbility.getLevel() + 1);
					skillCost = newActivty.getLevelCost();
					GuiUtils.drawHoveringText(stack, Arrays.asList(new TranslationTextComponent("ds.skill.level.up", skillCost)), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}
		});
	}
}