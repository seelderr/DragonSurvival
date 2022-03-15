package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.components.CustomizationConfirmation;
import by.jackraidenph.dragonsurvival.client.gui.components.DragonUIRenderComponent;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.*;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.ColoredDropdownValueEntry;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownEntry;
import by.jackraidenph.dragonsurvival.client.handlers.ClientEvents;
import by.jackraidenph.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.CustomizationRegistry;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.DragonCustomizationHandler;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.EnumSkinLayer;
import by.jackraidenph.dragonsurvival.client.skinPartSystem.objects.SkinPreset;
import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayerUtils;
import by.jackraidenph.dragonsurvival.client.util.TextRenderUtil;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.capability.subcapabilities.SkinCap;
import by.jackraidenph.dragonsurvival.common.util.DragonUtils;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.RequestClientData;
import by.jackraidenph.dragonsurvival.network.dragon_editor.SyncPlayerSkinPreset;
import by.jackraidenph.dragonsurvival.network.flight.SyncSpinStatus;
import by.jackraidenph.dragonsurvival.network.status.SyncAltarCooldown;
import by.jackraidenph.dragonsurvival.network.syncing.CompleteDataSync;
import by.jackraidenph.dragonsurvival.util.Functions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
	
	public SkinPreset preset = new SkinPreset();
	
	public boolean confirmation = false;
	public boolean showUi = true;
	
	private String[] animations = {"sit", "idle", "fly", "swim_fast", "run"};
	private int curAnimation = 0;
	
	public int currentSelected;
	private int lastSelected;
	
	private boolean hasInit = false;
	
	private DragonUIRenderComponent dragonRender;
	public DragonStateHandler handler = new DragonStateHandler();
	private CustomizationConfirmation conf;
	
	public void update()
	{
		handler.getSkin().skinPreset = preset;
		handler.setSize(level.size);
		handler.setHasWings(true);
		
		if (type != DragonType.NONE) {
			handler.setType(type);
		}
		
		if (currentSelected != lastSelected) {
			preset = new SkinPreset();
			preset.readNBT(CustomizationRegistry.savedCustomizations.skinPresets.get(type).get(currentSelected).writeNBT());
			handler.getSkin().skinPreset = preset;
		}
		
		lastSelected = currentSelected;
		
		children.removeIf((s) -> s instanceof DragonUIRenderComponent);
		
		initDragonRender();
	}
	
	private void initDragonRender()
	{
		float yRot = -3, xRot = -5, zoom = 0, xOffset = 0, yOffset = 0;
		if(dragonRender != null){
			yRot = dragonRender.yRot;
			xRot = dragonRender.xRot;
			zoom = dragonRender.zoom;
			xOffset = dragonRender.xOffset;
			yOffset = dragonRender.yOffset;
		}
		
		dragonRender = new DragonUIRenderComponent(this, width / 2 - 70, guiTop, 140, 125, () -> FakeClientPlayerUtils.getFakeDragon(0, handler));
		dragonRender.xRot = xRot;
		dragonRender.yRot = yRot;
		dragonRender.zoom = zoom;
		dragonRender.xOffset = xOffset;
		dragonRender.yOffset = yOffset;
		
		children.add(dragonRender);
	}
	
	@Override
	public void init()
	{
		super.init();
		
		this.guiLeft = (this.width - 256) / 2;
		this.guiTop = (this.height - 120) / 2;
		
		conf = new CustomizationConfirmation(this, width / 2 - 100, height / 2 - (150 / 2), 200, 150);
		initDragonRender();
		
		DragonStateHandler localHandler = DragonStateProvider.getCap(getMinecraft().player).orElse(null);
		
		if (!hasInit) {
			level = localHandler.getLevel();
			dragonRender.zoom = (float)(level.size * preset.sizeMul);
			
			if (type == DragonType.NONE) {
				type = localHandler.getType();
			}
			
			currentSelected = CustomizationRegistry.savedCustomizations.current.getOrDefault(type, new HashMap<>()).getOrDefault(level, 0);
			preset = new SkinPreset();
			preset.readNBT(CustomizationRegistry.savedCustomizations.skinPresets.getOrDefault(type, new HashMap<>()).getOrDefault(currentSelected, new SkinPreset()).writeNBT());
			handler.getSkin().skinPreset = preset;
			
			this.handler.setHasWings(true);
			this.handler.setType(type);
			hasInit = true;
			handler.getSkin().updateLayers.addAll(Arrays.stream(EnumSkinLayer.values()).distinct().collect(Collectors.toList()));
			update();
		}
		
		addButton(new HelpButton(type, guiLeft, 10, 16, 16, "ds.help.customization"));
		
		addButton(new Button(width / 2 - 180, guiTop - 30, 120, 20, new TranslationTextComponent("ds.level.newborn"), (btn) -> {
			level = DragonLevel.BABY;
			dragonRender.zoom = level.size;
			handler.getSkin().updateLayers.addAll(Arrays.stream(EnumSkinLayer.values()).distinct().collect(Collectors.toList()));
			update();
		})
		{
			@Override
			public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
			{
				this.active = this.visible = showUi;
				super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
			}
			
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				int j = isHovered || level == DragonLevel.BABY ? 16777215 : 10526880;
				TextRenderUtil.drawCenteredScaledText(stack, x + (width / 2), y + 4, 1.5f, this.getMessage().getString(), j | MathHelper.ceil(this.alpha * 255.0F) << 24);
			}
		});
		addButton(new Button(width / 2 - 60, guiTop - 30, 120, 20, new TranslationTextComponent("ds.level.young"), (btn) -> {
			level = DragonLevel.YOUNG;
			dragonRender.zoom = level.size;
			handler.getSkin().updateLayers.addAll(Arrays.stream(EnumSkinLayer.values()).distinct().collect(Collectors.toList()));
			update();
		})
		{
			@Override
			public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
			{
				this.active = this.visible = showUi;
				super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
			}
			
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				int j = isHovered || level == DragonLevel.YOUNG ? 16777215 : 10526880;
				TextRenderUtil.drawCenteredScaledText(stack, x + (width / 2), y + 4, 1.5f, this.getMessage().getString(), j | MathHelper.ceil(this.alpha * 255.0F) << 24);
			}
		});
		addButton(new Button(width / 2 + 60, guiTop - 30, 120, 20, new TranslationTextComponent("ds.level.adult"), (btn) -> {
			level = DragonLevel.ADULT;
			dragonRender.zoom = level.size;
			handler.getSkin().updateLayers.addAll(Arrays.stream(EnumSkinLayer.values()).distinct().collect(Collectors.toList()));
			update();
		})
		{
			@Override
			public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
			{
				this.active = this.visible = showUi;
				super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
			}
			
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				int j = isHovered || level == DragonLevel.ADULT ? 16777215 : 10526880;
				TextRenderUtil.drawCenteredScaledText(stack, x + (width / 2), y + 4, 1.5f, this.getMessage().getString(), j | MathHelper.ceil(this.alpha * 255.0F) << 24);
			}
		});
		
		int maxWidth = -1;
		
		for (EnumSkinLayer layers : EnumSkinLayer.values()) {
			String name = layers.name().substring(0, 1).toUpperCase(Locale.ROOT) + layers.name().toLowerCase().substring(1).replace("_", " ");
			maxWidth = (int)Math.max(maxWidth, font.width(name) * 1.45F);
		}
		
		int i = 0;
		for (EnumSkinLayer layers : EnumSkinLayer.values()) {
			ArrayList<String> valueList = DragonCustomizationHandler.getKeys(type, layers);
			
			if (layers != EnumSkinLayer.BASE) {
				valueList.add(0, SkinCap.defaultSkinValue);
			}
			
			String[] values = valueList.toArray(new String[0]);
			String curValue = preset.skinAges.get(level).layerSettings.get(layers).selectedSkin;
			DropDownButton btn = new DropDownButton(i < 5 ? width / 2 - 100 - 100 : width / 2 + 83, guiTop + 10 + ((i >= 5 ? (i - 5) * 30 : i * 30)), 100, 15, curValue, values, (s) -> {
				preset.skinAges.get(level).layerSettings.get(layers).selectedSkin = s;
				handler.getSkin().updateLayers.add(layers);
				update();
			})
			{
				@Override
				public DropdownEntry createEntry(int pos, String val)
				{
					return new ColoredDropdownValueEntry(this, pos, val, setter);
				}
				
				@Override
				public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
				{
					this.active = this.visible = showUi;
					super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
					String curValue = preset.skinAges.get(level).layerSettings.get(layers).selectedSkin;
					
					if (curValue != this.current) {
						this.current = curValue;
						updateMessage();
					}
					
					ArrayList<String> valueList = DragonCustomizationHandler.getKeys(type, layers);
					
					if (layers != EnumSkinLayer.BASE) {
						valueList.add(0, SkinCap.defaultSkinValue);
					}
					
					this.values = valueList.toArray(new String[0]);
					
					this.active = !preset.skinAges.get(level).defaultSkin;
				}
			};
			
			addButton(btn);
			
			addButton(new ColorSelectorButton(this, layers, btn.x + btn.getWidth() + 2, btn.y, btn.getHeight(), btn.getHeight(), (s) -> {
				preset.skinAges.get(level).layerSettings.get(layers).hue = s.floatValue();
				handler.getSkin().updateLayers.add(layers);
				update();
			}));
			i++;
		}
		
		addButton(new Button(width / 2 + 30, height / 2 + 75 - 7, 15, 15, StringTextComponent.EMPTY, (btn) -> {
			curAnimation += 1;
			
			if (curAnimation >= animations.length) {
				curAnimation = 0;
			}
		})
		{
			@Override
			public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
			{
				this.active = this.visible = showUi;
				super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
			}
			
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
		
		addButton(new Button(width / 2 - 30 - 15, height / 2 + 75 - 7, 15, 15, StringTextComponent.EMPTY, (btn) -> {
			curAnimation -= 1;
			
			if (curAnimation < 0) {
				curAnimation = animations.length - 1;
			}
		})
		{
			@Override
			public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
			{
				this.active = this.visible = showUi;
				super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
			}
			
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				Minecraft.getInstance().getTextureManager().bind(ClientMagicHUDHandler.widgetTextures);
				
				if (isHovered()) {
					blit(stack, x, y, 22 / 2, 222 / 2, 11, 17, 128, 128);
				} else {
					blit(stack, x, y, 0, 222 / 2, 11, 17, 128, 128);
				}
			}
		});
		
		for (int num = 1; num <= 9; num++) {
			addButton(new CustomizationSlotButton(width / 2 + 195 + 13, guiTop + ((num - 1) * 12) + 5 + 20, num, this));
		}
		
		addButton(new Slider(width / 2 - 100 - 100, height - 25, 100, 20, new TranslationTextComponent("ds.gui.customization.size"), new StringTextComponent("%"), ConfigHandler.SERVER.minSizeVari.get(), ConfigHandler.SERVER.maxSizeVari.get(), Math.round((preset.sizeMul - 1.0) * 100), false, true, (p) -> {}, (p) -> {
			double val = 1.0 + (p.getValueInt() / 100.0);
			if (preset.sizeMul != val) {
				preset.sizeMul = val;
				dragonRender.zoom = (float)(level.size * preset.sizeMul);
			}
		})
		{
			@Override
			public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
			{
				super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
				double val = Math.round((preset.sizeMul - 1.0) * 100);
				
				if (val > 0) {
					dispString = new TranslationTextComponent("ds.gui.customization.size").append("+");
				} else {
					dispString = new TranslationTextComponent("ds.gui.customization.size");
				}
				
				if (getValue() != val) {
					setValue(val);
					updateSlider();
				}
				
				if (!isMouseOver(pMouseX, pMouseY) && isDragging()) {
					mouseReleased(pMouseX, pMouseY, 0);
				}
			}
		});
		
		addButton(new ExtendedCheckbox(width / 2 + 100, height - 15, 100, 10, 10, new TranslationTextComponent("ds.gui.customization.wings"), preset.skinAges.get(level).wings, (p) -> preset.skinAges.get(level).wings = p.selected()));
		addButton(new ExtendedCheckbox(width / 2 + 100, height - 28, 100, 10, 10, new TranslationTextComponent("ds.gui.customization.default_skin"), preset.skinAges.get(level).defaultSkin, (p) -> preset.skinAges.get(level).defaultSkin = p.selected()));
		
		addButton(new ExtendedButton(width / 2 - 75 - 10, height - 25, 75, 20, new TranslationTextComponent("ds.gui.customization.save"), null)
		{
			Widget renderButton;
			boolean toggled;
			
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				super.renderButton(mStack, mouseX, mouseY, partial);
				if (isHovered) {
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.customization.tooltip.done")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
				
				if (toggled && (!visible || !confirmation)) {
					toggled = false;
					Screen screen = Minecraft.getInstance().screen;
					screen.children.removeIf((s) -> s == conf);
					screen.buttons.removeIf((s) -> s == renderButton);
				}
			}
			
			@Override
			public void onPress()
			{
				DragonStateProvider.getCap(minecraft.player).ifPresent(cap -> {
					minecraft.player.level.playSound(minecraft.player, minecraft.player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 1, 0.7f);
					
					if (cap.getType() != type && cap.getType() != DragonType.NONE) {
						if (!ConfigHandler.SERVER.saveAllAbilities.get() || !ConfigHandler.SERVER.saveGrowthStage.get()) {
							confirmation = true;
							return;
						}
					}
					if (!confirmation) {
						confirm();
					}
				});
				
				if (confirmation) {
					if (!toggled) {
						renderButton = new ExtendedButton(0, 0, 0, 0, StringTextComponent.EMPTY, null)
						{
							@Override
							public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
							{
								this.active = this.visible = false;
								
								if (conf != null && confirmation) {
									conf.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
								}
							}
						};
						children.add(0, conf);
						children.add(conf);
						buttons.add(renderButton);
					}
					toggled = !toggled;
				} else {
					children.removeIf((s) -> s == conf);
					buttons.removeIf((s) -> s == renderButton);
				}
			}
		});
		
		addButton(new ExtendedButton(width / 2 + 10, height - 25, 75, 20, new TranslationTextComponent("ds.gui.customization.back"), null)
		{
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				super.renderButton(mStack, mouseX, mouseY, partial);
				
				if (isHovered) {
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.customization.tooltip.back")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}
			
			@Override
			public void onPress()
			{
				Minecraft.getInstance().setScreen(source);
			}
		});
		
		addButton(new ExtendedButton(guiLeft + 256 + 16, 9, 19, 19, StringTextComponent.EMPTY, (btn) -> {
			preset = new SkinPreset();
			handler.getSkin().updateLayers.addAll(Arrays.stream(EnumSkinLayer.values()).distinct().collect(Collectors.toList()));
			update();
		})
		{
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
		
		addButton(new ExtendedButton(guiLeft + 256 + 30 + 16, 9, 19, 19, StringTextComponent.EMPTY, (btn) -> {
			for (EnumSkinLayer layer : EnumSkinLayer.values()) {
				ArrayList<String> keys = DragonCustomizationHandler.getKeys(FakeClientPlayerUtils.getFakePlayer(0, handler), layer);
				
				if (layer != EnumSkinLayer.BASE) {
					keys.add(SkinCap.defaultSkinValue);
				}
				
				if (keys.size() > 0) {
					preset.skinAges.get(level).layerSettings.get(layer).selectedSkin = keys.get(minecraft.player.level.random.nextInt(keys.size()));
					preset.skinAges.get(level).layerSettings.get(layer).hue = 0.25f + (minecraft.player.level.random.nextFloat() * 0.5f);
					preset.skinAges.get(level).layerSettings.get(layer).saturation = 0.25f + (minecraft.player.level.random.nextFloat() * 0.5f);
					preset.skinAges.get(level).layerSettings.get(layer).brightness = 0.25f + (minecraft.player.level.random.nextFloat() * 0.5f);
					preset.skinAges.get(level).layerSettings.get(layer).modifiedColor = true;
				}
				handler.getSkin().updateLayers.add(layer);
			}
			
			update();
		})
		{
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
		
		addButton(new ExtendedButton(width / 2 + 193 + 13, guiTop + 5, 16, 16, StringTextComponent.EMPTY, (p) -> {})
		{
			@Override
			public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
			{
				this.active = this.visible = showUi;
				super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
				
				if (visible) {
					Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/save_icon.png"));
					blit(pMatrixStack, x, y, 0, 0, 16, 16, 16, 16);
					
					if (this.isHovered()) {
						this.renderToolTip(pMatrixStack, pMouseX, pMouseY);
					}
				}
			}
			
			@Override
			public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_)
			{
				GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.customization.save_slot")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
			}
			
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial) {}
		});
		
		addButton(new CopySettingsButton(this, width / 2 + 193 + 13, guiTop - 16, 16, 16, StringTextComponent.EMPTY, (p) -> {}));
		
		addButton(new ExtendedButton(dragonRender.x + dragonRender.width - 17, dragonRender.y + dragonRender.height + 3, 15, 15, new TranslationTextComponent(""), (btn) -> {
			dragonRender.yRot = -3;
			dragonRender.xRot = -5;
			dragonRender.xOffset = 0;
			dragonRender.yOffset = 0;
			dragonRender.zoom = (float)(level.size * preset.sizeMul);
		})
		{
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
			
			@Override
			public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
			{
				this.active = this.visible = showUi;
				super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
			}
		});
		
		addButton(new ExtendedCheckbox(guiLeft + 230, 11, 100, 16, 16, new TranslationTextComponent("ds.gui.customization.show_ui"), showUi, (p) -> showUi = p.selected()));
		
		addButton(new ExtendedButton(guiLeft - 35, 10, 18, 18, StringTextComponent.EMPTY, (p) -> {
			int width = 1024;
			int height = 1024;
			
			RenderSystem.pushMatrix();
			RenderSystem.clear(16640, Minecraft.ON_OSX);
			
			Framebuffer framebuffer = new Framebuffer(width, height, true, false);
			framebuffer.setClearColor(1f, 1f, 1f, 0f);
			framebuffer.bindWrite(true);
			framebuffer.blitToScreen(width, height);
			
			ClientDragonRender.renderEntityInInventory(FakeClientPlayerUtils.getFakeDragon(0, handler), width / 2, height / 2, dragonRender.zoom * 4, dragonRender.xRot, dragonRender.yRot, 0, 0);
			
			NativeImage nativeimage = new NativeImage(width, height, false);
			RenderSystem.bindTexture(framebuffer.getColorTextureId());
			nativeimage.downloadTexture(0, false);
			nativeimage.flipY();
			
			File file1 = new File(Minecraft.getInstance().gameDirectory, "screenshots/dragon-survival");
			file1.mkdir();
			File target = getFile(file1);
			
			Util.ioPool().execute(() -> {
				try {
					nativeimage.writeToFile(target);
				} catch (Exception ignored) {
				} finally {
					nativeimage.close();
				}
			});
			
			framebuffer.unbindWrite();
			framebuffer.destroyBuffers();
			RenderSystem.popMatrix();
			
			Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
		}){
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/screenshot_icon.png"));
				blit(mStack, x, y, 0, 0, width, height, width, height);
				
				if (this.isHovered()) {
					this.renderToolTip(mStack, mouseX, mouseY);
				}
			}
		
		@Override
		public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_)
		{
			GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.customization.screenshot")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
		}
		});
	}
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private static File getFile(File pGameDirectory) {
		String s = DATE_FORMAT.format(new Date());
		int i = 1;
		
		while(true) {
			File file1 = new File(pGameDirectory, s + (i == 1 ? "" : "_" + i) + ".png");
			if (!file1.exists()) {
				return file1;
			}
			
			++i;
		}
	}
	
	private static ResourceLocation backgroundTexture = new ResourceLocation("textures/block/dirt.png");
	
	@Override
	public void renderBackground(MatrixStack pMatrixStack)
	{
		super.renderBackground(pMatrixStack);
		DragonAltarGUI.renderBorders(backgroundTexture, 0, width, 32, height - 32, width, height);
	}
	
	float tick = 0;
	boolean autosave = false;
	
	@Override
	public void render(MatrixStack stack, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		tick += p_230430_4_;
		if(tick >= (60 * 20)){
			autosave = true;
			save();
			autosave = false;
			tick = 0;
		}
		
		if(showUi){
			dragonRender.x = width / 2 - 70;
			dragonRender.y = guiTop;
			dragonRender.width = 140;
			dragonRender.height = 125;
		}else{
			dragonRender.x = 0;
			dragonRender.width = width;
		}
		
		FakeClientPlayerUtils.getFakePlayer(0, handler).animationSupplier = () -> animations[curAnimation];
		
		stack.pushPose();
		stack.translate(0,0,-600);
		this.renderBackground(stack);
		stack.popPose();
		
		TextRenderUtil.drawCenteredScaledText(stack, width / 2, 10, 2f, title.getString(), DyeColor.WHITE.getTextColor());
		
		if(showUi) {
			int i = 0;
			for (EnumSkinLayer layers : EnumSkinLayer.values()) {
				String name = layers.name;
				SkinsScreen.drawNonShadowLineBreak(stack, font, new StringTextComponent(name), (i < 5 ? width / 2 - 100 - 100 : width / 2 + 83) + 50, guiTop + 10 + ((i >= 5 ? (i - 5) * 30 : i * 30)) - 12, DyeColor.WHITE.getTextColor());
				i++;
			}
		}
		
		if(showUi) {
			SkinsScreen.drawNonShadowLineBreak(stack, font, new StringTextComponent(WordUtils.capitalize(animations[curAnimation].replace("_", " "))), width / 2, height / 2 + 72, DyeColor.GRAY.getTextColor());
		}
		
		super.render(stack, p_230430_2_, p_230430_3_, p_230430_4_);
		
		for(int x = 0; x < this.children.size(); ++x) {
			IGuiEventListener ch = children.get(x);
			if(ch instanceof IRenderable){
				((IRenderable)ch).render(stack, p_230430_2_, p_230430_3_, p_230430_4_);
			}
		}
	}
	
	
	public void confirm()
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
				cap.getMovementData().spinLearned = ConfigHandler.SERVER.saveGrowthStage.get() && cap.getMovementData().spinLearned;
				
				NetworkHandler.CHANNEL.sendToServer(new CompleteDataSync(Minecraft.getInstance().player.getId()));
				NetworkHandler.CHANNEL.sendToServer(new SyncAltarCooldown(Minecraft.getInstance().player.getId(), Functions.secondsToTicks(ConfigHandler.SERVER.altarUsageCooldown.get())));
				NetworkHandler.CHANNEL.sendToServer(new SyncSpinStatus(Minecraft.getInstance().player.getId(), cap.getMovementData().spinAttack, cap.getMovementData().spinCooldown, cap.getMovementData().spinLearned));
				ClientEvents.sendClientData(new RequestClientData(cap.getType(), cap.getLevel()));
			}
		});
		
		save();
		
		Minecraft.getInstance().player.closeContainer();
	}
	
	public void save(){
		if(DragonUtils.getDragonType(minecraft.player) == this.type) {
			NetworkHandler.CHANNEL.sendToServer(new SyncPlayerSkinPreset(minecraft.player.getId(), preset));
		}
		
		CustomizationRegistry.savedCustomizations.skinPresets.computeIfAbsent(this.type, (t) -> new HashMap<>());
		CustomizationRegistry.savedCustomizations.skinPresets.get(this.type).put(currentSelected, preset);
		
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			FileWriter writer = new FileWriter(CustomizationRegistry.savedFile);
			gson.toJson(CustomizationRegistry.savedCustomizations, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
		if(!super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)){
			if(dragonRender != null && dragonRender.isMouseOver(pMouseX, pMouseY)){
				return dragonRender.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
			}
		}
		
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}
}
