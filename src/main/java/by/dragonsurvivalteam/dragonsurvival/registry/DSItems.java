package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.SeaDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.items.*;
import by.dragonsurvivalteam.dragonsurvival.common.items.armor.DragonHunterWeapon;
import by.dragonsurvivalteam.dragonsurvival.common.items.armor.EvilDragonArmorItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.armor.GoodDragonArmorItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.food.ChargedSoupItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.food.DragonFoodItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.growth.StarBoneItem;
import by.dragonsurvivalteam.dragonsurvival.common.items.growth.StarHeartItem;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.BlockPosHelper;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DSItems {
    public static final DeferredRegister<Item> DS_ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, DragonSurvival.MODID);
    private static final Consumer<LivingEntity> REMOVE_EFFECTS_CURED_BY_MILK = entity -> entity.removeEffectsCuredBy(EffectCures.MILK);

    @Translation(type = Translation.Type.ITEM, comments = "Star Bone")
    public static final Holder<Item> STAR_BONE = DS_ITEMS.register("star_bone", () -> new StarBoneItem(new Properties()));

    @Translation(type = Translation.Type.ITEM, comments = "Star Heart")
    public static final Holder<Item> STAR_HEART = DS_ITEMS.register("star_heart", () -> new StarHeartItem(new Properties()));

    @Translation(type = Translation.Type.DESCRIPTION, comments = {
            "§6■ Part of the Elder dragon.§r",
            "■§7 Dust left from an ancient creature, this is used in many dragon recipes. Can be found in ore and treasure."
    })
    @Translation(type = Translation.Type.ITEM, comments = "Elder Dragon Dust")
    public static final Holder<Item> ELDER_DRAGON_DUST = DS_ITEMS.register("elder_dragon_dust", location -> new CustomHoverTextItem(new Item.Properties(), location.getPath()));

    @Translation(type = Translation.Type.DESCRIPTION, comments = {
            "§6■ Part of the Elder dragon.§r",
            "■§7 The remains of small dragons. This is used in many dragon recipes. The dragons were created from the body of an elder creature that sacrificed itself to save the world."
    })
    @Translation(type = Translation.Type.ITEM, comments = "Elder Dragon Bone")
    public static final Holder<Item> ELDER_DRAGON_BONE = DS_ITEMS.register("elder_dragon_bone", location -> new CustomHoverTextItem(new Item.Properties(), location.getPath()));

    public static final Holder<Item> DRAGON_HEART_SHARD = DS_ITEMS.register("heart_element", () -> new CustomHoverTextItem(new Item.Properties(), "ds.description.heartElement"));
    public static final Holder<Item> WEAK_DRAGON_HEART = DS_ITEMS.register("weak_dragon_heart", () -> new CustomHoverTextItem(new Item.Properties(), "ds.description.weakDragonHeart"));
    public static final Holder<Item> ELDER_DRAGON_HEART = DS_ITEMS.register("elder_dragon_heart", () -> new CustomHoverTextItem(new Item.Properties(), "ds.description.elderDragonHeart"));

    public static final Holder<Item> CHARGED_COAL = DS_ITEMS.register("charged_coal", () -> new ChargedCoalItem(new Properties(), DragonTypes.CAVE, REMOVE_EFFECTS_CURED_BY_MILK));
    public static final Holder<Item> CHARGED_SOUP = DS_ITEMS.register("charged_soup", () -> new ChargedSoupItem(new Properties()));
    public static final Holder<Item> CHARRED_MEAT = DS_ITEMS.register("charred_meat", () -> new DragonFoodItem(new Properties()));
    public static final Holder<Item> CHARRED_VEGETABLE = DS_ITEMS.register("charred_vegetable", () -> new DragonFoodItem(new Properties()));
    public static final Holder<Item> CHARRED_MUSHROOM = DS_ITEMS.register("charred_mushroom", () -> new DragonFoodItem(new Properties()));
    public static final Holder<Item> CHARRED_SEAFOOD = DS_ITEMS.register("charred_seafood", () -> new DragonFoodItem(new Properties()));
    public static final Holder<Item> HOT_DRAGON_ROD = DS_ITEMS.register("hot_dragon_rod", () -> new DragonFoodItem(new Properties(), DragonTypes.CAVE, () -> new MobEffectInstance(DSEffects.FIRE, Functions.minutesToTicks(1))));
    public static final Holder<Item> EXPLOSIVE_COPPER = DS_ITEMS.register("explosive_copper", () -> new DragonFoodItem(new Properties(), null, entity -> {
        entity.hurt(entity.damageSources().explosion(entity, entity), 1f);
        entity.level().addParticle(ParticleTypes.EXPLOSION, entity.getX(), entity.getEyeY(), entity.getZ(), 1.0D, 0.0D, 0.0D);
        entity.level().playSound(null, BlockPosHelper.get(entity.getEyePosition()), SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR, SoundSource.PLAYERS, 1f, 1f);
    }));
    public static final Holder<Item> QUARTZ_EXPLOSIVE_COPPER = DS_ITEMS.register("quartz_explosive_copper", () -> new DragonFoodItem(new Properties(), DragonTypes.CAVE, entity -> {
        entity.removeEffect(MobEffects.POISON);
        entity.removeEffect(MobEffects.WITHER);
    }, () -> new MobEffectInstance(MobEffects.ABSORPTION, Functions.minutesToTicks(5)), () -> new MobEffectInstance(MobEffects.REGENERATION, Functions.secondsToTicks(10), 1)));
    public static final Holder<Item> DOUBLE_QUARTZ = DS_ITEMS.register("double_quartz", () -> new DragonFoodItem(new Properties(), DragonTypes.CAVE, () -> new MobEffectInstance(MobEffects.REGENERATION, Functions.secondsToTicks(5))));

    public static final Holder<Item> SWEET_SOUR_RABBIT = DS_ITEMS.register("sweet_sour_rabbit", () -> new DragonFoodItem(new Properties(), DragonTypes.FOREST, REMOVE_EFFECTS_CURED_BY_MILK));
    public static final Holder<Item> LUMINOUS_OINTMENT = DS_ITEMS.register("luminous_ointment", () -> new DragonFoodItem(new Properties(), DragonTypes.FOREST, () -> new MobEffectInstance(MobEffects.GLOWING, Functions.minutesToTicks(5)), () -> new MobEffectInstance(DSEffects.MAGIC, Functions.minutesToTicks(5))));
    public static final Holder<Item> DIAMOND_CHORUS = DS_ITEMS.register("diamond_chorus", () -> new DragonFoodItem(new Properties(), DragonTypes.FOREST, entity -> {
        entity.removeEffect(MobEffects.POISON);
        entity.removeEffect(MobEffects.WITHER);
    }, () -> new MobEffectInstance(MobEffects.ABSORPTION, Functions.minutesToTicks(5)), () -> new MobEffectInstance(MobEffects.REGENERATION, Functions.secondsToTicks(10), 1)));
    public static final Holder<Item> SMELLY_MEAT_PORRIDGE = DS_ITEMS.register("smelly_meat_porridge", () -> new DragonFoodItem(new Properties()));
    public static final Holder<Item> MEAT_WILD_BERRIES = DS_ITEMS.register("meat_wild_berries", () -> new DragonFoodItem(new Properties()));
    public static final Holder<Item> MEAT_CHORUS_MIX = DS_ITEMS.register("meat_chorus_mix", () -> new DragonFoodItem(new Properties(), DragonTypes.FOREST, () -> new MobEffectInstance(MobEffects.REGENERATION, Functions.secondsToTicks(5))));

    public static final Holder<Item> SEASONED_FISH = DS_ITEMS.register("seasoned_fish", () -> new DragonFoodItem(new Properties()));
    public static final Holder<Item> GOLDEN_CORAL_PUFFERFISH = DS_ITEMS.register("golden_coral_pufferfish", () -> new DragonFoodItem(new Properties(), DragonTypes.SEA, () -> new MobEffectInstance(MobEffects.REGENERATION, Functions.secondsToTicks(5))));
    public static final Holder<Item> FROZEN_RAW_FISH = DS_ITEMS.register("frozen_raw_fish", () -> new DragonFoodItem(new Properties(), DragonTypes.SEA, entity -> {
        REMOVE_EFFECTS_CURED_BY_MILK.accept(entity);

        if (entity instanceof ServerPlayer serverPlayer) {
            if (DragonStateProvider.getData(serverPlayer).getType() instanceof SeaDragonType type) {
                type.timeWithoutWater = 0;
            }
        }
    }));
    public static final Holder<Item> GOLDEN_TURTLE_EGG = DS_ITEMS.register("golden_turtle_egg", () -> new DragonFoodItem(new Properties(), DragonTypes.SEA, entity -> {
        entity.removeEffect(MobEffects.POISON);
        entity.removeEffect(MobEffects.WITHER);
    }, () -> new MobEffectInstance(MobEffects.ABSORPTION, Functions.minutesToTicks(5)), () -> new MobEffectInstance(MobEffects.REGENERATION, Functions.secondsToTicks(10), 1)));

    public static final Holder<Item> SEA_DRAGON_TREAT = DS_ITEMS.register("sea_dragon_treat", () -> new DragonTreatItem(DragonTypes.SEA, new Properties()));
    public static final Holder<Item> CAVE_DRAGON_TREAT = DS_ITEMS.register("cave_dragon_treat", () -> new DragonTreatItem(DragonTypes.CAVE, new Properties()));
    public static final Holder<Item> FOREST_DRAGON_TREAT = DS_ITEMS.register("forest_dragon_treat", () -> new DragonTreatItem(DragonTypes.FOREST, new Properties()));

    public static final Holder<Item> WING_GRANT_ITEM = DS_ITEMS.register("wing_grant", () -> new WingGrantItem(new Properties()));
    public static final Holder<Item> SPIN_GRANT_ITEM = DS_ITEMS.register("spin_grant", () -> new SpinGrantItem(new Properties()));

    public static final Holder<Item> GOOD_DRAGON_UPGRADE = DS_ITEMS.register("good_dragon_upgrade", () -> new CustomHoverTextItem(new Properties().rarity(Rarity.RARE), "ds.description.good_dragon_upgrade"));
    public static final Supplier<ArmorItem> GOOD_DRAGON_HELMET = DS_ITEMS.register("good_dragon_helmet", () -> new GoodDragonArmorItem(
            ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(100)).rarity(Rarity.EPIC).fireResistant())
    );
    public static final Supplier<ArmorItem> GOOD_DRAGON_CHESTPLATE = DS_ITEMS.register("good_dragon_chestplate", () -> new GoodDragonArmorItem(
            ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(100)).rarity(Rarity.EPIC).fireResistant())
    );
    public static final Supplier<ArmorItem> GOOD_DRAGON_LEGGINGS = DS_ITEMS.register("good_dragon_leggings", () -> new GoodDragonArmorItem(
            ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(100)).rarity(Rarity.EPIC).fireResistant())
    );
    public static final Supplier<ArmorItem> GOOD_DRAGON_BOOTS = DS_ITEMS.register("good_dragon_boots", () -> new GoodDragonArmorItem(
            ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(100)).rarity(Rarity.EPIC).fireResistant())
    );

    public static final Holder<Item> EVIL_DRAGON_UPGRADE = DS_ITEMS.register("evil_dragon_upgrade", () -> new CustomHoverTextItem(new Properties().rarity(Rarity.RARE), "ds.description.evil_dragon_upgrade"));
    public static final Supplier<ArmorItem> EVIL_DRAGON_HELMET = DS_ITEMS.register("evil_dragon_helmet", () -> new EvilDragonArmorItem(
            ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(100)).rarity(Rarity.EPIC).fireResistant())
    );
    public static final Supplier<ArmorItem> EVIL_DRAGON_CHESTPLATE = DS_ITEMS.register("evil_dragon_chestplate", () -> new EvilDragonArmorItem(
            ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(100)).rarity(Rarity.EPIC).fireResistant())
    );
    public static final Supplier<ArmorItem> EVIL_DRAGON_LEGGINGS = DS_ITEMS.register("evil_dragon_leggings", () -> new EvilDragonArmorItem(
            ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(100)).rarity(Rarity.EPIC).fireResistant())
    );
    public static final Supplier<ArmorItem> EVIL_DRAGON_BOOTS = DS_ITEMS.register("evil_dragon_boots", () -> new EvilDragonArmorItem(
            ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(100)).rarity(Rarity.EPIC).fireResistant())
    );

    public static final Holder<Item> DRAGON_HUNTER_SWORD = DS_ITEMS.register("dragon_hunter_sword", () -> new DragonHunterWeapon(
                    DSEquipment.DRAGON_HUNTER, new Item.Properties().rarity(Rarity.EPIC).fireResistant().attributes(SwordItem.createAttributes(Tiers.NETHERITE, 4, -2.8F))) {
                @Override
                public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                    pTooltipComponents.add(Component.translatable("ds.description.dragon_hunter_sword"));
                }
            }
    );
    public static final Holder<Item> PARTISAN = DS_ITEMS.register("hunter_partisan", () -> new SwordItem(
                    Tiers.IRON, new Item.Properties().component(
                    DataComponents.ATTRIBUTE_MODIFIERS,
                    ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 6, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.2f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(DragonSurvival.res("dragonsurvival.partisan_block_reach"), 1f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(DragonSurvival.res("dragonsurvival.partisan_attack_reach"), 1f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .build()
            )) {
                @Override
                public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                    pTooltipComponents.add(Component.translatable("ds.description.hunter_partisan"));
                }
            }
    );

    public static final Holder<Item> HUNTER_PARTISAN_DIAMOND = DS_ITEMS.register("hunter_partisan_diamond", () -> new DragonHunterWeapon(
                    Tiers.DIAMOND, new Item.Properties().component(
                    DataComponents.ATTRIBUTE_MODIFIERS,
                    ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 7, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.2f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(DragonSurvival.res("dragonsurvival.partisan_block_reach"), 1f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(DragonSurvival.res("dragonsurvival.partisan_attack_reach"), 1f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .build()
            )) {
                @Override
                public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                    pTooltipComponents.add(Component.translatable("ds.description.hunter_partisan"));
                }
            }
    );

    public static final Holder<Item> HUNTER_PARTISAN_NETHERITE = DS_ITEMS.register("hunter_partisan_netherite", () -> new DragonHunterWeapon(
                    Tiers.NETHERITE, new Item.Properties().component(
                    DataComponents.ATTRIBUTE_MODIFIERS,
                    ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 8, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.2f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(DragonSurvival.res("dragonsurvival.partisan_block_reach"), 1f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(DragonSurvival.res("dragonsurvival.partisan_attack_reach"), 1f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .build()
            )) {
                @Override
                public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                    pTooltipComponents.add(Component.translatable("ds.description.hunter_partisan"));
                }
            }
    );

    public static final String GOOD_DRAGON_KEY_ID = "good_dragon_key";
    public static final Holder<Item> GOOD_DRAGON_KEY = DS_ITEMS.register(GOOD_DRAGON_KEY_ID, () -> new RotatingKeyItem(
                    new Item.Properties().rarity(Rarity.UNCOMMON).component(DSDataComponents.TARGET_POSITION, new Vector3f()),
                    DragonSurvival.res("geo/" + GOOD_DRAGON_KEY_ID + ".geo.json"),
                    DragonSurvival.res("textures/item/" + GOOD_DRAGON_KEY_ID + ".png"),
                    DragonSurvival.res("treasure_friendly")) {
                @Override
                public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                    pTooltipComponents.add(Component.translatable("ds.description." + GOOD_DRAGON_KEY_ID));
                }
            }
    );

    public static final String EVIL_DRAGON_KEY_ID = "evil_dragon_key";
    public static final Holder<Item> EVIL_DRAGON_KEY = DS_ITEMS.register(EVIL_DRAGON_KEY_ID, () -> new RotatingKeyItem(
                    new Item.Properties().rarity(Rarity.UNCOMMON).component(DSDataComponents.TARGET_POSITION, new Vector3f()),
                    DragonSurvival.res("geo/" + EVIL_DRAGON_KEY_ID + ".geo.json"),
                    DragonSurvival.res("textures/item/" + EVIL_DRAGON_KEY_ID + ".png"),
                    DragonSurvival.res("treasure_angry")) {
                @Override
                public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                    pTooltipComponents.add(Component.translatable("ds.description." + EVIL_DRAGON_KEY_ID));
                }
            }
    );

    public static final String HUNTER_KEY_ID = "hunter_key";
    public static final Holder<Item> HUNTER_DRAGON_KEY = DS_ITEMS.register(HUNTER_KEY_ID, () -> new RotatingKeyItem(
                    new Item.Properties().rarity(Rarity.UNCOMMON).component(DSDataComponents.TARGET_POSITION, new Vector3f()),
                    DragonSurvival.res("geo/" + HUNTER_KEY_ID + ".geo.json"),
                    DragonSurvival.res("textures/item/" + HUNTER_KEY_ID + ".png"),
                    DragonSurvival.res("treasure_hunter")) {
                @Override
                public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
                    super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
                    pTooltipComponents.add(Component.translatable("ds.description." + HUNTER_KEY_ID));
                }
            }
    );

    public static final Holder<Item> DRAGON_SOUL = DS_ITEMS.register("dragon_soul", () -> new DragonSoulItem(new Properties().rarity(Rarity.EPIC)));

    public static final Holder<Item> SPEARMAN_PROMOTION = DS_ITEMS.register("spearman_promotion", () -> new Item(new Properties().rarity(Rarity.COMMON)) {
        @Override
        public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
            super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
            pTooltipComponents.add(Component.translatable("ds.description.spearman_promotion"));
        }
    });

    // --- Spawn eggs --- //

    public static final Holder<Item> HOUND_SPAWN_EGG = DS_ITEMS.register("hound_spawn_egg", () -> new DeferredSpawnEggItem(DSEntities.HUNTER_HOUND, 0xA66A2C, 0xD5AA72, new Properties().rarity(Rarity.COMMON)));
    public static final Holder<Item> SPEARMAN_SPAWN_EGG = DS_ITEMS.register("spearman_spawn_egg", () -> new DeferredSpawnEggItem(DSEntities.HUNTER_SPEARMAN, 0xE6E3E1, 0xD1C8B8, new Properties().rarity(Rarity.COMMON)));
    public static final Holder<Item> KNIGHT_SPAWN_EGG = DS_ITEMS.register("knight_spawn_egg", () -> new DeferredSpawnEggItem(DSEntities.HUNTER_KNIGHT, 0x615B62, 0xCCBCAD, new Properties().rarity(Rarity.COMMON)));
    public static final Holder<Item> AMBUSHER_SPAWN_EGG = DS_ITEMS.register("ambusher_spawn_egg", () -> new DeferredSpawnEggItem(DSEntities.HUNTER_AMBUSHER, 0x756C63, 0x423930, new Properties().rarity(Rarity.COMMON)));
    public static final Holder<Item> GRIFFIN_SPAWN_EGG = DS_ITEMS.register("griffin_spawn_egg", () -> new DeferredSpawnEggItem(DSEntities.HUNTER_GRIFFIN, 0xE9D5CC, 0x71260A, new Properties().rarity(Rarity.COMMON)));
    public static final Holder<Item> LEADER_SPAWN_EGG = DS_ITEMS.register("leader_spawn_egg", () -> new DeferredSpawnEggItem(DSEntities.HUNTER_LEADER, 0x202020, 0xb3814e, new Properties().rarity(Rarity.COMMON)));

    // --- Not shown in creative tab --- //

    public static final Holder<Item> BOLAS = DS_ITEMS.register("bolas", () -> new BolasArrowItem(new Item.Properties()));
    public static final Holder<Item> HUNTING_NET = DS_ITEMS.register("dragon_hunting_mesh", () -> new Item(new Item.Properties()));
    public static final Holder<Item> LIGHTNING_TEXTURE_ITEM = DS_ITEMS.register("lightning", () -> new Item(new Item.Properties()));

    public static final Holder<Item> PASSIVE_MAGIC_BEACON = DS_ITEMS.register("beacon_magic_1", () -> new Item(new Item.Properties()));
    public static final Holder<Item> PASSIVE_PEACE_BEACON = DS_ITEMS.register("beacon_peace_1", () -> new Item(new Item.Properties()));
    public static final Holder<Item> PASSIVE_FIRE_BEACON = DS_ITEMS.register("beacon_fire_1", () -> new Item(new Item.Properties()));

    public static final Holder<Item> INACTIVE_MAGIC_DRAGON_BEACON = DS_ITEMS.register("beacon_magic_0", () -> new Item(new Item.Properties()));
    public static final Holder<Item> INACTIVE_PEACE_DRAGON_BEACON = DS_ITEMS.register("beacon_peace_0", () -> new Item(new Item.Properties()));
    public static final Holder<Item> INACTIVE_FIRE_DRAGON_BEACON = DS_ITEMS.register("beacon_fire_0", () -> new Item(new Item.Properties()));

    private static class CustomHoverTextItem extends Item {
        private final String description;

        public CustomHoverTextItem(final Properties properties, final String key) {
            super(properties);
            this.description = Translation.Type.DESCRIPTION.prefix + key;
        }

        @Override
        public void appendHoverText(@NotNull final ItemStack stack, @NotNull final Item.TooltipContext context, @NotNull final List<Component> tooltips, @NotNull final TooltipFlag flag) {
            super.appendHoverText(stack, context, tooltips, flag);
            tooltips.add(Component.translatable(description));
        }
    }
}