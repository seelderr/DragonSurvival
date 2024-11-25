package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.block_effects;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public record BonemealEffect(LevelBasedValue attempts, LevelBasedValue probability) implements BlockEffect {
    public static final MapCodec<BonemealEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("attempts").forGetter(BonemealEffect::attempts),
            LevelBasedValue.CODEC.fieldOf("probability").forGetter(BonemealEffect::probability)
    ).apply(instance, BonemealEffect::new));

    @Override
    public void apply(final ServerLevel level, final Player dragon, final DragonAbilityInstance ability, final BlockPos position) {
        BlockState state = level.getBlockState(position);

        if (state.getBlock() instanceof BonemealableBlock bonemealableBlock) {
            int abilityLevel = ability.getLevel();
            float attempts = attempts().calculate(abilityLevel);

            for (int i = 0; i < attempts; i++) {
                if (dragon.getRandom().nextDouble() < probability().calculate(abilityLevel)) { // TODO :: do the probability check for each loop iteration or just once at the start?
                    bonemealableBlock.performBonemeal(level, dragon.getRandom(), position, state);
                }
            }
        }
    }

    @Override
    public MapCodec<? extends BlockEffect> codec() {
        return CODEC;
    }
}
