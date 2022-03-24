package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
import by.jackraidenph.dragonsurvival.client.gui.AbilityScreen;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
=======
import by.dragonsurvivalteam.dragonsurvival.client.gui.AbilityScreen;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java

public class SkillProgressButton extends Button
{
	private int slot;
	
=======
public class SkillProgressButton extends Button{
	private final int slot;

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
	private ActiveDragonAbility ability;
	private final AbilityScreen screen;

	public SkillProgressButton(int x, int y, int slot, AbilityScreen screen){
		super(x, y, 16, 16, null, (button) -> {});
		this.slot = slot;
		this.screen = screen;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
	public void renderButton(PoseStack stack, int mouseX, int mouseY, float p_230431_4_)
	{
=======
	public void renderButton(MatrixStack stack, int mouseX, int mouseY, float p_230431_4_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
		
		RenderSystem.setShaderTexture(0, AbilityButton.BLANK_2_TEXTURE);
		blit(stack, x - 1, y - 1, 0, 0, 18, 18, 18, 18);
		
		RenderSystem.setShaderTexture(0, texture);
=======

		Minecraft.getInstance().getTextureManager().bind(AbilityButton.BLANK_2_TEXTURE);
		blit(stack, x - 1, y - 1, 0, 0, 18, 18, 18, 18);

		Minecraft.getInstance().getTextureManager().bind(texture);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
		blit(stack, x, y, 0, 0, 16, 16, 16, 16);

		if(ability != null){
			DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
				if (ability.getLevel() > cap.getMagic().getAbilityLevel(ability) + 1) {
					Gui.fill(stack, x, y, x + 16, y + 16, new Color(0.25F, 0.25F, 0.25F, 0.75F).getRGB());
=======
				if(ability.getLevel() > cap.getMagic().getAbilityLevel(ability) + 1){
					AbstractGui.fill(stack, x, y, x + 16, y + 16, new Color(0.25F, 0.25F, 0.25F, 0.75F).getRGB());
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
				}
			});
		}
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
	public void renderToolTip(PoseStack  stack, int mouseX, int mouseY)
	{
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			if(ability != null) {
				ChatFormatting format = cap.getType() == DragonType.CAVE ? ChatFormatting.DARK_RED : cap.getType() == DragonType.SEA ? ChatFormatting.AQUA : cap.getType() == DragonType.FOREST ? ChatFormatting.GREEN : ChatFormatting.WHITE;
				ArrayList<Component> description = new ArrayList<>(Arrays.asList(ability.getTitle().withStyle(format).append(" (" + ability.getLevel() + " / " + ability.getMaxLevel() + ")")));
				
=======
	public void renderToolTip(MatrixStack stack, int mouseX, int mouseY){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			if(ability != null){
				TextFormatting format = cap.getType() == DragonType.CAVE ? TextFormatting.DARK_RED : cap.getType() == DragonType.SEA ? TextFormatting.AQUA : cap.getType() == DragonType.FOREST ? TextFormatting.GREEN : TextFormatting.WHITE;
				ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(ability.getTitle().withStyle(format).append(" (" + ability.getLevel() + " / " + ability.getMaxLevel() + ")")));

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
				if(ability.getLevelUpInfo().size() > 0){
					description.addAll(ability.getLevelUpInfo());
				}

				int requiredLevel = ability.getCurrentRequiredLevel();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
				
				if (requiredLevel != -1) {
					description.add(new TranslatableComponent("ds.skill.required_level", requiredLevel).withStyle(ChatFormatting.WHITE));
				}
				
				screen.renderComponentTooltip(stack, description, mouseX, mouseY);
=======

				if(requiredLevel != -1){
					description.add(new TranslationTextComponent("ds.skill.required_level", requiredLevel).withStyle(TextFormatting.WHITE));
				}

				GuiUtils.drawHoveringText(stack, description, mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/SkillProgressButton.java
			}
		});
	}
}