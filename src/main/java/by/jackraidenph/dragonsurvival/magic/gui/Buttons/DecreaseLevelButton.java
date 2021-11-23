package by.jackraidenph.dragonsurvival.magic.gui.Buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.magic.Abilities.DragonAbilities;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.PassiveDragonAbility;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.gui.AbilityScreen;
import by.jackraidenph.dragonsurvival.network.Abilities.ChangeSkillLevel;
import by.jackraidenph.dragonsurvival.util.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.Arrays;

public class DecreaseLevelButton extends Button
{
	private int slot;
	
	private DragonAbility ability;
	private DragonType type;
	private AbilityScreen screen;
	
	public DecreaseLevelButton(int x, int y, int slot, AbilityScreen screen)
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
			blit(stack, x, y, 22 / 2, 222 / 2, 11, 17,128, 128);
		}else{
			blit(stack, x, y, 0, 222 / 2, 11, 17, 128, 128);
		}
	}
	
	@Override
	public void onPress()
	{
		super.onPress();
		
		ability = DragonAbilities.PASSIVE_ABILITIES.get(type).get(slot);
		
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			if(ability != null) {
				if (cap.getAbility(ability) != null && cap.getAbilityLevel(ability) > ability.getMinLevel()) {
					DragonSurvivalMod.CHANNEL.sendToServer(new ChangeSkillLevel(cap.getAbilityLevel(ability) - 1, ability.getId()));
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
				
				if(currentAbility != null && currentAbility.getLevel() > currentAbility.getMinLevel()) {
					GuiUtils.drawHoveringText(stack, Arrays.asList(new TranslationTextComponent("ds.skill.level.down", (int)Math.max(1, currentAbility.getLevelCost() * 0.8F))), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}
		});
	}
}