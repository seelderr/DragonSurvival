package by.jackraidenph.dragonsurvival.common.handlers;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DragonFoodHandler {

	private static ConcurrentHashMap<DragonType, Map<Item, FoodProperties>> DRAGON_FOODS;
	public static CopyOnWriteArrayList<Item> CAVE_D_FOOD;
	public static CopyOnWriteArrayList<Item> FOREST_D_FOOD;
	public static CopyOnWriteArrayList<Item> SEA_D_FOOD;
	public static boolean isDrawingOverlay = false;

	private Minecraft mc;
	private static final ResourceLocation FOOD_ICONS= new ResourceLocation(DragonSurvivalMod.MODID + ":textures/gui/dragon_hud.png");
	private static final Random rand = new Random();


	public DragonFoodHandler() {
		if (FMLLoader.getDist() == Dist.CLIENT)
			mc = Minecraft.getInstance();
	}
	
	@SubscribeEvent
	public static void onConfigLoad(ModConfigEvent.Loading event) {
		if (event.getConfig().getType() == Type.SERVER)
			rebuildFoodMap();
	}
	
	private static void rebuildFoodMap() {
		ConcurrentHashMap<DragonType, ConcurrentHashMap<Item, FoodProperties>> dragonMap = new ConcurrentHashMap<DragonType, ConcurrentHashMap<Item, FoodProperties>>();
		dragonMap.put(DragonType.CAVE, buildDragonFoodMap(DragonType.CAVE));
		dragonMap.put(DragonType.FOREST, buildDragonFoodMap(DragonType.FOREST));
		dragonMap.put(DragonType.SEA, buildDragonFoodMap(DragonType.SEA));
		DRAGON_FOODS = new ConcurrentHashMap<>(dragonMap);
	}
	
	public static CopyOnWriteArrayList<Item> getSafeEdibleFoods(DragonType dragonType) {
		if (dragonType == DragonType.FOREST && FOREST_D_FOOD != null)
			return FOREST_D_FOOD;
		else if (dragonType == DragonType.SEA && SEA_D_FOOD != null)
			return SEA_D_FOOD;
		else if (dragonType == DragonType.CAVE && CAVE_D_FOOD != null)
			return CAVE_D_FOOD;
		
		if(DRAGON_FOODS == null){
			rebuildFoodMap();
		}
		
		CopyOnWriteArrayList<Item> foods = new CopyOnWriteArrayList<>();
		for (Item item : DRAGON_FOODS.get(dragonType).keySet()) {
			boolean safe = true;
			final FoodProperties food = DRAGON_FOODS.get(dragonType).get(item);
			if (food != null) {
				for (Pair<MobEffectInstance, Float> effect : food.getEffects()) {
					MobEffect e = effect.getFirst().getEffect();
					if (!e.isBeneficial() && e != MobEffects.CONFUSION) { // Because we decided to leave confusion on pufferfish
						safe = false;
						break;
					}
				}
				if (safe)
					foods.add(item);
			}
		}
		if (dragonType == DragonType.FOREST && FOREST_D_FOOD == null)
			FOREST_D_FOOD = foods;
		else if (dragonType == DragonType.CAVE && CAVE_D_FOOD == null)
			CAVE_D_FOOD = foods;
		else if (dragonType == DragonType.SEA && SEA_D_FOOD == null)
			SEA_D_FOOD = foods;
		return foods;
	}
	
	private static ConcurrentHashMap<Item, FoodProperties> buildDragonFoodMap(DragonType type) {
		ConcurrentHashMap<Item, FoodProperties> foodMap = new ConcurrentHashMap<Item, FoodProperties>();
		
		if(!ConfigHandler.SERVER.customDragonFoods.get()){
			return foodMap;
		}
		
		String[] configFood;
		switch (type) {
			case CAVE:
				configFood = ConfigHandler.SERVER.caveDragonFoods.get().toArray(new String[0]);
				break;
			case FOREST:
				configFood = ConfigHandler.SERVER.forestDragonFoods.get().toArray(new String[0]);
				break;
			case SEA:
				configFood = ConfigHandler.SERVER.seaDragonFoods.get().toArray(new String[0]);
				break;
			default:
				configFood = new String[0];
				break;
		}
		configFood = Stream.of(configFood).sorted(Comparator.reverseOrder()).toArray(String[]::new);
		for (String entry : configFood) {
			final String[] sEntry = entry.split(":");
			final ResourceLocation rlEntry = new ResourceLocation(sEntry[1], sEntry[2]);
			if (sEntry[0].equalsIgnoreCase("tag")) {
				final Tag<Item> tag = ItemTags.getAllTags().getTag(rlEntry);
				if (tag != null && tag.getValues().size() != 0) {
					for (Item item : tag.getValues()) {
						FoodProperties food = calculateDragonFoodProperties(item, type,
						                                          sEntry.length == 5 ? Integer.parseInt(sEntry[3]) : item.getFoodProperties() != null ? item.getFoodProperties().getNutrition() : 1,
						                                          sEntry.length == 5 ? Integer.parseInt(sEntry[4]) : item.getFoodProperties() != null ? (int) (item.getFoodProperties().getNutrition() * (item.getFoodProperties().getSaturationModifier() * 2.0F)) : 0,
						                                          true);
						if(food != null){
							foodMap.put(item, food);
						}
					}
				}
				else
					DragonSurvivalMod.LOGGER.warn("Null or empty tag '{}:{}' in {} dragon food config.", sEntry[1], sEntry[2], type.toString().toLowerCase());
			} else {
				final Item item = ForgeRegistries.ITEMS.getValue(rlEntry);
				if (item != null && item != Items.AIR) {
					FoodProperties food = calculateDragonFoodProperties(item, type,
					                                          sEntry.length == 5 ? Integer.parseInt(sEntry[3]) : item.getFoodProperties() != null ? item.getFoodProperties().getNutrition() : 1,
					                                          sEntry.length == 5 ? Integer.parseInt(sEntry[4]) : item.getFoodProperties() != null ? (int) (item.getFoodProperties().getNutrition() * (item.getFoodProperties().getSaturationModifier() * 2.0F)) : 0,
					                                          true);
					
					if(food != null) {
						foodMap.put(item, food);
					}
				} else
					DragonSurvivalMod.LOGGER.warn("Unknown item '{}:{}' in {} dragon food config.", sEntry[1], sEntry[2], type.toString().toLowerCase());
			}
		}
		for (Item item : ForgeRegistries.ITEMS.getValues())
			if (!foodMap.containsKey(item) && item.isEdible()){
				FoodProperties food = calculateDragonFoodProperties(item, type, 0, 0, false);
				
				if(food != null) {
					foodMap.put(item, food);
				}
			}
		return new ConcurrentHashMap<>(foodMap);
	}

	@Nullable
	private static FoodProperties calculateDragonFoodProperties(Item item, DragonType type, int nutrition, int saturation, boolean dragonFood) {
		if (!ConfigHandler.SERVER.customDragonFoods.get() || type == DragonType.NONE)
			return item.getFoodProperties();
		FoodProperties.Builder builder = new FoodProperties.Builder();
		if (dragonFood) {
			builder.nutrition(nutrition)
				.saturationMod(((float)saturation / (float)nutrition) / 2.0F);
			if (item.getFoodProperties() != null) {
				FoodProperties humanFood = item.getFoodProperties();
				if (humanFood.isMeat())
					builder.meat();
				if (humanFood.canAlwaysEat())
					builder.alwaysEat();
				if (humanFood.isFastFood())
					builder.fast();
				for (Pair<MobEffectInstance, Float> effect : humanFood.getEffects())
					if (effect.getFirst().getEffect() != MobEffects.HUNGER && effect.getFirst().getEffect() != MobEffects.POISON)
						builder.effect(() -> effect.getFirst(), effect.getSecond());
			}
		} else {
			FoodProperties humanFood = item.getFoodProperties();
			builder.nutrition(humanFood.getNutrition())
				.saturationMod(humanFood.getSaturationModifier());
			if (humanFood.isMeat())
				builder.meat();
			if (humanFood.canAlwaysEat())
				builder.alwaysEat();
			if (humanFood.isFastFood())
				builder.fast();
			for (Pair<MobEffectInstance, Float> effect : humanFood.getEffects())
				if (effect.getFirst().getEffect() != MobEffects.HUNGER)
					builder.effect(() -> effect.getFirst(), effect.getSecond());
			builder.effect(() -> new MobEffectInstance(MobEffects.HUNGER, 20 * 60, 0), 1.0F);
		}
		return builder.build();
	}
	
	@Nullable
	public static FoodProperties getDragonFoodProperties(Item item, DragonType type) {
		if (DRAGON_FOODS == null || !ConfigHandler.SERVER.customDragonFoods.get() || type == DragonType.NONE)
			return item.getFoodProperties();
		if (DRAGON_FOODS.get(type).containsKey(item))
			return DRAGON_FOODS.get(type).get(item);
		return null;
	}
	
	public static boolean isDragonEdible(Item item, DragonType type) {
		if (ConfigHandler.SERVER.customDragonFoods.get() && type != DragonType.NONE)
			return DRAGON_FOODS != null && DRAGON_FOODS.containsKey(type) && item != null && DRAGON_FOODS.get(type).containsKey(item);
		return item.getFoodProperties() != null;
	}
	
	public static void dragonEat(FoodData foodStats, Item item, ItemStack itemStack, DragonType type) {
		if (isDragonEdible(item, type)) {
			FoodProperties food = getDragonFoodProperties(item, type);
			foodStats.eat(food.getNutrition(), food.getSaturationModifier());
		}
	}
	
	public static int getUseDuration(ItemStack item, DragonType type) {
		if (isDragonEdible(item.getItem(), type)) {
			return item.getItem().getFoodProperties() != null && item.getItem().getFoodProperties().isFastFood() ? 16 : 32;
		} else
			return item.getUseDuration(); // VERIFY THIS
	}
	
	@SubscribeEvent
	public void onItemUseStart(LivingEntityUseItemEvent.Start event) {
		DragonStateProvider.getCap(event.getEntityLiving()).ifPresent(dragonStateHandler -> {
			if (dragonStateHandler.isDragon())
				event.setDuration(getUseDuration(event.getItem(), dragonStateHandler.getType()));
		});
	}
	
	@SubscribeEvent
	public void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
		DragonStateProvider.getCap(event.getEntityLiving()).ifPresent(dragonStateHandler -> {
			if (dragonStateHandler.isDragon()) {
				if (!event.getPlayer().level.isClientSide) {
					ServerPlayer player = (ServerPlayer) event.getPlayer();
					ServerLevel level = player.getLevel();
					InteractionHand hand = event.getHand();
					ItemStack stack = player.getItemInHand(event.getHand());
					if (isDragonEdible(stack.getItem(), dragonStateHandler.getType())) {
						int i = stack.getCount();
						int j = stack.getDamageValue();
						InteractionResultHolder<ItemStack> actionresult = stack.use(level, player, hand);
						ItemStack itemstack = actionresult.getObject();
						if (itemstack == stack && itemstack.getCount() == i && getUseDuration(itemstack, dragonStateHandler.getType()) <= 0 && itemstack.getDamageValue() == j) {
							{
								event.setCancellationResult(actionresult.getResult());
							}
						} else if (actionresult.getResult() == InteractionResult.FAIL && getUseDuration(itemstack, dragonStateHandler.getType()) > 0 && !player.isUsingItem()) {
							{
								event.setCancellationResult(actionresult.getResult());
								event.setCanceled(true);
							}
						} else {
							player.setItemInHand(hand, itemstack);
							if (player.isCreative()) {
								itemstack.setCount(i);
								if (itemstack.isDamageableItem() && itemstack.getDamageValue() != j) {
									itemstack.setDamageValue(j);
								}
							}

							if (itemstack.isEmpty()) {
								player.setItemInHand(hand, ItemStack.EMPTY);
							}

							event.setCancellationResult(actionresult.getResult());
							event.setCanceled(true);
						}
					}
				}
			}
		});
	}
	
	public static int rightHeight = 0;
	
	public static void onRenderFoodBar(ForgeIngameGui gui, PoseStack mStack, float partialTicks, int width, int height) {
		LocalPlayer player = Minecraft.getInstance().player;
		
		if(Minecraft.getInstance().options.hideGui || !gui.shouldDrawSurvivalElements()) return;
		if (!ConfigHandler.SERVER.customDragonFoods.get() || !DragonUtils.isDragon(player)) {
			ForgeIngameGui.FOOD_LEVEL_ELEMENT.render(gui, mStack, partialTicks, width, height);
			return;
		}
		
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if (dragonStateHandler.isDragon()) {
				
				rand.setSeed(player.tickCount * 312871L);
				
				RenderSystem.enableBlend();
				RenderSystem.setShaderTexture(0,FOOD_ICONS);
				
				if(Minecraft.getInstance().gui instanceof ForgeIngameGui) {
					rightHeight = ((ForgeIngameGui)Minecraft.getInstance().gui).right_height;
					((ForgeIngameGui)Minecraft.getInstance().gui).right_height += 10;
				}
				
				final int left = width / 2 + 91;
                final int top = height - rightHeight;
				rightHeight += 10;
                final FoodData food = player.getFoodData();

                final int type = dragonStateHandler.getType() == DragonType.FOREST ? 0 : dragonStateHandler.getType() == DragonType.CAVE ? 9 : 18;
                
                final boolean hunger = player.hasEffect(MobEffects.HUNGER);
                
                for (int i = 0; i < 10; ++i) {
                	int idx = i * 2 + 1;
                	int y = top;
                	
                	if (food.getSaturationLevel() <= 0.0F && player.tickCount % (food.getFoodLevel() * 3 + 1) == 0)
                		y = top + (rand.nextInt(3) - 1);

					gui.blit(mStack, left - i * 8 - 9, y, (hunger ? 117 : 0), type, 9, 9);
                	
                	if (idx < food.getFoodLevel())
                		gui.blit(mStack, left - i * 8 - 9, y, (hunger ? 72 : 36), type, 9, 9);
                	else if (idx == food.getFoodLevel())
                		gui.blit(mStack, left - i * 8 - 9, y, (hunger ? 81 : 45), type, 9, 9);
                }
                
        		RenderSystem.setShaderTexture(0,Gui.GUI_ICONS_LOCATION);
        		RenderSystem.disableBlend();
			} else
				isDrawingOverlay = false;
		});
	}
}
