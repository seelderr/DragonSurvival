package by.dragonsurvivalteam.dragonsurvival.config;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.AmbusherEntity;
import by.dragonsurvivalteam.dragonsurvival.config.obj.*;
import by.dragonsurvivalteam.dragonsurvival.config.types.BlockStateConfig;
import by.dragonsurvivalteam.dragonsurvival.config.types.ItemHurtConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;


public class ServerConfig {
    public static final Double DEFAULT_MAX_GROWTH_SIZE = 60.0;

    ServerConfig(ModConfigSpec.Builder builder) {
        ConfigHandler.createConfigEntries(builder, ConfigSide.SERVER);
    }

    @ConfigOption(side = ConfigSide.SERVER, category = "general", key = "forceStateUpdatingOnVaults", comment = "Debug only config. Forces the state updating to resume on vaults always.")
    public static Boolean forceStateUpdatingOnVaults = false;

    @ConfigRange(min = 0, max = 1000)
    @ConfigOption(side = ConfigSide.SERVER, category = "general", key = "altarUsageCooldown", comment = "How long of a cooldown in seconds the altar has after each use.")
    public static Integer altarUsageCooldown = 0;

    @ConfigOption(side = ConfigSide.SERVER, category = "general", key = "altarCraftable", comment = "Whether dragon altars are craftable or not. When disabled you can only use the command or creative mode to become a dragon.")
    public static Boolean altarCraftable = true;

    @ConfigOption(side = ConfigSide.SERVER, category = "general", key = "keepClawItems", comment = "Whether to keep items in the claw slots on death otherwise they will drop on death.")
    public static Boolean keepClawItems = false;

    @ConfigOption(side = ConfigSide.SERVER, category = "general", key = "syncClawRender", comment = "If players are allowed to hide their claws and teeth from other players. If it is important to you to see your opponent's weapon during pvp, set false.")
    public static Boolean syncClawRender = true;

    @ConfigOption(side = ConfigSide.SERVER, category = "general", key = "canMoveInEmote", comment = "If players are allowed to move while performing emotes")
    public static Boolean canMoveInEmote = true;

    @ConfigOption(side = ConfigSide.SERVER, category = "general", key = "canMoveWhileCasting", comment = "If you should be able to move while casting certain skills or if player movement can be prevented.")
    public static Boolean canMoveWhileCasting = false;

    @ConfigOption(side = ConfigSide.SERVER, category = "general", key = "startWithDragonChoice", comment = "Should the dragon altar interface be opened when the player first joins the world?")
    public static Boolean startWithDragonChoice = true;

    @ConfigOption(side = ConfigSide.SERVER, category = "general", key = "allowDragonChoiceFromInventory", comment = "Should the dragon altar be accessible from the vanilla inventory if the player has not made a choice yet?")
    public static Boolean allowDragonChoiceFromInventory = true;

    @ConfigOption( side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "useBlacklistForDestructibleBlocks", comment = "Use a blacklist for destructible blocks instead of a whitelist.")
    public static Boolean useBlacklistForDestructibleBlocks = false;

    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "allowLargeBlockDestruction", comment = "Allow a dragon to instantly destroy certain colliding blocks if they are above a certain size.")
    public static Boolean allowLargeBlockDestruction = false;

    @ConfigRange(min = 0.0, max = 1.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "largeBlockDestructionRemovePercentage", comment = "The percentage of blocks removed instead of destroyed when a dragon instantly destroys blocks. If a block is removed, it doesn't make a sound or particle effect. This is to minimize lag from particle VFX and audio spam.")
    public static Double largeBlockDestructionRemovePercentage = 0.96;

    @ConfigRange(min = 14.0, max = 1000000.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "largeBlockDestructionSize", comment = "The size threshold for a dragon to start instantly destroying blocks. Crouching prevents destruction from occurring.")
    public static Double largeBlockDestructionSize = 120.0;

    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "allowCrushing", comment = "Allow a dragon to crush entities beneath it after being above a certain size. Crouching prevents crushing from occurring.")
    public static Boolean allowCrushing = false;

    @ConfigRange(min = 14.0, max = 1000000.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "crushingSize", comment = "The size at which a dragon can begin to crush entities.")
    public static Double crushingSize = 120.0;

    @ConfigRange(min = 0.0, max = 20.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "crushingDamageScalar", comment = "The amount damage dealt per dragon size when crushing entities.")
    public static Double crushingDamageScalar = 0.05;

    @ConfigRange(min = 0, max = 20)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "crushingTickDelay", comment = "The amount of ticks before entities can be crushed again after they were already crushed.")
    public static Integer crushingTickDelay = 20;

    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "allowLargeScaling", comment = "Allow a dragon's max health, damage, reach, step height, and jump height to continue to scale with growth beyond its normal limits.")
    public static Boolean allowLargeScaling = false;

    @ConfigRange(min = 1, max = 1000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "largeMaxHealthScalar", comment = "The bonus max health given per 60 size once the dragon is above the default growth size of 60 if large scaling is enabled.")
    public static Integer largeMaxHealthScalar = 40;
    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "largeMovementSpeedScalar", comment = "The bonus movement speed multiplier per 60 size when the dragon is at maximum growth size if large scaling is enabled.")
    public static Double largeMovementSpeedScalar = 0.0;

    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "largeDamageBonus", comment = "The bonus damage per 60 size when the dragon is at maximum growth size if large scaling is enabled.")
    public static Double largeDamageBonus = 3.0;

    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "largeReachScalar", comment = "The bonus reach given per 60 size once the dragon is above the default growth size of 60 if large scaling is enabled.")
    public static Double largeReachScalar = 0.5;

    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "largeStepHeightScalar", comment = "The bonus step height given per 60 size once the dragon is above the default growth size of 60 if large scaling is enabled.")
    public static Double largeStepHeightScalar = 1.0;

    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "largeJumpHeightScalar", comment = "The bonus jump height given per 60 size once the dragon is above the default growth size of 60 if large scaling is enabled.")
    public static Double largeJumpHeightScalar = 0.05;

    @ConfigRange(min = 0.0, max = 10.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "big_dragon"}, key = "largeBlockBreakRadiusScalar", comment = "The bonus block break radius given per 60 size once the dragon is above the default growth size of 60 if large scaling is enabled. A block radius of 0 disables this feature. Crouching allows you to mine one block at a time.")
    public static Double largeBlockBreakRadiusScalar = 0.0;

    @ConfigType(Item.class)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth"}, key = "growNewborn", validation = Validation.RESOURCE_LOCATION, comment = "List of items to grow newborn dragon. Format: item/modid:id")
    public static List<String> growNewborn = List.of("dragonsurvival:heart_element", "dragonsurvival:weak_dragon_heart", "dragonsurvival:elder_dragon_heart");

    @ConfigType(Item.class)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth"}, key = "growYoung", validation = Validation.RESOURCE_LOCATION, comment = "List of items to grow young dragon. Format: item/modid:id")
    public static List<String> growYoung = List.of("dragonsurvival:weak_dragon_heart", "dragonsurvival:elder_dragon_heart");

    @ConfigType(Item.class)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth"}, key = "growAdult", validation = Validation.RESOURCE_LOCATION, comment = "List of items to grow adult dragon. Format: item/modid:id")
    public static List<String> growAdult = List.of("dragonsurvival:elder_dragon_heart");

    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "standard_dragon"}, key = "alternateGrowing", comment = "If true, dragons will grow without the use of catalyst grow items. Does not broker the use of items. Just an additional type of growth.")
    public static Boolean alternateGrowing = true;

    @ConfigRange(min = 14.0, max = 1000000.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth"}, key = "maxGrowthSize", comment = "Defines the max size your dragon can grow to. Values that are too high can break your game. It is not advisable to set a number higher than 60.")
    public static Double maxGrowthSize = DEFAULT_MAX_GROWTH_SIZE;

    @ConfigRange(min = 0, max = 1000000.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "standard_dragon"}, key = "moveSpeedNewborn", comment = "The movement speed multiplier for newborn dragons. Default is 1.0.")
    public static Double moveSpeedNewborn = 1.0;

    @ConfigRange(min = 0, max = 1000000.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "standard_dragon"}, key = "moveSpeedYoung", comment = "The movement speed multiplier for young dragons. Default is 1.0.")
    public static Double moveSpeedYoung = 1.0;

    @ConfigRange(min = 0, max = 1000000.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "standard_dragon"}, key = "moveSpeedAdult", comment = "The movement speed multiplier for adult dragons. Default is 1.0.")
    public static Double moveSpeedAdult = 1.0;

    @ConfigRange(min = 0, max = 1000000.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "standard_dragon"}, key = "reachBonus", comment = "The bonus that is given to dragons at 60 size. The bonus gradually scales up to the maximum size. Human players have 1.0x reach and a size 60 dragon will have 1.5x distance with default values.")
    public static Double reachBonus = 0.5;

    @ConfigOption(side = ConfigSide.SERVER, category = {"growth"}, key = "saveGrowthStage", comment = "Should the growth stage of a dragon be saved even when you change. Does not affect the saving progress of magic (use saveAllAbilities). The author does not approve of weredragons, but if you insist...")
    public static Boolean saveGrowthStage = false;

    @ConfigRange(min = 1, max = 100)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "standard_dragon"}, key = "minHealth", comment = "Dragon starting health. Minimum health dragons will start off with.")
    public static Integer minHealth = 14;

    @ConfigRange(min = 1, max = 100)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "standard_dragon"}, key = "maxHealth", comment = "The maximum health when the dragon is fully grown.")
    public static Integer maxHealth = 40;

    @ConfigRange(min = 1, max = 100)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "standard_dragon"}, key = "maxHealthSize", comment = "The size at which the maximum health is reached.")
    public static Integer maxHealthSize = 40;

    @ConfigRange(min = 0.0, max = 1000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "standard_dragon"}, key = "newbornGrowthModifier", comment = "A multiplier to change the growth rate from newborn to young. At 1.0 it takes about 3 hours to turn a newborn dragon into a young dragon.")
    public static Double newbornGrowthModifier = 0.3;

    @ConfigRange(min = 0.0, max = 1000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "standard_dragon"}, key = "youngGrowthModifier", comment = "A multiplier to change the growth rate from young to adult. At 1.0 it takes about 1 day to turn a young dragon into a adult dragon.")
    public static Double youngGrowthModifier = 0.5;

    @ConfigRange(min = 0.0, max = 1000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "standard_dragon"}, key = "adultGrowthModifier", comment = "A multiplier to change the growth rate from adult to a full sized adult. At 1.0 it takes about 3 days to become a dragon of maximum adult size.")
    public static Double adultGrowthModifier = 0.9;

    @ConfigRange(min = 0.0, max = 1000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"growth", "standard_dragon"}, key = "maxGrowthModifier", comment = "A multiplier to change the growth rate from full sized adult to max size. The change in growth after the maximum adult size is measured in months and years.")
    public static Double maxGrowthModifier = 1.0;

    @ConfigRange(min = 0.0, max = 1.0)
    @ConfigOption(side = ConfigSide.SERVER, category = "drops", key = "dragonHeartShardChance", comment = "The chance for dragon heart shards to drop from any mobs with max health between 14-20")
    public static Double dragonHeartShardChance = 0.03;

    @ConfigRange(min = 0.0, max = 1.0)
    @ConfigOption(side = ConfigSide.SERVER, category = "drops", key = "weakDragonHeartChance", comment = "The chance for weak dragon heart to drop from any mobs with max health between 20-50")
    public static Double weakDragonHeartChance = 0.01;

    @ConfigRange(min = 0.0, max = 1.0)
    @ConfigOption(side = ConfigSide.SERVER, category = "drops", key = "elderDragonHeartChance", comment = "The chance for dragon heart to drop from any mobs with max health above 50")
    public static Double elderDragonHeartChance = 0.01;

    @ConfigType(EntityType.class)
    @ConfigOption(side = ConfigSide.SERVER, category = "drops", key = "dragonHeartEntityList", validation = Validation.RESOURCE_LOCATION, comment = "Decide which entities can drop dragon hearts")
    public static List<String> dragonHeartEntityList = List.of();

    @ConfigType(EntityType.class)
    @ConfigOption(side = ConfigSide.SERVER, category = "drops", key = "weakDragonHeartEntityList", validation = Validation.RESOURCE_LOCATION, comment = "Decide which entities can drop weak dragon hearts")
    public static List<String> weakDragonHeartEntityList = List.of();

    @ConfigType(EntityType.class)
    @ConfigOption(side = ConfigSide.SERVER, category = "drops", key = "elderDragonHeartEntityList", validation = Validation.RESOURCE_LOCATION, comment = "Decide which entities can drop elder dragon hearts")
    public static List<String> elderDragonHeartEntityList = List.of();

    @ConfigOption(side = ConfigSide.SERVER, category = "drops", key = "dragonHeartWhiteList", comment = "Should the dragonHeartEntityList be treated as an allowlist rather than a block list?")
    public static Boolean dragonHeartWhiteList = false;

    @ConfigOption(side = ConfigSide.SERVER, category = "drops", key = "weakDragonHeartWhiteList", comment = "Should the weakDragonHeartEntityList be treated as an allowlist rather than a block list?")
    public static Boolean weakDragonHeartWhiteList = false;

    @ConfigOption(side = ConfigSide.SERVER, category = "drops", key = "elderDragonHeartWhiteList", comment = "Should the elderDragonHeartEntityList be treated as an allowlist rather than a block list?")
    public static Boolean elderDragonHeartWhiteList = false;

    @ConfigOption(side = ConfigSide.SERVER, category = "treasure", key = "treasureHealthRegen", comment = "Whether sleeping on treasure will recover health or not. ")
    public static Boolean treasureHealthRegen = true;

    @ConfigRange(min = 1, max = 10000000)
    @ConfigOption(side = ConfigSide.SERVER, category = "treasure", key = "treasureRegenTicks", comment = "The time in ticks it takes to recover 1hp while sleeping on one treasure. A large number of treasures in one place reduces time.")
    public static Integer treasureRegenTicks = 280;

    @ConfigRange(min = 1, max = 10000000)
    @ConfigOption(side = ConfigSide.SERVER, category = "treasure", key = "treasureRegenTicksReduce", comment = "The amount of ticks each additional treasure reduces the regen time by")
    public static Integer treasureRegenTicksReduce = 1;

    @ConfigRange(min = 1, max = 10000000)
    @ConfigOption(side = ConfigSide.SERVER, category = "treasure", key = "maxTreasures", comment = "The max amount of additional treasure that can be used to reduce the regen time")
    public static Integer maxTreasures = 240;

    @ConfigOption(side = ConfigSide.SERVER, category = "source_of_magic", key = "damageWrongSourceOfMagic", comment = "Whether using the the source of magic intended for another dragon type will hurt you.")
    public static Boolean damageWrongSourceOfMagic = true;

    @ConfigRange(min = 1, max = 10000)
    @ConfigOption(side = ConfigSide.SERVER, category = "source_of_magic", key = "elderDragonDustTime", comment = "How long duration of the infinite magic effect using elder dragon dust gives in seconds. Note that you also spend 10 seconds while waiting.")
    public static Integer elderDragonDustTime = 20;

    @ConfigRange(min = 1, max = 10000)
    @ConfigOption(side = ConfigSide.SERVER, category = "source_of_magic", key = "elderDragonBoneTime", comment = "How long duration of the infinite magic effect using elder dragon bone gives in seconds. Note that you also spend 10 seconds while waiting.")
    public static Integer elderDragonBoneTime = 60;

    @ConfigRange(min = 1, max = 10000)
    @ConfigOption(side = ConfigSide.SERVER, category = "source_of_magic", key = "weakHeartShardTime", comment = "How long duration of the infinite magic effect using weak heart shard gives in seconds. Note that you also spend 10 seconds while waiting.")
    public static Integer weakHeartShardTime = 110;

    @ConfigRange(min = 1, max = 10000)
    @ConfigOption(side = ConfigSide.SERVER, category = "source_of_magic", key = "weakDragonHeartTime", comment = "How long duration of the infinite magic effect using weak dragon heart gives in seconds. Note that you also spend 10 seconds while waiting.")
    public static Integer weakDragonHeartTime = 310;

    @ConfigRange(min = 1, max = 10000)
    @ConfigOption(side = ConfigSide.SERVER, category = "source_of_magic", key = "elderDragonHeartTime", comment = "How long duration of the infinite magic effect using elder dragon heart gives in seconds. Note that you also spend 10 seconds while waiting.")
    public static Integer elderDragonHeartTime = 1010;

    @ConfigOption(side = ConfigSide.SERVER, category = "general", key = "disableDragonSuffocation", comment = "Should suffocation damage be disabled for dragon players?")
    public static Boolean disableDragonSuffocation = true;

    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "healthAdjustments", comment = "Apply a health modifier for dragons. The older the dragon, the more health it has.")
    public static Boolean healthAdjustments = true;

    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "bonusesEnabled", comment = "Set too false to toggle off all dragon bonuses and play as human.")
    public static Boolean bonusesEnabled = true;

    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "attackDamage", comment = "Apply an attack damage modifier for dragons.")
    public static Boolean attackDamage = true;

    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "newbornBonusDamage", comment = "Attack modifier for newborn dragons.")
    public static Double newbornBonusDamage = 1.0;

    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "youngAttackMod", comment = "Attack modifier for young dragons.")
    public static Double youngBonusDamage = 2.0;

    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "adultAttackMod", comment = "Attack modifier for adult dragons.")
    public static Double adultBonusDamage = 3.0;

    @ConfigRange(min = 0.0, max = 0.9)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "newbornJump", comment = "Jumping height bonus for a newborn dragon.")
    public static Double newbornJump = 0.025;

    @ConfigRange(min = 0.0, max = 0.9)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "youngJump", comment = "Jumping height bonus for a young dragon.")
    public static Double youngJump = 0.05;

    @ConfigRange(min = 0.0, max = 0.9)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "adultJump", comment = "Jumping height bonus for an adult dragon.")
    public static Double adultJump = 0.1;

    @ConfigRange(min = 0.0, max = 10.0)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "newbornStepHeight", comment = "Step height for a newborn dragon. Default is 1 block.")
    public static Double newbornStepHeight = 0.0;

    @ConfigRange(min = 0.0, max = 10.0)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "youngStepHeight", comment = "Step height for a young dragon. Default is 1.25 block.")
    public static Double youngStepHeight = 0.25;

    @ConfigRange(min = 0.0, max = 10.0)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "adultStepHeight", comment = "Step height for a adult dragon. Default is 1.5 block.")
    public static Double adultStepHeight = 0.5;


    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "clawsAreTools", comment = "Whether dragon claws function as tools.")
    public static Boolean clawsAreTools = true;

    @ConfigRange(min = 0, max = 100)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "baseHarvestLevel", comment = "The harvest level to apply when dragons breaks a block, regardless of dragon/tool type.")
    public static Integer baseHarvestLevel = 0;

    @ConfigRange(min = 0, max = 100)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "bonusHarvestLevel", comment = "The harvest level to apply to a dragons specific tool type once unlocked.")
    public static Integer bonusHarvestLevel = 1;

    @ConfigRange(min = 1, max = 10)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "bonusBreakSpeed", comment = "Bonus break speed against blocks which are effective for the dragon type (break speed * bonus) - only applied if the bonus is unlocked")
    public static Float bonusBreakSpeed = 2f;

    @ConfigRange(min = 1, max = 10)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "bonusBreakSpeedAdult", comment = "Bonus break speed against blocks which are effective for the dragon type (break speed * bonus) - only applied if the bonus is unlocked and the dragon is fully grown")
    public static Float bonusBreakSpeedAdult = 2.5f;

    @ConfigRange(min = 1, max = 10)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "baseBreakSpeedAdult", comment = "Bonus break speed against all blocks (break speed * bonus) - only applied if the dragon is fully grown (unlocked bonus value will overwrite this one for effective blocks)")
    public static Float baseBreakSpeedAdult = 1.5f;

    @ConfigRange(min = 1, max = 10)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "bonusBreakSpeedReduction", comment = "Value the bonus will be divided by if an effective claw tool is present for the block")
    public static Float bonusBreakSpeedReduction = 2f;

    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "bonusUnlockedAt", comment = "The stage that dragons unlock the bonus harvest level.")
    public static DragonLevel bonusUnlockedAt = DragonLevel.YOUNG;

    @ConfigRange(min = 0, max = 100)
    @ConfigOption(side = ConfigSide.SERVER, category = "bonuses", key = "speedupEffectLevel", comment = "The speed effect level for dragon block-specific speedups. Set to 0 to disable.")
    public static Integer speedupEffectLevel = 2;


    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "cave_dragon"}, key = "caveFireImmunity", comment = "Whether cave dragons are immune to fire damage types.")
    public static Boolean caveFireImmunity = true;

    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "cave_dragon"}, key = "caveLavaSwimming", comment = "Set to false to disable cave dragon fast lava swimming.")
    public static Boolean caveLavaSwimming = true;

    @ConfigRange(min = 0, max = 100000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "cave_dragon"}, key = "caveLavaSwimmingTicks", comment = "The maximum number of ticks a cave dragon can swim in lava. Set to 0 to allow unlimited air while under lava.")
    public static Integer caveLavaSwimmingTicks = 3600;


    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "forest_dragon"}, key = "forestFallReduction", comment = "How many blocks of fall damage is mitigated for forest dragons. Set to 0.0 to disable.")
    public static Double forestFallReduction = 5.0;

    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "forest_dragon"}, key = "forestBushImmunity", comment = "Whether forest dragons are immune to Sweet Berry Bush damage.")
    public static Boolean forestBushImmunity = true;

    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "forest_dragon"}, key = "forestCactiImmunity", comment = "Whether forest dragons are immune to Cactus damage.")
    public static Boolean forestCactiImmunity = true;


    @ConfigOption(side = ConfigSide.SERVER, category = {"bonuses", "sea_dragon"}, key = "seaSwimmingBonuses", comment = "Whether sea dragons gain bonus swim speed and unlimited air.")
    public static Boolean seaSwimmingBonuses = true;

    //Dragon Penalties
    @ConfigOption( side = ConfigSide.SERVER, category = "penalties", key = "penaltiesEnabled", comment = "Set to false to toggle off all dragon penalties." )
    public static Boolean penaltiesEnabled = true;

    @ConfigOption(side = ConfigSide.SERVER, category = "penalties", key = "dragonsAreScary", comment = "Whether dragons are scary for animals or not.")
    public static Boolean dragonsAreScary = true;

    @ConfigOption(side = ConfigSide.SERVER, category = "penalties", key = "limitedRiding", comment = "Should dragons be limited by which entities they can ride")
    public static Boolean limitedRiding = true;

    @ConfigType(Item.class) // FIXME :: handle with tag (and keep this config for regex-only)
    @ConfigOption( side = ConfigSide.SERVER, category = "penalties", key = "blacklistedItems", validation = Validation.RESOURCE_LOCATION_REGEX, comment = "List of items that are disallowed to be used by dragons. Format: item/modid:id" )
    public static List<String> blacklistedItems = List.of(
            "c:tools/shield",
            "c:tools/bow",
            "c:tools/crossbow",
            "minecraft:trident",
            "minecraft:elytra",
            "quark:flamerang",
            "quark:pickarang",
            "mowziesmobs:wrought_axe",
            "nethers_exoticism:rambutan_shield",
            "aquaculture:neptunium_bow",
            "endermanoverhaul:corrupted_shield",
            "upgradednetherite:echo_upgraded_netherite_shield",
            "upgradednetherite:corrupt_upgraded_netherite_shield",
            "upgradednetherite:feather_upgraded_netherite_shield",
            "upgradednetherite:phantom_upgraded_netherite_shield",
            "upgradednetherite:poison_upgraded_netherite_shield",
            "upgradednetherite:wither_upgraded_netherite_shield",
            "upgradednetherite:water_upgraded_netherite_shield",
            "upgradednetherite:ender_upgraded_netherite_shield",
            "magistuarmory:bronze_ellipticalshield",
            "magistuarmory:wood_roundshield",
            "magistuarmory:stone_roundshield",
            "magistuarmory:iron_roundshield",
            "magistuarmory:gold_roundshield",
            "magistuarmory:diamond_roundshield",
            "magistuarmory:netherite_roundshield",
            "magistuarmory:copper_roundshield",
            "magistuarmory:steel_roundshield",
            "magistuarmory:silver_roundshield",
            "magistuarmory:tin_roundshield",
            "magistuarmory:bronze_roundshield",
            "magistuarmory:wood_kiteshield",
            "magistuarmory:stone_kiteshield",
            "magistuarmory:iron_kiteshield",
            "magistuarmory:gold_kiteshield",
            "magistuarmory:diamond_kiteshield",
            "magistuarmory:netherite_kiteshield",
            "magistuarmory:steel_kiteshield",
            "magistuarmory:copper_kiteshield",
            "magistuarmory:silver_kiteshield",
            "magistuarmory:tin_kiteshield",
            "magistuarmory:bronze_kiteshield",
            "unusualend:blob_shield",
            "upgradednetherite:netherite_shield",
            "upgradednetherite:gold_upgraded_netherite_shield",
            "upgradednetherite:fire_upgraded_netherite_shield",
            "magistuarmory:stone_ellipticalshield",
            "magistuarmory:iron_ellipticalshield",
            "magistuarmory:diamond_ellipticalshield",
            "magistuarmory:gold_ellipticalshield",
            "infernalexp:glowsilk_bow",
            "magistuarmory:tin_ellipticalshield",
            "upgradednetherite:gold_upgraded_netherite_bow",
            "upgradednetherite:fire_upgraded_netherite_bow",
            "upgradednetherite:ender_upgraded_netherite_bow",
            "magistuarmory:silver_ellipticalshield",
            "magistuarmory:steel_ellipticalshield",
            "magistuarmory:copper_ellipticalshield",
            "magistuarmory:netherite_ellipticalshield",
            "upgradednetherite:netherite_crossbow",
            "upgradednetherite:gold_upgraded_netherite_crossbow",
            "upgradednetherite:fire_upgraded_netherite_crossbow",
            "upgradednetherite:ender_upgraded_netherite_crossbow",
            "upgradednetherite:water_upgraded_netherite_crossbow",
            "upgradednetherite:wither_upgraded_netherite_crossbow",
            "upgradednetherite:poison_upgraded_netherite_crossbow",
            "upgradednetherite:phantom_upgraded_netherite_crossbow",
            "upgradednetherite:feather_upgraded_netherite_crossbow",
            "upgradednetherite:corrupt_upgraded_netherite_crossbow",
            "upgradednetherite:echo_upgraded_netherite_crossbow",
            "magistuarmory:wood_heatershield", "magistuarmory:stone_heatershield",
            "magistuarmory:iron_heatershield", "magistuarmory:gold_heatershield",
            "magistuarmory:diamond_heatershield", "magistuarmory:netherite_heatershield",
            "magistuarmory:copper_heatershield", "magistuarmory:steel_heatershield",
            "magistuarmory:silver_heatershield", "magistuarmory:tin_heatershield",
            "magistuarmory:bronze_heatershield", "magistuarmory:wood_ellipticalshield",
            "magistuarmory:silver_target", "magistuarmory:steel_target",
            "magistuarmory:copper_target", "magistuarmory:netherite_target",
            "magistuarmory:diamond_target",
            "magistuarmory:gold_target",
            "magistuarmory:iron_target",
            "magistuarmory:stone_target",
            "aquaculture:neptunium_bow",
            "magistuarmory:gold_pavese",
            "magistuarmory:iron_pavese",
            "magistuarmory:wood_pavese",
            "magistuarmory:tin_tartsche",
            "magistuarmory:silver_tartsche",
            "magistuarmory:steel_tartsche",
            "magistuarmory:copper_tartsche",
            "magistuarmory:diamond_tartsche",
            "magistuarmory:iron_tartsche",
            "magistuarmory:stone_tartsche",
            "magistuarmory:wood_tartsche",
            "magistuarmory:bronze_rondache",
            "magistuarmory:tin_rondache",
            "magistuarmory:netherite_rondache",
            "magistuarmory:diamond_rondache",
            "magistuarmory:gold_rondache",
            "magistuarmory:iron_rondache",
            "magistuarmory:wood_rondache",
            "magistuarmory:tin_buckler",
            "magistuarmory:steel_buckler",
            "magistuarmory:copper_buckler",
            "magistuarmory:netherite_buckler",
            "magistuarmory:diamond_buckler",
            "magistuarmory:gold_buckler",
            "magistuarmory:iron_buckler",
            "magistuarmory:stone_buckler",
            "magistuarmory:tin_target",
            "magistuarmory:diamond_pavese",
            "magistuarmory:netherite_pavese",
            "magistuarmory:copper_pavese",
            "magistuarmory:steel_pavese",
            "magistuarmory:tin_pavese",
            "endermanoverhaul:corrupted_shield",
            "upgradednetherite:wither_upgraded_netherite_bow",
            "upgradednetherite:water_upgraded_netherite_bow",
            "upgradednetherite:poison_upgraded_netherite_bow",
            "upgradednetherite:phantom_upgraded_netherite_bow",
            "upgradednetherite:feather_upgraded_netherite_bow",
            "upgradednetherite:corrupt_upgraded_netherite_bow",
            "upgradednetherite:echo_upgraded_netherite_bow",
            "cataclysm:ignitium_elytra_chestplate",
            "revised_phantoms:phantom_wings_chestplate",
            "deeperdarker:soul_elytra",
            "born_in_chaos_v1:staffof_magic_arrows",
            "magistuarmory:heavy_crossbow",
            "spartanweaponry:boomerang_.*",
            "spartanshields:shield_.*",
            "spartanweaponry:heavy_crossbow_.*",
            "spartanweaponry:longbow_.*"
    );

    // Cave Dragon Penalties
    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"penalties", "cave_dragon"}, key = "caveWaterDamage", comment = "The amount of damage taken per water damage tick (once every 10 ticks). Set to 0.0 to disable water damage.")
    public static Double caveWaterDamage = 1.0;

    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"penalties", "cave_dragon"}, key = "caveRainDamage", comment = "The amount of damage taken per rain damage tick (once every 40 ticks). Set to 0.0 to disable rain damage.")
    public static Double caveRainDamage = 1.0;

    @ConfigRange(min = 0.0, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"penalties", "cave_dragon"}, key = "caveSplashDamage", comment = "The amount of damage taken when hit with a snowball or a water bottle. Set to 0.0 to disable splash damage.")
    public static Double caveSplashDamage = 2.0;

    // Forest Dragon Penalties
    @ConfigRange(min = 0, max = 10000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"penalties", "forest_dragon"}, key = "forestStressTicks", comment = "The number of ticks in darkness before the forest dragon gets Stress effect. Set to 0 to disable to stress effect.")
    public static Integer forestStressTicks = 100;

    @ConfigRange(min = 2, max = 100000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"penalties", "forest_dragon"}, key = "forestStressEffectDuration", comment = "The number of seconds the stress effect lasts for.")
    public static Integer forestStressEffectDuration = 10;

    @ConfigRange(min = 0.1, max = 4.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"penalties", "forest_dragon"}, key = "forestStressExhaustion", comment = "The amount of exhaustion applied per 10 ticks during the stress effect.")
    public static Double forestStressExhaustion = 1.0;

    // Sea Dragon Penalties
    @ConfigRange(min = 0, max = 100000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"penalties", "sea_dragon"}, key = "seaTicksBasedOnTemperature", comment = "The number of ticks out of water before the sea dragon will start taking dehydration damage. Set to 0 to disable. Note: This value can stack up to double while dehydrated.")
    public static Integer seaTicksWithoutWater = 1000;

    @ConfigOption(side = ConfigSide.SERVER, category = {"penalties", "sea_dragon"}, key = "waterConsumptionDependsOnTemperature", comment = "Whether the sea dragon should lose more water in warmer biomes and less during the night.")
    public static Boolean seaTicksBasedOnTemperature = true;

    @ConfigRange(min = 0.5, max = 100.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"penalties", "sea_dragon"}, key = "seaDehydrationDamage", comment = "The amount of damage taken per tick while dehydrated (once every 40 ticks unless fully dehydrated, then once every 20 ticks).")
    public static Double seaDehydrationDamage = 1.0;

    @ConfigOption(side = ConfigSide.SERVER, category = {"penalties", "sea_dragon"}, key = "seaAllowWaterBottles", comment = "Set to false to disable sea dragons using vanilla water bottles to avoid dehydration.")
    public static Boolean seaAllowWaterBottles = true;

    @ConfigRange(min = 0, max = 100000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"penalties", "sea_dragon"}, key = "seaTicksWithoutWaterRestored", comment = "How many ticks do water restoration items restore when used. Set to 0 to disable.")
    public static Integer seaTicksWithoutWaterRestored = 5000;


    // Ore Loot
    @ConfigRange(min = 0.0, max = 1.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"drops", "ore"}, key = "humanOreDustChance", comment = "The odds of dust dropping when a human harvests an ore.")
    public static Double humanOreDustChance = 0.1;

    @ConfigRange(min = 0.0, max = 1.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"drops", "ore"}, key = "dragonOreDustChance", comment = "The odds of dust dropping when a dragon harvests an ore.")
    public static Double dragonOreDustChance = 0.2;

    @ConfigRange(min = 0.0, max = 1.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"drops", "ore"}, key = "humanOreBoneChance", comment = "The odds of a bone dropping when a human harvests an ore.")
    public static Double humanOreBoneChance = 0.0;

    @ConfigRange(min = 0.0, max = 1.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"drops", "ore"}, key = "dragonOreBoneChance", comment = "The odds of a bone dropping when a dragon harvests an ore.")
    public static Double dragonOreBoneChance = 0.01;

    @ConfigOption(side = ConfigSide.SERVER, category = {"food", "cave_dragon"}, key = "caveDragonHurtfulItems", validation = Validation.RESOURCE_LOCATION_NUMBER, comment = "Items which will cause damage to cave dragons when consumed - Formatting: namespace:path:damage (prefix namespace with # for tags)")
    public static List<ItemHurtConfig> caveDragonHurtfulItems = List.of(
            ItemHurtConfig.of("minecraft:potion:2"),
            ItemHurtConfig.of("minecraft:water_bottle:2"),
            ItemHurtConfig.of("minecraft:milk_bucket:2")
    );

    @ConfigOption(side = ConfigSide.SERVER, category = {"food", "sea_dragon"}, key = "seaDragonHurtfulItems", validation = Validation.RESOURCE_LOCATION_NUMBER, comment = "Items which will cause damage to sea dragons when consumed - Formatting: namespace:path:damage (prefix namespace with # for tags)")
    public static List<ItemHurtConfig> seaDragonHurtfulItems = List.of();

    @ConfigOption(side = ConfigSide.SERVER, category = {"food", "forest_dragon"}, key = "forestDragonHurtfulItems", validation = Validation.RESOURCE_LOCATION_NUMBER, comment = "Items which will cause damage to forest dragons when consumed - Formatting: namespace:path:damage (prefix namespace with # for tags)")
    public static List<ItemHurtConfig> forestDragonHurtfulItems = List.of();

    @ConfigRange(min = 0, max = 10000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"food", "cave_dragon"}, key = "chargedSoupBuffDuration", comment = "How long in seconds should the cave fire effect from charged soup last. Set to 0 to disable.")
    public static Integer chargedSoupBuffDuration = 300;


    @ConfigOption(side = ConfigSide.SERVER, category = {"magic"}, key = "cave_conditional_mana_blocks", comment = "Blocks that restore mana or cave dragons when under certain conditions (block states) - Formatting: namespace:path:key=value,key=value (prefix namespace with # for tags)")
    public static List<BlockStateConfig> caveConditionalManaBlocks = List.of(
            BlockStateConfig.of("#minecraft:campfires:lit=true"),
            BlockStateConfig.of("#c:player_workstations/furnaces:lit=true"),
            BlockStateConfig.of("minecraft:smoker:lit=true"),
            BlockStateConfig.of("minecraft:blast_furnace:lit=true")
    );

    @ConfigOption(side = ConfigSide.SERVER, category = {"magic"}, key = "sea_conditional_mana_blocks", comment = "Blocks that restore mana for sea dragons when under certain conditions (block states) - Formatting: namespace:path:key=value,key=value (prefix namespace with # for tags)")
    public static List<BlockStateConfig> seaConditionalManaBlocks = List.of();

    @ConfigOption(side = ConfigSide.SERVER, category = {"magic"}, key = "forest_conditional_mana_blocks", comment = "Blocks that restore mana for forest dragons when under certain conditions (block states) - Formatting: namespace:path:key=value,key=value (prefix namespace with # for tags)")
    public static List<BlockStateConfig> forestConditionalManaBlocks = List.of();


    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities"}, key = "dragonAbilities", comment = "Whether dragon abilities should be enabled")
    public static Boolean dragonAbilities = true;

    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon"}, key = "caveDragonAbilities", comment = "Whether cave dragon abilities should be enabled")
    public static Boolean caveDragonAbilities = true;

    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "forest_dragon"}, key = "forestDragonAbilities", comment = "Whether forest dragon abilities should be enabled")
    public static Boolean forestDragonAbilities = true;

    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities", "sea_dragon"}, key = "seaDragonAbilities", comment = "Whether sea dragon abilities should be enabled")
    public static Boolean seaDragonAbilities = true;


    @ConfigOption(side = ConfigSide.SERVER, category = "magic", key = "noEXPRequirements", comment = "Disable the exp requirements for leveling up active skills")
    public static Boolean noEXPRequirements = false;

    @ConfigOption(side = ConfigSide.SERVER, category = "magic", key = "consumeEXPAsMana", comment = "Whether to use exp instead of mana if mana is empty")
    public static Boolean consumeEXPAsMana = true;

    @ConfigRange(min = 0, max = 100)
    @ConfigOption(side = ConfigSide.SERVER, category = "magic", key = "initialPassiveCost", comment = "The initial exp cost for leveling passive skills.")
    public static Integer initialPassiveCost = 1;

    @ConfigRange(min = 0, max = 100)
    @ConfigOption(side = ConfigSide.SERVER, category = "magic", key = "passiveScalingCost", comment = "The multiplier that is used to increase the passive skill costs per level")
    public static Double passiveScalingCost = 4.0;


    @ConfigRange(min = 1, max = 1000)
    @ConfigOption(side = ConfigSide.SERVER, category = "magic", key = "favorableManaRegen", comment = "How fast in ticks should mana be recovered in favorable conditions")
    public static Integer favorableManaTicks = 1;

    @ConfigRange(min = 1, max = 1000)
    @ConfigOption(side = ConfigSide.SERVER, category = "magic", key = "normalManaRegen", comment = "How fast in ticks should mana be recovered in normal conditions")
    public static Integer normalManaTicks = 10;


    @ConfigOption(side = ConfigSide.SERVER, category = {"magic", "abilities"}, key = "saveAllAbilities", comment = "Whether to save passives skills when changing dragon type")
    public static Boolean saveAllAbilities = false;

    @ConfigOption(side = ConfigSide.SERVER, category = "general", key = "endVoidTeleport", comment = "Should the player be teleported to the overworld when they fall in the end?")
    public static Boolean endVoidTeleport = true;

    @ConfigRange(min = 1, max = 1000)
    @ConfigOption(side = ConfigSide.SERVER, category = "dragon_hunters", key = "pillageXPGain", comment = "How many experience points does the villager gain each time you steal from him?")
    public static Integer pillageXPGain = 4;

    @ConfigType(EntityType.class)
    @ConfigOption(side = ConfigSide.SERVER, category = "dragon_hunters", key = "hunterOmenStatusGivers", validation = Validation.RESOURCE_LOCATION, comment = "Entities which give 'Hunter Omen' status on death in addition to villagers.")
    public static List<String> hunterOmenStatusGivers = List.of("minecraft:iron_golem");

    @ConfigRange(min = 10d, max = 80d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "knight"}, key = "knightHealth", comment = "Dragon Knight health")
    public static Double knightHealth = 40d;

    @ConfigRange(min = 1d, max = 32d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "knight"}, key = "knightDamage", comment = "Dragon Knight base damage")
    public static Double knightDamage = 12d;

    @ConfigRange(min = 0d, max = 30d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "knight"}, key = "knightArmor", comment = "Dragon Knight armor")
    public static Double knightArmor = 10d;

    @ConfigRange(min = 0.1d, max = 0.6d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "knight"}, key = "knightSpeed", comment = "Dragon Knight speed")
    public static Double knightSpeed = 0.3d;

    @ConfigRange(min = 0.0d, max = 1d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "knight"}, key = "knightShieldChance", comment = "Chance of having shield")
    public static Double knightShieldChance = 0.1d;

    @ConfigRange(min = 60, max = 1200000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "ambusher"}, key = "ambusherSpawnAttemptFrequency", comment = "How often the ambusher attempts to spawn.")
    public static int ambusherSpawnAttemptFrequency = 12000;

    @ConfigRange(min = 0.0, max = 1.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "ambusher"}, key = "ambusherSpawnChance", comment = "Chance of the ambusher spawning when the spawn attempt is made.")
    public static double ambusherSpawnChance = 0.2;

    @ConfigRange(min = 10d, max = 80d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "ambusher"}, key = "ambusherHealth", comment = "Dragon Ambusher health")
    public static Double ambusherHealth = 40d;

    @ConfigRange(min = 1, max = 32)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "ambusher"}, key = "ambusherDamage", comment = "Dragon Ambusher base damage")
    public static Integer ambusherDamage = 12;

    @ConfigRange(min = 0d, max = 30d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "ambusher"}, key = "ambusherArmor", comment = "Dragon Ambusher armor")
    public static Double ambusherArmor = 10d;

    @ConfigRange(min = 0.1d, max = 0.6d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "ambusher"}, key = "ambusherSpeed", comment = "Dragon Ambusher speed")
    public static Double ambusherSpeed = 0.3d;

    @ConfigRange(min = AmbusherEntity.CROSSBOW_SHOOT_AND_RELOAD_TIME + 5, max = 1000)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "ambusher"}, key = "ambusherAttackInterval", comment = "How often the ambusher attacks with their crossbow")
    public static Integer ambusherAttackInterval = 65;

    @ConfigRange(min = 0, max = 10)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "ambusher"}, key = "ambusherSpearmanReinforcementCount", comment = "How many spearman reinforce the ambusher when he is attacked")
    public static Integer ambusherSpearmanReinforcementCount = 4;

    @ConfigRange(min = 0, max = 10)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "ambusher"}, key = "ambusherHoundReinforcementCount", comment = "How many hounds reinforce the ambusher when he is attacked")
    public static Integer ambusherHoundReinforcementCount = 2;

    @ConfigRange(min = 8d, max = 40d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "hound"}, key = "houndHealth", comment = "Knight Hound health")
    public static Double houndHealth = 10d;

    @ConfigRange(min = 1d, max = 10d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "hound"}, key = "houndDamage", comment = "Knight Hound damage")
    public static Double houndDamage = 2d;

    @ConfigRange(min = 0.1d, max = 0.6d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "hound"}, key = "houndSpeed", comment = "Knight Hound speed")
    public static Double houndSpeed = 0.45d;

    @ConfigRange(min = 0.1d, max = 1.0d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "hound"}, key = "houndSlowdownChance", comment = "Probably of the hound applying slowdown with their attack")
    public static Double houndSlowdownChance = 0.5d;

    @ConfigRange(min = 8d, max = 40d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "griffin"}, key = "griffinHealth", comment = "Griffin health")
    public static Double griffinHealth = 10d;

    @ConfigRange(min = 1d, max = 10d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "griffin"}, key = "griffinDamage", comment = "Griffin damage")
    public static Double griffinDamage = 2d;

    @ConfigRange(min = 0.1d, max = 1.0d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "griffin"}, key = "griffinSpeed", comment = "Griffin speed")
    public static Double griffinSpeed = 0.2d;

    @ConfigRange(min = 0.1d, max = 2.0d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "griffin"}, key = "griffinRange", comment = "Griffin attack range (how much the attack hitbox is expanded in all directions)")
    public static Double griffinRange = 0.9d;


    @ConfigRange(min = 1.0, max = 60.0)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters"}, key = "hunterTrappedDebuffDuration", comment = "How long does the trapped debuff last?")
    public static Double hunterTrappedDebuffDuration = 5.0;

    @ConfigRange(min = 10d, max = 60d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "spearman"}, key = "spearmanHealth", comment = "Dragon Spearman health")
    public static Double spearmanHealth = 24d;

    @ConfigRange(min = 2d, max = 20d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "spearman"}, key = "spearmanDamage", comment = "Dragon Spearman damage")
    public static Double spearmanDamage = 6d;

    @ConfigRange(min = 0.1d, max = 0.6d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "spearman"}, key = "spearmanSpeed", comment = "Dragon Spearman speed")
    public static Double spearmanSpeed = 0.35d;

    @ConfigRange(min = 0d, max = 20d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "spearman"}, key = "spearmanArmor", comment = "Dragon Spearman armor")
    public static Double spearmanArmor = 2d;

    @ConfigRange(min = 0d, max = 20d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "spearman"}, key = "spearmanBonusHorizontalReach", comment = "Additional horizontal reach that the spearman gets over normal mobs.")
    public static Double spearmanBonusHorizontalReach = 0.5d;

    @ConfigRange(min = 0d, max = 20d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "spearman"}, key = "spearmanBonusVerticalReach", comment = "Additional vertical reach that the spearman gets over normal mobs.")
    public static Double spearmanBonusVerticalReach = 2.5d;

    @ConfigRange(min = 0.1d, max = 0.6d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "leader"}, key = "leaderSpeed", comment = "Dragon Leader speed")
    public static Double leaderSpeed = 0.35d;

    @ConfigRange(min = 10d, max = 60d)
    @ConfigOption(side = ConfigSide.SERVER, category = {"dragon_hunters", "leader"}, key = "leaderHealth", comment = "Dragon Leader health")
    public static Double leaderHealth = 24d;

    @ConfigRange(min = 1, max = 60 * 60)
    @ConfigOption(side = ConfigSide.SERVER, category = "dragon_beacons", key = "secondsOfBeaconEffect", comment = "Duration of effect given by beacon constantly in seconds")
    public static Integer secondsOfBeaconEffect = 20;

    @ConfigRange(min = 1, max = 60 * 2)
    @ConfigOption(side = ConfigSide.SERVER, category = "dragon_beacons", key = "minutesOfDragonEffect", comment = "Duration of effect given in exchange for experience in minutes")
    public static Integer minutesOfDragonEffect = 10;
    //Please help me to fix this config. It doesn't work for buying in exchange for experience in beacon. My fix doesn't help, so I think the problem is deeper than I can solve it.

    @ConfigType(MobEffect.class)
    @ConfigOption( side = ConfigSide.SERVER, category = "dragon_beacons", key = "peaceBeaconEffects", validation = Validation.RESOURCE_LOCATION, comment = "Effects of Peace beacon" )
    public static List<String> peaceBeaconEffects = List.of("dragonsurvival:peace", "dragonsurvival:animal_peace");

    @ConfigType(MobEffect.class)
    @ConfigOption( side = ConfigSide.SERVER, category = "dragon_beacons", key = "magicBeaconEffects", validation = Validation.RESOURCE_LOCATION, comment = "Effects of Magic beacon" )
    public static List<String> magicBeaconEffects = List.of("dragonsurvival:magic", "minecraft:haste");

    @ConfigType(MobEffect.class)
    @ConfigOption( side = ConfigSide.SERVER, category = "dragon_beacons", key = "fireBeaconEffects", validation = Validation.RESOURCE_LOCATION, comment = "Effects of Fire beacon" )
    public static List<String> fireBeaconEffects = List.of("dragonsurvival:fire", "dragonsurvival:strong_leather");

    @ConfigRange(min = 0, max = 60)
    @ConfigOption(side = ConfigSide.SERVER, category = "wings", key = "levitationAfterEffect", comment = "For how many seconds wings are disabled after the levitation effect has ended")
    public static Integer levitationAfterEffect = 3;
}