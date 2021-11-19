package by.jackraidenph.dragonsurvival.gui.Buttons;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.magic.Abilities.Actives.AoeBuffAbility;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.InnateDragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.PassiveDragonAbility;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.gui.AbilityScreen;
import by.jackraidenph.dragonsurvival.util.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.List;

public class AbilityButton extends Button {
	public static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/blank.png");
	public static final ResourceLocation BLANK_1_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/blank1.png");
	public static final ResourceLocation BLANK_2_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/blank2.png");
	public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
	public static final ResourceLocation TOOLTIP_BARS = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/tooltip_bars.png");
	
	private DragonAbility ability;
	private AbilityScreen screen;
	private DragonType type;
	
	public AbilityButton(int x, int y, DragonAbility ability, AbilityScreen screen)
	{
		super(x, y, 16, 16, null, (button) -> {});
		this.ability = ability;
		this.screen = screen;
	}
	@Override
	public void renderButton(MatrixStack stack, int mouseX, int mouseY, float p_230431_4_)
	{
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			DragonAbility ab = cap.getAbility(ability);
			
			if(ab != null) {
				ability = ab;
			}
			
			type = cap.getType();
		});
		
		Minecraft.getInstance().getTextureManager().bind(ability instanceof PassiveDragonAbility ? BLANK_2_TEXTURE : BLANK_1_TEXTURE);
		blit(stack, x - 1, y - 1, 0, 0, 18, 18, 18, 18);
		
		
		Minecraft.getInstance().getTextureManager().bind(ability.getIcon());
		blit(stack, x, y, 0, 0, 16, 16, 16, 16);
	}
	
	@Override
	public void onPress()
	{
		super.onPress();
		
		if(ability instanceof PassiveDragonAbility){
			if(ability.getLevel() > 0) {
				((PassiveDragonAbility)ability).toggle();
			}
		}
	}
	
	@Override
	public void renderToolTip(MatrixStack stack, int mouseX, int mouseY)
	{
		drawHover(stack,  ability);
	}
	
	public void drawHover(MatrixStack stack, DragonAbility ability) {
		int origYPos = this.y;
		int width = 150;
		
		int lx = 29 + Minecraft.getInstance().font.width(ability.getTitle().getContents());
		List<IReorderingProcessor> description = Minecraft.getInstance().font.split(ability.getDescription(), width - 7);
		
		for(IReorderingProcessor ireorderingprocessor : description) {
			lx = Math.max(lx, Minecraft.getInstance().font.width(ireorderingprocessor));
		}
		
		origYPos -= (description.size() * 7);
		
		Minecraft.getInstance().getTextureManager().bind(WIDGETS_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		
		if (!description.isEmpty()) {
			Minecraft.getInstance().getTextureManager().bind(WIDGETS_LOCATION);
			
			int extraWidth = (int)(width / 1.5);
			
			if(ability instanceof ActiveDragonAbility) {
				IFormattableTextComponent textContents = new TranslationTextComponent("ds.skill.mana_cost", ((ActiveDragonAbility)ability).getManaCost());
				
				if(((ActiveDragonAbility)ability).getCastingTime() > 0){
					textContents.append("\n");
					textContents.append(new TranslationTextComponent("ds.skill.cast_time", Functions.ticksToSeconds(((ActiveDragonAbility)ability).getCastingTime())));
				}
				
				if(((ActiveDragonAbility)ability).getMaxCooldown() > 0){
					textContents.append("\n");
					textContents.append(new TranslationTextComponent("ds.skill.cooldown", Functions.ticksToSeconds(((ActiveDragonAbility)ability).getMaxCooldown())));
				}
				
				if(ability instanceof AoeBuffAbility){
					textContents.append("\n");
					textContents.append(new TranslationTextComponent("ds.skill.aoe", ((AoeBuffAbility)ability).getRange() + "x" + ((AoeBuffAbility)ability).getRange()));
				}
				
				List<IReorderingProcessor> text = Minecraft.getInstance().font.split(textContents, extraWidth - 5);
				
				if(Screen.hasShiftDown()){
					this.render9Sprite(stack, this.x -extraWidth, origYPos + 3, extraWidth, 35 + 24 + (text.size() * 4), 10, 50, 26, 0, 52);
				}else {
					this.render9Sprite(stack, this.x - 10, origYPos + 3, 10 + 5, 35 + 24 + (text.size() * 4), 10, 50, 26, 0, 52);
				}
				
				Minecraft.getInstance().getTextureManager().bind(TOOLTIP_BARS);
				
				if(Screen.hasShiftDown()){
					this.blit(stack, this.x - extraWidth + 3, origYPos + 9, 0, 20, 200, 20);
					
					AbstractGui.drawCenteredString(stack, Minecraft.getInstance().font, new TranslationTextComponent("ds.skill.info"), this.x - (width / 2) + 3 + 25, (origYPos + 15), -1);
					
					for(int k1 = 0; k1 < text.size(); ++k1) {
						Minecraft.getInstance().font.draw(stack, text.get(k1), this.x - extraWidth + 5, (origYPos + 15) + 18 + (k1 * 9), -5592406);
					}
					
				} else{
					this.blit(stack, this.x - 10 + 3, origYPos + 9, 0, 20, 200, 20);
				}
			}
			
			Minecraft.getInstance().getTextureManager().bind(WIDGETS_LOCATION);
			this.render9Sprite(stack, this.x - 2, origYPos - 4, width + 5, 35 + 24 + (description.size() * 9), 10, 200, 26, 0, 52);
		}
		
		Minecraft.getInstance().getTextureManager().bind(TOOLTIP_BARS);
		int yPos = ability instanceof ActiveDragonAbility ? 20 : ability instanceof InnateDragonAbility ? 40 : 0;
		this.blit(stack, this.x, origYPos + 3, 0, yPos, 200, 20);
		
		Minecraft.getInstance().getTextureManager().bind(WIDGETS_LOCATION);
		this.blit(stack, this.x, origYPos, 0, 128 + 26, 26, 26);
		
		String skillType = ability instanceof ActiveDragonAbility ? "active" : ability instanceof InnateDragonAbility ? "innate" : ability instanceof PassiveDragonAbility ? "passive" : null;
		
		if(skillType != null){
			Color c = ability instanceof ActiveDragonAbility ? new Color(200, 143, 31) : ability instanceof InnateDragonAbility ? new Color(150, 56, 175) : new Color(127, 145, 46);
			AbstractGui.drawCenteredString(stack, Minecraft.getInstance().font, new TranslationTextComponent("ds.skill.type." + skillType), this.x + (width / 2), origYPos + 30, c.getRGB());
		}
		
		
		if(ability.getMaxLevel() > 1) {
			AbstractGui.drawCenteredString(stack, Minecraft.getInstance().font, new StringTextComponent(ability.getLevel() + "/" + ability.getMaxLevel()), (this.x + width - 15), (origYPos + 9), -1);
			AbstractGui.drawCenteredString(stack, Minecraft.getInstance().font, ability.getTitle(), this.x + (width / 2), origYPos + 9, -1);
		}else{
			AbstractGui.drawCenteredString(stack, Minecraft.getInstance().font, ability.getTitle(), this.x + (width / 2) + 10, origYPos + 9, -1);
		}
		
		for(int k1 = 0; k1 < description.size(); ++k1) {
			Minecraft.getInstance().font.draw(stack, description.get(k1), (float)(this.x + 5), (float)(origYPos + 47 + k1 * 9), -5592406);
		}
		
		
		Minecraft.getInstance().textureManager.bind(ability.getIcon());
		this.blit(stack, this.x + 5, origYPos + 5, 0, 0, 16, 16, 16, 16);
	}
	
	protected void render9Sprite(MatrixStack p_238691_1_, int p_238691_2_, int p_238691_3_, int p_238691_4_, int p_238691_5_, int p_238691_6_, int p_238691_7_, int p_238691_8_, int p_238691_9_, int p_238691_10_) {
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
	
	protected void renderRepeating(MatrixStack p_238690_1_, int p_238690_2_, int p_238690_3_, int p_238690_4_, int p_238690_5_, int p_238690_6_, int p_238690_7_, int p_238690_8_, int p_238690_9_) {
		for(int i = 0; i < p_238690_4_; i += p_238690_8_) {
			int j = p_238690_2_ + i;
			int k = Math.min(p_238690_8_, p_238690_4_ - i);
			
			for(int l = 0; l < p_238690_5_; l += p_238690_9_) {
				int i1 = p_238690_3_ + l;
				int j1 = Math.min(p_238690_9_, p_238690_5_ - l);
				this.blit(p_238690_1_, j, i1, p_238690_6_, p_238690_7_, k, j1);
			}
		}
	}
}