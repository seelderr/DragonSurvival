package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.AbilityScreen;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.awt.Color;
import java.util.List;

public class AbilityButton extends Button{
	public static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/blank.png");
	public static final ResourceLocation BLANK_1_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/blank1.png");
	public static final ResourceLocation BLANK_2_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/blank2.png");
	public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
	public static final ResourceLocation TOOLTIP_BARS = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/tooltip_bars.png");
	public static final ResourceLocation INVALID_ICON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/disabled.png");
	private final AbilityScreen screen;
	private DragonAbility ability;
	private DragonType type;

	public AbilityButton(int x, int y, DragonAbility ability, AbilityScreen screen){
		super(x, y, 16, 16, null, (button) -> {});
		this.ability = ability;
		this.screen = screen;

		if(ability.player == null){
			ability.player = Minecraft.getInstance().player;
		}
	}

	@Override
	public void renderButton(PoseStack stack, int mouseX, int mouseY, float p_230431_4_){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			DragonAbility ab = cap.getMagic().getAbility(ability);

			if(ab != null){
				ability = ab;
			}

			type = cap.getType();
		});

		RenderSystem.setShaderTexture(0, ability instanceof PassiveDragonAbility ? BLANK_2_TEXTURE : BLANK_1_TEXTURE);
		blit(stack, x - 1, y - 1, 0, 0, 20, 20, 20, 20);


		RenderSystem.setShaderTexture(0, ability.getIcon());
		blit(stack, x, y, 0, 0, 18, 18, 18, 18);

		if(ability.isDisabled()){
			RenderSystem.enableBlend();
			RenderSystem.setShaderTexture(0, INVALID_ICON);
			blit(stack, x, y, 0, 0, 18, 18, 18, 18);
			RenderSystem.disableBlend();
		}
	}

	@Override
	public void renderToolTip(PoseStack stack, int mouseX, int mouseY){
		drawHover(stack, ability);
	}

	public void drawHover(PoseStack stack, DragonAbility ability){
		int origYPos = this.y;
		int width = 150;

		int lx = 29 + Minecraft.getInstance().font.width(ability.getTitle().getString());
		FormattedText desc = ability.getDescription();

		if(ability.getInfo().size() > 0){
			desc = FormattedText.composite(desc, new TextComponent("\n\n"));
		}

		List<FormattedCharSequence> description = Minecraft.getInstance().font.split(desc, width - 7);

		for(FormattedCharSequence ireorderingprocessor : description){
			lx = Math.max(lx, Minecraft.getInstance().font.width(ireorderingprocessor));
		}

		origYPos -= (description.size() * 7);

		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
		RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();

		if(!description.isEmpty()){
			RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);

			int extraWidth = (int)(width / 1.25);

			if(ability.getInfo().size() > 0){
				FormattedText textContents = new TextComponent("");

				for(Component component : ability.getInfo()){
					textContents = FormattedText.composite(textContents, new TextComponent("\n"));
					textContents = FormattedText.composite(textContents, component);
				}

				List<FormattedCharSequence> text = Minecraft.getInstance().font.split(textContents, extraWidth - 5);

				int longest = 0;

				for(FormattedCharSequence textR : text){
					longest = Math.max(longest, Minecraft.getInstance().font.width(textR) + 20);
				}

				extraWidth = Math.min(longest, extraWidth);

				if(Screen.hasShiftDown()){
					this.render9Sprite(stack, this.x - extraWidth, origYPos + 3, extraWidth, 27 + (text.size() * 9), 10, 200, 26, 0, 52);
				}else{
					this.render9Sprite(stack, this.x - 10, origYPos + 3, 10 + 5, Math.min((27 + (text.size() * 9)), (35 + 24 + (description.size() * 9)) - 10), 10, 50, 26, 0, 52);
				}

				RenderSystem.setShaderTexture(0, TOOLTIP_BARS);
				int yPos = ability instanceof ActiveDragonAbility ? 20 : ability instanceof InnateDragonAbility ? 40 : 0;

				if(Screen.hasShiftDown()){
					blit(stack, this.x - extraWidth + 3, origYPos + 9, 0, yPos, 200, 20);
					Gui.drawString(stack, Minecraft.getInstance().font, new TranslatableComponent("ds.skill.info"), this.x - extraWidth + 10, (origYPos + 15), -1);

					for(int k1 = 0; k1 < text.size(); ++k1){
						Minecraft.getInstance().font.draw(stack, text.get(k1), this.x - extraWidth + 5, (origYPos + 5) + 18 + (k1 * 9), -5592406);
					}
				}else{
					blit(stack, this.x - 10 + 3, origYPos + 9, 0, yPos, 200, 20);
				}
			}

			RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
			this.render9Sprite(stack, this.x - 2, origYPos - 4, width + 5, 35 + 24 + (description.size() * 9), 10, 200, 26, 0, 52);
		}

		RenderSystem.setShaderTexture(0, TOOLTIP_BARS);
		int yPos = ability instanceof ActiveDragonAbility ? 20 : ability instanceof InnateDragonAbility ? 40 : 0;
		blit(stack, this.x, origYPos + 3, 0, yPos, 200, 20);

		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
		blit(stack, this.x, origYPos, 0, 128 + 26, 26, 26);

		String skillType = ability instanceof ActiveDragonAbility ? "active" : ability instanceof InnateDragonAbility ? "innate" : ability instanceof PassiveDragonAbility ? "passive" : null;

		if(skillType != null){
			Color c = ability instanceof ActiveDragonAbility ? new Color(200, 143, 31) : ability instanceof InnateDragonAbility ? new Color(150, 56, 175) : new Color(127, 145, 46);
			Gui.drawCenteredString(stack, Minecraft.getInstance().font, new TranslatableComponent("ds.skill.type." + skillType), this.x + (width / 2), origYPos + 30, c.getRGB());
		}


		if(ability.getMaxLevel() > 1){
			Gui.drawCenteredString(stack, Minecraft.getInstance().font, new TextComponent(ability.getLevel() + "/" + ability.getMaxLevel()), (this.x + width - 18), (origYPos + 9), -1);
			Gui.drawCenteredString(stack, Minecraft.getInstance().font, ability.getTitle(), this.x + (width / 2), origYPos + 9, -1);
		}else{
			Gui.drawCenteredString(stack, Minecraft.getInstance().font, ability.getTitle(), this.x + (width / 2) + 10, origYPos + 9, -1);
		}

		for(int k1 = 0; k1 < description.size(); ++k1){
			Minecraft.getInstance().font.draw(stack, description.get(k1), (float)(this.x + 5), (float)(origYPos + 47 + k1 * 9), -5592406);
		}

		if(ability.getInfo().size() > 0){
			Gui.drawCenteredString(stack, Minecraft.getInstance().font, new TranslatableComponent("ds.skill.info.hold_shift").withStyle(ChatFormatting.DARK_GRAY), this.x + (width / 2), (origYPos + 47 + (description.size() - 1) * 9), 0);
		}

		RenderSystem.setShaderTexture(0, ability.getIcon());
		blit(stack, this.x + 5, origYPos + 5, 0, 0, 16, 16, 16, 16);

		if(ability.isDisabled()){
			RenderSystem.enableBlend();
			RenderSystem.setShaderTexture(0, INVALID_ICON);
			blit(stack, this.x + 5, origYPos + 5, 0, 0, 16, 16, 16, 16);
			RenderSystem.disableBlend();
		}
	}


	protected void render9Sprite(PoseStack p_238691_1_, int p_238691_2_, int p_238691_3_, int p_238691_4_, int p_238691_5_, int p_238691_6_, int p_238691_7_, int p_238691_8_, int p_238691_9_, int p_238691_10_){
		this.blit(p_238691_1_, p_238691_2_, p_238691_3_, p_238691_9_, p_238691_10_, p_238691_6_, p_238691_6_);
		this.renderRepeating(p_238691_1_, p_238691_2_ + p_238691_6_, p_238691_3_, p_238691_4_ - p_238691_6_ - p_238691_6_, p_238691_6_, p_238691_9_ + p_238691_6_, p_238691_10_, p_238691_7_ - p_238691_6_ - p_238691_6_, p_238691_8_);
		this.blit(p_238691_1_, p_238691_2_ + p_238691_4_ - p_238691_6_, p_238691_3_, p_238691_9_ + p_238691_7_ - p_238691_6_, p_238691_10_, p_238691_6_, p_238691_6_);
		this.blit(p_238691_1_, p_238691_2_, p_238691_3_ + p_238691_5_ - p_238691_6_, p_238691_9_, p_238691_10_ + p_238691_8_ - p_238691_6_, p_238691_6_, p_238691_6_);
		this.renderRepeating(p_238691_1_, p_238691_2_ + p_238691_6_, p_238691_3_ + p_238691_5_ - p_238691_6_, p_238691_4_ - p_238691_6_ - p_238691_6_, p_238691_6_, p_238691_9_ + p_238691_6_, p_238691_10_ + p_238691_8_ - p_238691_6_, p_238691_7_ - p_238691_6_ - p_238691_6_, p_238691_8_);
		this.blit(p_238691_1_, p_238691_2_ + p_238691_4_ - p_238691_6_, p_238691_3_ + p_238691_5_ - p_238691_6_, p_238691_9_ + p_238691_7_ - p_238691_6_, p_238691_10_ + p_238691_8_ - p_238691_6_, p_238691_6_, p_238691_6_);
		this.renderRepeating(p_238691_1_, p_238691_2_, p_238691_3_ + p_238691_6_, p_238691_6_, p_238691_5_ - p_238691_6_ - p_238691_6_, p_238691_9_, p_238691_10_ + p_238691_6_, p_238691_7_, p_238691_8_ - p_238691_6_ - p_238691_6_);
		this.renderRepeating(p_238691_1_, p_238691_2_ + p_238691_6_, p_238691_3_ + p_238691_6_, p_238691_4_ - p_238691_6_ - p_238691_6_, p_238691_5_ - p_238691_6_ - p_238691_6_, p_238691_9_ + p_238691_6_, p_238691_10_ + p_238691_6_, p_238691_7_ - p_238691_6_ - p_238691_6_, p_238691_8_ - p_238691_6_ - p_238691_6_);
		this.renderRepeating(p_238691_1_, p_238691_2_ + p_238691_4_ - p_238691_6_, p_238691_3_ + p_238691_6_, p_238691_6_, p_238691_5_ - p_238691_6_ - p_238691_6_, p_238691_9_ + p_238691_7_ - p_238691_6_, p_238691_10_ + p_238691_6_, p_238691_7_, p_238691_8_ - p_238691_6_ - p_238691_6_);
	}

	protected void renderRepeating(PoseStack p_238690_1_, int p_238690_2_, int p_238690_3_, int p_238690_4_, int p_238690_5_, int p_238690_6_, int p_238690_7_, int p_238690_8_, int p_238690_9_){
		for(int i = 0; i < p_238690_4_; i += p_238690_8_){
			int j = p_238690_2_ + i;
			int k = Math.min(p_238690_8_, p_238690_4_ - i);

			for(int l = 0; l < p_238690_5_; l += p_238690_9_){
				int i1 = p_238690_3_ + l;
				int j1 = Math.min(p_238690_9_, p_238690_5_ - l);
				this.blit(p_238690_1_, j, i1, p_238690_6_, p_238690_7_, k, j1);
			}
		}
	}
}