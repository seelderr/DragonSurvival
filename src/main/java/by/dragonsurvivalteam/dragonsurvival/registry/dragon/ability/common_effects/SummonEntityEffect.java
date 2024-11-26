package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.common_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierType;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.block_effects.BlockEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.EntityEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public record SummonEntityEffect(HolderSet<EntityType<?>> entities, List<AttributeScale> attributeScales, boolean joinTeam) implements BlockEffect, EntityEffect {
    public static final MapCodec<SummonEntityEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entities").forGetter(SummonEntityEffect::entities),
                    AttributeScale.CODEC.listOf().optionalFieldOf("attribute_scales", List.of()).forGetter(SummonEntityEffect::attributeScales),
                    Codec.BOOL.optionalFieldOf("join_team", false).forGetter(SummonEntityEffect::joinTeam)
            ).apply(instance, SummonEntityEffect::new)
    );

    public record AttributeScale(HolderSet<Attribute> attributes, LevelBasedValue scale) {
        public static final Codec<AttributeScale> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RegistryCodecs.homogeneousList(Registries.ATTRIBUTE).fieldOf("attributes").forGetter(AttributeScale::attributes),
                LevelBasedValue.CODEC.fieldOf("scale").forGetter(AttributeScale::scale)
        ).apply(instance, AttributeScale::new));

        public void apply(final LivingEntity entity, int abilityLevel) {
            for (Holder<Attribute> attribute : attributes()) {
                AttributeInstance instance = entity.getAttribute(attribute);

                if (instance != null) {
                    ResourceLocation id = ModifierType.CUSTOM.randomId(attribute, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    instance.addPermanentModifier(new AttributeModifier(id, scale().calculate(abilityLevel), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                }
            }
        }
    }

    @Override
    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability, final BlockPos position) {
        spawn(level, dragon, ability, position);
    }

    @Override
    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability, final Entity entity) {
        spawn(level, dragon, ability, entity.blockPosition());
    }

    private void spawn(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability, final BlockPos spawnPosition) {
        if (Level.isInSpawnableBounds(spawnPosition)) {
            Optional<Holder<EntityType<?>>> optional = this.entities().getRandomElement(level.getRandom());

            if (optional.isPresent()) {
                Entity entity = optional.get().value().spawn(level, spawnPosition, MobSpawnType.TRIGGERED);

                if (entity != null) {
                    if (entity instanceof LivingEntity livingEntity) {
                        int abilityLevel = ability.getLevel();
                        attributeScales().forEach(attributeScale -> attributeScale.apply(livingEntity, abilityLevel));
                    }

                    if (entity instanceof LightningBolt lightningbolt) {
                        lightningbolt.setCause((ServerPlayer) dragon); // TODO :: remove cast once parameter is server player
                    }

                    if (joinTeam() && entity.getTeam() != null) {
                        level.getScoreboard().addPlayerToTeam(entity.getScoreboardName(), entity.getTeam());
                    }

                    entity.moveTo(spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ(), entity.getYRot(), entity.getXRot());
                }
            }
        }
    }

    @Override
    public MapCodec<? extends BlockEffect> blockCodec() {
        return CODEC;
    }

    @Override
    public MapCodec<? extends EntityEffect> entityCodec() {
        return CODEC;
    }
}
