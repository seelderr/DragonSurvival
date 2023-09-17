package by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.utils.TooltipRender;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayerUtils;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotButton extends ExtendedButton implements TooltipRender{
	private static final ResourceLocation ICON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/screenshot_icon.png");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private final DragonEditorScreen dragonEditorScreen;

	public ScreenshotButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler, DragonEditorScreen dragonEditorScreen){
		super(xPos, yPos, width, height, displayString, handler);
		this.dragonEditorScreen = dragonEditorScreen;
	}

	@Override
	public void onPress(){
		super.onPress();
		if(true) return;
		
		int width = 1024;
		int height = 1024;

		NativeImage nativeimage = new NativeImage(width, height, false);
		TextureTarget framebuffer = new TextureTarget(width, height, true, false);
		framebuffer.bindWrite(true);

		LivingEntity entity = FakeClientPlayerUtils.getFakeDragon(0, dragonEditorScreen.handler);
		ClientDragonRender.renderEntityInInventory(entity, width / 2, height / 2, dragonEditorScreen.dragonRender.zoom * 4, dragonEditorScreen.dragonRender.xRot, dragonEditorScreen.dragonRender.yRot, 0, 0);

		RenderSystem.bindTexture(framebuffer.getColorTextureId());
		nativeimage.downloadTexture(0, false);
		nativeimage.flipY();

		File file1 = new File(Minecraft.getInstance().gameDirectory, "screenshots/dragon-survival");
		file1.mkdirs();
		File target = getFile(file1);
		Util.ioPool().execute(() -> {
			try{
				nativeimage.writeToFile(target);
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				nativeimage.close();
			}
		});

		framebuffer.destroyBuffers();
		Minecraft.getInstance().levelRenderer.graphicsChanged();
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
	public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

		if (isHoveredOrFocused()) {
			guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.empty().append("Currently, out of order."), pMouseX, pMouseY);
		}
	}

	@Override
	public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial){
		guiGraphics.blit(ICON, getX(), getY(), 0, 0, width, height, width, height);
	}
}