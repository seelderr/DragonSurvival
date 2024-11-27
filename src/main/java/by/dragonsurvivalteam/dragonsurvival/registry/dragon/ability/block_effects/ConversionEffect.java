package by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.block_effects;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.AbilityInfo;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;

@AbilityInfo(compatibleWith = {AbilityInfo.Type.PASSIVE, AbilityInfo.Type.ACTIVE_SIMPLE, AbilityInfo.Type.ACTIVE_CHANNELED})
public record ConversionEffect(List<ConversionData> conversionData, LevelBasedValue probability) implements AbilityBlockEffect {
    public static final MapCodec<ConversionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ConversionData.CODEC.listOf().fieldOf("conversion_data").forGetter(ConversionEffect::conversionData),
            LevelBasedValue.CODEC.fieldOf("probability").forGetter(ConversionEffect::probability)
    ).apply(instance, ConversionEffect::new));

    public record ConversionData(HolderSet<Block> blocks, double chance) {
        public static final Codec<ConversionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(ConversionData::blocks),
                Codec.DOUBLE.fieldOf("chance").forGetter(ConversionData::chance)
        ).apply(instance, ConversionData::new));
    }

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final BlockPos position) {
        if (dragon.getRandom().nextDouble() < probability().calculate(ability.getLevel())) {
            return;
        }

        if (conversionData().isEmpty()) {
            return;
        }

        ConversionData data = conversionData().get(dragon.getRandom().nextInt(conversionData().size()));
        Optional<Holder<Block>> block = data.blocks().getRandomElement(dragon.getRandom());
        block.ifPresent(blockHolder -> dragon.serverLevel().setBlock(position, blockHolder.value().defaultBlockState(), Block.UPDATE_ALL));
    }

    @Override
    public MapCodec<? extends AbilityBlockEffect> blockCodec() {
        return CODEC;
    }
}
