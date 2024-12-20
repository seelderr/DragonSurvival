package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.predicates.EyeInFluidPredicate;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.predicates.WeatherPredicate;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FluidPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.List;
import java.util.Optional;

public class DragonPenalties {
    @Translation(type = Translation.Type.PENALTY_DESCRIPTION, comments = {
            "■ Cave dragons take §cdamage§r snow and rain slowly due to their fiery nature.\n",
            "■ The skill «Contrast Shower» §7could make your life easier.\n",
    })
    @Translation(type = Translation.Type.PENALTY, comments = "Snow and Rain Weakness")
    public static final ResourceKey<DragonPenalty> SNOW_AND_RAIN_WEAKNESS = DragonPenalties.key("snow_and_rain_weakness");

    @Translation(type = Translation.Type.PENALTY_DESCRIPTION, comments = {
            "■ Cave dragons take §cdamage§r from water quickly due to their fiery nature.\n",
            "■ The skill effect «Cave Fire» §7could make your life easier.\n",
    })
    @Translation(type = Translation.Type.PENALTY, comments = "Water Weakness")
    public static final ResourceKey<DragonPenalty> WATER_WEAKNESS = DragonPenalties.key("water_weakness");

    @Translation(type = Translation.Type.PENALTY_DESCRIPTION, comments = {
            "■ Cave dragons can swim in lava, but still need to hold their breath when swimming in it.\n",
    })
    @Translation(type = Translation.Type.PENALTY, comments = "Lava Swimming")
    public static final ResourceKey<DragonPenalty> LAVA_SWIMMING = DragonPenalties.key("lava_swimming");

    public static void registerPenalties(final BootstrapContext<DragonPenalty> context) {
        context.register(SNOW_AND_RAIN_WEAKNESS, new DragonPenalty(
                DragonSurvival.res("textures/skills/cave/hot_blood_0.png"),
                List.of(
                        new DragonPenalty.Condition(
                                Optional.empty(),
                                Optional.of(EntityPredicate.Builder.entity().located(LocationPredicate.Builder.location().setCanSeeSky(true)).build()),
                                Optional.of(new WeatherPredicate(Optional.of(true), Optional.empty()))
                        ),
                        new DragonPenalty.Condition(
                                Optional.empty(),
                                Optional.of(EntityPredicate.Builder.entity().steppingOn(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.SNOW, Blocks.POWDER_SNOW, Blocks.SNOW_BLOCK))).build()),
                                Optional.empty()
                        )
                ),
                new DamagePenalty(
                        context.lookup(Registries.DAMAGE_TYPE).getOrThrow(DSDamageTypes.RAIN_BURN),
                        1.0f
                ),
                new SupplyTrigger(
                        "rain_supply",
                        DSAttributes.PENALTY_RESISTANCE_TIME,
                        40,
                        1.0f,
                        0.013f
                ))
        );

        context.register(WATER_WEAKNESS, new DragonPenalty(
                DragonSurvival.res("textures/skills/cave/hot_blood_0.png"),
                List.of(
                        new DragonPenalty.Condition(
                                Optional.empty(),
                                Optional.of(EntityPredicate.Builder.entity().located(LocationPredicate.Builder.location().setFluid(FluidPredicate.Builder.fluid().of(HolderSet.direct(Fluids.WATER.builtInRegistryHolder(), Fluids.FLOWING_WATER.builtInRegistryHolder())))).build()),
                                Optional.empty()
                        )
                ),
                new DamagePenalty(
                        context.lookup(Registries.DAMAGE_TYPE).getOrThrow(DSDamageTypes.WATER_BURN),
                        1.0f
                ),

                new InstantTrigger(
                        10
                )
        ));

        context.register(LAVA_SWIMMING, new DragonPenalty(
                DragonSurvival.res("textures/skills/cave/hot_blood_0.png"),
                List.of(
                        new DragonPenalty.Condition(
                                Optional.of(new EyeInFluidPredicate(NeoForgeMod.LAVA_TYPE)),
                                Optional.empty(),
                                Optional.empty()
                        )
                ),
                new DamagePenalty(
                        context.lookup(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.DROWN),
                        2.0f
                ),
                new SupplyTrigger(
                        "lava_supply",
                        DSAttributes.LAVA_OXYGEN_AMOUNT,
                        40,
                        1.0f,
                        0.013f
                )
        ));
    }

    public static ResourceKey<DragonPenalty> key(final ResourceLocation location) {
        return ResourceKey.create(DragonPenalty.REGISTRY, location);
    }

    public static ResourceKey<DragonPenalty> key(final String path) {
        return key(DragonSurvival.res(path));
    }
}
