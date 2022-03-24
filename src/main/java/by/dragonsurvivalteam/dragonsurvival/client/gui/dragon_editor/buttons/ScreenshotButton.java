package by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayerUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ScreenshotButton extends ExtendedButton{
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private final DragonEditorScreen dragonEditorScreen;

	public ScreenshotButton(int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable handler, DragonEditorScreen dragonEditorScreen){
		super(xPos, yPos, width, height, displayString, handler);
		this.dragonEditorScreen = dragonEditorScreen;
	}

	@Override
	public void onPress(){
		super.onPress();
		int width = 1024;
		int height = 1024;

		RenderSystem.pushMatrix();
		Framebuffer framebuffer = new Framebuffer(width, height, true, false);
		framebuffer.bindWrite(true);
		framebuffer.blitToScreen(width, height);

		ClientDragonRender.renderEntityInInventory(FakeClientPlayerUtils.getFakeDragon(0, dragonEditorScreen.handler), width / 2, height / 2, dragonEditorScreen.dragonRender.zoom * 4, dragonEditorScreen.dragonRender.xRot, dragonEditorScreen.dragonRender.yRot, 0, 0);

		NativeImage nativeimage = new NativeImage(width, height, false);
		RenderSystem.bindTexture(framebuffer.getColorTextureId());
		nativeimage.downloadTexture(0, false);
		nativeimage.flipY();

		File file1 = new File(Minecraft.getInstance().gameDirectory, "screenshots/dragon-survival");
		file1.mkdir();
		File target = getFile(file1);

		Util.ioPool().execute(() -> {
			try{
				nativeimage.writeToFile(target);
			}catch(Exception ignored){
			}finally{
				nativeimage.close();
			}
		});

		framebuffer.unbindWrite();
		framebuffer.destroyBuffers();
		RenderSystem.popMatrix();

		Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
	}

	public static File getFile(File pGameDirectory){
		String s = DATE_FORMAT.format(new Date());
		int i = 1;

		while(true){
			File file1 = new File(pGameDirectory, s + (i == 1 ? "" : "_" + i) + ".png");
			if(!file1.exists()){
				return file1;
			}

			++i;
		}
	}

	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial){
		Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/screenshot_icon.png"));
		blit(mStack, x, y, 0, 0, width, height, width, height);

		if(this.isHovered()){
			this.renderToolTip(mStack, mouseX, mouseY);
		}
	}

	@Override
	public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_){
		GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.dragon_editor.screenshot")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
	}
}