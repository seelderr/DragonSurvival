package by.dragonsurvivalteam.dragonsurvival.config;

import by.dragonsurvivalteam.dragonsurvival.config.obj.*;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class ServerConfig{
	ServerConfig(Builder builder){
		ConfigHandler.addConfigs(builder, ConfigSide.SERVER);
	}

	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "harvestableStarBlock", comment = "Whether silk touch hoes can be used to harvest Predator Stars." )
	public static Boolean mineStarBlock = false;

	@ConfigRange( min = 0, max = 1000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "altarUsageCooldown", comment = "How long of a cooldown in seconds the altar has after each use." )
	public static Integer altarUsageCooldown = 0;

	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "altarCraftable", comment = "Whether dragon altars are craftable or not. When disabled you can only use the command or creative mode to become a dragon." )
	public static Boolean altarCraftable = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "keepClawItems", comment = "Whether to keep items in the claw slots on death otherwise they will drop on death." )
	public static Boolean keepClawItems = false;

	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "syncClawRender", comment = "If players are allowed to hide their claws and teeth from other players. If it is important to you to see your opponent's weapon during pvp, set false." )
	public static Boolean syncClawRender = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "canMoveInEmote", comment = "If players are allowed to move while performing emotes" )
	public static Boolean canMoveInEmote = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "canMoveWhileCasting", comment = "If you should be able to move while casting certain skills or if player movement can be prevented." )
	public static Boolean canMoveWhileCasting = false;

	@ConfigRange( min = 0.0, max = 1000.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "maxSizeVari", comment = "The maximum size variation in percentage" )
	public static Double maxSizeVari = 10.0;

	@ConfigRange( min = -1000.0, max = 0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "minSizeVari", comment = "The minimum size variation in percentage" )
	public static Double minSizeVari = -10.0;

	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "useModifiedHitboxes", comment = "Should the mod use the new modified hitboxes for dragon players? Can have bugs, but more comfortable for pvp." )
	public static Boolean useModifiedHitboxes = false;

	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "startWithDragonChoice", comment = "Should the dragon altar interface be opened when the player first joins the world?" )
	public static Boolean startWithDragonChoice = true;

	// Growth
	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "sizeChangesHitbox", comment = "Whether the dragon size determines its hitbox size. The bigger the dragon, the bigger the hitbox. If false standard player's hitbox be used." )
	public static Boolean sizeChangesHitbox = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "largerDragonHitbox", comment = "Whether the dragon hitbox grows past a human hitbox." )
	public static Boolean hitboxGrowsPastHuman = true;

	@ConfigType(Item.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "growNewborn", comment = "List of items to grow newborn dragon. Format: item/modid:id" )
	public static List<String> growNewborn = List.of("dragonsurvival:heart_element", "dragonsurvival:weak_dragon_heart", "dragonsurvival:elder_dragon_heart");

	@ConfigType(Item.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "growYoung", comment = "List of items to grow young dragon. Format: item/modid:id" )
	public static List<String> growYoung = List.of("dragonsurvival:weak_dragon_heart", "dragonsurvival:elder_dragon_heart");

	@ConfigType(Item.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "growAdult", comment = "List of items to grow adult dragon. Format: item/modid:id" )
	public static List<String> growAdult = List.of("dragonsurvival:elder_dragon_heart");

	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "alternateGrowing", comment = "Defines if dragon should grow without requirement of catalyst items. Your dragon will just grow over time." )
	public static Boolean alternateGrowing = true;

	@ConfigRange( min = 14.0, max = 1000000.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "maxGrowthSize", comment = "Defines the max size your dragon can grow to. Values that are too high can break your game. It is not advisable to set a number higher than 60." )
	public static Double maxGrowthSize = 60.0;

	@ConfigRange( min = 0, max = 1000000.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "reachBonus", comment = "The bonus that is given to dragons at ever 60 size. Human players have 1.0x reach and a size 60 dragon will have 1.5x distance with default value. Only applies to block mining." )
	public static Double reachBonus = 0.5;

	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "saveGrowthStage", comment = "Should the growth stage of a dragon be saved even when you change. Does not affect the saving progress of magic (use saveAllAbilities). The author does not approve of weredragons, but if you insist..." )
	public static Boolean saveGrowthStage = false;

	@ConfigRange( min = 1, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "minHealth", comment = "Dragon starting health. Minimum health dragons will start off with." )
	public static Integer minHealth = 14;

	@ConfigRange( min = 1, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "maxHealth", comment = "Maximum health dragons can grow to." )
	public static Integer maxHealth = 40;

	@ConfigRange( min = 0.0, max = 1000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "newbornGrowthModifier", comment = "A multiplier to change the growth rate from newborn to young. At 1.0 it takes about 3 hours to turn a newborn dragon into a young dragon." )
	public static Double newbornGrowthModifier = 0.3;

	@ConfigRange( min = 0.0, max = 1000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "youngGrowthModifier", comment = "A multiplier to change the growth rate from young to adult. At 1.0 it takes about 1 day to turn a young dragon into a adult dragon." )
	public static Double youngGrowthModifier = 0.5;

	@ConfigRange( min = 0.0, max = 1000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "adultGrowthModifier", comment = "A multiplier to change the growth rate from adult to a full sized adult. At 1.0 it takes about 3 days to become a dragon of maximum adult size." )
	public static Double adultGrowthModifier = 0.9;

	@ConfigRange( min = 0.0, max = 1000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "growth", key = "maxGrowthModifier", comment = "A multiplier to change the growth rate from full sized adult to max size. The change in growth after the maximum adult size is measured in months and years." )
	public static Double maxGrowthModifier = 1.0;

	@ConfigRange( min = 0.0, max = 1.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "drops", key = "dragonHeartShardChance", comment = "The chance for dragon heart shards to drop from any mobs with max health between 14-20" )
	public static Double dragonHeartShardChance = 0.01;

	@ConfigRange( min = 0.0, max = 1.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "drops", key = "weakDragonHeartChance", comment = "The chance for weak dragon heart to drop from any mobs with max health between 20-50" )
	public static Double weakDragonHeartChance = 0.01;

	@ConfigRange( min = 0.0, max = 1.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "drops", key = "elderDragonHeartChance", comment = "The chance for dragon heart to drop from any mobs with max health above 50" )
	public static Double elderDragonHeartChance = 0.1;

	@ConfigType(EntityType.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "drops", key = "dragonHeartEntityList", comment = "Decide which entities can drop dragon hearts" )
	public static List<String> dragonHeartEntityList = List.of();

	@ConfigType(EntityType.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "drops", key = "weakDragonHeartEntityList", comment = "Decide which entities can drop weak dragon hearts" )
	public static List<String> weakDragonHeartEntityList = List.of();

	@ConfigType(EntityType.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "drops", key = "elderDragonHeartEntityList", comment = "Decide which entities can drop elder dragon hearts" )
	public static List<String> elderDragonHeartEntityList = List.of();

	@ConfigOption( side = ConfigSide.SERVER, category = "drops", key = "dragonHeartWhiteList", comment = "Should the dragonHeartEntityList be treated as an allowlist rather than a block list?" )
	public static Boolean dragonHeartWhiteList = false;

	@ConfigOption( side = ConfigSide.SERVER, category = "drops", key = "weakDragonHeartWhiteList", comment = "Should the weakDragonHeartEntityList be treated as an allowlist rather than a block list?" )
	public static Boolean weakDragonHeartWhiteList = false;

	@ConfigOption( side = ConfigSide.SERVER, category = "drops", key = "elderDragonHeartWhiteList", comment = "Should the elderDragonHeartEntityList be treated as an allowlist rather than a block list?" )
	public static Boolean elderDragonHeartWhiteList = false;

	@ConfigOption( side = ConfigSide.SERVER, category = "drops", key = "dragonHeartUseList", comment = "Should the dragonHeartEntityList be used instead of the health requirement?" )
	public static Boolean dragonHeartUseList = false;

	@ConfigOption( side = ConfigSide.SERVER, category = "drops", key = "weakDragonHeartUseList", comment = "Should the weakDragonHeartUseList be used instead of the health requirement?" )
	public static Boolean weakDragonHeartUseList = false;

	@ConfigOption( side = ConfigSide.SERVER, category = "drops", key = "elderDragonHeartUseList", comment = "Should the elderDragonHeartUseList be used instead of the health requirement?" )
	public static Boolean elderDragonHeartUseList = false;

	@ConfigOption( side = ConfigSide.SERVER, category = "treasure", key = "treasureHealthRegen", comment = "Whether sleeping on treasure will recover health or not. " )
	public static Boolean treasureHealthRegen = true;

	@ConfigRange( min = 1, max = 10000000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "treasure", key = "treasureRegenTicks", comment = "The time in ticks it takes to recover 1hp while sleeping on one treasure. A large number of treasures in one place reduces time." )
	public static Integer treasureRegenTicks = 24010;

	@ConfigRange( min = 1, max = 10000000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "treasure", key = "treasureRegenTicksReduce", comment = "The amount of ticks each additional treasure reduces the regen time by" )
	public static Integer treasureRegenTicksReduce = 100;

	@ConfigRange( min = 1, max = 10000000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "treasure", key = "maxTreasures", comment = "The max amount of additional treasure that can be used to reduce the regen time" )
	public static Integer maxTreasures = 240;

	@ConfigOption( side = ConfigSide.SERVER, category = "source_of_magic", key = "sourceOfMagicInfiniteMagic", comment = "Whether using the source of magic block will grant the infinite magic buff." )
	public static Boolean sourceOfMagicInfiniteMagic = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "source_of_magic", key = "damageWrongSourceOfMagic", comment = "Whether using the the source of magic intended for another dragon type will hurt you." )
	public static Boolean damageWrongSourceOfMagic = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "source_of_magic", key = "canUseAllSourcesOfMagic", comment = "Whether you are able to use all types of source of magic no matter your dragon type." )
	public static Boolean canUseAllSourcesOfMagic = false;

	@ConfigRange( min = 1, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "source_of_magic", key = "elderDragonDustTime", comment = "How long duration of the infinite magic effect using elder dragon dust gives in seconds. Note that you also spend 10 seconds while waiting." )
	public static Integer elderDragonDustTime = 20;

	@ConfigRange( min = 1, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "source_of_magic", key = "elderDragonBoneTime", comment = "How long duration of the infinite magic effect using elder dragon bone gives in seconds. Note that you also spend 10 seconds while waiting." )
	public static Integer elderDragonBoneTime = 60;

	@ConfigRange( min = 1, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "source_of_magic", key = "weakHeartShardTime", comment = "How long duration of the infinite magic effect using weak heart shard gives in seconds. Note that you also spend 10 seconds while waiting." )
	public static Integer weakHeartShardTime = 110;

	@ConfigRange( min = 1, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "source_of_magic", key = "weakDragonHeartTime", comment = "How long duration of the infinite magic effect using weak dragon heart gives in seconds. Note that you also spend 10 seconds while waiting." )
	public static Integer weakDragonHeartTime = 310;

	@ConfigRange( min = 1, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "source_of_magic", key = "elderDragonHeartTime", comment = "How long duration of the infinite magic effect using elder dragon heart gives in seconds. Note that you also spend 10 seconds while waiting." )
	public static Integer elderDragonHeartTime = 1010;

	@ConfigRange( min = 0.1, max = 1 )
	@ConfigOption( side = ConfigSide.SERVER, category = "wings", key = "maxFlightSpeed", comment = "Maximum acceleration fly speed up and down. Take into account the chunk load speed. A speed of 0.3 is optimal." )
	public static Double maxFlightSpeed = 0.3;

	@ConfigOption( side = ConfigSide.SERVER, category = "wings", key = "startWithWings", comment = "Whether dragons born with wings." )
	public static Boolean startWithWings = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "wings", key = "enderDragonGrantsSpin", comment = "Whether you should be able to obtain the spin ability from the ender dragon." )
	public static Boolean enderDragonGrantsSpin = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "wings", key = "allowFlyingWhenTotallyHungry", comment = "Whether dragons can fly when totally hungry. You can't open your wings if you're hungry." )
	public static Boolean allowFlyingWithoutHunger = false;

	@ConfigRange( min = 0, max = 20 )
	@ConfigOption( side = ConfigSide.SERVER, category = "wings", key = "flightHungerThreshold", comment = "If the player's hunger is below this parameter, he can't open his wings." )
	public static Integer flightHungerThreshold = 6;

	@ConfigRange( min = 0, max = 20 )
	@ConfigOption( side = ConfigSide.SERVER, category = "wings", key = "flightHungerThreshold", comment = "If the player's hunger is less then or equal to this parameter, the wings will be folded even during flight." )
	public static Integer foldWingsThreshold = 0;

	@ConfigOption( side = ConfigSide.SERVER, category = "wings", key = "flyingUsesHunger", comment = "Whether you use up hunger while flying." )
	public static Boolean flyingUsesHunger = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "wings", key = "enableFlightFallDamage", comment = "Whether fall damage in flight is included. If true dragon will take damage from the fall." )
	public static Boolean enableFlightFallDamage = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "wings", key = "lethalFallDamage", comment = "Whether fall damage from flight is lethal, otherwise it will leave you at half a heart" )
	public static Boolean lethalFlight = false;

	@ConfigOption( side = ConfigSide.SERVER, category = "wings", key = "foldWingsOnLand", comment = "Whether your wings will fold automatically when landing. Has protection against accidental triggering, so the wings do not always close. If False you must close the wings manually." )
	public static Boolean foldWingsOnLand = false;

	@ConfigOption( side = ConfigSide.SERVER, category = "wings", key = "alternateFlight", comment = "Whether to use flight similar to creative rather then gliding." )
	public static Boolean creativeFlight = false;

	@ConfigRange( min = 0, max = 100000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "wings", key = "flightSpinCooldown", comment = "The cooldown in seconds in between uses of the spin attack in flight" )
	public static Integer flightSpinCooldown = 5;


	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "healthMod", comment = "Apply a health modifier for dragons. The older the dragon, the more health it has." )
	public static Boolean healthAdjustments = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "bonuses", comment = "Set to false to toggle off all dragon bonuses and play as human." )
	public static Boolean bonuses = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "attackMod", comment = "Apply an attack damage modifier for dragons." )
	public static Boolean attackDamage = true;

	@ConfigRange( min = 0.0, max = 100.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "babyAttackMod", comment = "Attack modifier for baby dragons." )
	public static Double babyBonusDamage = 1.0;

	@ConfigRange( min = 0.0, max = 100.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "youngAttackMod", comment = "Attack modifier for young dragons." )
	public static Double youngBonusDamage = 2.0;

	@ConfigRange( min = 0.0, max = 100.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "adultAttackMod", comment = "Attack modifier for adult dragons." )
	public static Double adultBonusDamage = 3.0;

	@ConfigRange( min = 0.0, max = 0.9 )
	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "newbornJump", comment = "Jumping height for a newborn dragon. Default is 1 block." )
	public static Double newbornJump = 0.025;

	@ConfigRange( min = 0.0, max = 0.9 )
	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "youngJump", comment = "Jumping height for a young dragon. Default is 1.5 block." )
	public static Double youngJump = 0.1;

	@ConfigRange( min = 0.0, max = 0.9 )
	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "adultJump", comment = "Jumping height for a adult dragon. Default is 2 block." )
	public static Double adultJump = 0.15;

	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "clawsAreTools", comment = "Whether dragon claws function as tools." )
	public static Boolean clawsAreTools = true;

	@ConfigRange( min = -1, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "baseHarvestLevel", comment = "The harvest level to apply when dragons breaks a block, regardless of dragon/tool type." )
	public static Integer baseHarvestLevel = 0;

	@ConfigRange( min = -1, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "bonusHarvestLevel", comment = "The harvest level to apply to a dragons specific tool type once unlocked." )
	public static Integer bonusHarvestLevel = 1;

	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "bonusUnlockedAt", comment = "The stage that dragons unlock the bonus harvest level." )
	public static DragonLevel bonusUnlockedAt = DragonLevel.YOUNG;

	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = "bonuses", key = "speedupEffectLevel", comment = "The speed effect level for dragon block-specific speedups. Set to 0 to disable." )
	public static Integer speedupEffectLevel = 2;


	@ConfigOption( side = ConfigSide.SERVER, category = {"bonuses", "cave"}, key = "fireImmunity", comment = "Whether cave dragons are immune to fire damage types." )
	public static Boolean caveFireImmunity = true;

	@ConfigOption( side = ConfigSide.SERVER, category = {"bonuses", "cave"}, key = "lavaSwimming", comment = "Set to false to disable cave dragon fast lava swimming." )
	public static Boolean caveLavaSwimming = true;

	@ConfigRange( min = 0, max = 100000 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"bonuses", "cave"}, key = "lavaSwimTicks", comment = "The maximum number of ticks a cave dragon can swim in lava. Set to 0 to allow unlimited air while under lava." )
	public static Integer caveLavaSwimmingTicks = 3600;

	@ConfigType(Block.class)
	@ConfigOption( side = ConfigSide.SERVER, category = {"bonuses", "cave"}, key = "caveSpeedupBlocks", comment = "Blocks cave dragons gain speed when standing above. Formatting: block/modid:id" )
	public static List<String> caveSpeedupBlocks = List.of("minecraft:base_stone_nether", "minecraft:base_stone_overworld", "minecraft:stone_bricks", "minecraft:beacon_base_blocks", "forge:cobblestone", "forge:sandstone", "forge:stone", "forge:ores", "quark:deepslate", "quark:deepslate_bricks", "quark:cobbled_deepslate");


	@ConfigRange( min = 0.0, max = 100.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"bonuses", "forest"}, key = "fallReduction", comment = "How many blocks of fall damage is mitigated for forest dragons. Set to 0.0 to disable." )
	public static Double forestFallReduction = 5.0;

	@ConfigOption( side = ConfigSide.SERVER, category = {"bonuses", "forest"}, key = "bushImmunity", comment = "Whether forest dragons are immune to Sweet Berry Bush damage." )
	public static Boolean forestBushImmunity = true;

	@ConfigOption( side = ConfigSide.SERVER, category = {"bonuses", "forest"}, key = "cactiImmunity", comment = "Whether forest dragons are immune to Cactus damage." )
	public static Boolean forestCactiImmunity = true;

	@ConfigType(Block.class)
	@ConfigOption( side = ConfigSide.SERVER, category = {"bonuses", "forest"}, key = "forestSpeedupBlocks", comment = "Blocks forest dragons gain speed when standing above. Formatting: block/modid:id" )
	public static List<String> forestSpeedupBlocks = List.of("minecraft:logs", "minecraft:leaves", "minecraft:planks", "forge:dirt");


	@ConfigOption( side = ConfigSide.SERVER, category = {"bonuses", "sea"}, key = "waterBonuses", comment = "Whether sea dragons gain bonus swim speed and unlimited air." )
	public static Boolean seaSwimmingBonuses = true;

	@ConfigType(Block.class)
	@ConfigOption( side = ConfigSide.SERVER, category = {"bonuses", "sea"}, key = "seaSpeedupBlocks", comment = "Blocks sea dragons gain speed when standing above. Formatting: block/modid:id" )
	public static List<String> seaSpeedupBlocks = List.of("minecraft:ice", "minecraft:impermeable", "minecraft:sand", "minecraft:coral_blocks", "forge:sand", "minecraft:dirt_path", "minecraft:sandstone", "minecraft:cut_sandstone", "minecraft:chiseled_sandstone", "minecraft:smooth_sandstone", "minecraft:red_sandstone", "minecraft:cut_red_sandstone", "minecraft:chiseled_red_sandstone", "minecraft:smooth_red_sandstone", "minecraft:water");

	//Dragon Penalties
	@ConfigOption( side = ConfigSide.SERVER, category = "penalties", key = "penalties", comment = "Set to false to toggle off all dragon penalties." )
	public static Boolean penalties = true;

	@ConfigType(EntityType.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "penalties", key = "allowedVehicles", comment = "List of rideable entities. Format: modid:id" )
	public static List<String> allowedVehicles = List.of("minecraft:boat", "littlelogistics:seater_barge", "minecraft:minecart", "create:seat", "create:contraption", "create:gantry_contraption", "create:stationary_contraption", "botania:player_mover", "quark:quark_boat");

	@ConfigOption( side = ConfigSide.SERVER, category = "penalties", key = "limitedRiding", comment = "Should dragons be limited by which entities they can ride" )
	public static Boolean ridingBlacklist = true;

	@ConfigType(Item.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "penalties", key = "blacklistedItems", comment = "List of items that disallowed to be used by dragons. Format: item/modid:id" )
	public static List<String> blacklistedItems = List.of("minecraft:bow", "spartanshields:shield_basic_nickel", "spartanshields:shield_basic_invar", "spartanshields:shield_basic_constantan", "spartanshields:shield_basic_platinum", "spartanshields:shield_mekanism_refined_glowstone", "spartanshields:shield_tower_wood", "spartanshields:shield_tower_stone", "spartanshields:shield_tower_iron", "spartanshields:shield_tower_gold", "spartanshields:shield_tower_diamond", "spartanshields:shield_tower_netherite", "spartanshields:shield_tower_obsidian", "spartanshields:shield_tower_copper", "spartanshields:shield_tower_tin", "spartanshields:shield_tower_bronze", "spartanshields:shield_tower_steel", "spartanshields:shield_tower_silver", "spartanshields:shield_tower_lead", "spartanshields:shield_tower_nickel", "spartanshields:shield_tower_constantan", "spartanshields:shield_tower_invar", "spartanshields:shield_tower_platinum", "spartanshields:shield_tower_electrum", "spartanshields:shield_mekanism_powered_ultimate", "quark:flamerang", "quark:pickarang", "spartanshields:shield_botania_manasteel", "spartanshields:shield_botania_elementium", "spartanshields:shield_mekanism_osmium", "spartanshields:shield_mekanism_lapis_lazuli", "spartanshields:shield_basic_electrum", "spartanshields:shield_mekanism_refined_obsidian", "spartanshields:shield_mekanism_powered_basic", "spartanshields:shield_mekanism_powered_advanced", "spartanshields:shield_mekanism_powered_elite", "spartanweaponry:boomerang_steel", "spartanweaponry:boomerang_invar", "spartanweaponry:boomerang_platinum", "spartanweaponry:boomerang_electrum", "spartanshields:shield_basic_bronze", "spartanshields:shield_basic_tin", "spartanshields:shield_basic_copper", "spartanshields:shield_basic_obsidian", "spartanshields:shield_basic_netherite", "spartanshields:shield_basic_diamond", "spartanshields:shield_basic_gold", "spartanshields:shield_basic_iron", "spartanshields:shield_basic_stone", "spartanshields:shield_basic_wood", "spartanweaponry:boomerang_lead", "spartanweaponry:boomerang_nickel", "spartanshields:shield_basic_steel", "spartanshields:shield_basic_silver", "spartanshields:shield_basic_lead", "spartanweaponry:boomerang_bronze", "spartanweaponry:boomerang_tin", "spartanweaponry:boomerang_copper", "spartanweaponry:boomerang_netherite", "spartanweaponry:boomerang_gold", "spartanweaponry:boomerang_iron", "spartanweaponry:boomerang_stone", "spartanweaponry:heavy_crossbow_bronze", "mowziesmobs:wrought_axe", "spartanshields:shield_botania_terrasteel", "spartanweaponry:heavy_crossbow_leather", "spartanweaponry:heavy_crossbow_iron", "spartanweaponry:heavy_crossbow_gold", "spartanweaponry:heavy_crossbow_diamond", "spartanweaponry:heavy_crossbow_netherite", "spartanweaponry:heavy_crossbow_copper", "spartanweaponry:heavy_crossbow_tin", "spartanweaponry:boomerang_wood", "nethers_exoticism:rambutan_shield", "minecraft:shield", "minecraft:trident", "spartanweaponry:heavy_crossbow_lead", "spartanweaponry:heavy_crossbow_nickel", "spartanweaponry:heavy_crossbow_electrum", "spartanweaponry:heavy_crossbow_platinum", "spartanweaponry:heavy_crossbow_invar", "spartanweaponry:heavy_crossbow_silver", "spartanweaponry:heavy_crossbow_steel", "spartanweaponry:boomerang_diamond", "spartanweaponry:heavy_crossbow_wood", "minecraft:crossbow", "aquaculture:neptunium_bow", "spartanweaponry:longbow_electrum", "spartanweaponry:longbow_invar", "infernalexp:glowsilk_bow", "spartanweaponry:longbow_wood", "spartanweaponry:longbow_leather", "spartanweaponry:longbow_silver", "spartanweaponry:longbow_steel", "spartanweaponry:longbow_bronze", "spartanweaponry:longbow_tin", "spartanweaponry:longbow_copper", "spartanweaponry:longbow_netherite", "spartanweaponry:longbow_diamond", "spartanweaponry:longbow_gold", "spartanweaponry:longbow_iron", "spartanweaponry:boomerang_diamond", "spartanweaponry:boomerang_iron", "spartanweaponry:boomerang_wood", "spartanweaponry:boomerang_gold", "spartanweaponry:boomerang_netherite", "spartanweaponry:boomerang_copper", "spartanweaponry:boomerang_tin", "spartanweaponry:boomerang_bronze", "spartanweaponry:boomerang_stone", "spartanweaponry:boomerang_platinum", "spartanweaponry:boomerang_electrum", "spartanweaponry:boomerang_steel", "spartanweaponry:boomerang_lead", "spartanweaponry:boomerang_invar", "spartanweaponry:boomerang_nickel");

	@ConfigOption( side = ConfigSide.SERVER, category = "penalties", key = "blacklistedSlots", comment = "List of slots to handle blacklistedItems option" )
	public static List<Integer> blacklistedSlots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 45);

	// Cave Dragon Penalties
	@ConfigRange( min = 0.0, max = 100.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"penalties", "cave"}, key = "waterDamage", comment = "The amount of damage taken per water damage tick (once every 10 ticks). Set to 0.0 to disable water damage." )
	public static Double caveWaterDamage = 1.0;

	@ConfigRange( min = 0.0, max = 100.0 )
	@ConfigOption( side = ConfigSide.SERVER, category =  {"penalties", "cave"}, key = "rainDamage", comment = "The amount of damage taken per rain damage tick (once every 40 ticks). Set to 0.0 to disable rain damage." )
	public static Double caveRainDamage = 1.0;

	@ConfigRange( min = 0.0, max = 100.0 )
	@ConfigOption( side = ConfigSide.SERVER, category  = {"penalties", "cave"}, key = "splashDamage", comment = "The amount of damage taken when hit with a snowball or a water bottle. Set to 0.0 to disable splash damage." )
	public static Double caveSplashDamage = 2.0;

	// Forest Dragon Penalties
	@ConfigRange( min = 0, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category  = {"penalties", "forest"}, key = "ticksBeforeStressed", comment = "The number of ticks in darkness before the forest dragon gets Stress effect. Set to 0 to disable to stress effect." )
	public static Integer forestStressTicks = 200;

	@ConfigRange( min = 2, max = 100000 )
	@ConfigOption( side = ConfigSide.SERVER, category  = {"penalties", "forest"}, key = "stressEffectDuration", comment = "The number of seconds the stress effect lasts for." )
	public static Integer forestStressEffectDuration = 40;

	@ConfigRange( min = 0.1, max = 4.0 )
	@ConfigOption( side = ConfigSide.SERVER, category  = {"penalties", "forest"}, key = "stressExhaustion", comment = "The amount of exhaustion applied per 10 ticks during the stress effect." )
	public static Double stressExhaustion = 1.0;

	// Sea Dragon Penalties

	@ConfigRange( min = 0, max = 100000 )
	@ConfigOption( side = ConfigSide.SERVER, category  = {"penalties", "sea"}, key = "ticksWithoutWater", comment = "The number of ticks out of water before the sea dragon will start taking dehydration damage. Set to 0 to disable. Note: This value can stack up to double while dehydrated." )
	public static Integer seaTicksWithoutWater = 1000;

	@ConfigOption( side = ConfigSide.SERVER, category  = {"penalties", "sea"}, key = "waterConsumptionDependsOnTemperature", comment = "Whether the sea dragon should lose more water in warmer biomes and less during the night." )
	public static Boolean seaTicksBasedOnTemperature = true;

	@ConfigRange( min = 0.5, max = 100.0 )
	@ConfigOption( side = ConfigSide.SERVER, category  = {"penalties", "sea"}, key = "dehydrationDamage", comment = "The amount of damage taken per tick while dehydrated (once every 40 ticks unless fully dehydrated, then once every 20 ticks)." )
	public static Double seaDehydrationDamage = 1.0;

	@ConfigType(Block.class)
	@ConfigOption( side = ConfigSide.SERVER, category  = {"penalties", "sea"}, key = "seaHydrationBlocks", comment = "When sea dragons stand on these blocks, hydration is restored. Format: block/modid:id" )
	public static List<String> seaHydrationBlocks = List.of("minecraft:ice", "minecraft:snow", "minecraft:powder_snow","minecraft:snow_block", "dragonsurvival:sea_source_of_magic", "immersive_weathering:icicle");

	@ConfigOption( side = ConfigSide.SERVER, category  = {"penalties", "sea"}, key = "allowWaterBottles", comment = "Set to false to disable sea dragons using vanilla water bottles to avoid dehydration." )
	public static Boolean seaAllowWaterBottles = true;

	@ConfigRange( min = 0, max = 100000 )
	@ConfigOption( side = ConfigSide.SERVER, category  = {"penalties", "sea"}, key = "waterItemRestorationTicks", comment = "How many ticks do water restoration items restore when used. Set to 0 to disable." )
	public static Integer seaTicksWithoutWaterRestored = 5000;

	@ConfigType(Item.class)
	@ConfigOption( side = ConfigSide.SERVER, category  = {"penalties", "sea"}, key = "seaHydrationItems", comment = "Additional modded USEABLE items that restore water when used (called from LivingEntityUseItemEvent.Finish). Format: item/modid:id" )
	public static List<String> seaAdditionalWaterUseables = List.of("minecraft:enchanted_golden_apple", "immersive_weathering:icicle");

	// Ore Loot
	@ConfigRange( min = 0.0, max = 1.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "ore", key = "humanOreDustChance", comment = "The odds of dust dropping when a human harvests an ore." )
	public static Double humanOreDustChance = 0.1;

	@ConfigRange( min = 0.0, max = 1.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "ore", key = "dragonOreDustChance", comment = "The odds of dust dropping when a dragon harvests an ore." )
	public static Double dragonOreDustChance = 0.2;

	@ConfigRange( min = 0.0, max = 1.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "ore", key = "humanOreBoneChance", comment = "The odds of a bone dropping when a human harvests an ore." )
	public static Double humanOreBoneChance = 0.0;

	@ConfigRange( min = 0.0, max = 1.0 )
	@ConfigOption( side = ConfigSide.SERVER, category = "ore", key = "dragonOreBoneChance", comment = "The odds of a bone dropping when a dragon harvests an ore." )
	public static Double dragonOreBoneChance = 0.01;

	@ConfigOption( side = ConfigSide.SERVER, category = "ore", key = "oresTag", comment = "The tag that contains all ores that can drop dust/bones when harvested. Will not drop if the ore drops another of the items in this tag. Format: modid:id" )
	public static String oresTag = "forge:ores";

	// Food general
	@ConfigOption( side = ConfigSide.SERVER, category = "food", key = "dragonFoods", comment = "Force dragons to eat a unique diet for their type." )
	public static Boolean customDragonFoods = true;

	@IgnoreConfigCheck
	@ConfigType(Item.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "food", key = "hurtfulToCaveDragon", comment = "Items which will cause damage to cave dragons when consumed. Formatting: item/modid:itemid:damage" )
	public static List<String> caveDragonHurtfulItems = Arrays.asList("minecraft:potion:2", "minecraft:water_bottle:2", "minecraft:milk_bucket:2");

	@IgnoreConfigCheck
	@ConfigType(Item.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "food", key = "hurtfulToSeaDragon", comment = "Items which will cause damage to sea dragons when consumed. Formatting: item/modid:itemid:damage" )
	public static List<String> seaDragonHurtfulItems = Collections.emptyList();

	@IgnoreConfigCheck
	@ConfigType(Item.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "food", key = "hurtfulToForestDragon", comment = "Items which will cause damage to forest dragons when consumed. Formatting: item/modid:itemid:damage" )
	public static List<String> forestDragonHurtfulItems = Collections.emptyList();

	@ConfigRange( min = 0, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "food", key = "chargedSoupBuffDuration", comment = "How long in seconds should the cave fire effect from charged soup last. (Default to 5min) Set to 0 to disable." )
	public static Integer chargedSoupBuffDuration = 300;

	// Dragon Food List
	@IgnoreConfigCheck
	@ConfigType(Item.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "food", key = "caveDragon", comment = {"Dragon food formatting: item/modid:id:food:saturation", "Dragon food formatting: item/modid:id:food:saturation. Food/saturation values are optional as the human values will be used if missing."} )
	public static List<String> caveDragonFoods = Arrays.asList("minecraft:coals:1:1", "minecraft:charcoal:1:2", "minecraft:golden_apple", "minecraft:enchanted_golden_apple", "dragonsurvival:charged_coal:6:1", "dragonsurvival:charred_meat:10:12", "dragonsurvival:cave_dragon_treat:14:12", "dragonsurvival:charred_seafood:8:10", "dragonsurvival:charred_vegetable:8:9", "dragonsurvival:charred_mushroom:8:5", "dragonsurvival:charged_soup:20:15", "desolation:cinder_fruit:6:7", "desolation:powered_cinder_fruit:8:12", "desolation:activatedcharcoal:2:2", "desolation:infused_powder:10:10", "desolation:primed_ash:7:8", "pickletweaks:diamond_apple", "pickletweaks:emerald_apple", "undergarden:ditchbulb:5,6", "xreliquary:molten_core:1:1", "silents_mechanisms:coal_generator_fuels:1:1", "mekanism:dust_charcoal:1:1", "mekanism:dust_coal:1:1", "rats:nether_cheese", "potionsmaster:charcoal_powder:1:1", "potionsmaster:coal_powder:1:1", "potionsmaster:activated_charcoal:2:2", "thermal:coal_coke:1:1", "infernalexp:glowcoal:2:3", "resourcefulbees:coal_honeycomb:5:5", "resourcefulbees:netherite_honeycomb:5:5", "lazierae2:coal_dust:1:1", "wyrmroost:jewelled_apple", "silents_mechanisms:coal_dust:1:1", "potionsmaster:calcinatedcoal_powder:1:1", "thermal:basalz_rod:2:4", "thermal:basalz_powder:1:2", "druidcraft:fiery_glass:2:2");

	@IgnoreConfigCheck
	@ConfigType(Item.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "food", key = "forestDragon", comment = {"Dragon food formatting: item/modid:id:food:saturation", "Dragon food formatting: item/modid:id:food:saturation. Food/saturation values are optional as the human values will be used if missing."} )
	public static List<String> forestDragonFoods = Arrays.asList("forge:raw_meats:5:7", "minecraft:sweet_berries:1:1", "minecraft:rotten_flesh:2:3", "minecraft:spider_eye:7:8", "minecraft:rabbit:7:13", "minecraft:poisonous_potato:7:10", "minecraft:chorus_fruit:9:12", "minecraft:golden_apple", "minecraft:enchanted_golden_apple", "minecraft:honey_bottle", "dragonsurvival:forest_dragon_treat:10:12", "aoa3:fiery_chops:6:7", "aoa3:raw_chimera_chop:6:7", "aoa3:raw_furlion_chop:6:7", "aoa3:raw_halycon_beef:7:8", "aoa3:raw_charger_shank:6:7", "aoa3:trilliad_leaves:8:11", "aoa3:heart_fruit:9:10", "pamhc2foodextended:rawtofabbititem", "pamhc2foodextended:rawtofickenitem", "pamhc2foodextended:rawtofuttonitem", "alexsmobs:kangaroo_meat:5:6", "alexsmobs:moose_ribs:6:8", "simplefarming:raw_horse_meat:5:6", "simplefarming:raw_bacon:3:3", "simplefarming:raw_chicken_wings:2:3", "simplefarming:raw_sausage:3:4", "xenoclustwo:raw_tortice:7:8", "unnamedanimalmod:musk_ox_shank:7:8", "unnamedanimalmod:frog_legs:5:6", "unnamedanimalmod:mangrove_fruit:4:7", "betteranimalsplus:venisonraw:7:6", "betteranimalsplus:pheasantraw:7:5", "betteranimalsplus:turkey_leg_raw:4:5", "infernalexp:raw_hogchop:6:7", "infernalexp:cured_jerky:10:7", "druidcraft:elderberries:3:4", "rats:raw_rat:4:5", "aquaculture:frog:4:5", "aquaculture:frog_legs_raw:4:4", "aquaculture:box_turtle:4:5", "aquaculture:arrau_turtle:4:5", "aquaculture:starshell_turtle:4:5", "nethers_exoticism:kiwano:3:4", "undergarden:raw_gloomper_leg:4:5", "undergarden:raw_dweller_meat:6:7", "farmersdelight:chicken_cuts:3:3", "farmersdelight:bacon:3:3", "farmersdelight:ham:9:10", "farmersdelight:minced_beef:5:3", "farmersdelight:mutton_chops:5:3", "abnormals_delight:duck_fillet:2:3", "abnormals_delight:venison_shanks:7:3", "pickletweaks:diamond_apple", "pickletweaks:emerald_apple", "autumnity:foul_berries:2:4", "autumnity:turkey:7:8", "autumnity:turkey_piece:2:4", "autumnity:foul_soup:12:8", "endergetic:bolloom_fruit:3:4", "quark:frog_leg:4:5", "nethers_delight:hoglin_loin:8:6", "nethers_delight:raw_stuffed_hoglin:18:10", "xreliquary:zombie_heart:4:7", "xreliquary:bat_wing:2:2", "eidolon:zombie_heart:7:7", "forbidden_arcanus:bat_wing:5:2", "twilightforest:raw_venison:7:7", "twilightforest:raw_meef:9:5", "twilightforest:hydra_chop", "cyclic:chorus_flight", "cyclic:chorus_spectral", "cyclic:apple_ender", "cyclic:apple_honey", "cyclic:apple_chorus", "cyclic:apple_bone", "cyclic:apple_prismarine", "cyclic:apple_lapis", "cyclic:apple_iron", "cyclic:apple_diamond", "cyclic:apple_emerald", "cyclic:apple_chocolate", "cyclic:toxic_carrot:15:15", "artifacts:everlasting_beef", "resourcefulbees:rainbow_honey_bottle", "resourcefulbees:diamond_honeycomb:5:5", "byg:soul_shroom:9:5", "byg:death_cap:9:8", "byg:holly_berries:2:2", "minecolonies:chorus_bread", "wyrmroost:jewelled_apple", "wyrmroost:raw_lowtier_meat:3:2", "wyrmroost:raw_common_meat:5:3", "wyrmroost:raw_apex_meat:8:6", "wyrmroost:raw_behemoth_meat:11:12", "wyrmroost:desert_wyrm:4:3", "eanimod:rawchicken_darkbig:9:5", "eanimod:rawchicken_dark:5:4", "eanimod:rawchicken_darksmall:3:2", "eanimod:rawchicken_pale:5:3", "eanimod:rawchicken_palesmall:4:3", "eanimod:rawrabbit_small:4:4", "environmental:duck:4:3", "environmental:venison:7:7", "cnb:lizard_item_jungle:4:4", "cnb:lizard_item_mushroom:4:4", "cnb:lizard_item_jungle_2:4:4", "cnb:lizard_item_desert_2:4:4", "cnb:lizard_egg:5:2", "cnb:lizard_item_desert:4:4", "create:honeyed_apple:10:7", "snowpig:frozen_porkchop:7:3", "snowpig:frozen_ham:5:7", "untamedwilds:spawn_snake:4:4", "untamedwilds:snake_green_mamba:4:4", "untamedwilds:snake_rattlesnake:4:4", "untamedwilds:snake_emerald:4:4", "untamedwilds:snake_carpet_python:4:4", "untamedwilds:snake_corn:4:4", "untamedwilds:snake_gray_kingsnake:4:4", "untamedwilds:snake_coral:4:4", "untamedwilds:snake_ball_python:4:4", "untamedwilds:snake_black_mamba:4:4", "untamedwilds:snake_western_rattlesnake:4:4", "untamedwilds:snake_taipan:4:4", "untamedwilds:snake_adder:4:4", "untamedwilds:snake_rice_paddy:4:4", "untamedwilds:snake_coral_blue:4:4", "untamedwilds:snake_cave_racer:4:4", "untamedwilds:snake_swamp_moccasin:4:4", "untamedwilds:softshell_turtle_pig_nose:4:4", "untamedwilds:softshell_turtle_flapshell:4:4", "untamedwilds:softshell_turtle_chinese:4:4", "untamedwilds:tortoise_asian_box:4:4", "untamedwilds:tortoise_gopher:4:4", "untamedwilds:tortoise_leopard:4:4", "untamedwilds:spawn_softshell_turtle:4:4", "untamedwilds:softshell_turtle_nile:4:4", "untamedwilds:softshell_turtle_spiny:4:4", "untamedwilds:tortoise_sulcata:4:4", "untamedwilds:tortoise_star:4:4", "untamedwilds:spawn_tortoise:4:4", "naturalist:venison:7:6", "leescreatures:raw_boarlin:6:6", "mysticalworld:venison:5:5", "toadterror:toad_chops:8:7", "prehistoricfauna:raw_large_thyreophoran_meat:7:6", "prehistoricfauna:raw_large_marginocephalian_meat:8:6", "prehistoricfauna:raw_small_ornithischian_meat:4:3", "prehistoricfauna:raw_large_sauropod_meat:11:9", "prehistoricfauna:raw_small_sauropod_meat:4:4", "prehistoricfauna:raw_large_theropod_meat:7:7", "prehistoricfauna:raw_small_theropod_meat:4:4", "prehistoricfauna:raw_small_archosauromorph_meat:3:3", "prehistoricfauna:raw_large_archosauromorph_meat:6:5", "prehistoricfauna:raw_small_reptile_meat:4:3", "prehistoricfauna:raw_large_synapsid_meat:5:6");

	@IgnoreConfigCheck
	@ConfigType(Item.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "food", key = "seaDragon", comment = {"Dragon food formatting: item/modid:id:food:saturation", "Dragon food formatting: item/modid:id:food:saturation. Food/saturation values are optional as the human values will be used if missing."} )
	public static List<String> seaDragonFoods = Arrays.asList("forge:raw_fishes:6:7", "minecraft:dried_kelp:1:1", "minecraft:kelp:2:3", "minecraft:pufferfish:10:15", "minecraft:golden_apple", "minecraft:enchanted_golden_apple", "minecraft:honey_bottle", "dragonsurvival:sea_dragon_treat:10:12", "aoa3:raw_candlefish:9:9", "aoa3:raw_crimson_skipper:8:8", "aoa3:raw_fingerfish:4:4", "aoa3:raw_pearl_stripefish:5:4", "aoa3:raw_limefish:5:5", "aoa3:raw_sailback:6:5", "aoa3:raw_golden_gullfish:10:2", "aoa3:raw_turquoise_stripefish:7:6", "aoa3:raw_violet_skipper:7:7", "aoa3:raw_rocketfish:4:10", "aoa3:raw_crimson_stripefish:8:7", "aoa3:raw_sapphire_strider:9:8", "aoa3:raw_dark_hatchetfish:9:9", "aoa3:raw_ironback:10:9", "aoa3:raw_rainbowfish:11:11", "aoa3:raw_razorfish:12:14", "quark:golden_frog_leg", "alexsmobs:lobster_tail:4:5", "alexsmobs:blobfish:8:9", "oddwatermobs:raw_ghost_shark:8:8", "oddwatermobs:raw_isopod:4:2", "oddwatermobs:raw_mudskipper:6:7", "oddwatermobs:raw_coelacanth:9:10", "oddwatermobs:raw_anglerfish:6:6", "oddwatermobs:deep_sea_fish:4:2", "oddwatermobs:crab_leg:5:6", "simplefarming:raw_calamari:5:6", "unnamedanimalmod:elephantnose_fish:5:6", "unnamedanimalmod:flashlight_fish:5:6", "unnamedanimalmod:rocket_killifish:5:6", "unnamedanimalmod:leafy_seadragon:5:6", "unnamedanimalmod:elephantnose_fish:5:6", "betteranimalsplus:eel_meat_raw:5:6", "betteranimalsplus:calamari_raw:4:5", "betteranimalsplus:crab_meat_raw:4:4", "aquaculture:fish_fillet_raw:2:2", "aquaculture:goldfish:8:4", "aquaculture:box_turtle:4:5", "aquaculture:arrau_turtle:4:5", "aquaculture:starshell_turtle:4:5", "aquaculture:algae:3:2", "betterendforge:end_fish_raw:6:7", "betterendforge:hydralux_petal:3:3", "betterendforge:charnia_green:2:2", "shroomed:raw_shroomfin:5:6", "undergarden:raw_gwibling:5:6", "pickletweaks:diamond_apple", "pickletweaks:emerald_apple", "bettas:betta_fish:4:5", "quark:crab_leg:4:4", "pamhc2foodextended:rawtofishitem", "fins:banded_redback_shrimp:6:1", "fins:night_light_squid:6:2", "fins:night_light_squid_tentacle:6:2", "fins:emerald_spindly_gem_crab:7:2", "fins:amber_spindly_gem_crab:7:2", "fins:rubby_spindly_gem_crab:7:2", "fins:sapphire_spindly_gem_crab:7:2", "fins:pearl_spindly_gem_crab:7:2", "fins:papa_wee:6:2", "fins:bugmeat:4:2", "fins:raw_golden_river_ray_wing:6:2", "fins:red_bull_crab_claw:4:4", "fins:white_bull_crab_claw:4:4", "fins:wherble_fin:1:1", "forbidden_arcanus:tentacle:5:2", "pneumaticcraft:raw_salmon_tempura:6:10", "rats:ratfish:4:2", "cyclic:chorus_flight", "cyclic:chorus_spectral", "cyclic:apple_ender", "cyclic:apple_honey", "cyclic:apple_chorus", "cyclic:apple_bone", "cyclic:apple_prismarine", "cyclic:apple_lapis", "cyclic:apple_iron", "cyclic:apple_diamond", "cyclic:apple_emerald", "cyclic:apple_chocolate", "upgrade_aquatic:purple_pickerelweed:2:2", "upgrade_aquatic:blue_pickerelweed:2:2", "upgrade_aquatic:polar_kelp:2:2", "upgrade_aquatic:tongue_kelp:2:2", "upgrade_aquatic:thorny_kelp:2:2", "upgrade_aquatic:ochre_kelp:2:2", "upgrade_aquatic:lionfish:8:9", "resourcefulbees:gold_honeycomb:5:5", "resourcefulbees:rainbow_honey_bottle", "wyrmroost:jewelled_apple", "aquaculture:sushi:6:5", "freshwarriors:fresh_soup:15:10", "freshwarriors:beluga_caviar:10:3", "freshwarriors:piranha:4:1", "freshwarriors:tilapia:4:1", "freshwarriors:stuffed_piranha:4:1", "freshwarriors:tigerfish:5:5", "freshwarriors:toe_biter_leg:3:3", "untamedwilds:egg_arowana:4:4", "untamedwilds:egg_trevally_jack:4:4", "untamedwilds:egg_trevally:4:4", "untamedwilds:egg_giant_salamander:6:4", "untamedwilds:egg_giant_salamander_hellbender:6:4", "untamedwilds:egg_giant_salamander_japanese:6:4", "untamedwilds:giant_clam:4:4", "untamedwilds:giant_clam_derasa:4:4", "untamedwilds:giant_clam_maxima:4:4", "untamedwilds:giant_clam_squamosa:4:4", "untamedwilds:egg_trevally_giant:6:4", "untamedwilds:egg_trevally:6:4", "untamedwilds:egg_trevally_bigeye:6:4", "untamedwilds:egg_sunfish:6:4", "untamedwilds:egg_sunfish_sunfish:6:4", "untamedwilds:egg_giant_clam_squamosa:6:4", "untamedwilds:egg_giant_clam_gigas:6:4", "untamedwilds:egg_giant_clam_derasa:6:4", "untamedwilds:egg_giant_clam:6:4", "untamedwilds:egg_football_fish:6:4", "untamedwilds:egg_arowana:6:4", "untamedwilds:egg_arowana_jardini:6:4", "untamedwilds:egg_arowana_green:6:4", "mysticalworld:raw_squid:6:5", "aquafina:fresh_soup:15:10", "aquafina:beluga_caviar:10:3", "aquafina:raw_piranha:4:1", "aquafina:raw_tilapia:4:1", "aquafina:stuffed_piranha:4:1", "aquafina:tigerfish:5:5", "aquafina:toe_biter_leg:3:3", "aquafina:raw_angelfish:4:1", "aquafina:raw_football_fish:4:1", "aquafina:raw_foxface_fish:4:1", "aquafina:raw_royal_gramma:4:1", "aquafina:raw_starfish:4:1", "aquafina:spider_crab_leg:4:1", "aquafina:raw_stingray_slice:4:1", "prehistoricfauna:raw_ceratodus:5:5", "prehistoricfauna:raw_cyclurus:4:4", "prehistoricfauna:raw_potamoceratodus:5:5", "prehistoricfauna:raw_myledaphus:4:4", "prehistoricfauna:raw_gar:4:4", "prehistoricfauna:raw_oyster:4:3", "prehistoric_delight:prehistoric_fillet:3:3");


	@ConfigType(Block.class)
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "mana"}, key = "seaDragonManaBlocks", comment = "Blocks that will restore mana quicker when a sea dragon is standing on it. Formatting: block/modid:blockid" )
	public static List<String> seaDragonManaBlocks = List.of("dragonsurvival:sea_source_of_magic", "minecraft:ice", "minecraft:snow", "minecraft:snow_block", "minecraft:water", "minecraft:wet_sponge", "minecraft:cauldron");

	@ConfigType(Block.class)
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "mana"}, key = "forestDragonManaBlocks", comment = "Blocks that will restore mana quicker when a forest dragon is standing on it. Formatting: block/modid:blockid" )
	public static List<String> forestDragonManaBlocks = List.of("dragonsurvival:forest_source_of_magic", "minecraft:small_flowers", "minecraft:flowers", "minecraft:tall_flowers");

	@ConfigType(Block.class)
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "mana"}, key = "caveDragonManaBlocks", comment = "Blocks that will restore mana quicker when a cave dragon is standing on it. Formatting: block/modid:blockid" )
	public static List<String> caveDragonManaBlocks = List.of("dragonsurvival:cave_source_of_magic", "minecraft:campfires", "minecraft:lava", "minecraft:smoker", "minecraft:furnace", "minecraft:magma_block", "minecraft:blast_furnace");


	@ConfigOption( side = ConfigSide.SERVER, category = "magic", key = "dragonAbilities", comment = "Whether dragon abilities should be enabled" )
	public static Boolean dragonAbilities = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "magic", key = "caveDragonAbilities", comment = "Whether cave dragon abilities should be enabled" )
	public static Boolean caveDragonAbilities = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "magic", key = "forestDragonAbilities", comment = "Whether forest dragon abilities should be enabled" )
	public static Boolean forestDragonAbilities = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "magic", key = "seaDragonAbilities", comment = "Whether sea dragon abilities should be enabled" )
	public static Boolean seaDragonAbilities = true;


	@ConfigOption( side = ConfigSide.SERVER, category = "magic", key = "noEXPRequirements", comment = "Disable the exp requirements for leveling up active skills" )
	public static Boolean noEXPRequirements = false;

	@ConfigOption( side = ConfigSide.SERVER, category = "magic", key = "consumeEXPAsMana", comment = "Whether to use exp instead of mana if mana is empty" )
	public static Boolean consumeEXPAsMana = true;

	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = "magic", key = "initialPassiveCost", comment = "The initial exp cost for leveling passive skills." )
	public static Integer initialPassiveCost = 2;

	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = "magic", key = "passiveScalingCost", comment = "The multiplier that is used to increase the passive skill costs per level" )
	public static Double passiveScalingCost = 4.0;


	@ConfigRange( min = 1, max = 1000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "magic", key = "favorableManaRegen", comment = "How fast in seconds should mana be recovered in favorable conditions" )
	public static Integer favorableManaTicks = 5;

	@ConfigRange( min = 1, max = 1000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "magic", key = "normalManaRegen", comment = "How fast in seconds should mana be recovered in normal conditions" )
	public static Integer normalManaTicks = 15;


	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities"}, key = "saveAllAbilities", comment = "Whether to save passives skills when changing dragon type" )
	public static Boolean saveAllAbilities = false;

	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "endVoidTeleport", comment = "Should the player be teleported to the overworld when they fall in the end?" )
	public static Boolean endVoidTeleport = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "general", key = "elytraForDragon", comment = "Whether dragons are allowed to use Elytra" )
	public static Boolean dragonsAllowedToUseElytra = false;

	@ConfigRange( min = 1, max = 120 )
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "princessDespawnDelay", comment = "Princess or prince may despawn after this many minutes" )
	public static Integer princessDespawnDelay = 15;

	@ConfigRange( min = 1, max = 120 )
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "hunterDespawnDelay", comment = "Any dragon hunter may despawn after this many minutes" )
	public static Integer hunterDespawnDelay = 15;

	@ConfigRange( min = 10, max = 240 )
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "princessSpawnDelay", comment = "Minimum delay between prince or princess spawning, in minutes" )
	public static Integer princessSpawnDelay = 120;

	@ConfigRange( min = 12, max = 240 )
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "hunterGroupSpawnDelay", comment = "Minimum delay between Dragon hunter group spawning, in minutes" )
	public static Integer hunterSpawnDelay = 20;

	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "allowKnightSpawning", comment = "Dragon knight spawning enabled?" )
	public static Boolean spawnKnight = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "allowSquireSpawning", comment = "Dragon Squire spawning enabled?" )
	public static Boolean spawnSquire = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "allowHunterSpawning", comment = "Dragon Hunter spawning enabled?" )
	public static Boolean spawnHunter = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "allowHoundSpawning", comment = "Dragon Knight hound spawning enabled?" )
	public static Boolean spawnHound = true;

	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "allowPrinceAndPrincessSpawning", comment = "Princess and prince spawning enabled?" )
	public static Boolean spawnPrinceAndPrincess = true;

	@ConfigRange( min = 10, max = 1000 )
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "villagerKillxp", comment = "How many experience points are gained for killing a villager" )
	public static Integer xpGain = 10;

	@ConfigType(EntityType.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "evilDragonStatusGivers", comment = "Entities which give 'Evil dragon' status on death" )
	public static List<String> evilDragonStatusGivers = List.of("minecraft:villager", "dragonsurvival:hunter_hound", "dragonsurvival:knight", "dragonsurvival:shooter", "dragonsurvival:squire", "dragonsurvival:prince", "dragonsurvival:princess", "dragonsurvival:princess_entity");

	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "preserveEvilDragonAfterDeath", comment = "Preserve effect 'Evil dragon' after death?" )
	public static Boolean preserveEvilDragonEffectAfterDeath = false;

	@ConfigRange( min = 6, max = 128 )
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "princessAndHuntersLowerSpawnBound", comment = "Lowest Y value allowed for princess and hunter spawning" )
	public static Integer riderSpawnLowerBound = 32;

	@ConfigRange( min = 64, max = 250 )
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_hunters", key = "princessAndHuntersUpperSpawnBound", comment = "Highest Y value allowed for princess and hunter spawning" )
	public static Integer riderSpawnUpperBound = 80;

	@ConfigRange( min = 10d, max = 80d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "knight"}, key = "knightHealth", comment = "Dragon Knight health" )
	public static Double knightHealth = 40d;

	@ConfigRange( min = 1d, max = 32d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "knight"}, key = "knightDamage", comment = "Dragon Knight base damage" )
	public static Double knightDamage = 12d;

	@ConfigRange( min = 0d, max = 30d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "knight"}, key = "knightArmor", comment = "Dragon Knight armor" )
	public static Double knightArmor = 10d;

	@ConfigRange( min = 0.1d, max = 0.6d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "knight"}, key = "knightSpeed", comment = "Dragon Knight speed" )
	public static Double knightSpeed = 0.35d;

	@ConfigRange( min = 0.0d, max = 1d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "knight"}, key = "knightShieldChance", comment = "Chance of having shield" )
	public static Double knightShieldChance = 0.1d;

	@ConfigRange( min = 8d, max = 40d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "hound"}, key = "houndHealth", comment = "Knight Hound health" )
	public static Double houndHealth = 10d;

	@ConfigRange( min = 1d, max = 10d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "hound"}, key = "houndDamage", comment = "Knight Hound damage" )
	public static Double houndDamage = 2d;

	@ConfigRange( min = 0.1d, max = 0.6d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "hound"}, key = "houndSpeed", comment = "Knight Hound speed" )
	public static Double houndSpeed = 0.45d;

	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "hound"}, key = "houndDoesSlowdown", comment = "Does Knight Hound apply speed slowdown?" )
	public static Boolean houndDoesSlowdown = true;

	@ConfigRange( min = 10d, max = 60d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "hunter"}, key = "hunterHealth", comment = "Dragon Hunter health" )
	public static Double hunterHealth = 24d;

	@ConfigRange( min = 2d, max = 20d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "hunter"}, key = "hunterDamage", comment = "Dragon Hunter damage" )
	public static Double hunterDamage = 5d;

	@ConfigRange( min = 0.1d, max = 0.6d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "hunter"}, key = "hunterSpeed", comment = "Dragon Hunter speed" )
	public static Double hunterSpeed = 0.35d;

	@ConfigRange( min = 0d, max = 20d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "hunter"}, key = "hunterArmor", comment = "Dragon Hunter armor" )
	public static Double hunterArmor = 0d;

	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "hunter"}, key = "hunterThrowsBolas", comment = "Is Dragon hunter able to throw a bolas?" )
	public static Boolean hunterHasBolas = true;

	@ConfigRange( min = 10d, max = 60d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "squire"}, key = "squireHealth", comment = "Dragon Squire health" )
	public static Double squireHealth = 24d;

	@ConfigRange( min = 2d, max = 20d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "squire"}, key = "squireDamage", comment = "Dragon Squire damage" )
	public static Double squireDamage = 6d;

	@ConfigRange( min = 0.1d, max = 0.6d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "squire"}, key = "squireSpeed", comment = "Dragon Squire speed" )
	public static Double squireSpeed = 0.35d;

	@ConfigRange( min = 0d, max = 20d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "squire"}, key = "squireArmor", comment = "Dragon Squire armor" )
	public static Double squireArmor = 2d;


	@ConfigRange( min = 10d, max = 60d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "prince"}, key = "princeHealth", comment = "Prince health" )
	public static Double princeHealth = 20d;

	@ConfigRange( min = 1d, max = 20d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "prince"}, key = "princeDamage", comment = "Prince base damage" )
	public static Double princeDamage = 1d;

	@ConfigRange( min = 0.2d, max = 0.6d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "prince"}, key = "princeSpeed", comment = "Prince speed" )
	public static Double princeSpeed = 0.3d;

	@ConfigRange( min = 0d, max = 20d )
	@ConfigOption( side = ConfigSide.SERVER, category = {"dragon_hunters", "prince"}, key = "princeArmor", comment = "Prince armor" )
	public static Double princeArmor = 6d;

	@ConfigRange( min = 1, max = 60 * 60 )
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_beacons", key = "constantEffect", comment = "Duration of effect given by beacon constantly in seconds" )
	public static Integer secondsOfBeaconEffect = 20;

	@ConfigRange( min = 1, max = 60 * 2 )
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_beacons", key = "temporaryEffect", comment = "Duration of effect given in exchange for experience in minutes" )
	public static Integer minutesOfDragonEffect = 10;

	@ConfigType(MobEffect.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_beacons", key = "peaceBeaconEffects", comment = "Effects of Peace beacon" )
	public static List<String> peaceBeaconEffects = List.of("dragonsurvival:peace", "dragonsurvival:animal_peace");

	@ConfigType(MobEffect.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_beacons", key = "magicBeaconEffects", comment = "Effects of Magic beacon" )
	public static List<String> magicBeaconEffects = List.of("dragonsurvival:magic", "dragonsurvival:predator_anti_spawn");

	@ConfigType(MobEffect.class)
	@ConfigOption( side = ConfigSide.SERVER, category = "dragon_beacons", key = "fireBeaconEffects", comment = "Effects of Fire beacon" )
	public static List<String> fireBeaconEffects = List.of("dragonsurvival:fire", "dragonsurvival:strong_leather");
}