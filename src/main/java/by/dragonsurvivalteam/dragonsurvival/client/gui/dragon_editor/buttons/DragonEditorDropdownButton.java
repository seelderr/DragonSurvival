package by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.DropDownButton;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.SkinCap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.Locale;

public class DragonEditorDropdownButton extends DropDownButton{
	private final DragonEditorScreen dragonEditorScreen;
	private final EnumSkinLayer layers;

	public DragonEditorDropdownButton(DragonEditorScreen dragonEditorScreen, int x, int y, int xSize, int ySize, String current, String[] values, EnumSkinLayer layers){
		super(x, y, xSize, ySize, current, values, selected -> {
			dragonEditorScreen.preset.skinAges.get(dragonEditorScreen.level).get().layerSettings.get(layers).get().selectedSkin = DragonEditorScreen.partToTechnical(selected);
			dragonEditorScreen.handler.getSkinData().compileSkin();
			dragonEditorScreen.update();
		});
		this.dragonEditorScreen = dragonEditorScreen;
		this.layers = layers;
	}

	@Override
	public void render(@NotNull final GuiGraphics guiGraphics, int p_230430_2_, int p_230430_3_, float p_230430_4_){
		active = visible = dragonEditorScreen.showUi;
		super.render(guiGraphics, p_230430_2_, p_230430_3_, p_230430_4_);
		String currentValue = dragonEditorScreen.preset.skinAges.get(dragonEditorScreen.level).get().layerSettings.get(layers).get().selectedSkin;

		if (!Objects.equals(currentValue, current)) {
			current = DragonEditorScreen.partToTranslation(currentValue);
			updateMessage();
		}

		List<String> valueList = DragonEditorHandler.getKeys(dragonEditorScreen.dragonType, layers);

		if(layers != EnumSkinLayer.BASE){
			valueList.add(0, SkinCap.defaultSkinValue);
		}

		valueList = valueList.stream().map(DragonEditorScreen::partToTranslation).toList();

		values = valueList.toArray(new String[0]);
		active = !dragonEditorScreen.preset.skinAges.get(dragonEditorScreen.level).get().defaultSkin;
	}

	@Override
	public void updateMessage(){
		if(current != null){
			message = Component.translatable(DragonEditorScreen.partToTranslation(current));
		}
	}

/*	@Override
	public void onPress(){
		Screen screen = Minecraft.getInstance().screen;

		if(!toggled){
			int offset = screen.height - (getY() + height + 80);
			list = new DropdownList(getX(), getY() + height + Math.min(offset, 0), width, (int)(Math.max(1, Math.min(values.length, maxItems)) * (height * 1.5f)), width / 2 - 6);
			DropdownEntry center = null;

			for(int i = 0; i < values.length; i += 2){
				String val = values[i];
				String val2 = i < values.length - 1 ? values[i + 1] : null;

				DragonEditorDropdownEntry ent = new DragonEditorDropdownEntry(this, i);

				int width = list.getWidth() / 2 - 6;

				ent.children.add(new EditorPartButton(dragonEditorScreen, this, getX() + 3, getY(), width, width, val, setter, layers));

				if(val2 != null){
					ent.children.add(new EditorPartButton(dragonEditorScreen, this, getX() + 3 + width, getY(), width, width, val2, setter, layers));
				}

				list.addEntry(ent);

				if(Objects.equals(val, current)){
					center = ent;
				}
			}

			if(center != null){
				list.centerScrollOn(center);
			}

			boolean hasBorder = false;
			if(!screen.children.isEmpty()){
				screen.children.add(0, list);
				screen.children.add(list);

				for(GuiEventListener child : screen.children){
					if(child instanceof AbstractSelectionList){
						if(((AbstractSelectionList<?>)child).renderTopAndBottom){
							hasBorder = true;
							break;
						}
					}
				}
			}else{
				screen.children.add(list);
			}

			boolean finalHasBorder = hasBorder;
			renderButton = new ExtendedButton(0, 0, 0, 0, Component.empty(), null){
				@Override
				public void render(@NotNull final GuiGraphics guiGraphics, int p_230430_2_, int p_230430_3_, float p_230430_4_){
					active = visible = false;
					list.visible = DragonEditorDropdownButton.this.visible;

					if(finalHasBorder){
						RenderSystem.enableScissor(0, (int)(32 * Minecraft.getInstance().getWindow().getGuiScale()), Minecraft.getInstance().getWindow().getScreenWidth(), Minecraft.getInstance().getWindow().getScreenHeight() - (int)(32 * Minecraft.getInstance().getWindow().getGuiScale()) * 2);
					}

					if(list.visible){
						// TODO :: What does this render exactly? Disabling it doesn't seem to change anything
						list.render(guiGraphics, p_230430_2_, p_230430_3_, p_230430_4_);
					}

					if(finalHasBorder){
						RenderSystem.disableScissor();
					}
				}
			};
			screen.renderables.add(renderButton);
		}else{
			screen.children.removeIf(s -> s == list);
			screen.renderables.removeIf(s -> s == renderButton);
		}

		toggled = !toggled;
		updateMessage();
	}*/
}