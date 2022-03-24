package by.dragonsurvivalteam.dragonsurvival.common.handlers;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
=======
import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
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
import net.minecraftforge.api.distmarker.OnlyIn;
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

@Mod.EventBusSubscriber( modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class DragonFoodHandler{

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
	private static ConcurrentHashMap<DragonType, Map<Item, FoodProperties>> DRAGON_FOODS;
	public static CopyOnWriteArrayList<Item> CAVE_D_FOOD;
	public static CopyOnWriteArrayList<Item> FOREST_D_FOOD;
	public static CopyOnWriteArrayList<Item> SEA_D_FOOD;
	public static boolean isDrawingOverlay = false;

	private Minecraft mc;
	private static final ResourceLocation FOOD_ICONS= new ResourceLocation(DragonSurvivalMod.MODID + ":textures/gui/dragon_hud.png");
	private static final Random rand = new Random();

=======
	private final ResourceLocation FOOD_ICONS;
	private final Random rand;
	public static CopyOnWriteArrayList<Item> CAVE_D_FOOD;
	public static CopyOnWriteArrayList<Item> FOREST_D_FOOD;
	public static CopyOnWriteArrayList<Item> SEA_D_FOOD;
	public static boolean isDrawingOverlay;
	public static int rightHeight = 0;
	private static ConcurrentHashMap<DragonType, Map<Item, Food>> DRAGON_FOODS;
	private Minecraft mc;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java

	public DragonFoodHandler(){
		if(FMLLoader.getDist() == Dist.CLIENT){
			mc = Minecraft.getInstance();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
=======
		}
		rand = new Random();
		FOOD_ICONS = new ResourceLocation(DragonSurvivalMod.MODID + ":textures/gui/dragon_hud.png");
		isDrawingOverlay = false;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
	}

	@SubscribeEvent
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
	public static void onConfigLoad(ModConfigEvent.Loading event) {
		if (event.getConfig().getType() == Type.SERVER)
=======
	public static void onConfigLoad(ModConfig.Loading event){
		if(event.getConfig().getType() == Type.SERVER){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
			rebuildFoodMap();
		}
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
	
	private static void rebuildFoodMap() {
		ConcurrentHashMap<DragonType, ConcurrentHashMap<Item, FoodProperties>> dragonMap = new ConcurrentHashMap<DragonType, ConcurrentHashMap<Item, FoodProperties>>();
=======

	private static void rebuildFoodMap(){
		ConcurrentHashMap<DragonType, ConcurrentHashMap<Item, Food>> dragonMap = new ConcurrentHashMap<DragonType, ConcurrentHashMap<Item, Food>>();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
		dragonMap.put(DragonType.CAVE, buildDragonFoodMap(DragonType.CAVE));
		dragonMap.put(DragonType.FOREST, buildDragonFoodMap(DragonType.FOREST));
		dragonMap.put(DragonType.SEA, buildDragonFoodMap(DragonType.SEA));
		DRAGON_FOODS = new ConcurrentHashMap<>(dragonMap);
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
	
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
		
=======

	private static ConcurrentHashMap<Item, Food> buildDragonFoodMap(DragonType type){
		ConcurrentHashMap<Item, Food> foodMap = new ConcurrentHashMap<Item, Food>();

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
		if(!ConfigHandler.SERVER.customDragonFoods.get()){
			return foodMap;
		}

		String[] configFood;
		switch(type){
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
		for(String entry : configFood){
			final String[] sEntry = entry.split(":");
			final ResourceLocation rlEntry = new ResourceLocation(sEntry[1], sEntry[2]);
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
			if (sEntry[0].equalsIgnoreCase("tag")) {
				final Tag<Item> tag = ItemTags.getAllTags().getTag(rlEntry);
				if (tag != null && tag.getValues().size() != 0) {
					for (Item item : tag.getValues()) {
						FoodProperties food = calculateDragonFoodProperties(item, type,
						                                          sEntry.length == 5 ? Integer.parseInt(sEntry[3]) : item.getFoodProperties() != null ? item.getFoodProperties().getNutrition() : 1,
						                                          sEntry.length == 5 ? Integer.parseInt(sEntry[4]) : item.getFoodProperties() != null ? (int) (item.getFoodProperties().getNutrition() * (item.getFoodProperties().getSaturationModifier() * 2.0F)) : 0,
						                                          true);
=======
			if(sEntry[0].equalsIgnoreCase("tag")){
				final ITag<Item> tag = ItemTags.getAllTags().getTag(rlEntry);
				if(tag != null && tag.getValues().size() != 0){
					for(Item item : tag.getValues()){
						Food food = calculateDragonFoodProperties(item, type, sEntry.length == 5 ? Integer.parseInt(sEntry[3]) : item.getFoodProperties() != null ? item.getFoodProperties().getNutrition() : 1, sEntry.length == 5 ? Integer.parseInt(sEntry[4]) : item.getFoodProperties() != null ? (int)(item.getFoodProperties().getNutrition() * (item.getFoodProperties().getSaturationModifier() * 2.0F)) : 0, true);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
						if(food != null){
							foodMap.put(item, food);
						}
					}
				}else{
					DragonSurvivalMod.LOGGER.warn("Null or empty tag '{}:{}' in {} dragon food config.", sEntry[1], sEntry[2], type.toString().toLowerCase());
				}
			}else{
				final Item item = ForgeRegistries.ITEMS.getValue(rlEntry);
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
				if (item != null && item != Items.AIR) {
					FoodProperties food = calculateDragonFoodProperties(item, type,
					                                          sEntry.length == 5 ? Integer.parseInt(sEntry[3]) : item.getFoodProperties() != null ? item.getFoodProperties().getNutrition() : 1,
					                                          sEntry.length == 5 ? Integer.parseInt(sEntry[4]) : item.getFoodProperties() != null ? (int) (item.getFoodProperties().getNutrition() * (item.getFoodProperties().getSaturationModifier() * 2.0F)) : 0,
					                                          true);
					
					if(food != null) {
=======
				if(item != null && item != Items.AIR){
					Food food = calculateDragonFoodProperties(item, type, sEntry.length == 5 ? Integer.parseInt(sEntry[3]) : item.getFoodProperties() != null ? item.getFoodProperties().getNutrition() : 1, sEntry.length == 5 ? Integer.parseInt(sEntry[4]) : item.getFoodProperties() != null ? (int)(item.getFoodProperties().getNutrition() * (item.getFoodProperties().getSaturationModifier() * 2.0F)) : 0, true);

					if(food != null){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
						foodMap.put(item, food);
					}
				}else{
					DragonSurvivalMod.LOGGER.warn("Unknown item '{}:{}' in {} dragon food config.", sEntry[1], sEntry[2], type.toString().toLowerCase());
				}
			}
		}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
		for (Item item : ForgeRegistries.ITEMS.getValues())
			if (!foodMap.containsKey(item) && item.isEdible()){
				FoodProperties food = calculateDragonFoodProperties(item, type, 0, 0, false);
				
				if(food != null) {
=======
		for(Item item : ForgeRegistries.ITEMS.getValues()){
			if(!foodMap.containsKey(item) && item.isEdible()){
				Food food = calculateDragonFoodProperties(item, type, 0, 0, false);

				if(food != null){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
					foodMap.put(item, food);
				}
			}
		}
		return new ConcurrentHashMap<>(foodMap);
	}

	@Nullable
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
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
=======
	private static Food calculateDragonFoodProperties(Item item, DragonType type, int nutrition, int saturation, boolean dragonFood){
		if(!ConfigHandler.SERVER.customDragonFoods.get() || type == DragonType.NONE){
			return item.getFoodProperties();
		}
		Food.Builder builder = new Food.Builder();
		if(dragonFood){
			builder.nutrition(nutrition).saturationMod(((float)saturation / (float)nutrition) / 2.0F);
			if(item.getFoodProperties() != null){
				Food humanFood = item.getFoodProperties();
				if(humanFood.isMeat()){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
					builder.meat();
				}
				if(humanFood.canAlwaysEat()){
					builder.alwaysEat();
				}
				if(humanFood.isFastFood()){
					builder.fast();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
				for (Pair<MobEffectInstance, Float> effect : humanFood.getEffects())
					if (effect.getFirst().getEffect() != MobEffects.HUNGER && effect.getFirst().getEffect() != MobEffects.POISON)
=======
				}
				for(Pair<EffectInstance, Float> effect : humanFood.getEffects()){
					if(effect.getFirst().getEffect() != Effects.HUNGER && effect.getFirst().getEffect() != Effects.POISON){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
						builder.effect(() -> effect.getFirst(), effect.getSecond());
					}
				}
			}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
		} else {
			FoodProperties humanFood = item.getFoodProperties();
			builder.nutrition(humanFood.getNutrition())
				.saturationMod(humanFood.getSaturationModifier());
			if (humanFood.isMeat())
=======
		}else{
			Food humanFood = item.getFoodProperties();
			builder.nutrition(humanFood.getNutrition()).saturationMod(humanFood.getSaturationModifier());
			if(humanFood.isMeat()){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
				builder.meat();
			}
			if(humanFood.canAlwaysEat()){
				builder.alwaysEat();
			}
			if(humanFood.isFastFood()){
				builder.fast();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
			for (Pair<MobEffectInstance, Float> effect : humanFood.getEffects())
				if (effect.getFirst().getEffect() != MobEffects.HUNGER)
					builder.effect(() -> effect.getFirst(), effect.getSecond());
			builder.effect(() -> new MobEffectInstance(MobEffects.HUNGER, 20 * 60, 0), 1.0F);
=======
			}
			for(Pair<EffectInstance, Float> effect : humanFood.getEffects()){
				if(effect.getFirst().getEffect() != Effects.HUNGER){
					builder.effect(() -> effect.getFirst(), effect.getSecond());
				}
			}
			builder.effect(() -> new EffectInstance(Effects.HUNGER, 20 * 60, 0), 1.0F);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
		}
		return builder.build();
	}

	public static CopyOnWriteArrayList<Item> getSafeEdibleFoods(DragonType dragonType){
		if(dragonType == DragonType.FOREST && FOREST_D_FOOD != null){
			return FOREST_D_FOOD;
		}else if(dragonType == DragonType.SEA && SEA_D_FOOD != null){
			return SEA_D_FOOD;
		}else if(dragonType == DragonType.CAVE && CAVE_D_FOOD != null){
			return CAVE_D_FOOD;
		}

		if(DRAGON_FOODS == null){
			rebuildFoodMap();
		}

		CopyOnWriteArrayList<Item> foods = new CopyOnWriteArrayList<>();
		for(Item item : DRAGON_FOODS.get(dragonType).keySet()){
			boolean safe = true;
			final Food food = DRAGON_FOODS.get(dragonType).get(item);
			if(food != null){
				for(Pair<EffectInstance, Float> effect : food.getEffects()){
					Effect e = effect.getFirst().getEffect();
					if(!e.isBeneficial() && e != Effects.CONFUSION){ // Because we decided to leave confusion on pufferfish
						safe = false;
						break;
					}
				}
				if(safe){
					foods.add(item);
				}
			}
		}
		if(dragonType == DragonType.FOREST && FOREST_D_FOOD == null){
			FOREST_D_FOOD = foods;
		}else if(dragonType == DragonType.CAVE && CAVE_D_FOOD == null){
			CAVE_D_FOOD = foods;
		}else if(dragonType == DragonType.SEA && SEA_D_FOOD == null){
			SEA_D_FOOD = foods;
		}
		return foods;
	}

	public static void dragonEat(FoodStats foodStats, Item item, ItemStack itemStack, DragonType type){
		if(isDragonEdible(item, type)){
			Food food = getDragonFoodProperties(item, type);
			foodStats.eat(food.getNutrition(), food.getSaturationModifier());
		}
	}

	@Nullable
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
	public static FoodProperties getDragonFoodProperties(Item item, DragonType type) {
		if (DRAGON_FOODS == null || !ConfigHandler.SERVER.customDragonFoods.get() || type == DragonType.NONE)
=======
	public static Food getDragonFoodProperties(Item item, DragonType type){
		if(DRAGON_FOODS == null || !ConfigHandler.SERVER.customDragonFoods.get() || type == DragonType.NONE){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
			return item.getFoodProperties();
		}
		if(DRAGON_FOODS.get(type).containsKey(item)){
			return DRAGON_FOODS.get(type).get(item);
		}
		return null;
	}

	public static boolean isDragonEdible(Item item, DragonType type){
		if(ConfigHandler.SERVER.customDragonFoods.get() && type != DragonType.NONE){
			return DRAGON_FOODS != null && DRAGON_FOODS.containsKey(type) && item != null && DRAGON_FOODS.get(type).containsKey(item);
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
		return item.getFoodProperties() != null;
	}
	
	public static void dragonEat(FoodData foodStats, Item item, ItemStack itemStack, DragonType type) {
		if (isDragonEdible(item, type)) {
			FoodProperties food = getDragonFoodProperties(item, type);
			foodStats.eat(food.getNutrition(), food.getSaturationModifier());
=======
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
		}
		return item.getFoodProperties() != null;
	}

	@SubscribeEvent
	public void onItemUseStart(LivingEntityUseItemEvent.Start event){
		DragonStateProvider.getCap(event.getEntityLiving()).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				event.setDuration(getUseDuration(event.getItem(), dragonStateHandler.getType()));
			}
		});
	}

	public static int getUseDuration(ItemStack item, DragonType type){
		if(isDragonEdible(item.getItem(), type)){
			return item.getItem().getFoodProperties() != null && item.getItem().getFoodProperties().isFastFood() ? 16 : 32;
		}else{
			return item.getUseDuration(); // VERIFY THIS
		}
	}

	@SubscribeEvent
	public void onItemRightClick(PlayerInteractEvent.RightClickItem event){
		DragonStateProvider.getCap(event.getEntityLiving()).ifPresent(dragonStateHandler -> {
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
			if (dragonStateHandler.isDragon()) {
				if (!event.getPlayer().level.isClientSide) {
					ServerPlayer player = (ServerPlayer) event.getPlayer();
					ServerLevel level = player.getLevel();
					InteractionHand hand = event.getHand();
=======
			if(dragonStateHandler.isDragon()){
				if(!event.getPlayer().level.isClientSide){
					ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
					ServerWorld level = player.getLevel();
					Hand hand = event.getHand();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
					ItemStack stack = player.getItemInHand(event.getHand());
					if(isDragonEdible(stack.getItem(), dragonStateHandler.getType())){
						int i = stack.getCount();
						int j = stack.getDamageValue();
						InteractionResultHolder<ItemStack> actionresult = stack.use(level, player, hand);
						ItemStack itemstack = actionresult.getObject();
						if(itemstack == stack && itemstack.getCount() == i && getUseDuration(itemstack, dragonStateHandler.getType()) <= 0 && itemstack.getDamageValue() == j){
							{
								event.setCancellationResult(actionresult.getResult());
							}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
						} else if (actionresult.getResult() == InteractionResult.FAIL && getUseDuration(itemstack, dragonStateHandler.getType()) > 0 && !player.isUsingItem()) {
=======
						}else if(actionresult.getResult() == ActionResultType.FAIL && getUseDuration(itemstack, dragonStateHandler.getType()) > 0 && !player.isUsingItem()){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
							{
								event.setCancellationResult(actionresult.getResult());
								event.setCanceled(true);
							}
						}else{
							player.setItemInHand(hand, itemstack);
							if(player.isCreative()){
								itemstack.setCount(i);
								if(itemstack.isDamageableItem() && itemstack.getDamageValue() != j){
									itemstack.setDamageValue(j);
								}
							}

							if(itemstack.isEmpty()){
								player.setItemInHand(hand, ItemStack.EMPTY);
							}

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
=======
							if(!player.isUsingItem()){
								player.refreshContainer(player.inventoryMenu);
							}

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
							event.setCancellationResult(actionresult.getResult());
							event.setCanceled(true);
						}
					}
				}
			}
		});
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
	
	public static int rightHeight = 0;
	
	@OnlyIn(Dist.CLIENT)
	public static void onRenderFoodBar(ForgeIngameGui gui, PoseStack mStack, float partialTicks, int width, int height) {
		LocalPlayer player = Minecraft.getInstance().player;
		
		if(Minecraft.getInstance().options.hideGui || !gui.shouldDrawSurvivalElements()) return;
		if (!ConfigHandler.SERVER.customDragonFoods.get() || !DragonUtils.isDragon(player)) {
			ForgeIngameGui.FOOD_LEVEL_ELEMENT.render(gui, mStack, partialTicks, width, height);
			return;
		}
		
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if (dragonStateHandler.isDragon()) {
				
=======

	@SubscribeEvent( priority = EventPriority.LOWEST )
	@OnlyIn( Dist.CLIENT )
	public void onRenderFoodBar(RenderGameOverlayEvent.Pre event){
		ClientPlayerEntity player = this.mc.player;

		isDrawingOverlay = !event.isCanceled() && ConfigHandler.SERVER.customDragonFoods.get();
		if(!isDrawingOverlay){
			return;
		}

		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){

				if(event.getType() != RenderGameOverlayEvent.ElementType.FOOD || player.isCreative() || player.isSpectator()){
					return;
				}

				//event.setCanceled(true);

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
				rand.setSeed(player.tickCount * 312871L);

				RenderSystem.enableBlend();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
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
=======
				this.mc.getTextureManager().bind(FOOD_ICONS);

				if(ConfigHandler.CLIENT.appleskinSupport.get()){
					rightHeight = ForgeIngameGui.right_height;
					ForgeIngameGui.right_height = 0;
				}else{
					rightHeight = ForgeIngameGui.right_height;
					ForgeIngameGui.right_height += 10;
				}

				final int left = this.mc.getWindow().getGuiScaledWidth() / 2 + 91;
				final int top = this.mc.getWindow().getGuiScaledHeight() - rightHeight;
				rightHeight += 10;
				final FoodStats food = player.getFoodData();

				final int type = dragonStateHandler.getType() == DragonType.FOREST ? 0 : dragonStateHandler.getType() == DragonType.CAVE ? 9 : 18;

				final boolean hunger = player.hasEffect(Effects.HUNGER);

				for(int i = 0; i < 10; ++i){
					int idx = i * 2 + 1;
					int y = top;

					if(food.getSaturationLevel() <= 0.0F && player.tickCount % (food.getFoodLevel() * 3 + 1) == 0){
						y = top + (rand.nextInt(3) - 1);
					}

					mc.gui.blit(event.getMatrixStack(), left - i * 8 - 9, y, (hunger ? 117 : 0), type, 9, 9);

					if(idx < food.getFoodLevel()){
						mc.gui.blit(event.getMatrixStack(), left - i * 8 - 9, y, (hunger ? 72 : 36), type, 9, 9);
					}else if(idx == food.getFoodLevel()){
						mc.gui.blit(event.getMatrixStack(), left - i * 8 - 9, y, (hunger ? 81 : 45), type, 9, 9);
					}
				}

				this.mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
				RenderSystem.disableBlend();
			}else{
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
				isDrawingOverlay = false;
			}
		});
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonFoodHandler.java
}
=======

	@SubscribeEvent( priority = EventPriority.LOWEST )
	@OnlyIn( Dist.CLIENT )
	public void onPostRenderFood(RenderGameOverlayEvent.Post event){
		if(!ConfigHandler.CLIENT.appleskinSupport.get()){
			return;
		}

		ClientPlayerEntity player = this.mc.player;

		isDrawingOverlay = !event.isCanceled() && ConfigHandler.SERVER.customDragonFoods.get();
		if(!isDrawingOverlay){
			return;
		}

		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){

				if(event.getType() != RenderGameOverlayEvent.ElementType.FOOD || player.isCreative() || player.isSpectator()){
					return;
				}

				ForgeIngameGui.right_height = rightHeight;
			}else{
				isDrawingOverlay = false;
			}
		});
	}
}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler.java
