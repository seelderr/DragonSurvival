package by.dragonsurvivalteam.dragonsurvival.client.gui;


import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.*;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AbilityScreen extends Screen{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/magic_interface.png");

	private final int xSize = 256;
	private final int ySize = 256;
	public Screen sourceScreen;
	public ArrayList<ActiveDragonAbility> unlockAbleSkills = new ArrayList<>();
	private int guiLeft;
	private int guiTop;
	private DragonType type;

	public AbilityScreen(Screen sourceScreen){
		super(new StringTextComponent("AbilityScreen"));
		this.sourceScreen = sourceScreen;
	}

	public List<Widget> widgetList(){
		return buttons;
	}

	@Override
	public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks){
		if(this.minecraft == null){
			return;
		}

		this.renderBackground(stack);


		int startX = this.guiLeft;
		int startY = this.guiTop;

		this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
		blit(stack, startX, startY, 0, 0, 256, 256);

		if(type != null){
			int barYPos = type == DragonType.SEA ? 198 : type == DragonType.FOREST ? 186 : 192;

			minecraft.getTextureManager().bind(ClientMagicHUDHandler.widgetTextures);

			float progress = MathHelper.clamp((minecraft.player.experienceLevel / 50F), 0, 1);
			float progress1 = Math.min(1F, (Math.min(0.5F, progress) * 2F));
			float progress2 = Math.min(1F, (Math.min(0.5F, progress - 0.5F) * 2F));

			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.5F, 0.75F, 0F);
			blit(stack, startX + (23 / 2), startY + 28, 0, 180 / 2, 105, 3, 128, 128);
			blit(stack, startX + (254 / 2), startY + 28, 0, 180 / 2, 105, 3, 128, 128);

			blit(stack, startX + (23 / 2), startY + 28, 0, barYPos / 2, (int)(105 * progress1), 3, 128, 128);

			if(progress > 0.5){
				blit(stack, startX + (254 / 2), startY + 28, 0, barYPos / 2, (int)(105 * progress2), 3, 128, 128);
			}

			int expChange = -1;

			for(Widget btn : buttons){
				if(!btn.isHovered()){
					continue;
				}

				if(btn instanceof IncreaseLevelButton){
					expChange = ((IncreaseLevelButton)btn).skillCost;
					break;
				}
			}

			if(expChange != -1){
				float Changeprogress = MathHelper.clamp((expChange / 50F), 0, 1); //Total exp required to hit level 50
				float Changeprogress1 = Math.min(1F, (Math.min(0.5F, Changeprogress) * 2F));
				float Changeprogress2 = Math.min(1F, (Math.min(0.5F, Changeprogress - 0.5F) * 2F));

				blit(stack, startX + (23 / 2), startY + 28, 0, 174 / 2, (int)(105 * Changeprogress1), 3, 128, 128);

				if(Changeprogress2 > 0.5){
					blit(stack, startX + (254 / 2) - (int)(105 * progress1), startY + 28, 0, 174 / 2, (int)(105 * Changeprogress2), 3, 128, 128);
				}
			}

			RenderSystem.popMatrix();

			RenderSystem.pushMatrix();
			ITextComponent textComponent = new StringTextComponent(Integer.toString(minecraft.player.experienceLevel)).withStyle(TextFormatting.DARK_GRAY);
			int xPos = startX + 117 + 1;
			float finalXPos = (float)(xPos - minecraft.font.width(textComponent) / 2);
			minecraft.font.draw(stack, textComponent, finalXPos, startY + 26, 0);
			RenderSystem.popMatrix();
		}

		super.render(stack, mouseX, mouseY, partialTicks);

		for(Widget btn : buttons){
			if(btn.isHovered()){
				btn.renderToolTip(stack, mouseX, mouseY);
			}
		}
	}

	@Override
	public void init(Minecraft p_231158_1_, int width, int height){
		super.init(p_231158_1_, width, height);

		int startX = this.guiLeft;
		int startY = this.guiTop;

		//Inventory
		addButton(new TabButton(startX + 5, startY - 26, 0, this));
		addButton(new TabButton(startX + 33, startY - 28, 1, this));
		addButton(new TabButton(startX + 62, startY - 26, 2, this));
		addButton(new TabButton(startX + 91, startY - 26, 3, this));

		addButton(new SkillProgressButton(guiLeft + (int)(219 / 2F), startY + 8, 4, this));

		for(int i = 0; i <= 4; i++){
			addButton(new SkillProgressButton(guiLeft + (int)(219 / 2F) - (i * (23 + ((4 - i) / 4))), startY + 8, 4 - i, this));
			addButton(new SkillProgressButton(guiLeft + (int)(219 / 2F) + (i * (23 + ((4 - i) / 4))), startY + 8, 4 + i, this));
		}

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {

			int num = 0;
			for(ActiveDragonAbility ability : DragonAbilities.ACTIVE_ABILITIES.get(cap.getType())){
				if(ability != null){
					addButton(new AbilityButton((int)(guiLeft + (90 / 2.0)), (guiTop + 40 + (num * 23)), ability, this));
					num++;
				}
			}

			num = 0;
			for(PassiveDragonAbility ability : DragonAbilities.PASSIVE_ABILITIES.get(cap.getType())){
				if(ability != null){
					addButton(new AbilityButton(guiLeft + (int)(217 / 2F), (guiTop + 40 + (num * 23)), ability, this));
					addButton(new IncreaseLevelButton(guiLeft + (int)(219 / 2F) + 25, (guiTop + 40 + (num * 23)), num, this));
					addButton(new DecreaseLevelButton(guiLeft + (int)(219 / 2F) - 25, (guiTop + 40 + (num * 23)), num, this));
					num++;
				}
			}

			num = 0;
			for(InnateDragonAbility ability : DragonAbilities.INNATE_ABILITIES.get(cap.getType())){
				if(ability != null){
					addButton(new AbilityButton(guiLeft + (int)(346 / 2F), (guiTop + 40 + (num * 23)), ability, this));
					num++;
				}
			}
		});

		addButton(new HelpButton(startX + (218 / 2) + 3, startY + (263 / 2) + 4, 9, 9, "ds.skill.help", 0));
	}

	@Override
	protected void init(){
		super.init();

		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize / 2) / 2;
	}

	@Override
	public void tick(){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			type = cap.getType();
			unlockAbleSkills.clear();

			for(ActiveDragonAbility ab : DragonAbilities.ACTIVE_ABILITIES.get(cap.getType())){
				DragonAbility ability = cap.getMagic().getAbility(ab);
				ActiveDragonAbility db = ability != null ? ((ActiveDragonAbility)ability) : ab;

				if(db != null){
					for(int i = db.getLevel(); i < db.getMaxLevel(); i++){
						ActiveDragonAbility newActivty = db.createInstance();
						newActivty.setLevel(i + 1);
						unlockAbleSkills.add(newActivty);
					}
				}
			}

			unlockAbleSkills.sort(Comparator.comparingInt(c -> c.getCurrentRequiredLevel()));
		});
	}

	@Override
	public boolean isPauseScreen(){
		return false;
	}
}