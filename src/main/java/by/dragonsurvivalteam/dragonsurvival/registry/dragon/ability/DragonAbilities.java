package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Activation;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.ProjectileEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.SelfTarget;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.ProjectileData;
import by.dragonsurvivalteam.dragonsurvival.registry.projectile.Projectiles;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.List;
import java.util.Optional;

public class DragonAbilities {

    @Translation(type = Translation.Type.ABILITY, comments = {"Test fireball ability."})
    public static final ResourceKey<DragonAbility> FIRE_BALL_TEST = key("fire_ball_test");

    public static void registerAbilities(final BootstrapContext<DragonAbility> context) {
        context.register(FIRE_BALL_TEST, new DragonAbility(
                Optional.of(new Activation(
                        Activation.Type.SIMPLE,
                        Optional.empty(),
                        Optional.of(LevelBasedValue.constant(1)),
                        Optional.of(LevelBasedValue.constant((float) Functions.secondsToTicks(2)))
                )),
                Optional.empty(),
                Optional.empty(),
                List.of(
                        new SelfTarget(
                                Optional.empty(),
                                new ProjectileEffect(
                                        context.lookup(ProjectileData.REGISTRY).getOrThrow(Projectiles.FIRE_BALL_TEST),
                                        LevelBasedValue.constant(1),
                                        LevelBasedValue.constant(0),
                                        LevelBasedValue.constant(1)
                                )
                        )
                ),
                ResourceLocation.fromNamespaceAndPath(DragonSurvival.MODID, "textures/icons/body_type_central.png"),
                Component.literal("test description")
                )
        );
    }

    public static ResourceKey<DragonAbility> key(final ResourceLocation location) {
        return ResourceKey.create(DragonAbility.REGISTRY, location);
    }

    private static ResourceKey<DragonAbility> key(final String path) {
        return key(DragonSurvival.res(path));
    }
}
