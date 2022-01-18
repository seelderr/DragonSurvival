package by.jackraidenph.dragonsurvival.client.gui.widgets;

import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
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
	public float yRot = -3;
	public float xRot = -5;
	public float zoom = 0;
	
	private Screen screen;
	private int x, y, width, height;
	private Supplier<DragonEntity> getter;
	
	public DragonUIRenderComponent(Screen screen, int x, int y, int xSize, int ySize, Supplier<DragonEntity> dragonGetter)
	{
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.width = xSize;
		this.height = ySize;
		this.getter = dragonGetter;
	}
	
	public boolean isMouseOver(double pMouseX, double pMouseY) {
		return pMouseX >= x && pMouseX <= x + width && pMouseY >= y && pMouseY <= y + height;
	}
	
	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		if(isMouseOver(pMouseX, pMouseY)){
			screen.setFocused(this);
		}
		
		pMatrixStack.pushPose();
		final IBone neckandHead = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("Neck");
		
		if (neckandHead != null) {
			neckandHead.setHidden(false);
		}
		
		float scale = zoom;
		pMatrixStack.scale(scale, scale, scale);
		pMatrixStack.translate(0, 0, 400);
		ClientDragonRender.dragonModel.setCurrentTexture(null);
		ClientDragonRender.renderEntityInInventory(getter.get(), x + width / 2, y + height - 30, scale, xRot, yRot);
		pMatrixStack.popPose();
	}
	
	@Override
	public boolean mouseDragged(double x1, double y1, int p_231045_5_, double x2, double y2)
	{
		xRot -= x2 / 6;
		yRot -= y2 / 6;
		
		xRot = MathHelper.clamp(xRot, -17, 17);
		yRot = MathHelper.clamp(yRot, -17, 17);
		return true;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		if(isMouseOver(mouseX, mouseY)) {
			zoom += amount;
			zoom = MathHelper.clamp(zoom, 10, 80);
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}
	
	@Override
	public List<? extends IGuiEventListener> children()
	{
		return ImmutableList.of();
	}
}