package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.AbilityScreen;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class SkillProgressButton extends Button{
	private final int slot;

	private ActiveDragonAbility ability;
	private final AbilityScreen screen;

	public SkillProgressButton(int x, int y, int slot, AbilityScreen screen){
		super(x, y, 16, 16, null, (button) -> {});
		this.slot = slot;
		this.screen = screen;
	}

	@Override
	public void renderButton(MatrixStack stack, int mouseX, int mouseY, float p_230431_4_){
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

		if(ability != null){
			DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
				if(ability.getLevel() > cap.getMagic().getAbilityLevel(ability) + 1){
					AbstractGui.fill(stack, x, y, x + 16, y + 16, new Color(0.25F, 0.25F, 0.25F, 0.75F).getRGB());
				}
			});
		}
	}

	@Override
	public void renderToolTip(MatrixStack stack, int mouseX, int mouseY){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			if(ability != null){
				TextFormatting format = cap.getType() == DragonType.CAVE ? TextFormatting.DARK_RED : cap.getType() == DragonType.SEA ? TextFormatting.AQUA : cap.getType() == DragonType.FOREST ? TextFormatting.GREEN : TextFormatting.WHITE;
				ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(ability.getTitle().withStyle(format).append(" (" + ability.getLevel() + " / " + ability.getMaxLevel() + ")")));

				if(ability.getLevelUpInfo().size() > 0){
					description.addAll(ability.getLevelUpInfo());
				}

				int requiredLevel = ability.getCurrentRequiredLevel();

				if(requiredLevel != -1){
					description.add(new TranslationTextComponent("ds.skill.required_level", requiredLevel).withStyle(TextFormatting.WHITE));
				}

				GuiUtils.drawHoveringText(stack, description, mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
			}
		});
	}
}