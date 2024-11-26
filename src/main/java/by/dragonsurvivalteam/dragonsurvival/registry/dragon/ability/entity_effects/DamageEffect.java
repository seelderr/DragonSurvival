package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record DamageEffect(Holder<DamageType> damageType, LevelBasedValue amount) implements EntityEffect {
    public static final MapCodec<DamageEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageType.CODEC.fieldOf("damage_type").forGetter(DamageEffect::damageType),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(DamageEffect::amount)
    ).apply(instance, DamageEffect::new));

    @Override
    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability, final Entity entity) {
        // TODO :: also apply damage to entities (e.g. items, boats or experience)?
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.hurt(new DamageSource(damageType(), dragon), amount().calculate(ability.getLevel()));
        }
    }

    @Override
    public MapCodec<? extends EntityEffect> entityCodec() {
        return CODEC;
    }
}
