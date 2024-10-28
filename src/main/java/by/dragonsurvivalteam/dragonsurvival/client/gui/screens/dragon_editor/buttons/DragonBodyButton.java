package by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class DragonBodyButton extends Button {
	private final DragonEditorScreen dragonEditorScreen;
	private final AbstractDragonBody dragonBody;
	private final ResourceLocation location;
	private final int pos;
	private final boolean locked;

	public DragonBodyButton(DragonEditorScreen dragonEditorScreen, int x, int y, int xSize, int ySize, AbstractDragonBody dragonBody, int pos, boolean locked) {
		super(x, y, xSize, ySize, Component.literal(dragonBody.toString()), btn -> {
			if (!locked) {
				dragonEditorScreen.actionHistory.add(new DragonEditorScreen.EditorAction<>(dragonEditorScreen.dragonBodySelectAction, dragonBody));
			}
		}, DEFAULT_NARRATION);
		location = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/body_type_icon_" + dragonEditorScreen.dragonType.getTypeNameLowerCase() + ".png");
		this.dragonEditorScreen = dragonEditorScreen;
		this.dragonBody = dragonBody;
		this.pos = pos;
		this.locked = locked;
		this.setTooltip(Tooltip.create(Component.translatable("ds.gui.body_types." + dragonBody.getBodyName().toLowerCase() + ".tooltip")));
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		active = visible = dragonEditorScreen.showUi;
		RenderSystem.setShaderTexture(0, location);
		RenderSystem.setShader(GameRenderer::getRendertypeTranslucentShader);

		int i = 0;
		if (this.dragonBody.equals(dragonEditorScreen.dragonBody)) {
			i = 2;
		} else if (this.locked) {
			i = 3;
		} else if (this.isHoveredOrFocused()) {
			i = 1;
		}
		guiGraphics.blit(this.location, getX(), getY(), pos * this.width, i * this.height, this.width, this.height, 256, 256);
	}
}
