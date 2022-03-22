package by.dragonsurvivalteam.dragonsurvival.client.gui;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.AltarTypeButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.HelpButton;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayerUtils;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class DragonAltarGUI extends Screen{
	public static final ResourceLocation CONFIRM_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/confirm_button.png");
	public static final ResourceLocation CANCEL_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/cancel_button.png");
	private static final ResourceLocation backgroundTexture = new ResourceLocation("textures/block/dirt.png");
	public DragonStateHandler handler1 = new DragonStateHandler();
	public DragonStateHandler handler2 = new DragonStateHandler();
	private int guiLeft;
	private int guiTop;
	private boolean hasInit = false;
	private final String[] animations = {"sit", "idle", "fly", "swim_fast", "run"};
	private int animation1 = 1;
	private int animation2 = 0;
	private int tick;

	public DragonAltarGUI(){
		super(new TranslationTextComponent("ds.gui.dragon_altar"));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        if(this.minecraft == null){
            return;
        }

		matrixStack.pushPose();
		matrixStack.translate(0, 0, -600);
		this.renderBackground(matrixStack);
		matrixStack.popPose();

		tick++;

		if((tick % 200 * 20) == 0){
			animation1++;
			animation2++;

			if(animation1 >= animations.length){
				animation1 = 0;
			}

			if(animation2 >= animations.length){
				animation2 = 0;
			}
		}

		for(Widget btn : buttons){
			if(btn instanceof AltarTypeButton){
				AltarTypeButton button = (AltarTypeButton)btn;

				if(button.isHovered()){
					handler1.setType(button.type);
					handler1.setHasWings(true);
					handler1.setSize(DragonLevel.BABY.size);
					handler1.getSkin().skinPreset.skinAges.get(DragonLevel.BABY).defaultSkin = true;

					handler2.setType(button.type);
					handler2.setHasWings(true);
					handler2.setSize(button.type == DragonType.NONE ? DragonLevel.BABY.size : DragonLevel.ADULT.size);
					handler2.getSkin().skinPreset.skinAges.get(button.type == DragonType.NONE ? DragonLevel.BABY : DragonLevel.ADULT).defaultSkin = true;

					FakeClientPlayerUtils.getFakePlayer(0, handler1).animationSupplier = () -> animations[animation1];
					FakeClientPlayerUtils.getFakePlayer(1, handler2).animationSupplier = () -> animations[animation2];

					renderDragon(width / 2 + 170, button.y + (button.getHeight() / 2) + 20, 5, matrixStack, 20, FakeClientPlayerUtils.getFakePlayer(0, handler1), FakeClientPlayerUtils.getFakeDragon(0, handler1));
					renderDragon(width / 2 - 205, button.y + (button.getHeight() / 2) + 1, -4, matrixStack, 40, FakeClientPlayerUtils.getFakePlayer(1, handler2), FakeClientPlayerUtils.getFakeDragon(1, handler2));
				}
			}
		}

		TextRenderUtil.drawCenteredScaledText(matrixStack, width / 2, 10, 2f, title.getString(), DyeColor.WHITE.getTextColor());

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void renderBackground(MatrixStack pMatrixStack){
		super.renderBackground(pMatrixStack);
		renderBorders(backgroundTexture, 0, width, 32, height - 32, width, height);
	}

	public static void renderBorders(ResourceLocation texture, int x0, int x1, int y0, int y1, int width, int height){
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		Minecraft.getInstance().getTextureManager().bind(texture);

		RenderSystem.pushMatrix();
		double zLevel = 0;

		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.vertex(x0, y0, zLevel).uv(0.0F, (float)y0 / 32.0F).color(64, 64, 64, 255).endVertex();
		bufferbuilder.vertex(x0 + width, y0, zLevel).uv((float)width / 32.0F, (float)y0 / 32.0F).color(64, 64, 64, 255).endVertex();
		bufferbuilder.vertex(x0 + width, 0.0D, zLevel).uv((float)width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
		bufferbuilder.vertex(x0, 0.0D, zLevel).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
		bufferbuilder.vertex(x0, height, zLevel).uv(0.0F, (float)height / 32.0F).color(64, 64, 64, 255).endVertex();
		bufferbuilder.vertex(x0 + width, height, zLevel).uv((float)width / 32.0F, (float)height / 32.0F).color(64, 64, 64, 255).endVertex();
		bufferbuilder.vertex(x0 + width, y1, zLevel).uv((float)width / 32.0F, (float)y1 / 32.0F).color(64, 64, 64, 255).endVertex();
		bufferbuilder.vertex(x0, y1, zLevel).uv(0.0F, (float)y1 / 32.0F).color(64, 64, 64, 255).endVertex();
		tessellator.end();


		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
		RenderSystem.disableAlphaTest();
		RenderSystem.shadeModel(7425);
		RenderSystem.disableTexture();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.vertex(x0, y0 + 4, zLevel).uv(0.0F, 1.0F).color(0, 0, 0, 0).endVertex();
		bufferbuilder.vertex(x1, y0 + 4, zLevel).uv(1.0F, 1.0F).color(0, 0, 0, 0).endVertex();
		bufferbuilder.vertex(x1, y0, zLevel).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
		bufferbuilder.vertex(x0, y0, zLevel).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
		bufferbuilder.vertex(x0, y1, zLevel).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
		bufferbuilder.vertex(x1, y1, zLevel).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
		bufferbuilder.vertex(x1, y1 - 4, zLevel).uv(1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
		bufferbuilder.vertex(x0, y1 - 4, zLevel).uv(0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
		tessellator.end();
		RenderSystem.popMatrix();
	}

	private void renderDragon(int x, int y, int xrot, MatrixStack matrixStack, float size, PlayerEntity player, DragonEntity dragon){
		matrixStack.pushPose();
		float scale = size * 1.5f;
		matrixStack.scale(scale, scale, scale);
		matrixStack.translate(0, 0, 400);
		ClientDragonRender.dragonModel.setCurrentTexture(null);
		ClientDragonRender.renderEntityInInventory(DragonUtils.isDragon(player) ? dragon : player, x, y, scale, xrot, -3);
		matrixStack.popPose();
	}


	@Override
	protected void init(){
		super.init();

		if(!hasInit){
			hasInit = true;
		}

		this.guiLeft = (this.width - 304) / 2;
		this.guiTop = (this.height - 190) / 2;

		this.addButton(new HelpButton(width / 2 - 9, 32 + 0, 16, 16, "ds.help.altar", 1));

		addButton(new AltarTypeButton(this, DragonType.CAVE, width / 2 - 104, this.guiTop + 30));
		addButton(new AltarTypeButton(this, DragonType.FOREST, width / 2 - 51, this.guiTop + 30));
		addButton(new AltarTypeButton(this, DragonType.SEA, width / 2 + 2, this.guiTop + 30));
		addButton(new AltarTypeButton(this, DragonType.NONE, width / 2 + 55, guiTop + 30));

		addButton(new ExtendedButton(width / 2 - 75, height - 25, 150, 20, new TranslationTextComponent("ds.gui.dragon_editor"), (btn) -> {
			Minecraft.getInstance().setScreen(new DragonEditorScreen(Minecraft.getInstance().screen));
		}){
			@Override
			public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
				this.visible = DragonUtils.isDragon(minecraft.player);
				super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
			}
		});
	}
}