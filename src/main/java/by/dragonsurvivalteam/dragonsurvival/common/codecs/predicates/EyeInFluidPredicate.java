package by.dragonsurvivalteam.dragonsurvival.common.codecs.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public record EyeInFluidPredicate(Holder<FluidType> fluidType) {
    public static final Codec<EyeInFluidPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NeoForgeRegistries.FLUID_TYPES.holderByNameCodec().fieldOf("fluid").forGetter(EyeInFluidPredicate::fluidType)
    ).apply(instance, EyeInFluidPredicate::new));

    public boolean matches(Entity entity) {
        return entity.isEyeInFluidType(fluidType().value());
    }
}
