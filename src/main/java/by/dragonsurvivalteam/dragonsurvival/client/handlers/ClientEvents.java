package by.dragonsurvivalteam.dragonsurvival.client.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.TabButton;
import by.dragonsurvivalteam.dragonsurvival.client.render.CaveLavaFluidRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.DragonEditorRegistry;
import by.dragonsurvivalteam.dragonsurvival.client.skinPartSystem.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.BolasEntity;
import by.dragonsurvivalteam.dragonsurvival.common.items.DSItems;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives.ContrastShowerAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives.LightInDarknessAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives.WaterAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawRender;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonAltar;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonInventory;
import by.dragonsurvivalteam.dragonsurvival.network.dragon_editor.SyncPlayerSkinPreset;
import by.dragonsurvivalteam.dragonsurvival.network.entity.player.SyncDragonSkinSettings;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.FluidBlockRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber( Dist.CLIENT )
public class ClientEvents{

	public static final ResourceLocation DRAGON_HUD = new ResourceLocation(DragonSurvivalMod.MODID + ":textures/gui/dragon_hud.png");
	/**
	 * Durations of jumps
	 */
	public static ConcurrentHashMap<Integer, Integer> dragonsJumpingTicks = new ConcurrentHashMap<>(20);
	public static double mouseX = -1;
	public static double mouseY = -1;
	private static ItemStack BOLAS;
	private static boolean wasCaveDragon = false;
	private static FluidBlockRenderer prevFluidRenderer;

	@SubscribeEvent
	public static void decreaseJumpDuration(TickEvent.PlayerTickEvent playerTickEvent){
		if(playerTickEvent.phase == TickEvent.Phase.END){
			PlayerEntity playerEntity = playerTickEvent.player;
			dragonsJumpingTicks.computeIfPresent(playerEntity.getId(), (playerEntity1, integer) -> integer > 0 ? integer - 1 : integer);
		}
	}

	@OnlyIn( Dist.CLIENT )
	public static void sendClientData(RequestClientData message){
		PlayerEntity player = Minecraft.getInstance().player;

		if(player != null && player.level != null){
			NetworkHandler.CHANNEL.sendToServer(new SyncDragonClawRender(player.getId(), ConfigHandler.CLIENT.renderDragonClaws.get()));
			NetworkHandler.CHANNEL.sendToServer(new SyncDragonSkinSettings(player.getId(), ConfigHandler.CLIENT.renderNewbornSkin.get(), ConfigHandler.CLIENT.renderYoungSkin.get(), ConfigHandler.CLIENT.renderAdultSkin.get()));

			DragonStateProvider.getCap(player).ifPresent(cap -> {
				cap.getMagic().getAbilities();

				if(DragonEditorRegistry.savedCustomizations != null){
					int currentSelected = DragonEditorRegistry.savedCustomizations.current.getOrDefault(message.type, new HashMap<>()).getOrDefault(message.level, 0);
					SkinPreset preset = DragonEditorRegistry.savedCustomizations.skinPresets.getOrDefault(message.type, new HashMap<>()).getOrDefault(currentSelected, new SkinPreset());
					NetworkHandler.CHANNEL.sendToServer(new SyncPlayerSkinPreset(player.getId(), preset));
				}
			});
		}
	}

	@SubscribeEvent( priority = EventPriority.LOWEST )
	public static void syncClientData(PlayerEvent.PlayerLoggedInEvent loggedInEvent){
		PlayerEntity player = loggedInEvent.getPlayer();
		if(!player.level.isClientSide){
			DragonStateProvider.getCap(player).ifPresent(cap -> {
				cap.hasUsedAltar = cap.hasUsedAltar || cap.isDragon();

				if(!cap.hasUsedAltar && ConfigHandler.COMMON.startWithDragonChoice.get()){
					NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new OpenDragonAltar());
					cap.hasUsedAltar = true;
				}
			});
		}
	}

	@SubscribeEvent
	public static void onOpenScreen(GuiOpenEvent openEvent){
		ClientPlayerEntity player = Minecraft.getInstance().player;

		if(!ConfigHandler.CLIENT.dragonInventory.get()){
			return;
		}
		if(Minecraft.getInstance().screen != null){
			return;
		}
		if(player == null || player.isCreative() || !DragonUtils.isDragon(player)){
			return;
		}

		if(openEvent.getGui() instanceof InventoryScreen){
			openEvent.setCanceled(true);
			NetworkHandler.CHANNEL.sendToServer(new OpenDragonInventory());
		}
	}

	@SubscribeEvent
	public static void addCraftingButton(GuiScreenEvent.InitGuiEvent.Post initGuiEvent){
		Screen sc = initGuiEvent.getGui();

		if(!DragonUtils.isDragon(Minecraft.getInstance().player)){
			return;
		}

		if(sc instanceof InventoryScreen){
			InventoryScreen screen = (InventoryScreen)sc;

			if(ConfigHandler.CLIENT.dragonTabs.get()){
				initGuiEvent.addWidget(new TabButton(screen.getGuiLeft(), screen.getGuiTop() - 28, 0, screen){
					@Override
					public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_){
						super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
						this.x = screen.getGuiLeft();
					}
				});

				initGuiEvent.addWidget(new TabButton(screen.getGuiLeft() + 28, screen.getGuiTop() - 26, 1, screen){
					@Override
					public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_){
						super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
						this.x = screen.getGuiLeft() + 28;
					}
				});

				initGuiEvent.addWidget(new TabButton(screen.getGuiLeft() + 57, screen.getGuiTop() - 26, 2, screen){
					@Override
					public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_){
						super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
						this.x = screen.getGuiLeft() + 57;
					}
				});

				initGuiEvent.addWidget(new TabButton(screen.getGuiLeft() + 86, screen.getGuiTop() - 26, 3, screen){
					@Override
					public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_){
						super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
						this.x = screen.getGuiLeft() + 86;
					}
				});
			}

			if(ConfigHandler.CLIENT.inventoryToggle.get()){
				initGuiEvent.addWidget(new ImageButton(screen.getGuiLeft() + 128, screen.height / 2 - 22, 20, 18, 20, 0, 19, DragonScreen.INVENTORY_TOGGLE_BUTTON, p_onPress_1_ -> {
					NetworkHandler.CHANNEL.sendToServer(new OpenDragonInventory());
				}){
					@Override
					public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_){
						super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
						this.x = screen.getGuiLeft() + 128;

						if(isHovered()){
							ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(new TranslationTextComponent("ds.gui.toggle_inventory.dragon")));
							Minecraft.getInstance().screen.renderComponentTooltip(p_230431_1_, description, p_230431_2_, p_230431_3_);
						}
					}
				});
			}
		}

		if(sc instanceof CreativeScreen){
			CreativeScreen screen = (CreativeScreen)sc;

			if(ConfigHandler.CLIENT.inventoryToggle.get()){
				initGuiEvent.addWidget(new ImageButton(screen.getGuiLeft() + 128 + 20, screen.height / 2 - 50, 20, 18, 20, 0, 19, DragonScreen.INVENTORY_TOGGLE_BUTTON, p_onPress_1_ -> {
					NetworkHandler.CHANNEL.sendToServer(new OpenDragonInventory());
				}){
					@Override
					public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
						this.active = this.visible = screen.getSelectedTab() == ItemGroup.TAB_INVENTORY.getId();
						super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
					}

					@Override
					public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_){
						super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
						if(isHovered()){
							ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(new TranslationTextComponent("ds.gui.toggle_inventory.dragon")));
							Minecraft.getInstance().screen.renderComponentTooltip(p_230431_1_, description, p_230431_2_, p_230431_3_);
						}
					}
				});
			}
		}
	}

	@SubscribeEvent
	@OnlyIn( Dist.CLIENT )
	public static void removeFireOverlay(RenderBlockOverlayEvent event){
		ClientPlayerEntity player = Minecraft.getInstance().player;
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			if(cap.isDragon() && cap.getType() == DragonType.CAVE && event.getOverlayType() == OverlayType.FIRE){
				event.setCanceled(true);
			}
		});
	}

	@SubscribeEvent
	public static void renderTrap(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> postEvent){
		LivingEntity entity = postEvent.getEntity();
		if(!(entity instanceof PlayerEntity) && entity.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)){
			AttributeModifier bolasTrap = new AttributeModifier(BolasEntity.DISABLE_MOVEMENT, "Bolas trap", -entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue(), AttributeModifier.Operation.ADDITION);
			if(entity.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(bolasTrap)){
				int light = postEvent.getLight();
				int overlayCoords = LivingRenderer.getOverlayCoords(entity, 0);
				IRenderTypeBuffer buffers = postEvent.getBuffers();
				MatrixStack matrixStack = postEvent.getMatrixStack();
				renderBolas(light, overlayCoords, buffers, matrixStack);
			}
		}
	}

	public static void renderBolas(int light, int overlayCoords, IRenderTypeBuffer buffers, MatrixStack matrixStack){
		matrixStack.pushPose();
		matrixStack.scale(3, 3, 3);
		matrixStack.translate(0, 0.5, 0);
		if(BOLAS == null){
			BOLAS = new ItemStack(DSItems.huntingNet);
		}
		Minecraft.getInstance().getItemRenderer().renderStatic(BOLAS, ItemCameraTransforms.TransformType.NONE, light, overlayCoords, matrixStack, buffers);
		matrixStack.popPose();
	}

	@SubscribeEvent
	public static void unloadWorld(WorldEvent.Unload worldEvent){
		ClientDragonRender.playerDragonHashMap.clear();
	}

	public static String getMaterial(String texture, ItemStack clawItem){
		TieredItem item = (TieredItem)clawItem.getItem();
		IItemTier tier = item.getTier();
		if(tier == ItemTier.NETHERITE){
			texture += "netherite_";
		}else if(tier == ItemTier.DIAMOND){
			texture += "diamond_";
		}else if(tier == ItemTier.IRON){
			texture += "iron_";
		}else if(tier == ItemTier.GOLD){
			texture += "gold_";
		}else if(tier == ItemTier.STONE){
			texture += "stone_";
		}else if(tier == ItemTier.WOOD){
			texture += "wooden_";
		}else{
			texture += "moded_";
		}
		return texture;
	}

	@SubscribeEvent
	@OnlyIn( Dist.CLIENT )
	public static void onRenderWorldLastEvent(RenderWorldLastEvent event){
		Minecraft minecraft = Minecraft.getInstance();
		ClientPlayerEntity player = minecraft.player;
		DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
			if(playerStateHandler.getType() == DragonType.CAVE && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.caveLavaSwimming.get()){
				if(!wasCaveDragon){
					if(player.hasEffect(DragonEffects.LAVA_VISION)){
						RenderType lavaType = RenderType.translucent();
						RenderTypeLookup.setRenderLayer(Fluids.LAVA, lavaType);
						RenderTypeLookup.setRenderLayer(Fluids.FLOWING_LAVA, lavaType);
						prevFluidRenderer = minecraft.getBlockRenderer().liquidBlockRenderer;
						minecraft.getBlockRenderer().liquidBlockRenderer = new CaveLavaFluidRenderer();
						minecraft.levelRenderer.allChanged();
					}
				}else{
					if(!player.hasEffect(DragonEffects.LAVA_VISION)){
						if(prevFluidRenderer != null){
							RenderType lavaType = RenderType.solid();
							RenderTypeLookup.setRenderLayer(Fluids.LAVA, lavaType);
							RenderTypeLookup.setRenderLayer(Fluids.FLOWING_LAVA, lavaType);
							minecraft.getBlockRenderer().liquidBlockRenderer = prevFluidRenderer;
						}
						minecraft.levelRenderer.allChanged();
					}
				}
			}else{
				if(wasCaveDragon){
					if(prevFluidRenderer != null){
						RenderType lavaType = RenderType.solid();
						RenderTypeLookup.setRenderLayer(Fluids.LAVA, lavaType);
						RenderTypeLookup.setRenderLayer(Fluids.FLOWING_LAVA, lavaType);
						minecraft.getBlockRenderer().liquidBlockRenderer = prevFluidRenderer;
					}
					minecraft.levelRenderer.allChanged();
				}
			}
			wasCaveDragon = playerStateHandler.getType() == DragonType.CAVE && player.hasEffect(DragonEffects.LAVA_VISION);
		});
	}

	@SubscribeEvent
	@OnlyIn( Dist.CLIENT )
	public static void onRenderOverlayPreTick(RenderGameOverlayEvent.Pre event){
		Minecraft mc = Minecraft.getInstance();
		ClientPlayerEntity player = mc.player;
		DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
			if(event.getType() == RenderGameOverlayEvent.ElementType.AIR){
				if(playerStateHandler.getType() == DragonType.SEA && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.seaSwimmingBonuses.get()){
					event.setCanceled(true);
				}
				if(playerStateHandler.getDebuffData().timeWithoutWater > 0 && playerStateHandler.getType() == DragonType.SEA && ConfigHandler.SERVER.penalties.get() && ConfigHandler.SERVER.seaTicksWithoutWater.get() != 0){
					RenderSystem.enableBlend();
					mc.getTextureManager().bind(DRAGON_HUD);

					final int right_height = ForgeIngameGui.right_height;
					ForgeIngameGui.right_height += 10;

					int maxTimeWithoutWater = ConfigHandler.SERVER.seaTicksWithoutWater.get();
					DragonAbility waterAbility = playerStateHandler.getMagic().getAbility(DragonAbilities.WATER);

					if(waterAbility != null){
						maxTimeWithoutWater += Functions.secondsToTicks(((WaterAbility)waterAbility).getDuration());
					}

					double timeWithoutWater = maxTimeWithoutWater - playerStateHandler.getDebuffData().timeWithoutWater;
					boolean flag = false;
					if(timeWithoutWater < 0){
						flag = true;
						timeWithoutWater = Math.abs(timeWithoutWater);
					}

					final int left = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 91;
					final int top = Minecraft.getInstance().getWindow().getGuiScaledHeight() - right_height;
					final int full = flag ? MathHelper.floor(timeWithoutWater * 10.0D / maxTimeWithoutWater) : MathHelper.ceil((timeWithoutWater - 2) * 10.0D / maxTimeWithoutWater);
					final int partial = MathHelper.ceil(timeWithoutWater * 10.0D / maxTimeWithoutWater) - full;

					for(int i = 0; i < full + partial; ++i){
						Minecraft.getInstance().gui.blit(event.getMatrixStack(), left - i * 8 - 9, top, (flag ? 18 : i < full ? 0 : 9), 36, 9, 9);
					}


					mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
					RenderSystem.disableBlend();
				}
				if(playerStateHandler.getLavaAirSupply() < ConfigHandler.SERVER.caveLavaSwimmingTicks.get() && playerStateHandler.getType() == DragonType.CAVE && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.caveLavaSwimmingTicks.get() != 0 && ConfigHandler.SERVER.caveLavaSwimming.get()){
					RenderSystem.enableBlend();
					mc.getTextureManager().bind(DRAGON_HUD);

					final int right_height = ForgeIngameGui.right_height;
					ForgeIngameGui.right_height += 10;

					final int left = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 91;
					final int top = Minecraft.getInstance().getWindow().getGuiScaledHeight() - right_height;
					final int full = MathHelper.ceil((double)(playerStateHandler.getLavaAirSupply() - 2) * 10.0D / ConfigHandler.SERVER.caveLavaSwimmingTicks.get());
					final int partial = MathHelper.ceil((double)playerStateHandler.getLavaAirSupply() * 10.0D / ConfigHandler.SERVER.caveLavaSwimmingTicks.get()) - full;

					for(int i = 0; i < full + partial; ++i){
						Minecraft.getInstance().gui.blit(event.getMatrixStack(), left - i * 8 - 9, top, (i < full ? 0 : 9), 27, 9, 9);
					}

					mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
					RenderSystem.disableBlend();
				}
				if(playerStateHandler.getDebuffData().timeInDarkness > 0 && playerStateHandler.getType() == DragonType.FOREST && ConfigHandler.SERVER.penalties.get() && ConfigHandler.SERVER.forestStressTicks.get() != 0 && !player.hasEffect(DragonEffects.STRESS)){
					RenderSystem.enableBlend();
					mc.getTextureManager().bind(DRAGON_HUD);

					final int right_height = ForgeIngameGui.right_height;
					ForgeIngameGui.right_height += 10;

					int maxTimeInDarkness = ConfigHandler.SERVER.forestStressTicks.get();
					DragonAbility lightInDarkness = playerStateHandler.getMagic().getAbility(DragonAbilities.LIGHT_IN_DARKNESS);

					if(lightInDarkness != null){
						maxTimeInDarkness += Functions.secondsToTicks(((LightInDarknessAbility)lightInDarkness).getDuration());
					}

					final int timeInDarkness = maxTimeInDarkness - Math.min(playerStateHandler.getDebuffData().timeInDarkness, maxTimeInDarkness);

					final int left = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 91;
					final int top = Minecraft.getInstance().getWindow().getGuiScaledHeight() - right_height;
					final int full = MathHelper.ceil((double)(timeInDarkness - 2) * 10.0D / maxTimeInDarkness);
					final int partial = MathHelper.ceil((double)timeInDarkness * 10.0D / maxTimeInDarkness) - full;

					for(int i = 0; i < full + partial; ++i){
						Minecraft.getInstance().gui.blit(event.getMatrixStack(), left - i * 8 - 9, top, (i < full ? 0 : 9), 45, 9, 9);
					}

					mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
					RenderSystem.disableBlend();
				}

				if(playerStateHandler.getDebuffData().timeInRain > 0 && playerStateHandler.getType() == DragonType.CAVE && ConfigHandler.SERVER.penalties.get() && ConfigHandler.SERVER.caveRainDamage.get() != 0.0){
					RenderSystem.enableBlend();
					mc.getTextureManager().bind(DRAGON_HUD);

					final int right_height = ForgeIngameGui.right_height;
					ForgeIngameGui.right_height += 10;

					DragonAbility contrastShower = playerStateHandler.getMagic().getAbility(DragonAbilities.CONTRAST_SHOWER);
					int maxRainTime = 0;

					if(contrastShower != null){
						maxRainTime += Functions.secondsToTicks(((ContrastShowerAbility)contrastShower).getDuration());
					}


					final int timeInRain = maxRainTime - Math.min(playerStateHandler.getDebuffData().timeInRain, maxRainTime);

					final int left = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 + 91;
					final int top = Minecraft.getInstance().getWindow().getGuiScaledHeight() - right_height;
					final int full = MathHelper.ceil((double)(timeInRain - 2) * 10.0D / maxRainTime);
					final int partial = MathHelper.ceil((double)timeInRain * 10.0D / maxRainTime) - full;

					for(int i = 0; i < full + partial; ++i){
						Minecraft.getInstance().gui.blit(event.getMatrixStack(), left - i * 8 - 9, top, (i < full ? 0 : 9), 54, 9, 9);
					}

					mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
					RenderSystem.disableBlend();
				}
			}
		});
	}
}