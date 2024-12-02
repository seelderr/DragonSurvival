package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.AbilityInfo;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.LevelBasedValue;

@AbilityInfo(compatibleWith = {AbilityInfo.Type.PASSIVE, AbilityInfo.Type.ACTIVE_SIMPLE, AbilityInfo.Type.ACTIVE_CHANNELED})
public record DamageEffect(Holder<DamageType> type, LevelBasedValue amount) implements AbilityEntityEffect {
    public static final MapCodec<DamageEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageType.CODEC.fieldOf("type").forGetter(DamageEffect::type),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(DamageEffect::amount)
    ).apply(instance, DamageEffect::new));

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity) {
        entity.hurt(new DamageSource(type(), dragon), amount().calculate(ability.getLevel()));
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
