package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.items.food.ChargedSoupItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.food.CharredFoodItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.growth.StarBoneItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.growth.StarHeartItem;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber( modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class DSItems{

	public static Item dragonHeartShard, weakDragonHeart, elderDragonHeart;
	public static Item starBone, elderDragonBone, elderDragonDust;
	public static Item charredMeat, charredVegetable, charredMushroom, charredSeafood, chargedCoal, chargedSoup, hotDragonRod, explosiveCopper, doubleQuartz, quartzExplosiveCopper, meatChorusMix, meatWildBerries, smellyMeatPorridge, diamondChorus, luminousOintment, sweetSourRabbit, seasonedFish, goldenCoralPufferfish, frozenRawFish, goldenTurtleEgg;
	public static Item seaDragonTreat, caveDragonTreat, forestDragonTreat;
	public static Item huntingNet;
	public static Item passiveFireBeacon, passiveMagicBeacon, passivePeaceBeacon;
	public static Item starHeart;

	public static Item wingGrantItem, spinGrantItem;
	public static Item lightningTextureItem;

	@SubscribeEvent
	public static void register(final RegistryEvent.Register<Item> event){
		IForgeRegistry<Item> registry = event.getRegistry();

		starBone = registerItem(registry, new StarBoneItem(new Item.Properties().tab(DragonSurvivalMod.items)), "star_bone");
		starHeart = registerItem(registry, new StarHeartItem(new Item.Properties().tab(DragonSurvivalMod.items)), "star_heart");

		elderDragonDust = registerItem(registry, "elder_dragon_dust", "ds.description.elderDragonDust");
		elderDragonBone = registerItem(registry, "elder_dragon_bone", "ds.description.elderDragonBone");

		dragonHeartShard = registerItem(registry, "heart_element", "ds.description.heartElement");
		weakDragonHeart = registerItem(registry, "weak_dragon_heart", "ds.description.weakDragonHeart");
		elderDragonHeart = registerItem(registry, "elder_dragon_heart", "ds.description.elderDragonHeart");

		chargedCoal = registerItem(registry, new ChargedCoalItem(new Item.Properties().tab(DragonSurvivalMod.items)), "charged_coal");
		charredMeat = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "charred_meat");
		charredVegetable = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "charred_vegetable");
		charredMushroom = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "charred_mushroom");
		charredSeafood = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "charred_seafood");
		chargedSoup = registerItem(registry, new ChargedSoupItem(new Item.Properties().tab(DragonSurvivalMod.items)), "charged_soup");
		hotDragonRod = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "hot_dragon_rod");
		explosiveCopper = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "explosive_copper");
		quartzExplosiveCopper = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "quartz_explosive_copper");
		doubleQuartz = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "double_quartz");

		sweetSourRabbit = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "sweet_sour_rabbit");
		luminousOintment = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "luminous_ointment");
		diamondChorus = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "diamond_chorus");
		smellyMeatPorridge = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "smelly_meat_porridge");
		meatWildBerries = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "meat_wild_berries");
		meatChorusMix = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "meat_chorus_mix");

		seasonedFish = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "seasoned_fish");
		goldenCoralPufferfish = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "golden_coral_pufferfish");
		frozenRawFish = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "frozen_raw_fish");
		goldenTurtleEgg = registerItem(registry, new CharredFoodItem(new Item.Properties().tab(DragonSurvivalMod.items)), "golden_turtle_egg");

		seaDragonTreat = registerItem(registry, new DragonTreatItem(DragonType.SEA, new Item.Properties().tab(DragonSurvivalMod.items)), "sea_dragon_treat");
		caveDragonTreat = registerItem(registry, new DragonTreatItem(DragonType.CAVE, new Item.Properties().tab(DragonSurvivalMod.items)), "cave_dragon_treat");
		forestDragonTreat = registerItem(registry, new DragonTreatItem(DragonType.FOREST, new Item.Properties().tab(DragonSurvivalMod.items)), "forest_dragon_treat");


		huntingNet = registerItem(registry, new Item(new Item.Properties()), "dragon_hunting_mesh");
		passiveMagicBeacon = registerItem(registry, new Item(new Item.Properties()), "beacon_magic_1");
		passivePeaceBeacon = registerItem(registry, new Item(new Item.Properties()), "beacon_peace_1");
		passiveFireBeacon = registerItem(registry, new Item(new Item.Properties()), "beacon_fire_1");
		lightningTextureItem = registerItem(registry, new Item(new Item.Properties()), "lightning");

		wingGrantItem = registerItem(registry, new WingGrantItem(new Item.Properties().tab(DragonSurvivalMod.items)), "wing_grant");
		spinGrantItem = registerItem(registry, new SpinGrantItem(new Item.Properties().tab(DragonSurvivalMod.items)), "spin_grant");
	}

	public static Item registerItem(IForgeRegistry<Item> registry, String name, String description){
		Item item = new Item(new Item.Properties().tab(DragonSurvivalMod.items)){
			@Override
			public void appendHoverText(ItemStack stack,
				@Nullable
					World world, List<ITextComponent> list, ITooltipFlag tooltipFlag){
				super.appendHoverText(stack, world, list, tooltipFlag);
				list.add(new TranslationTextComponent(description));
			}
		};
		item.setRegistryName(DragonSurvivalMod.MODID, name);
		registry.register(item);
		return item;
	}

	public static Item registerItem(IForgeRegistry<Item> registry, Item item, String name){
		item.setRegistryName(DragonSurvivalMod.MODID, name);
		registry.register(item);
		return item;
	}
}