package by.dragonsurvivalteam.dragonsurvival.registry.datagen.advancements;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.criteria.*;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.BurnAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.CaveAthleticsAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.CaveMagicAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.ContrastShowerAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.CliffhangerAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.ForestAthleticsAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.ForestMagicAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.passive.LightInDarknessAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive.SeaAthleticsAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive.SeaMagicAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive.SpectralImpactAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.passive.WaterAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSItemTags;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType") // ignore
public class DSAdvancements implements AdvancementProvider.AdvancementGenerator {
    @Translation(type = Translation.Type.MISC, comments = "Dragon Survival")
    private static final String ROOT = Translation.Type.ADVANCEMENT.wrap("root");

    @Translation(type = Translation.Type.MISC, comments = "Elder Dragon Lore")
    private static final String COLLECT_DUST = Translation.Type.ADVANCEMENT.wrap("collect_dust");

    @Translation(type = Translation.Type.MISC, comments = "Collect elder dragon dust by mining ore.")
    private static final String COLLECT_DUST_DESCRIPTION = Translation.Type.ADVANCEMENT_DESCRIPTION.wrap("collect_dust");

    @Translation(type = Translation.Type.MISC, comments = "Be Yourself")
    private static final String BE_DRAGON = Translation.Type.ADVANCEMENT.wrap("be_dragon");

    @Translation(type = Translation.Type.MISC, comments = "Unless you can be a dragon.")
    private static final String BE_DRAGON_DESCRIPTION = Translation.Type.ADVANCEMENT_DESCRIPTION.wrap("be_dragon");

    @Translation(type = Translation.Type.MISC, comments = "Growth Process")
    private static final String BE_YOUNG_DRAGON = Translation.Type.ADVANCEMENT.wrap("be_young_dragon");

    @Translation(type = Translation.Type.MISC, comments = "Become a young dragon using time or heart parts.")
    private static final String BE_YOUNG_DRAGON_DESCRIPTION = Translation.Type.ADVANCEMENT_DESCRIPTION.wrap("be_young_dragon");

    @Translation(type = Translation.Type.MISC, comments = "Older And Stronger")
    private static final String BE_ADULT_DRAGON = Translation.Type.ADVANCEMENT.wrap("be_adult_dragon");

    @Translation(type = Translation.Type.MISC, comments = "Grow your dragon into an adult! Time to look for a player-rider.")
    private static final String BE_ADULT_DRAGON_DESCRIPTION = Translation.Type.ADVANCEMENT_DESCRIPTION.wrap("be_adult_dragon");

    private HolderLookup.Provider registries;
    private Consumer<AdvancementHolder> saver;
    private ExistingFileHelper helper;

    @Override
    public void generate(@NotNull HolderLookup.Provider registries, @NotNull Consumer<AdvancementHolder> saver, @NotNull ExistingFileHelper helper) {
        this.registries = registries;
        this.saver = saver;
        this.helper = helper;

        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        DSItems.ELDER_DRAGON_BONE.value(),
                        Component.translatable(ROOT),
                        Component.empty(),
                        DragonSurvival.res("textures/block/stone_dragon_door_top.png"),
                        AdvancementType.GOAL,
                        false,
                        false,
                        false
                )
                .addCriterion("root", PlayerTrigger.TriggerInstance.tick())
                .save(saver, DragonSurvival.res("root"), helper);

        // --- Parent: root --- //

        AdvancementHolder beDragon = create(root, "be_dragon", DSItems.STAR_BONE.value(), BE_DRAGON, BE_DRAGON_DESCRIPTION, beDragon(), 12);
        buildBeDragonChildren(beDragon);

        AdvancementHolder collectDust = create(root, "collect_dust", Items.COAL_ORE, COLLECT_DUST, COLLECT_DUST_DESCRIPTION, InventoryChangeTrigger.TriggerInstance.hasItems(DSItems.ELDER_DRAGON_DUST.value()), 6);
        buildCollectDustChildren(collectDust);

        create(root, "path_choice", Items.OAK_SIGN, "", "", noItemInteract(EntityType.VILLAGER), 6);

        AdvancementHolder placeAltar = create(root, "place_altar", DSBlocks.STONE_DRAGON_ALTAR.value(), "", "", placeBlock(DSItemTags.DRAGON_ALTARS), 6);

        // --- Parent: place_altar --- //

        AdvancementHolder beCaveDragon = createWithToast(placeAltar, "cave/be_dragon", DSItems.DRAGON_SOUL.value(), "", "", beDragon(DragonTypes.CAVE), 12);
        buildBeCaveDragonChildren(beCaveDragon);

        AdvancementHolder beForestDragon = createWithToast(placeAltar, "forest/be_dragon", DSItems.DRAGON_SOUL.value(), "", "", beDragon(DragonTypes.FOREST), 12);
        buildBeForestDragonChildren(beForestDragon);

        createWithToast(placeAltar, "sea/be_dragon", DSItems.DRAGON_SOUL.value(), "", "", beDragon(DragonTypes.SEA), 12);


    }

    private void buildBeForestDragonChildren(final AdvancementHolder parent) {
        // --- Parent: forest/be_dragon --- //

        AdvancementHolder standOnSweetBerries = create(parent, "forest/stand_on_sweet_berries", Items.SWEET_BERRIES, "", "", location(dragonType(DragonTypes.FOREST).steppingOn(block(Blocks.SWEET_BERRY_BUSH))), 30);

        // FIXME :: why did it previously check for the drain effect - what was the intention here?
        AdvancementHolder poisonousPotato = create(parent, "forest/poisonous_potato", Items.POISONOUS_POTATO, "", "", dragonHasItem(DragonTypes.FOREST, Items.POISONOUS_POTATO), 16);

        // --- Parent: forest/poisonous_potato --- //

        AdvancementHolder meatEater = create(poisonousPotato, "forest/meat_eater", DSItems.MEAT_WILD_BERRIES.value(), "", "", List.of(
                consumeItem(DSItems.SWEET_SOUR_RABBIT.value()),
                consumeItem(DSItems.LUMINOUS_OINTMENT.value()),
                consumeItem(DSItems.DIAMOND_CHORUS.value()),
                consumeItem(DSItems.SMELLY_MEAT_PORRIDGE.value()),
                consumeItem(DSItems.MEAT_WILD_BERRIES.value()),
                consumeItem(DSItems.MEAT_CHORUS_MIX.value()),
                consumeItem(DSItems.FOREST_DRAGON_TREAT.value())
        ), 60);

        // --- Parent: forest/meat_eater --- //

        create(meatEater, "forest/transplant_chorus_fruit", DSItems.DIAMOND_CHORUS.value(), "", "", placeBlockAsDragon(
                dragonType(DragonTypes.FOREST).located(LocationPredicate.Builder.location().setDimension(Level.OVERWORLD)), Blocks.CHORUS_FLOWER
        ), 90);

        // --- Parent: forest/stand_on_sweet_berries --- //

        create(standOnSweetBerries, "forest/prevent_darkness_penalty", DSItems.LUMINOUS_OINTMENT.value(), "", "", location(
                dragonType(DragonTypes.FOREST)
                        .located(light(MinMaxBounds.Ints.between(0, 3)))
                        .effects(MobEffectsPredicate.Builder.effects().and(DSEffects.MAGIC))
        ), 40);
    }

    private void buildBeCaveDragonChildren(final AdvancementHolder parent) {
        // --- Parent: cave/be_dragon --- //

        AdvancementHolder rockEater = create(parent, "cave/rock_eater", DSItems.CHARGED_COAL.value(), "", "", List.of(
                consumeItem(DSItems.CHARGED_COAL.value()),
                consumeItem(DSItems.CHARGED_SOUP.value()),
                consumeItem(DSItems.CHARRED_MEAT.value()),
                consumeItem(DSItems.CHARRED_SEAFOOD.value()),
                consumeItem(DSItems.HOT_DRAGON_ROD.value()),
                consumeItem(DSItems.EXPLOSIVE_COPPER.value()),
                consumeItem(DSItems.QUARTZ_EXPLOSIVE_COPPER.value()),
                consumeItem(DSItems.DOUBLE_QUARTZ.value()),
                consumeItem(DSItems.CAVE_DRAGON_TREAT.value())
        ), 60);

        AdvancementHolder swimInLava = create(parent, "cave/swim_in_lava", Items.LAVA_BUCKET, "", "", location(dragonType(DragonTypes.CAVE).located(lavaPredicate(registries))), 20);

        // --- Parent: cave/swim_in_lava --- //

        AdvancementHolder diamondsInLava = create(swimInLava, "cave/diamonds_in_lava", Items.LAVA_BUCKET, "", "", mineBlockUnderLava(registries, Tags.Blocks.ORES_DIAMOND), 40);

        // FIXME :: previously used change dimension but also had the lava vision effect check - what is the intention here?
        createWithToast(diamondsInLava, "cave/go_home", Items.NETHER_BRICK_STAIRS, "", "", location(
                dragonType(DragonTypes.CAVE)
                        .located(LocationPredicate.Builder.location().setDimension(Level.NETHER))
                        .effects(MobEffectsPredicate.Builder.effects().and(DSEffects.LAVA_VISION))
        ), 20);

        // --- Parent: cave/rock_eater --- //

        create(rockEater, "cave/water_safety", DSItems.CHARGED_SOUP.value(), "", "", location(
                dragonType(DragonTypes.CAVE)
                        .located(waterPredicate(registries))
                        .effects(MobEffectsPredicate.Builder.effects().and(DSEffects.FIRE))
        ), 40);
    }

    private void buildBeDragonChildren(final AdvancementHolder parent) {
        // --- Parent: be_dragon --- //

        AdvancementHolder useStarHeart = createWithToast(parent, "use_star_heart", DSItems.STAR_HEART.value(), "", "", useStarHeart(), 30);

        // --- Parent: use_star_heart --- //

        createWithToast(useStarHeart, "use_dragon_soul", DSItems.DRAGON_SOUL.value(), "", "", useDragonSoul(), 120);
    }

    private void buildCollectDustChildren(final AdvancementHolder parent) {
        // --- Parent: collect_dust --- //

        AdvancementHolder beYoungDragon = createWithToast(parent, "be_young_dragon", DSItems.DRAGON_HEART_SHARD.value(), BE_YOUNG_DRAGON, BE_YOUNG_DRAGON_DESCRIPTION, beDragon(19.9), 12);
        buildBeYoungDragonChildren(beYoungDragon);

        AdvancementHolder sleepOnTreasure = createWithAnnouncement(parent, "sleep_on_treasure", Items.GOLD_NUGGET, "", "", sleepOnTreasure(10), 10);
        buildSleepOnTreasureChildren(sleepOnTreasure);

        HolderSet.Named<Structure> dragonSkeletons = registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(TagKey.create(Registries.STRUCTURE, DragonSurvival.res("dragon_skeletons"))); // FIXME :: add proper tag
        createWithToast(parent, "find_bones", DSItems.STAR_BONE.value(), "", "", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location().setStructures(dragonSkeletons)), 12);

        AdvancementHolder useMemoryBlock = createWithToast(parent, "use_memory_block", DSBlocks.DRAGON_MEMORY_BLOCK.value(), "", "", itemUsedOnBlock(DSBlocks.DRAGON_MEMORY_BLOCK.value(), DSItemTags.DRAGON_BEACONS), 10);
        buildUseMemoryBlockChildren(useMemoryBlock);
    }

    private void buildUseMemoryBlockChildren(final AdvancementHolder parent) {
        // --- Parent: use_memory_block --- //

        AdvancementHolder changeBeacon = createWithToast(parent, "change_beacon", Items.NETHERITE_INGOT, "", "", itemUsedOnBlock(DSBlocks.EMPTY_DRAGON_BEACON.value(), Items.DIAMOND_BLOCK, Items.GOLD_BLOCK, Items.NETHERITE_INGOT), 10);

        // --- Parent: change_beacon --- //

        createWithToast(changeBeacon, "get_all_beacons", DSItems.ELDER_DRAGON_DUST.value(), "", "", List.of(
                effectWithMinDuration(DSEffects.PEACE, Functions.secondsToTicks(20)),
                effectWithMinDuration(DSEffects.FIRE, Functions.secondsToTicks(20)),
                effectWithMinDuration(DSEffects.MAGIC, Functions.secondsToTicks(20))
        ), 0);
    }

    private void buildSleepOnTreasureChildren(final AdvancementHolder parent) {
        // --- Parent: sleep_on_treasure --- //

        AdvancementHolder sleepOnHoard = createWithToast(parent, "sleep_on_hoard", Items.GOLD_INGOT, "", "", sleepOnTreasure(100), 40);

        // --- Parent: sleep_on_hoard --- //

        createWithToast(sleepOnHoard, "sleep_on_massive_hoard", DSBlocks.GOLD_DRAGON_TREASURE.value(), "", "", sleepOnTreasure(240), 120);
    }

    private void buildBeYoungDragonChildren(final AdvancementHolder parent) {
        // --- Parent: be_young_dragon --- //

        AdvancementHolder beAdultDragon = createWithToast(parent, "be_adult_dragon", DSItems.WEAK_DRAGON_HEART.value(), BE_ADULT_DRAGON, BE_ADULT_DRAGON_DESCRIPTION, beDragon(29.9), 0);

        // --- Parent: be_adult_dragon --- //

        AdvancementHolder collectHeartFromMonster = create(beAdultDragon, "collect_heart_from_monster", DSItems.ELDER_DRAGON_HEART.value(), "", "", InventoryChangeTrigger.TriggerInstance.hasItems(DSItems.ELDER_DRAGON_HEART.value()), 6);

        // --- Parent: collect_heart_from_monster --- //

        // FIXME :: why a different xp amount?
        AdvancementHolder beOldCaveDragon = createWithToast(collectHeartFromMonster, "cave/be_old_dragon", DSBlocks.CAVE_DRAGON_BEACON.value(), "", "", beDragon(59.9, DragonTypes.CAVE), 90);
        AdvancementHolder beOldForestDragon = createWithToast(collectHeartFromMonster, "forest/be_old_dragon", DSBlocks.FOREST_DRAGON_BEACON.value(), "", "", beDragon(59.9, DragonTypes.FOREST), 120);
        AdvancementHolder beOldSeaDragon = createWithToast(collectHeartFromMonster, "sea/be_old_dragon", DSBlocks.SEA_DRAGON_BEACON.value(), "", "", beDragon(59.9, DragonTypes.SEA), 120);

        // --- Parent: cave/be_old_dragon --- //

        createWithToast(beOldCaveDragon, "cave/master_all_passives", DSBlocks.CAVE_SOURCE_OF_MAGIC.value(), "", "", List.of(
                upgradeAbilityMax(BurnAbility.class),
                upgradeAbilityMax(CaveAthleticsAbility.class),
                upgradeAbilityMax(ContrastShowerAbility.class),
                upgradeAbilityMax(CaveMagicAbility.class)
        ), 90); // FIXME :: why a different amount?

        // --- Parent: forest/be_old_dragon --- //

        createWithToast(beOldForestDragon, "forest/master_all_passives", DSBlocks.FOREST_SOURCE_OF_MAGIC.value(), "", "", List.of(
                upgradeAbilityMax(CliffhangerAbility.class),
                upgradeAbilityMax(ForestAthleticsAbility.class),
                upgradeAbilityMax(LightInDarknessAbility.class),
                upgradeAbilityMax(ForestMagicAbility.class)
        ), 150);

        // --- Parent: sea/be_old_dragon --- //

        createWithToast(beOldSeaDragon, "sea/master_all_passives", DSBlocks.SEA_SOURCE_OF_MAGIC.value(), "", "", List.of(
                upgradeAbilityMax(SpectralImpactAbility.class),
                upgradeAbilityMax(SeaAthleticsAbility.class),
                upgradeAbilityMax(WaterAbility.class),
                upgradeAbilityMax(SeaMagicAbility.class)
        ), 150);
    }

    public AdvancementHolder createWithAnnouncement(final AdvancementHolder parent, final String path, final ItemLike displayItem, final String title, final String description, final Criterion<?> criterion, int experience) {
        return create(parent, path, displayItem, title, description, List.of(criterion), experience, false, true, false);
    }

    public AdvancementHolder createWithAnnouncement(final AdvancementHolder parent, final String path, final ItemLike displayItem, final String title, final String description, final List<Criterion<?>> criteria, int experience) {
        return create(parent, path, displayItem, title, description, criteria, experience, false, true, false);
    }

    public AdvancementHolder createWithToast(final AdvancementHolder parent, final String path, final ItemLike displayItem, final String title, final String description, final Criterion<?> criterion, int experience) {
        return create(parent, path, displayItem, title, description, List.of(criterion), experience, true, true, false);
    }

    public AdvancementHolder createWithToast(final AdvancementHolder parent, final String path, final ItemLike displayItem, final String title, final String description, final List<Criterion<?>> criteria, int experience) {
        return create(parent, path, displayItem, title, description, criteria, experience, true, true, false);
    }

    public AdvancementHolder create(final AdvancementHolder parent, final String path, final ItemLike displayItem, final String title, final String description, final Criterion<?> criterion, int experience) {
        return create(parent, path, displayItem, title, description, List.of(criterion), experience, false, false, false);
    }

    public AdvancementHolder create(final AdvancementHolder parent, final String path, final ItemLike displayItem, final String title, final String description, final List<Criterion<?>> criteria, int experience) {
        return create(parent, path, displayItem, title, description, criteria, experience, false, false, false);
    }

    public AdvancementHolder create(final AdvancementHolder parent, final String path, final ItemLike displayItem, final String title, final String description, final List<Criterion<?>> criteria, int experience, boolean showToast, boolean announceChat, boolean hidden) {
        Advancement.Builder advancement = Advancement.Builder.advancement()
                .parent(parent)
                .display(
                        displayItem,
                        Component.translatable(title),
                        Component.translatable(description),
                        null,
                        AdvancementType.TASK,
                        showToast,
                        announceChat,
                        hidden
                );

        for (int i = 0; i < criteria.size(); i++) {
            advancement.addCriterion("criterion_" + i, criteria.get(i));
        }

        if (experience > 0) {
            advancement.rewards(AdvancementRewards.Builder.experience(experience));
        }

        return advancement.save(saver, DragonSurvival.res(path), helper);
    }

    private static Optional<ContextAwarePredicate> caveDragonLavaPredicate(@NotNull final HolderLookup.Provider registries) {
        return Optional.of(EntityPredicate.wrap(dragonType(DragonTypes.CAVE).located(lavaPredicate(registries))));
    }

    private static LocationPredicate.Builder lavaPredicate(@NotNull final HolderLookup.Provider registries) {
        return LocationPredicate.Builder.location().setFluid(FluidPredicate.Builder.fluid().of(registries.lookupOrThrow(Registries.FLUID).getOrThrow(FluidTags.LAVA)));
    }

    private static LocationPredicate.Builder waterPredicate(@NotNull final HolderLookup.Provider registries) {
        return LocationPredicate.Builder.location().setFluid(FluidPredicate.Builder.fluid().of(registries.lookupOrThrow(Registries.FLUID).getOrThrow(FluidTags.WATER)));
    }

    private static LocationPredicate.Builder block(final Block block) {
        return LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(block));
    }

    private static LocationPredicate.Builder light(final MinMaxBounds.Ints bounds) {
        return LocationPredicate.Builder.location().setLight(LightPredicate.Builder.light().setComposite(bounds));
    }

    private static EntityPredicate.Builder dragonType(final AbstractDragonType type) {
        return EntityPredicate.Builder.entity().nbt(new NbtPredicate(dragonNBT(type)));
    }

    private static CompoundTag dragonNBT(final AbstractDragonType dragonType) {
        CompoundTag type = new CompoundTag();
        type.putString("type", dragonType.getTypeName());

        CompoundTag data = new CompoundTag();
        //noinspection DataFlowIssue -> ignore
        data.put(DragonSurvival.DRAGON_HANDLER.getKey().location().toString(), type);

        CompoundTag nbt = new CompoundTag();
        nbt.put(AttachmentHolder.ATTACHMENTS_NBT_KEY, data);

        return nbt;
    }

    // --- Misc --- //

    @SuppressWarnings("deprecation") // ignore
    public static Criterion<InventoryChangeTrigger.TriggerInstance> dragonHasItem(final AbstractDragonType dragonType, final ItemLike... items) {
        List<ItemPredicate> predicates = new ArrayList<>();

        for (ItemLike item : items) {
            predicates.add(new ItemPredicate(Optional.of(HolderSet.direct(item.asItem().builtInRegistryHolder())), MinMaxBounds.Ints.ANY, DataComponentPredicate.EMPTY, Map.of()));
        }

        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(dragonType(dragonType))), InventoryChangeTrigger.TriggerInstance.Slots.ANY, predicates));
    }

    public static Criterion<PlayerTrigger.TriggerInstance> location(final EntityPredicate.Builder builder) {
        return CriteriaTriggers.LOCATION.createCriterion(new PlayerTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(builder))));
    }

    public static Criterion<ConsumeItemTrigger.TriggerInstance> consumeItem(final Item... items) {
        return ConsumeItemTrigger.TriggerInstance.usedItem(ItemPredicate.Builder.item().of(items));
    }

    public static Criterion<UsingItemTrigger.TriggerInstance> usingItem(final Item item) {
        return CriteriaTriggers.USING_ITEM.createCriterion(new UsingItemTrigger.TriggerInstance(Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(item).build())));
    }

    public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placeBlockAsDragon(final EntityPredicate.Builder builder, final Block block) {
        ContextAwarePredicate blockPredicate = ContextAwarePredicate.create(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).build());
        return CriteriaTriggers.PLACED_BLOCK.createCriterion(new ItemUsedOnLocationTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(builder)), Optional.of(blockPredicate)));
    }

    public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placeBlock(final Block block) {
        return ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(block);
    }

    public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placeBlock(final TagKey<Item> blocks) {
        return ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(MatchTool.toolMatches(ItemPredicate.Builder.item().of(blocks)));
    }

    public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> itemUsedOnBlock(final Block block, final ItemLike... items) {
        return ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(block)), ItemPredicate.Builder.item().of(items));
    }

    public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> itemUsedOnBlock(final Block block, final TagKey<Item> items) {
        return ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(block)), ItemPredicate.Builder.item().of(items));
    }

    public static Criterion<EffectsChangedTrigger.TriggerInstance> effectWithMinDuration(final Holder<MobEffect> effect, int minDuration) {
        return EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects().and(effect, new MobEffectsPredicate.MobEffectInstancePredicate(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.atLeast(minDuration), Optional.empty(), Optional.empty())));
    }

    public static Criterion<PlayerInteractTrigger.TriggerInstance> noItemInteract(final EntityType<?> type) {
        return CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.createCriterion(new PlayerInteractTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(type)))));
    }

    // --- Mine Block Under Lava --- //

    @SuppressWarnings("deprecation") // ignore
    public static Criterion<MineBlockUnderLavaTrigger.MineBlockUnderLavaInstance> mineBlockUnderLava(@NotNull final HolderLookup.Provider registries, final Block... blocks) {
        return DSAdvancementTriggers.MINE_BLOCK_UNDER_LAVA.get().createCriterion(new MineBlockUnderLavaTrigger.MineBlockUnderLavaInstance(caveDragonLavaPredicate(registries), Optional.of(HolderSet.direct(Block::builtInRegistryHolder, blocks))));
    }

    public static Criterion<MineBlockUnderLavaTrigger.MineBlockUnderLavaInstance> mineBlockUnderLava(@NotNull final HolderLookup.Provider registries, final TagKey<Block> blocks) {
        return DSAdvancementTriggers.MINE_BLOCK_UNDER_LAVA.get().createCriterion(new MineBlockUnderLavaTrigger.MineBlockUnderLavaInstance(caveDragonLavaPredicate(registries), Optional.of(BuiltInRegistries.BLOCK.getOrCreateTag(blocks))));
    }

    // --- Use Dragon Soul --- //

    public static Criterion<UseDragonSoulTrigger.UseDragonSoulInstance> useDragonSoul() {
        return DSAdvancementTriggers.USE_DRAGON_SOUL.get().createCriterion(new UseDragonSoulTrigger.UseDragonSoulInstance(Optional.empty()));
    }

    // --- Use Star Heart --- //

    public static Criterion<UseStarHeartTrigger.UseStarHeartInstance> useStarHeart() {
        return DSAdvancementTriggers.USE_STAR_HEART.get().createCriterion(new UseStarHeartTrigger.UseStarHeartInstance(Optional.empty()));
    }


    // --- Sleep On Treasure --- //

    public static Criterion<SleepOnTreasureTrigger.SleepOnTreasureInstance> sleepOnTreasure(int nearbyTreasureAmount) {
        return DSAdvancementTriggers.SLEEP_ON_TREASURE.get().createCriterion(new SleepOnTreasureTrigger.SleepOnTreasureInstance(Optional.empty(), Optional.of(nearbyTreasureAmount)));
    }

    // --- Upgrade Ability --- //

    public static Criterion<UpgradeAbilityTrigger.UpgradeAbilityInstance> upgradeAbilityMax(final Class<? extends DragonAbility> abilityType) {
        DragonAbility ability;

        try {
            ability = abilityType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        return upgradeAbility(ability.getName(), ability.getMaxLevel());
    }

    public static Criterion<UpgradeAbilityTrigger.UpgradeAbilityInstance> upgradeAbility(final String ability, int level) {
        return DSAdvancementTriggers.UPGRADE_ABILITY.get().createCriterion(new UpgradeAbilityTrigger.UpgradeAbilityInstance(Optional.empty(), Optional.of(ability), Optional.of(level)));
    }

    // --- Be Dragon --- //

    public static Criterion<BeDragonTrigger.BeDragonInstance> beDragon() {
        return beDragon(Optional.empty(), Optional.empty());
    }

    public static Criterion<BeDragonTrigger.BeDragonInstance> beDragon(final AbstractDragonType type) {
        return beDragon(Optional.empty(), Optional.of(type.getTypeName()));
    }

    public static Criterion<BeDragonTrigger.BeDragonInstance> beDragon(double size) {
        return beDragon(Optional.of(size), Optional.empty());
    }

    public static Criterion<BeDragonTrigger.BeDragonInstance> beDragon(double size, final AbstractDragonType type) {
        return beDragon(Optional.of(size), Optional.of(type.getTypeName()));
    }

    public static Criterion<BeDragonTrigger.BeDragonInstance> beDragon(final Optional<Double> size, final Optional<String> type) {
        return DSAdvancementTriggers.BE_DRAGON.get().createCriterion(new BeDragonTrigger.BeDragonInstance(Optional.empty(), size, type));
    }
}
