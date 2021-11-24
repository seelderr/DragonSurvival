package by.jackraidenph.dragonsurvival.magic.gui.Buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.magic.gui.AbilityScreen;
import by.jackraidenph.dragonsurvival.network.magic.ChangeSkillLevel;
import by.jackraidenph.dragonsurvival.util.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class SkillProgressButton extends Button {
	private int slot;
	
	private ActiveDragonAbility ability;
	private AbilityScreen screen;
	
	public int skillCost;
	
	public static final ResourceLocation LEVELUP = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/levelup.png");
	public static final ResourceLocation NOLEVELUP = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/nolevelup.png");
	
	
	public SkillProgressButton(int x, int y, int slot, AbilityScreen screen)
	{
		super(x, y, 16, 16, null, (button) -> {});
		this.slot = slot;
		this.screen = screen;
	}
	
	@Override
	public void renderButton(MatrixStack stack, int mouseX, int mouseY, float p_230431_4_)
	{
		ResourceLocation texture = AbilityButton.BLANK_TEXTURE;
		
		if(screen.unlockAbleSkills.size() > slot){
			ActiveDragonAbility ability1 = screen.unlockAbleSkills.get(slot);
			
			if(ability1 != null && ability1.getIcon() != null){
				texture = ability1.getIcon();
				this.ability = ability1;
			}
		}
		
		if(texture == AbilityButton.BLANK_TEXTURE){
			this.ability = null;
		}
		
		Minecraft.getInstance().getTextureManager().bind(AbilityButton.BLANK_2_TEXTURE);
		blit(stack, x - 1, y - 1, 0, 0, 18, 18, 18, 18);
		
		Minecraft.getInstance().getTextureManager().bind(texture);
		blit(stack, x, y, 0, 0, 16, 16, 16, 16);
		
		skillCost = ability != null ? ability.getLevelCost() : 0;
		
		if(ability != null) {
			if(Minecraft.getInstance().player.experienceLevel < ability.getNextRequiredLevel()) {
				Minecraft.getInstance().getTextureManager().bind(NOLEVELUP);
				blit(stack, x, y, 0, 0, 16, 16, 16, 16);
			}
		}
	}
	
	@Override
	public void onPress()
	{
		super.onPress();
		
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			if(ability != null) {
				if (cap.getAbilityLevel(ability) < ability.getMaxLevel()) {
					if(Minecraft.getInstance().player.experienceLevel > ability.getCurrentRequiredLevel() && Minecraft.getInstance().player.experienceLevel >= ability.getLevelCost()) {
						DragonSurvivalMod.CHANNEL.sendToServer(new ChangeSkillLevel(cap.getAbilityLevel(ability) + 1, ability.getId()));
					}
				}
			}
		});
	}
	
	@Override
	public void renderToolTip(MatrixStack stack, int mouseX, int mouseY)
	{
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			if(ability != null) {
				TextFormatting format = cap.getType() == DragonType.CAVE ? TextFormatting.DARK_RED : cap.getType() == DragonType.SEA ? TextFormatting.AQUA : cap.getType() == DragonType.FOREST ? TextFormatting.GREEN : TextFormatting.WHITE;
				ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(ability.getTitle().withStyle(format).append(" (" + ability.getLevel() + " / " + ability.getMaxLevel() + ")"), ability.getDescription().withStyle(TextFormatting.GRAY)));
				
				int requiredLevel = ability.getCurrentRequiredLevel();
				
				if (Minecraft.getInstance().player.experienceLevel >= requiredLevel) {
					if(Minecraft.getInstance().player.experienceLevel >= ability.getLevelCost()){
						description.add(new TranslationTextComponent("ds.skill.can_unlock", ability.getLevelCost()).withStyle(TextFormatting.GOLD));
					}else{
						description.add(new TranslationTextComponent("ds.skill.can_unlock.cant_afford", ability.getLevelCost()).withStyle(TextFormatting.GOLD));
					}
					
				} else {
					if (requiredLevel != -1) {
						description.add(new TranslationTextComponent("ds.skill.required_level", requiredLevel).withStyle(TextFormatting.DARK_GRAY));
					}
				}
				
				GuiUtils.drawHoveringText(stack, description, mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
			}
		});
	}
}