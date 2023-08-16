package by.dragonsurvivalteam.dragonsurvival.client.gui;


import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.*;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.HelpButton;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.MagicCap;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class AbilityScreen extends Screen{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/magic_interface.png");

	private final int xSize = 256;
	private final int ySize = 256;
	public Screen sourceScreen;
	public ArrayList<ActiveDragonAbility> unlockAbleSkills = new ArrayList<>();
	private int guiLeft;
	private int guiTop;
	private AbstractDragonType type;

	public AbilityScreen(Screen sourceScreen){
		super(new TextComponent("AbilityScreen"));
		this.sourceScreen = sourceScreen;
	}

	public List<GuiEventListener> widgetList(){
		return children;
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks){
		if(minecraft == null){
			return;
		}

		renderBackground(stack);

		int startX = guiLeft;
		int startY = guiTop;

		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		blit(stack, startX, startY, 0, 0, 256, 256);

		if(type != null){
			int barYPos = Objects.equals(type, DragonTypes.SEA) ? 198 : Objects.equals(type, DragonTypes.FOREST) ? 186 : 192;

			RenderSystem.setShaderTexture(0, ClientMagicHUDHandler.widgetTextures);

			float progress = Mth.clamp(minecraft.player.experienceLevel / 50F, 0, 1);
			float progress1 = Math.min(1F, Math.min(0.5F, progress) * 2F);
			float progress2 = Math.min(1F, Math.min(0.5F, progress - 0.5F) * 2F);

			stack.pushPose();
			stack.translate(0.5F, 0.75F, 0F);
			blit(stack, startX + 23 / 2, startY + 28, 0, (float) 180 / 2, 105, 3, 128, 128);
			blit(stack, startX + 254 / 2, startY + 28, 0, (float) 180 / 2, 105, 3, 128, 128);

			blit(stack, startX + 23 / 2, startY + 28, 0, (float) barYPos / 2, (int)(105 * progress1), 3, 128, 128);

			if(progress > 0.5){
				blit(stack, startX + 254 / 2, startY + 28, 0, (float) barYPos / 2, (int)(105 * progress2), 3, 128, 128);
			}

			int expChange = -1;

			for(GuiEventListener btn : children){
				if(!(btn instanceof AbstractWidget) || !((AbstractWidget)btn).isHoveredOrFocused()){
					continue;
				}

				if(btn instanceof IncreaseLevelButton){
					expChange = ((IncreaseLevelButton)btn).skillCost;
					break;
				}
			}

			if(expChange != -1){
				float Changeprogress = Mth.clamp(expChange / 50F, 0, 1); //Total exp required to hit level 50
				float Changeprogress1 = Math.min(1F, Math.min(0.5F, Changeprogress) * 2F);
				float Changeprogress2 = Math.min(1F, Math.min(0.5F, Changeprogress - 0.5F) * 2F);

				blit(stack, startX + 23 / 2, startY + 28, 0, (float) 174 / 2, (int)(105 * Changeprogress1), 3, 128, 128);

				if(Changeprogress2 > 0.5){
					blit(stack, startX + 254 / 2, startY + 28, 0, (float) 174 / 2, (int)(105 * Changeprogress2), 3, 128, 128);
				}
			}

			stack.popPose();

			stack.pushPose();
			Component textComponent = new TextComponent(Integer.toString(minecraft.player.experienceLevel)).withStyle(ChatFormatting.DARK_GRAY);
			int xPos = startX + 117 + 1;
			float finalXPos = (float)(xPos - minecraft.font.width(textComponent) / 2);
			minecraft.font.draw(stack, textComponent, finalXPos, startY + 26, 0);
			stack.popPose();
		}

		super.render(stack, mouseX, mouseY, partialTicks);

		for(Widget btn : renderables){
			if(btn instanceof AbstractWidget && ((AbstractWidget)btn).isHoveredOrFocused()){
				((AbstractWidget)btn).renderToolTip(stack, mouseX, mouseY);
			}
		}

		renderables.forEach(s-> {
			if(s instanceof AbilityButton btn){
				if(btn.skillType == 0 && btn.dragging && btn.ability != null){
					RenderSystem.setShaderTexture(0, btn.ability.getIcon());
					blit(stack, mouseX, mouseY, 0, 0, 18, 18, 18, 18);
				}
			}
		});
	}

	@Override
	public void init(){
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize / 2) / 2;

		int startX = guiLeft;
		int startY = guiTop;

		//Inventory
		addRenderableWidget(new TabButton(startX + 5, startY - 26, 0, this));
		addRenderableWidget(new TabButton(startX + 34, startY - 28, 1, this));
		addRenderableWidget(new TabButton(startX + 62, startY - 26, 2, this));
		addRenderableWidget(new TabButton(startX + 91, startY - 26, 3, this));

		addRenderableWidget(new SkillProgressButton(guiLeft + (int)(219 / 2F), startY + 8, 4, this));

		for(int i = 1; i <= 4; i++){
			addRenderableWidget(new SkillProgressButton(guiLeft + (int)(219 / 2F) - i * 23, startY + 8, 4 - i, this));
			addRenderableWidget(new SkillProgressButton(guiLeft + (int)(219 / 2F) + i * 23, startY + 8, 4 + i, this));
		}

		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			for(int num = 0; num < MagicCap.activeAbilitySlots; num++){
				addRenderableWidget(new AbilityButton((int)(guiLeft + 90 / 2.0), guiTop + 40 + num * 23, 0, num, this));
			}

			for(int num = 0; num < MagicCap.passiveAbilitySlots; num++){
				addRenderableWidget(new AbilityButton(guiLeft + (int)(217 / 2F), guiTop + 40 + num * 23, 1, num, this));
				addRenderableWidget(new IncreaseLevelButton(guiLeft + (int)(219 / 2F) + 25, guiTop + 40 + num * 23, num));
				addRenderableWidget(new DecreaseLevelButton(guiLeft + (int)(219 / 2F) - 25, guiTop + 40 + num * 23, num));
			}

			for(int num = 0; num < MagicCap.innateAbilitySlots; num++){
				addRenderableWidget(new AbilityButton(guiLeft + (int)(346 / 2F), guiTop + 40 + num * 23, 2, num, this));
			}
		});

		addRenderableWidget(new HelpButton(startX + 218 / 2 + 3, startY + 263 / 2 + 4, 9, 9, "ds.skill.help", 0));
	}


	@Override
	public void tick(){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
			type = cap.getType();
			unlockAbleSkills.clear();

			for(ActiveDragonAbility ab : cap.getMagicData().getActiveAbilities()){
				ActiveDragonAbility ability = DragonAbilities.getSelfAbility(minecraft.player, ab.getClass());
				ActiveDragonAbility db = ability != null ? ability : ab;

				for(int i = db.getLevel(); i < db.getMaxLevel(); i++){
					try{
						ActiveDragonAbility newActivty = db.getClass().newInstance();
						newActivty.setLevel(i + 1);
						unlockAbleSkills.add(newActivty);
					}catch(InstantiationException | IllegalAccessException e){
						throw new RuntimeException(e);
					}
				}
			}

			unlockAbleSkills.sort(Comparator.comparingInt(ActiveDragonAbility::getCurrentRequiredLevel));
		});
	}

	@Override
	public boolean isPauseScreen(){
		return false;
	}
}