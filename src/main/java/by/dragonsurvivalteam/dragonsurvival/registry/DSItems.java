package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.SeaDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.items.ChargedCoalItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.DragonTreatItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.SpinGrantItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.WingGrantItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.food.ChargedSoupItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.food.DragonFoodItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.growth.StarBoneItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.growth.StarHeartItem;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncCapabilityDebuff;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber( modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class DSItems{
	public static HashMap<String, Item> DS_ITEMS = new HashMap<>();

	public static Item dragonHeartShard, weakDragonHeart, elderDragonHeart;
	public static Item starBone, elderDragonBone, elderDragonDust;
	public static Item charredMeat, charredVegetable, charredMushroom, charredSeafood, chargedCoal, chargedSoup, hotDragonRod, explosiveCopper, doubleQuartz, quartzExplosiveCopper, meatChorusMix, meatWildBerries, smellyMeatPorridge, diamondChorus, luminousOintment, sweetSourRabbit, seasonedFish, goldenCoralPufferfish, frozenRawFish, goldenTurtleEgg;
	public static Item seaDragonTreat, caveDragonTreat, forestDragonTreat;
	public static Item huntingNet;
	public static Item passiveFireBeacon, passiveMagicBeacon, passivePeaceBeacon;
	public static Item starHeart;

	public static Item predatorStar;

	public static Item wingGrantItem, spinGrantItem;
	public static Item lightningTextureItem;
	public static Item inactivePeaceDragonBeacon, inactiveMagicDragonBeacon, inactiveFireDragonBeacon;

	@SubscribeEvent
	public static void register(final RegistryEvent.Register<Item> event){
		IForgeRegistry<Item> registry = event.getRegistry();

		Properties defaultProperties = new Item.Properties().tab(DragonSurvivalMod.items);

		predatorStar = registerItem(registry, "predator_star", "ds.description.predatorStar");

		starBone = registerItem(registry, new StarBoneItem(defaultProperties), "star_bone");
		starHeart = registerItem(registry, new StarHeartItem(defaultProperties), "star_heart");

		elderDragonDust = registerItem(registry, "elder_dragon_dust", "ds.description.elderDragonDust");
		elderDragonBone = registerItem(registry, "elder_dragon_bone", "ds.description.elderDragonBone");

		dragonHeartShard = registerItem(registry, "heart_element", "ds.description.heartElement");
		weakDragonHeart = registerItem(registry, "weak_dragon_heart", "ds.description.weakDragonHeart");
		elderDragonHeart = registerItem(registry, "elder_dragon_heart", "ds.description.elderDragonHeart");


		chargedCoal = registerItem(registry, new ChargedCoalItem(defaultProperties, DragonTypes.CAVE , LivingEntity::removeAllEffects), "charged_coal");
		chargedSoup = registerItem(registry, new ChargedSoupItem(defaultProperties), "charged_soup");
		charredMeat = registerItem(registry, new DragonFoodItem(defaultProperties), "charred_meat");
		charredVegetable = registerItem(registry, new DragonFoodItem(defaultProperties), "charred_vegetable");
		charredMushroom = registerItem(registry, new DragonFoodItem(defaultProperties), "charred_mushroom");
		charredSeafood = registerItem(registry, new DragonFoodItem(defaultProperties), "charred_seafood");
		hotDragonRod = registerItem(registry, new DragonFoodItem(defaultProperties, DragonTypes.CAVE, new MobEffectInstance(DragonEffects.FIRE, Functions.secondsToTicks(1))), "hot_dragon_rod");
		explosiveCopper = registerItem(registry, new DragonFoodItem(defaultProperties, null, e -> {
			e.hurt(DamageSource.explosion(e), 1f);
			e.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, e.getX(), e.getEyeY(), e.getZ(), 1.0D, 0.0D, 0.0D);
		}), "explosive_copper");
		quartzExplosiveCopper = registerItem(registry, new DragonFoodItem(defaultProperties, DragonTypes.CAVE, e -> {
			e.removeEffect(MobEffects.POISON);
			e.removeEffect(MobEffects.WITHER);
		}, new MobEffectInstance(MobEffects.ABSORPTION, Functions.minutesToTicks(5)), new MobEffectInstance(MobEffects.REGENERATION, Functions.secondsToTicks(10), 1)), "quartz_explosive_copper");
		doubleQuartz = registerItem(registry, new DragonFoodItem(defaultProperties, DragonTypes.CAVE, new MobEffectInstance(MobEffects.REGENERATION, Functions.secondsToTicks(5))), "double_quartz");

		sweetSourRabbit = registerItem(registry, new DragonFoodItem(defaultProperties, DragonTypes.FOREST , LivingEntity::removeAllEffects), "sweet_sour_rabbit");
		luminousOintment = registerItem(registry, new DragonFoodItem(defaultProperties, DragonTypes.FOREST, new MobEffectInstance(MobEffects.GLOWING, Functions.minutesToTicks(5)), new MobEffectInstance(DragonEffects.MAGIC, Functions.minutesToTicks(5))), "luminous_ointment");
		diamondChorus = registerItem(registry, new DragonFoodItem(defaultProperties, DragonTypes.FOREST, e -> {
			e.removeEffect(MobEffects.POISON);
			e.removeEffect(MobEffects.WITHER);
		}, new MobEffectInstance(MobEffects.ABSORPTION, Functions.minutesToTicks(5)), new MobEffectInstance(MobEffects.REGENERATION, Functions.secondsToTicks(10), 1)), "diamond_chorus");
		smellyMeatPorridge = registerItem(registry, new DragonFoodItem(defaultProperties), "smelly_meat_porridge");
		meatWildBerries = registerItem(registry, new DragonFoodItem(defaultProperties), "meat_wild_berries");
		meatChorusMix = registerItem(registry, new DragonFoodItem(defaultProperties, DragonTypes.FOREST, new MobEffectInstance(MobEffects.REGENERATION, Functions.secondsToTicks(5))), "meat_chorus_mix");

		seasonedFish = registerItem(registry, new DragonFoodItem(defaultProperties), "seasoned_fish");
		goldenCoralPufferfish = registerItem(registry, new DragonFoodItem(defaultProperties, DragonTypes.SEA, new MobEffectInstance(MobEffects.REGENERATION, Functions.secondsToTicks(5))), "golden_coral_pufferfish");
		frozenRawFish = registerItem(registry, new DragonFoodItem(defaultProperties, DragonTypes.SEA , e -> {
			e.removeAllEffects();

			if(!e.level.isClientSide){
				if(DragonUtils.getHandler(e).getType() instanceof SeaDragonType type){
					type.timeWithoutWater = 0;
					NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e), new SyncCapabilityDebuff(e.getId(), 0, 0, 0, 0));
				}
			}
		}), "frozen_raw_fish");
		goldenTurtleEgg = registerItem(registry, new DragonFoodItem(defaultProperties, DragonTypes.SEA, e -> {
			e.removeEffect(MobEffects.POISON);
			e.removeEffect(MobEffects.WITHER);
		}, new MobEffectInstance(MobEffects.ABSORPTION, Functions.minutesToTicks(5)), new MobEffectInstance(MobEffects.REGENERATION, Functions.secondsToTicks(10), 1)), "golden_turtle_egg");

		seaDragonTreat = registerItem(registry, new DragonTreatItem(DragonTypes.SEA, defaultProperties), "sea_dragon_treat");
		caveDragonTreat = registerItem(registry, new DragonTreatItem(DragonTypes.CAVE, defaultProperties), "cave_dragon_treat");
		forestDragonTreat = registerItem(registry, new DragonTreatItem(DragonTypes.FOREST, defaultProperties), "forest_dragon_treat");

		huntingNet = registerItem(registry, new Item(new Item.Properties()), "dragon_hunting_mesh");
		lightningTextureItem = registerItem(registry, new Item(new Item.Properties()), "lightning");

		passiveMagicBeacon = registerItem(registry, new Item(new Item.Properties()), "beacon_magic_1");
		passivePeaceBeacon = registerItem(registry, new Item(new Item.Properties()), "beacon_peace_1");
		passiveFireBeacon = registerItem(registry, new Item(new Item.Properties()), "beacon_fire_1");

		inactiveMagicDragonBeacon = registerItem(registry, new Item(new Item.Properties()), "beacon_magic_0");
		inactivePeaceDragonBeacon = registerItem(registry, new Item(new Item.Properties()), "beacon_peace_0");
		inactiveFireDragonBeacon = registerItem(registry, new Item(new Item.Properties()), "beacon_fire_0");


		wingGrantItem = registerItem(registry, new WingGrantItem(defaultProperties), "wing_grant");
		spinGrantItem = registerItem(registry, new SpinGrantItem(defaultProperties), "spin_grant");
	}

	public static Item registerItem(IForgeRegistry<Item> registry, String name, String description){
		Item item = new Item(new Item.Properties().tab(DragonSurvivalMod.items)){
			@Override
			public void appendHoverText(ItemStack stack,
				@Nullable
					Level world, List<Component> list, TooltipFlag tooltipFlag){
				super.appendHoverText(stack, world, list, tooltipFlag);
				list.add(new TranslatableComponent(description));
			}
		};
		item.setRegistryName(DragonSurvivalMod.MODID, name);
		registry.register(item);
		DS_ITEMS.put(name, item);
		return item;
	}

	public static Item registerItem(IForgeRegistry<Item> registry, Item item, String name){
		item.setRegistryName(DragonSurvivalMod.MODID, name);
		registry.register(item);
		DS_ITEMS.put(name, item);
		return item;
	}
}