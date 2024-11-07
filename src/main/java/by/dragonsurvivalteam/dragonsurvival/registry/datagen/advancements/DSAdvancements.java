package by.dragonsurvivalteam.dragonsurvival.registry.datagen.advancements;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.criteria.BeDragonTrigger;
import by.dragonsurvivalteam.dragonsurvival.common.criteria.UpgradeAbilityTrigger;
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
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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

    private Consumer<AdvancementHolder> saver;
    private ExistingFileHelper helper;

    @Override
    public void generate(@NotNull HolderLookup.Provider registries, @NotNull Consumer<AdvancementHolder> saver, @NotNull ExistingFileHelper helper) {
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

        create(root, "be_dragon", DSItems.STAR_BONE.value(), BE_DRAGON, BE_DRAGON_DESCRIPTION, beDragon(), 12);
        AdvancementHolder collectDust = create(root, "collect_dust", Items.COAL_ORE, COLLECT_DUST, COLLECT_DUST_DESCRIPTION, InventoryChangeTrigger.TriggerInstance.hasItems(DSItems.ELDER_DRAGON_DUST.value()), 6);

        // --- Parent: collect_dust --- //

        AdvancementHolder beYoungDragon = createWithToast(collectDust, "be_young_dragon", DSItems.DRAGON_HEART_SHARD.value(), BE_YOUNG_DRAGON, BE_YOUNG_DRAGON_DESCRIPTION, beDragon(19.9), 12);
        buildBeYoungDragonChildren(beYoungDragon);

        HolderSet.Named<Structure> dragonSkeletons = registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(TagKey.create(Registries.STRUCTURE, DragonSurvival.res("dragon_skeletons"))); // FIXME :: add proper tag
        createWithToast(collectDust, "find_bones", DSItems.STAR_BONE.value(), "", "", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location().setStructures(dragonSkeletons)), 12);

        AdvancementHolder useMemoryBlock = createWithToast(collectDust, "use_memory_block", DSBlocks.DRAGON_MEMORY_BLOCK.value(), "", "", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(DSBlocks.DRAGON_MEMORY_BLOCK.value())), ItemPredicate.Builder.item().of(DSItemTags.DRAGON_BEACONS)), 10);

        // --- Parent: use_memory_block --- //

        AdvancementHolder changeBeacon = createWithToast(useMemoryBlock, "change_beacon", Items.NETHERITE_INGOT, "", "", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(DSBlocks.EMPTY_DRAGON_BEACON.value())), ItemPredicate.Builder.item().of(Items.DIAMOND_BLOCK, Items.GOLD_BLOCK, Items.NETHERITE_INGOT)), 10);

        // --- Parent: change_beacon --- //

        createWithToast(changeBeacon, "get_all_beacons", DSItems.ELDER_DRAGON_DUST.value(), "", "", List.of(
                EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects().and(DSEffects.PEACE, new MobEffectsPredicate.MobEffectInstancePredicate(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.atLeast(400), Optional.empty(), Optional.empty()))),
                EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects().and(DSEffects.FIRE, new MobEffectsPredicate.MobEffectInstancePredicate(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.atLeast(400), Optional.empty(), Optional.empty()))),
                EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects().and(DSEffects.MAGIC, new MobEffectsPredicate.MobEffectInstancePredicate(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.atLeast(400), Optional.empty(), Optional.empty())))
        ), 0);
    }

    private void buildBeYoungDragonChildren(final AdvancementHolder parent) {
        // --- Parent: be_young_dragon --- //

        AdvancementHolder beAdultDragon = createWithToast(parent, "be_adult_dragon", DSItems.WEAK_DRAGON_HEART.value(), BE_ADULT_DRAGON, BE_ADULT_DRAGON_DESCRIPTION, beDragon(29.9), 0);

        // --- Parent: be_adult_dragon --- //

        AdvancementHolder collectHeartFromMonster = create(beAdultDragon, "collect_heart_from_monster", DSItems.ELDER_DRAGON_HEART.value(), "", "", InventoryChangeTrigger.TriggerInstance.hasItems(DSItems.ELDER_DRAGON_HEART.value()), 6);

        // --- Parent: collect_heart_from_monster --- //

        AdvancementHolder beOldCaveDragon = createWithToast(collectHeartFromMonster, "be_old_cave_dragon", DSBlocks.CAVE_DRAGON_BEACON.value(), "", "", beDragon(59.9, DragonTypes.CAVE.getTypeName()), 90); // FIXME :: why a different amount?
        AdvancementHolder beOldForestDragon = createWithToast(collectHeartFromMonster, "be_old_forest_dragon", DSBlocks.FOREST_DRAGON_BEACON.value(), "", "", beDragon(59.9, DragonTypes.FOREST.getTypeName()), 120);
        AdvancementHolder beOldSeaDragon = createWithToast(collectHeartFromMonster, "be_old_sea_dragon", DSBlocks.SEA_DRAGON_BEACON.value(), "", "", beDragon(59.9, DragonTypes.SEA.getTypeName()), 120);

        // --- Parent: be_old_cave_dragon --- //

        createWithToast(beOldCaveDragon, "master_all_cave_passives", DSBlocks.CAVE_SOURCE_OF_MAGIC.value(), "", "", List.of(
                upgradeAbilityMax(BurnAbility.class),
                upgradeAbilityMax(CaveAthleticsAbility.class),
                upgradeAbilityMax(ContrastShowerAbility.class),
                upgradeAbilityMax(CaveMagicAbility.class)
        ), 90); // FIXME :: why a different amount?

        createWithToast(beOldForestDragon, "master_all_forest_passives", DSBlocks.FOREST_SOURCE_OF_MAGIC.value(), "", "", List.of(
                upgradeAbilityMax(CliffhangerAbility.class),
                upgradeAbilityMax(ForestAthleticsAbility.class),
                upgradeAbilityMax(LightInDarknessAbility.class),
                upgradeAbilityMax(ForestMagicAbility.class)
        ), 150);

        createWithToast(beOldSeaDragon, "master_all_sea_passives", DSBlocks.SEA_SOURCE_OF_MAGIC.value(), "", "", List.of(
                upgradeAbilityMax(SpectralImpactAbility.class),
                upgradeAbilityMax(SeaAthleticsAbility.class),
                upgradeAbilityMax(WaterAbility.class),
                upgradeAbilityMax(SeaMagicAbility.class)
        ), 150);
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

    // --- Upgrade Ability --- //

    public static Criterion<UpgradeAbilityTrigger.UpgradeAbilityInstance> upgradeAbilityMax(final Class<? extends DragonAbility> abilityType) {
        DragonAbility ability;

        try {
            ability = abilityType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        return DSAdvancementTriggers.UPGRADE_ABILITY.get().createCriterion(new UpgradeAbilityTrigger.UpgradeAbilityInstance(Optional.empty(), Optional.of(ability.getName()), Optional.of(ability.getMaxLevel())));
    }

    public static Criterion<UpgradeAbilityTrigger.UpgradeAbilityInstance> upgradeAbility(final String ability, final Integer level) {
        return DSAdvancementTriggers.UPGRADE_ABILITY.get().createCriterion(new UpgradeAbilityTrigger.UpgradeAbilityInstance(Optional.empty(), Optional.of(ability), Optional.of(level)));
    }

    // --- Be Dragon --- //

    public static Criterion<BeDragonTrigger.BeDragonInstance> beDragon(final String type) {
        return beDragon(Optional.empty(), Optional.of(type));
    }

    public static Criterion<BeDragonTrigger.BeDragonInstance> beDragon(final Double size) {
        return beDragon(Optional.of(size), Optional.empty());
    }

    public static Criterion<BeDragonTrigger.BeDragonInstance> beDragon(final Double size, final String type) {
        return beDragon(Optional.of(size), Optional.of(type));
    }

    public static Criterion<BeDragonTrigger.BeDragonInstance> beDragon() {
        return beDragon(Optional.empty(), Optional.empty());
    }

    public static Criterion<BeDragonTrigger.BeDragonInstance> beDragon(final Optional<Double> size, final Optional<String> type) {
        return DSAdvancementTriggers.BE_DRAGON.get().createCriterion(new BeDragonTrigger.BeDragonInstance(Optional.empty(), size, type));
    }
}
