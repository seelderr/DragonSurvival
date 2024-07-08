package by.dragonsurvivalteam.dragonsurvival.client.gui.screens;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;
import static com.mojang.blaze3d.platform.GlConst.GL_ALWAYS;
import static com.mojang.blaze3d.platform.GlConst.GL_LEQUAL;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.AltarTypeButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.HelpButton;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayerUtils;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonBodies;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DragonAltarScreen extends Screen{
	public static final ResourceLocation CONFIRM_BUTTON = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/confirm_button.png");
	public static final ResourceLocation CANCEL_BUTTON = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/cancel_button.png");
	private static final ResourceLocation backgroundTexture = ResourceLocation.withDefaultNamespace("textures/block/black_concrete.png");
	private final String[] animations = {"sit_head_locked", "idle_head_locked", "fly_head_locked", "swim_fast_head_locked", "run_head_locked", "dig_head_locked", "resting_left_head_locked", "vibing_sitting", "shy_sitting", "vibing_sitting", "rocking_on_back" };
	public DragonStateHandler handler1 = new DragonStateHandler();
	public DragonStateHandler handler2 = new DragonStateHandler();
	private int guiLeft;
	private int guiTop;
	private boolean hasInit = false;
	private int animation1 = 1;
	private int animation2 = 0;
	private int tick;

	static double xrot = 0;
	static double yrot = 0;
	static double zrot = 0;

	public DragonAltarScreen(){
		super(Component.translatable("ds.gui.dragon_altar"));
	}

	@Override
	public void onClose() {
		super.onClose();
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(
			cap -> {
				cap.isInAltar = false;
				if(!cap.hasUsedAltar) {
					Minecraft.getInstance().player.displayClientMessage(Component.translatable("ds.gui.altar.can_still_use"), false);
				}
			}
		);
	}

	@Override
	public void render(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if(minecraft == null){
			return;
		}

		renderBackground(guiGraphics, mouseX, mouseY, partialTick);

		tick++;

		if(tick % 200 * 20 == 0){
			animation1++;
			animation2++;

			int randBody = (int) (Math.random() * (DragonBodies.bodyMappings.size()));
			//System.out.println("body num: " + randBody + " and: " + DragonBodies.bodyMappings.keySet().toArray()[randBody]);

			if (handler1.getBody() == null) {
				handler1.setBody(DragonBodies.CENTER);
			}
			handler2.setBody(DragonBodies.staticBodies.get(handler1.getBody().getBodyName()));
			handler1.setBody(DragonBodies.staticBodies.get(DragonBodies.bodyMappings.keySet().toArray()[randBody]));

			if(animation1 >= animations.length){
				animation1 = 0;
			}

			if(animation2 >= animations.length){
				animation2 = 0;
			}
		}

		for(Renderable btn : renderables){
			if(btn instanceof AltarTypeButton button){
				if(button.isHoveredOrFocused()){
					handler1.setType(button.type);
					handler1.setHasFlight(true);
					handler1.setSize(DragonLevel.NEWBORN.size);
					handler1.getSkinData().skinPreset.skinAges.get(DragonLevel.NEWBORN).get().defaultSkin = true;

					handler2.setType(button.type);
					handler2.setHasFlight(true);
					handler2.setSize(button.type == null ? DragonLevel.NEWBORN.size : DragonLevel.ADULT.size);
					handler2.getSkinData().skinPreset.skinAges.get(button.type == null ? DragonLevel.NEWBORN : DragonLevel.ADULT).get().defaultSkin = true;

					FakeClientPlayerUtils.getFakePlayer(0, handler1).animationSupplier = () -> animations[animation1];
					FakeClientPlayerUtils.getFakePlayer(1, handler2).animationSupplier = () -> animations[animation2];

					LivingEntity entity1;
					int entity1Scale = 40;
					if(handler1.isDragon()) {
						entity1 = FakeClientPlayerUtils.getFakeDragon(0, handler1);
						entity1Scale = 20;
					} else {
						entity1 = FakeClientPlayerUtils.getFakePlayer(0, handler1);
					}

					LivingEntity entity2;
					if(handler2.isDragon()) {
						entity2 = FakeClientPlayerUtils.getFakeDragon(1, handler2);
					} else {
						entity2 = FakeClientPlayerUtils.getFakePlayer(1, handler2);
					}

					Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
					quaternion.rotateY((float)Math.toRadians(150));
					InventoryScreen.renderEntityInInventory(guiGraphics, (float) width / 2 + 170, button.getY() + button.getHeight(), entity1Scale, new Vector3f(), quaternion, null, entity1);

					Quaternionf quaternion2 = Axis.ZP.rotationDegrees(180.0F);
					quaternion2.rotateY((float)Math.toRadians(210));
					InventoryScreen.renderEntityInInventory(guiGraphics, (float) width / 2 - 170, button.getY() + button.getHeight(), 40, new Vector3f(), quaternion2, null, entity2);
				}
			}
		}

		TextRenderUtil.drawCenteredScaledText(guiGraphics, width / 2, 10, 2f, title.getString(), DyeColor.WHITE.getTextColor());

		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	@Override
	public void renderBackground(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
		// From super.renderBackground(guiGraphics);
		guiGraphics.fillGradient(0, 0, this.width, this.height, -300, -1072689136, -804253680);
		NeoForge.EVENT_BUS.post(new ScreenEvent.BackgroundRendered(this, guiGraphics));

		renderBorders(guiGraphics, backgroundTexture, 0, width, 32, height - 32, width, height);
	}

	public static void renderBorders(@NotNull final GuiGraphics guiGraphics, ResourceLocation texture, int x0, int x1, int y0, int y1, int width, int height){
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		RenderSystem.setShaderTexture(0, texture);
		float zLevel = 0;

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL_ALWAYS);
		bufferbuilder.addVertex(x0, y0, zLevel).setUv(0.0F, (float)y0 / 32.0F).setColor(64, 64, 64, 55);
		bufferbuilder.addVertex(x0 + width, y0, zLevel).setUv((float)width / 32.0F, (float)y0 / 32.0F).setColor(64, 64, 64, 255);
		bufferbuilder.addVertex(x0 + width, 0.0F, zLevel).setUv((float)width / 32.0F, 0.0F).setColor(64, 64, 64, 255);
		bufferbuilder.addVertex(x0, 0.0F, zLevel).setUv(0.0F, 0.0F).setColor(64, 64, 64, 255);
		bufferbuilder.addVertex(x0, height, zLevel).setUv(0.0F, (float)height / 32.0F).setColor(64, 64, 64, 255);
		bufferbuilder.addVertex(x0 + width, height, zLevel).setUv((float)width / 32.0F, (float)height / 32.0F).setColor(64, 64, 64, 255);
		bufferbuilder.addVertex(x0 + width, y1, zLevel).setUv((float)width / 32.0F, (float)y1 / 32.0F).setColor(64, 64, 64, 255);
		bufferbuilder.addVertex(x0, y1, zLevel).setUv(0.0F, (float)y1 / 32.0F).setColor(64, 64, 64, 255);
		BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());

		RenderSystem.depthFunc(GL_LEQUAL);
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
//		RenderSystem.disableTexture();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.addVertex(x0, y0 + 4, zLevel).setUv(0.0F, 1.0F).setColor(0, 0, 0, 0);
		bufferbuilder.addVertex(x1, y0 + 4, zLevel).setUv(1.0F, 1.0F).setColor(0, 0, 0, 0);
		bufferbuilder.addVertex(x1, y0, zLevel).setUv(1.0F, 0.0F).setColor(0, 0, 0, 255);
		bufferbuilder.addVertex(x0, y0, zLevel).setUv(0.0F, 0.0F).setColor(0, 0, 0, 255);
		bufferbuilder.addVertex(x0, y1, zLevel).setUv(0.0F, 1.0F).setColor(0, 0, 0, 255);
		bufferbuilder.addVertex(x1, y1, zLevel).setUv(1.0F, 1.0F).setColor(0, 0, 0, 255);
		bufferbuilder.addVertex(x1, y1 - 4, zLevel).setUv(1.0F, 0.0F).setColor(0, 0, 0, 0);
		bufferbuilder.addVertex(x0, y1 - 4, zLevel).setUv(0.0F, 0.0F).setColor(0, 0, 0, 0);
		BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
	}

	@Override
	protected void init(){
		super.init();

		if(!hasInit){
			hasInit = true;
		}

		guiLeft = (width - 304) / 2;
		guiTop = (height - 190) / 2;

		addRenderableWidget(new HelpButton(width / 2, 32 + 5, 16, 16, "ds.help.altar", 1));

		addRenderableWidget(new AltarTypeButton(this, DragonTypes.CAVE, width / 2 - 104, guiTop + 30));
		addRenderableWidget(new AltarTypeButton(this, DragonTypes.FOREST, width / 2 - 51, guiTop + 30));
		addRenderableWidget(new AltarTypeButton(this, DragonTypes.SEA, width / 2 + 2, guiTop + 30));
		addRenderableWidget(new AltarTypeButton(this, null, width / 2 + 55, guiTop + 30));

		addRenderableWidget(new ExtendedButton(width / 2 - 75, height - 25, 150, 20, Component.translatable("ds.gui.dragon_editor"), btn -> Minecraft.getInstance().setScreen(new DragonEditorScreen(Minecraft.getInstance().screen))){
			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
				visible = DragonStateProvider.isDragon(minecraft.player);
				super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
			}
		});
	}
}