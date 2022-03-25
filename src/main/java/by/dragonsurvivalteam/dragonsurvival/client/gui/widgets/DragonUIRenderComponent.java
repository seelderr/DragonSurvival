package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.entity.Dragon;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.processor.IBone;

import java.util.List;
import java.util.function.Supplier;

public class DragonUIRenderComponent extends AbstractContainerEventHandler implements Widget{
	private final Screen screen;
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	private final Supplier<Dragon> getter;
	public float yRot = -3;
	public float xRot = -5;
	public float zoom = 0;

	public DragonUIRenderComponent(Screen screen, int x, int y, int xSize, int ySize, Supplier<Dragon> dragonGetter){
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.width = xSize;
		this.height = ySize;
		this.getter = dragonGetter;
	}

	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks){
		if(isMouseOver(pMouseX, pMouseY)){
			screen.setFocused(this);
		}

		pPoseStack.pushPose();
		final IBone neckandHead = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("Neck");

		if(neckandHead != null){
			neckandHead.setHidden(false);
		}

		float scale = zoom;
		pPoseStack.scale(scale, scale, scale);
		pPoseStack.translate(0, 0, 400);
		ClientDragonRender.dragonModel.setCurrentTexture(null);
		ClientDragonRender.renderEntityInInventory(getter.get(), x + width / 2, y + height - 30, scale, xRot, yRot);
		pPoseStack.popPose();
	}

	public boolean isMouseOver(double pMouseX, double pMouseY){
		return pMouseX >= x && pMouseX <= x + width && pMouseY >= y && pMouseY <= y + height;
	}

	@Override
	public List<? extends GuiEventListener> children(){
		return ImmutableList.of();
	}

	@Override
	public boolean mouseDragged(double x1, double y1, int p_231045_5_, double x2, double y2){
		xRot -= x2 / 6;
		yRot -= y2 / 6;

		xRot = Mth.clamp(xRot, -17, 17);
		yRot = Mth.clamp(yRot, -17, 17);
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount){
		if(isMouseOver(mouseX, mouseY)){
			zoom += amount;
			zoom = Mth.clamp(zoom, 10, 80);
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}
}