package by.dragonsurvivalteam.dragonsurvival.client.gui.components;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.processor.IBone;

import java.util.List;
import java.util.function.Supplier;

public class DragonUIRenderComponent extends FocusableGui implements IRenderable{
	public float yRot = -3, xRot = -5;
	public float xOffset = 0, yOffset = 0;
	public float zoom = 0;
	public int x, y, width, height;
	private final Screen screen;
	private final Supplier<DragonEntity> getter;

	public DragonUIRenderComponent(Screen screen, int x, int y, int xSize, int ySize, Supplier<DragonEntity> dragonGetter){
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.width = xSize;
		this.height = ySize;
		this.getter = dragonGetter;
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks){
		if(isMouseOver(pMouseX, pMouseY)){
			screen.setFocused(this);
		}

		pMatrixStack.pushPose();
		final IBone neckandHead = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("Neck");

		if(neckandHead != null){
			neckandHead.setHidden(false);
		}

		float scale = zoom;
		pMatrixStack.scale(scale, scale, 0);
		ClientDragonRender.dragonModel.setCurrentTexture(null);
		ClientDragonRender.renderEntityInInventory(getter.get(), (x + width / 2), y + height - 50, scale, xRot, yRot, xOffset / 10, yOffset / 10);
		pMatrixStack.popPose();
	}

	public boolean isMouseOver(double pMouseX, double pMouseY){
		return pMouseX >= x && pMouseX <= x + width && pMouseY >= y && pMouseY <= y + height;
	}

	@Override
	public List<? extends IGuiEventListener> children(){
		return ImmutableList.of();
	}

	@Override
	public boolean mouseDragged(double x1, double y1, int p_231045_5_, double x2, double y2){
		if(isMouseOver(x1, y1)){
			if(p_231045_5_ == 0){
				xRot -= x2 / 5;
				yRot -= y2 / 5;
			}else if(p_231045_5_ == 1){
				xOffset -= x2 / 5;
				yOffset -= y2 / 5;

				xOffset = MathHelper.clamp(xOffset, -(width / 8), (width / 8));
				yOffset = MathHelper.clamp(yOffset, -(height / 8), (height / 8));
			}

			return true;
		}else{
			setDragging(false);
		}

		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount){
		if(isMouseOver(mouseX, mouseY)){
			zoom += (float)amount * 2;
			zoom = MathHelper.clamp(zoom, 10, 100);
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}
}