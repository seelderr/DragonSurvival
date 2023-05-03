package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;


import by.dragonsurvivalteam.dragonsurvival.client.gui.AbilityScreen;
import by.dragonsurvivalteam.dragonsurvival.client.util.TooltipRendering;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public class SkillProgressButton extends Button{
	private final int slot;
	private final AbilityScreen screen;
	private ActiveDragonAbility ability;

	public SkillProgressButton(int x, int y, int slot, AbilityScreen screen){
		super(x, y, 16, 16, null, button -> {});
		this.slot = slot;
		this.screen = screen;
	}

	@Override
	public void renderButton(PoseStack stack, int mouseX, int mouseY, float p_230431_4_){
		ResourceLocation texture = ability != null ? ability.getIcon() : AbilityButton.BLANK_TEXTURE;

		if(screen.unlockAbleSkills.size() > slot){
			ActiveDragonAbility ability1 = screen.unlockAbleSkills.get(slot);

			if(ability1 != null && ability1.getIcon() != null){
				texture = ability1.getIcon();
				ability = ability1;
			}
		}

		if(texture == AbilityButton.BLANK_TEXTURE)
			ability = null;


		RenderSystem.setShaderTexture(0, AbilityButton.BLANK_2_TEXTURE);
		blit(stack, x - 1, y - 1, 0, 0, 18, 18, 18, 18);

		RenderSystem.setShaderTexture(0, texture);

		blit(stack, x, y, 0, 0, 16, 16, 16, 16);

		if(ability != null)
			DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
				DragonAbility ability1 = DragonAbilities.getAbility(Minecraft.getInstance().player, ability.getClass());

				if(ability.getLevel() > ability1.getLevel() + 1){
					Gui.fill(stack, x, y, x + 16, y + 16, new Color(0.25F, 0.25F, 0.25F, 0.75F).getRGB());
				}
			});
	}

	@Override

	public void renderToolTip(PoseStack stack, int mouseX, int mouseY){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			if(ability != null){
				ChatFormatting format =  Objects.equals(cap.getType(), DragonTypes.CAVE) ? ChatFormatting.DARK_RED :  Objects.equals(cap.getType(), DragonTypes.SEA) ? ChatFormatting.AQUA :  Objects.equals(cap.getType(), DragonTypes.FOREST) ? ChatFormatting.GREEN : ChatFormatting.WHITE;
				Component component = ability.getTitle();
				ArrayList<Component> description = new ArrayList<>(Arrays.asList(Component.empty().append(component).append(" (" + ability.getLevel() + " / " + ability.getMaxLevel() + ")").withStyle(format)));

				if(ability.getLevelUpInfo().size() > 0)
					description.addAll(ability.getLevelUpInfo());

				int requiredLevel = ability.getCurrentRequiredLevel();


				if(requiredLevel != -1)
					description.add(Component.translatable("ds.skill.required_level", requiredLevel).withStyle(ChatFormatting.WHITE));

				TooltipRendering.drawHoveringText(stack, description, mouseX, mouseY);
			}
		});
	}
}