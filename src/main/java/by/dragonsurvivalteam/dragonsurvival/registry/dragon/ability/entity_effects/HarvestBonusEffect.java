package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.HarvestBonus;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public record HarvestBonusEffect(List<HarvestBonus> bonuses) implements AbilityEntityEffect{
    public static final MapCodec<HarvestBonusEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HarvestBonus.CODEC.listOf().fieldOf("bonuses").forGetter(HarvestBonusEffect::bonuses)
    ).apply(instance, HarvestBonusEffect::new));

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            bonuses().forEach(modifier -> modifier.apply(dragon, ability, livingEntity));
        }
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
