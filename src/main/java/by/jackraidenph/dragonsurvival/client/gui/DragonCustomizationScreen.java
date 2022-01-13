package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationRegistry;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.DragonCustomizationHandler;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.CustomizationCycleButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.CustomizationSlotButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.HelpButton;
import by.jackraidenph.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities.SkinCap;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.RequestClientData;
import by.jackraidenph.dragonsurvival.network.SkinCustomization.SyncPlayerAllCustomization;
import by.jackraidenph.dragonsurvival.network.SynchronizationController;
import by.jackraidenph.dragonsurvival.network.entity.player.SynchronizeDragonCap;
import by.jackraidenph.dragonsurvival.network.flight.SyncSpinStatus;
import by.jackraidenph.dragonsurvival.network.status.SyncAltarCooldown;
import by.jackraidenph.dragonsurvival.util.Functions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DragonCustomizationScreen extends Screen
{
	public DragonCustomizationScreen(Screen source)
	{
		this(source, DragonType.NONE);
	}
	
	public DragonCustomizationScreen(Screen source, DragonType type)
	{
		super(new TranslationTextComponent("ds.gui.customization"));
		this.source = source;
		this.type = type;
	}
	
	private int guiLeft;
	private int guiTop;
	
	private Screen source;
	
	public DragonLevel level = DragonLevel.ADULT;
	public DragonType type;
	
	public HashMap<DragonLevel, HashMap<CustomizationLayer, String>> map = new HashMap<>();
	
	private float yRot = -3;
	private float xRot = -5;
	private float zoom = 0;
	
	private DragonEntity dragon;
	private static RemoteClientPlayerEntity clientPlayer;
	
	private String[] animations = {"sit", "idle", "fly", "swim_fast", "run"};
	private int curAnimation = 0;
	
	public int currentSelected;
	private int lastSelected;
	
	private boolean hasInit = false;
	
	public void update(){
		DragonStateHandler clientHandler = DragonStateProvider.getCap(clientPlayer).orElse(null);
		
		clientHandler.getSkin().playerSkinLayers = map;
		clientHandler.setSize(level.size);
		
		if(type != DragonType.NONE) {
			clientHandler.setType(type);
		}
		
		if(currentSelected != lastSelected){
			CustomizationRegistry.savedCustomizations.saved.computeIfAbsent(clientHandler.getType(), (b) -> new HashMap<>());
			CustomizationRegistry.savedCustomizations.saved.get(clientHandler.getType()).computeIfAbsent(currentSelected, (b) -> new HashMap<>());
			
			map = new HashMap<>();
			CustomizationRegistry.savedCustomizations.saved.get(clientHandler.getType()).get(currentSelected).forEach((level, mp) -> {
				mp.forEach((layer, key) -> {
					map.computeIfAbsent(level, (b) -> new HashMap<>());
					map.get(level).put(layer, key);
				});
			});
			
			clientHandler.getSkin().playerSkinLayers = map;
		}
		
		lastSelected = currentSelected;
	}
	
	@Override
	protected void init()
	{
		super.init();
		DragonStateHandler handler = DragonStateProvider.getCap(getMinecraft().player).orElse(null);
		
		if(!hasInit){
			if(handler != null){
				level = handler.getLevel();
				zoom = level.size;
				
				if(type == DragonType.NONE){
					type = handler.getType();
				}
				
				currentSelected = CustomizationRegistry.savedCustomizations.current.getOrDefault(type, new HashMap<>()).getOrDefault(level, 0);
				HashMap<DragonLevel, HashMap<CustomizationLayer, String>> mp1 = CustomizationRegistry.savedCustomizations.saved.getOrDefault(type, new HashMap<>()).getOrDefault(currentSelected, new HashMap<>());
				
				mp1.forEach((level, mp) -> {
					mp.forEach((layer, key) -> {
						map.computeIfAbsent(level, (b) -> new HashMap<>());
						map.get(level).put(layer, key);
					});
				});
			}
			
			if(clientPlayer == null) {
				clientPlayer = new RemoteClientPlayerEntity(minecraft.level, new GameProfile(UUID.randomUUID(), "DRAGON_RENDER"));
				
				DragonStateProvider.getCap(clientPlayer).ifPresent((cap) -> {
					cap.setHasWings(true);
					cap.setType(type);
				});
			}
			
			dragon = new DragonEntity(DSEntities.DRAGON, minecraft.level){
				@Override
				public void registerControllers(AnimationData animationData) {
					animationData.addAnimationController(new AnimationController<DragonEntity>(this, "controller", 2, (event) -> {
						AnimationBuilder builder = new AnimationBuilder();
						builder.addAnimation(animations[curAnimation], true);
						event.getController().setAnimation(builder);
						return PlayState.CONTINUE;
					}));
				}
				
				@Override
				public PlayerEntity getPlayer()
				{
					return clientPlayer;
				}
			};
			hasInit = true;
			update();
		}
		
		this.guiLeft = (this.width - 256) / 2;
		this.guiTop = (this.height - 120) / 2;
		
		addButton(new HelpButton(type, guiLeft - 20, guiTop - 55, 16, 16,"ds.help.customization"));
		
		addButton(new Button(guiLeft - 120, guiTop - 22, 120, 20, new TranslationTextComponent("ds.level.newborn"), (btn) -> {
			level = DragonLevel.BABY;
			zoom = level.size;
			update();
		}){
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				int j = isHovered || level == DragonLevel.BABY ? 16777215 : 10526880;
				Functions.renderCenteredScaledText(stack, x + (width / 2), y + 4, 1.5f, this.getMessage().getString(), j | MathHelper.ceil(this.alpha * 255.0F) << 24);
			}
		});
		addButton(new Button(((width) / 2) - 60, guiTop - 22, 120, 20, new TranslationTextComponent("ds.level.young"), (btn) -> {
			level = DragonLevel.YOUNG;
			zoom = level.size;
			update();
		}){
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				int j = isHovered || level == DragonLevel.YOUNG ? 16777215 : 10526880;
				Functions.renderCenteredScaledText(stack, x + (width / 2), y + 4, 1.5f, this.getMessage().getString(), j | MathHelper.ceil(this.alpha * 255.0F) << 24);
			}
		});
		addButton(new Button(((width) / 2) + 120, guiTop - 22, 120, 20, new TranslationTextComponent("ds.level.adult"), (btn) -> {
			level = DragonLevel.ADULT;
			zoom = level.size;
			update();
		}){
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				int j = isHovered || level == DragonLevel.ADULT ? 16777215 : 10526880;
				Functions.renderCenteredScaledText(stack, x + (width / 2), y + 4, 1.5f, this.getMessage().getString(), j | MathHelper.ceil(this.alpha * 255.0F) << 24);
			}
		});
		
		int maxWidth = -1;
		
		for(CustomizationLayer layers : CustomizationLayer.values()) {
			String name = layers.name().substring(0, 1).toUpperCase(Locale.ROOT) + layers.name().toLowerCase().substring(1).replace("_", " ");
			maxWidth = (int)Math.max(maxWidth, font.width(name) * 1.45F);
		}
		
		int i = 0;
		for(CustomizationLayer layers : CustomizationLayer.values()){
			addButton(new CustomizationCycleButton(width / 2 + (15 + (i % 2 != 0 ? 130 : 0)) - 6 + maxWidth, guiTop + 15 + (i / 2) * 45 + 15, true, layers, this));
			addButton(new CustomizationCycleButton(width / 2 + (15 + (i % 2 != 0 ? 130 : 0)) - 6 - maxWidth, guiTop + 15 + (i / 2) * 45 + 15, false, layers, this));
			i++;
		}
		
		addButton(new Button(guiLeft + 40, guiTop + 15 + 45 + 60 + 5, 15, 15, new TranslationTextComponent(""), (btn) -> {
			curAnimation += 1;
			
			if(curAnimation >= animations.length){
				curAnimation = 0;
			}
		}){
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				Minecraft.getInstance().getTextureManager().bind(ClientMagicHUDHandler.widgetTextures);
				
				if (isHovered()) {
					blit(stack, x, y, 66 / 2, 222 / 2, 11, 17, 128, 128);
				} else {
					blit(stack, x, y, 44 / 2, 222 / 2, 11, 17, 128, 128);
				}
			}
		});
		
		addButton(new Button(guiLeft - 50, guiTop + 15 + 45 + 60 + 5, 15, 15, new TranslationTextComponent(""), (btn) -> {
			curAnimation -= 1;
			
			if(curAnimation < 0){
				curAnimation = animations.length - 1;
			}
		}){
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				Minecraft.getInstance().getTextureManager().bind(ClientMagicHUDHandler.widgetTextures);
				
				if(isHovered()){
					blit(stack, x, y, 22 / 2, 222 / 2, 11, 17,128, 128);
				}else{
					blit(stack, x, y, 0, 222 / 2, 11, 17, 128, 128);
				}
			}
		});
		
		for(int num = 1; num <= 9; num++){
			addButton(new CustomizationSlotButton(guiLeft - 90, guiTop + 10 + ((num-1) * 12) + 5 + 20, num, this));
		}
		
		addButton(new ExtendedButton(guiLeft - 10, guiTop + 150 + 10, 20, 20, new StringTextComponent(""), null){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				super.renderButton(mStack, mouseX, mouseY, partial);
				Minecraft.getInstance().getTextureManager().bind(DragonAltarGUI.CONFIRM_BUTTON);
				blit(mStack, x + 1, y, 0, 0, 20, 20, 20, 20);
				
				if(isHovered){
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.customization.tooltip.done")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}
			
			@Override
			public void onPress()
			{
				DragonStateProvider.getCap(minecraft.player).ifPresent(cap -> {
					minecraft.player.level.playSound(minecraft.player, minecraft.player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 1, 0.7f);
					
					if(cap.getType() != type) {
						Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("ds." + type.name().toLowerCase() + "_dragon_choice"), Minecraft.getInstance().player.getUUID());
						cap.setType(type);
						
						if (!ConfigHandler.SERVER.saveGrowthStage.get() || cap.getSize() == 0) {
							cap.setSize(DragonLevel.BABY.size);
						}
						
						cap.setHasWings(ConfigHandler.SERVER.saveGrowthStage.get() ? cap.hasWings() || ConfigHandler.SERVER.startWithWings.get() : ConfigHandler.SERVER.startWithWings.get());
						cap.setIsHiding(false);
						cap.getMovementData().spinLearned = false;
						
						NetworkHandler.CHANNEL.sendToServer(new SyncAltarCooldown(Minecraft.getInstance().player.getId(), Functions.secondsToTicks(ConfigHandler.SERVER.altarUsageCooldown.get())));
						NetworkHandler.CHANNEL.sendToServer(new SynchronizeDragonCap(minecraft.player.getId(), cap.isHiding(), cap.getType(), cap.getSize(), cap.hasWings(), ConfigHandler.SERVER.caveLavaSwimmingTicks.get(), 0));
						NetworkHandler.CHANNEL.sendToServer(new SyncSpinStatus(Minecraft.getInstance().player.getId(), cap.getMovementData().spinAttack, cap.getMovementData().spinCooldown, cap.getMovementData().spinLearned));
						SynchronizationController.sendClientData(new RequestClientData(cap.getType(), cap.getLevel()));
					}
				});
				
				DragonType type = DragonStateProvider.getDragonType(minecraft.player);
				NetworkHandler.CHANNEL.sendToServer(new SyncPlayerAllCustomization(minecraft.player.getId(), map));
				CustomizationRegistry.savedCustomizations.saved.computeIfAbsent(type, (t) -> new HashMap<>());
				CustomizationRegistry.savedCustomizations.saved.get(type).put(currentSelected, map);
				
				try {
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					FileWriter writer = new FileWriter(CustomizationRegistry.savedFile);
					gson.toJson(CustomizationRegistry.savedCustomizations, writer);
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Minecraft.getInstance().player.closeContainer();
			}
		});
		
		addButton(new ExtendedButton(guiLeft - 70, guiTop + 150 + 10, 50, 20, new TranslationTextComponent("ds.gui.customization.back"), null){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				super.renderButton(mStack, mouseX, mouseY, partial);

				if(isHovered){
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.customization.tooltip.back")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}
			
			@Override
			public void onPress()
			{
				Minecraft.getInstance().setScreen(source);
			}
		});
		
		addButton(new Button(guiLeft + 256, guiTop - 65 + 8, 19, 19, new TranslationTextComponent(""), (btn) -> {
			map.computeIfAbsent(level, (b) -> new HashMap<>());
			map.get(level).put(CustomizationLayer.HORNS, type.name().toLowerCase() + "_horns_" + level.ordinal());
			map.get(level).put(CustomizationLayer.SPIKES, type.name().toLowerCase() + "_spikes_" + level.ordinal());
			map.get(level).put(CustomizationLayer.BOTTOM, type.name().toLowerCase() + "_bottom_" + level.ordinal());
			map.get(level).put(CustomizationLayer.BASE, type.name().toLowerCase() + "_base_" + level.ordinal());
			update();
		}){
			@Override
			public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_)
			{
				GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.customization.reset")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
			}
			
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/reset_button.png"));
				blit(stack, x, y, 0, 0, width, height, width, height);
				
				if (this.isHovered()) {
					this.renderToolTip(stack, p_230431_2_, p_230431_3_);
				}
			}
		});
		
		addButton(new Button(guiLeft + 256 + 30, guiTop - 65 + 8, 19, 19, new TranslationTextComponent(""), (btn) -> {
			map.computeIfAbsent(level, (b) -> new HashMap<>());
			
			for(CustomizationLayer layer : CustomizationLayer.values()){
				ArrayList<String> keys = DragonCustomizationHandler.getKeys(minecraft.player, layer);
				
				if(layer != CustomizationLayer.BASE){
					keys.add(SkinCap.defaultSkinValue);
				}
				
				if(keys.size() > 0) {
					map.get(level).put(layer, keys.get(minecraft.player.level.random.nextInt(keys.size())));
				}
			}
			
			update();
		}){
			@Override
			public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_)
			{
				GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.customization.random")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
			}
			
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/random_icon.png"));
				blit(stack, x, y, 0, 0, width, height, width, height);
				
				if (this.isHovered()) {
					this.renderToolTip(stack, p_230431_2_, p_230431_3_);
				}
			}
		});
	}
	
	@Override
	public void render(MatrixStack stack, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		DragonStateHandler handler = DragonStateProvider.getCap(getMinecraft().player).orElse(null);
		
		Color darkening = new Color(0.05f, 0.05f, 0.05f, 0.5f);
		AbstractGui.fill(stack, 0, 0, width, height, darkening.getRGB());
		int startHeight = guiTop + 10;
		int endHeight = guiTop + 150;
		
		AbstractGui.fill(stack, 0, startHeight, this.width, endHeight, new Color(0.05F, 0.05F, 0.05F, 0.75F).getRGB());
		AbstractGui.fill(stack, 0, startHeight - 75, this.width, startHeight - 40, new Color(0.05F, 0.05F, 0.05F, 0.75F).getRGB());
		AbstractGui.fill(stack, 0, startHeight - 35, this.width, startHeight - 10, new Color(0.05F, 0.05F, 0.05F, 0.75F).getRGB());
		
		Functions.renderCenteredScaledText(stack, width / 2, startHeight - 65, 2f, title.getString(), DyeColor.WHITE.getTextColor());
		
		int i = 0;
		for(CustomizationLayer layers : CustomizationLayer.values()){
			String name = layers.name;
			String value = map.getOrDefault(level, new HashMap<>()).getOrDefault(layers, SkinCap.defaultSkinValue);
			SkinsScreen.drawNonShadowLineBreak(stack, font, new StringTextComponent(name), width / 2 + (15 + (i % 2 != 0 ? 130 : 0)), guiTop + 15 + (i / 2) * 45, DyeColor.WHITE.getTextColor());
			SkinsScreen.drawNonShadowLineBreak(stack, font, new StringTextComponent(value), width / 2 + (15 + (i % 2 != 0 ? 130 : 0)), guiTop + 15 + (i / 2) * 45 + 20, DyeColor.GRAY.getTextColor());
			i++;
		}
		
		stack.pushPose();
		final IBone neckandHead = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("Neck");
		
		if (neckandHead != null) {
			neckandHead.setHidden(false);
		}
		
		float scale = zoom;
		stack.scale(scale, scale, scale);
		stack.translate(0, 0, 400);
		ClientDragonRender.dragonModel.setCurrentTexture(null);
		renderEntityInInventory(guiLeft, guiTop + 15 + 45 + 50, scale, xRot, yRot, dragon);
		stack.popPose();
		
		SkinsScreen.drawNonShadowLineBreak(stack, font, new StringTextComponent(animations[curAnimation]), guiLeft, guiTop + 15 + 45 + 60 + 10, DyeColor.GRAY.getTextColor());
		
		Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/save_icon.png"));
		blit(stack,guiLeft - 92, guiTop + 10 + 5, 0, 0, 16, 16,16, 16);
		
		super.render(stack, p_230430_2_, p_230430_3_, p_230430_4_);
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}
	
	public void renderEntityInInventory(int p_228187_0_, int p_228187_1_, float p_228187_2_, float p_228187_3_, float p_228187_4_, LivingEntity p_228187_5_) {
		float f = p_228187_3_;
		float f1 = p_228187_4_;
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float)p_228187_0_, (float)p_228187_1_, 0);
		RenderSystem.scalef(1.0F, 1.0F, -1.0F);
		MatrixStack matrixstack = new MatrixStack();
		matrixstack.translate(0, (Math.abs(yRot) / 17) * -(Math.abs(zoom)), 0);
		matrixstack.scale((float)p_228187_2_, (float)p_228187_2_, (float)p_228187_2_);
		Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
		Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 10.0F);
		quaternion.mul(quaternion1);
		matrixstack.mulPose(quaternion);
		float f2 = p_228187_5_.yBodyRot;
		float f3 = p_228187_5_.yRot;
		float f4 = p_228187_5_.xRot;
		float f5 = p_228187_5_.yHeadRotO;
		float f6 = p_228187_5_.yHeadRot;
		p_228187_5_.yBodyRot = 180.0F + f * 10.0F;
		p_228187_5_.yRot = 180.0F + f * 10.0F;
		p_228187_5_.xRot = -f1 * 10.0F;
		p_228187_5_.yHeadRot = p_228187_5_.yRot;
		p_228187_5_.yHeadRotO = p_228187_5_.yRot;
		EntityRendererManager entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
		boolean renderHitbox = entityrenderermanager.shouldRenderHitBoxes();
		quaternion1.conj();
		entityrenderermanager.overrideCameraOrientation(quaternion1);
		entityrenderermanager.setRenderShadow(false);
		IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> {
			entityrenderermanager.setRenderHitBoxes(false);
			entityrenderermanager.render(p_228187_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
			entityrenderermanager.setRenderHitBoxes(renderHitbox);
		});
		irendertypebuffer$impl.endBatch();
		entityrenderermanager.setRenderShadow(true);
		
		p_228187_5_.yBodyRot = f2;
		p_228187_5_.yRot = f3;
		p_228187_5_.xRot = f4;
		p_228187_5_.yHeadRotO = f5;
		p_228187_5_.yHeadRot = f6;
		RenderSystem.popMatrix();
	}
	
	@Override
	public boolean mouseDragged(double x1, double y1, int p_231045_5_, double x2, double y2)
	{
		xRot -= x2 / 6;
		yRot -= y2 / 6;
		
		xRot = MathHelper.clamp(xRot, -17, 17);
		yRot = MathHelper.clamp(yRot, -17, 17);
		
		return super.mouseDragged(x1, y1, p_231045_5_, x2, y2);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		zoom += amount;
		zoom = MathHelper.clamp(zoom, 10, 80);
		
		return true;
	}
}
