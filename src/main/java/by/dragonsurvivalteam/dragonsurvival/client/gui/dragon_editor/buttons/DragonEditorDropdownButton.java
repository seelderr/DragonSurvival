package by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DragonEditorDropdownEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownEntry;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.DropDownButton;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.SkinCap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class DragonEditorDropdownButton extends DropDownButton{
	private final DragonEditorScreen dragonEditorScreen;
	private final EnumSkinLayer layers;

	public DragonEditorDropdownButton(DragonEditorScreen dragonEditorScreen, int x, int y, int xSize, int ySize, String current, String[] values, EnumSkinLayer layers){
		super(x, y, xSize, ySize, current, values, s -> {
			dragonEditorScreen.preset.skinAges.get(dragonEditorScreen.level).get().layerSettings.get(layers).get().selectedSkin = s;
			dragonEditorScreen.handler.getSkinData().compileSkin();
			dragonEditorScreen.update();
		});
		this.dragonEditorScreen = dragonEditorScreen;
		this.layers = layers;
	}

	@Override
	public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
		active = visible = dragonEditorScreen.showUi;
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		String curValue = dragonEditorScreen.preset.skinAges.get(dragonEditorScreen.level).get().layerSettings.get(layers).get().selectedSkin;

		if(curValue != current){
			current = curValue;
			updateMessage();
		}

		ArrayList<String> valueList = DragonEditorHandler.getKeys(dragonEditorScreen.type, layers);

		if(layers != EnumSkinLayer.BASE){
			valueList.add(0, SkinCap.defaultSkinValue);
		}

		values = valueList.toArray(new String[0]);
		active = !dragonEditorScreen.preset.skinAges.get(dragonEditorScreen.level).get().defaultSkin;
	}

	@Override
	public void updateMessage(){
		if(current != null){
			message = new TextComponent((current.substring(0, 1).toUpperCase(Locale.ROOT) + current.substring(1).toLowerCase(Locale.ROOT)).replace("_", " "));
		}
	}

	@Override
	public void onPress(){
		Screen screen = Minecraft.getInstance().screen;

		if(!toggled){
			int offset = screen.height - (y + height + 80);
			list = new DropdownList(x, y + height + Math.min(offset, 0), width, (int)(Math.max(1, Math.min(values.length, maxItems)) * (height * 1.5f)), width / 2 - 6);
			DropdownEntry center = null;

			for(int i = 0; i < values.length; i += 2){
				String val = values[i];
				String val2 = i < values.length - 1 ? values[i + 1] : null;

				DragonEditorDropdownEntry ent = new DragonEditorDropdownEntry(this, i);

				int width = list.getWidth() / 2 - 6;

				ent.children.add(new EditorPartButton(dragonEditorScreen, this, x + 3, y, width, width, val, setter, layers));

				if(val2 != null){
					ent.children.add(new EditorPartButton(dragonEditorScreen, this, x + 3 + width, y, width, width, val2, setter, layers));
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
			if(screen.children.size() > 0){
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
			renderButton = new ExtendedButton(0, 0, 0, 0, TextComponent.EMPTY, null){
				@Override
				public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
					active = visible = false;
					list.visible = DragonEditorDropdownButton.this.visible;

					if(finalHasBorder){
						RenderSystem.enableScissor(0, (int)(32 * Minecraft.getInstance().getWindow().getGuiScale()), Minecraft.getInstance().getWindow().getScreenWidth(), Minecraft.getInstance().getWindow().getScreenHeight() - (int)(32 * Minecraft.getInstance().getWindow().getGuiScale()) * 2);
					}

					if(list.visible){
						list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
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
	}
}