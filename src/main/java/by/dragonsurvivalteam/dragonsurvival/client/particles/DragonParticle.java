package by.dragonsurvivalteam.dragonsurvival.client.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class DragonParticle {
    public static class Type extends ParticleType<Data> {
        public Type(boolean pOverrideLimitter) {
            super(pOverrideLimitter);
        }

        @Override
        public @NotNull MapCodec<Data> codec() {
            return Data.CODEC(this);
        }

        @Override
        public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, Data> streamCodec() {
            return Data.STREAM_CODEC;
        }
    }

    public static class Data implements ParticleOptions {
        private final float duration;
        private final boolean swirls;

        public static MapCodec<Data> CODEC(ParticleType<Data> particleType) {
            return RecordCodecBuilder.mapCodec(codecBuilder -> codecBuilder.group(Codec.FLOAT.fieldOf("duration").forGetter(Data::getDuration), Codec.BOOL.fieldOf("swirls").forGetter(Data::getSwirls)).apply(codecBuilder, Data::new));
        }

        public static final StreamCodec<ByteBuf, Data> STREAM_CODEC = new StreamCodec<>(){
            @Override
            public void encode(ByteBuf pBuffer, Data pValue) {
                pBuffer.writeFloat(pValue.duration);
                pBuffer.writeBoolean(pValue.swirls);
            }

            @Override
            public @NotNull Data decode(ByteBuf pBuffer) {
                return new Data(pBuffer.readFloat(), pBuffer.readBoolean());
            }
        };

        public Data(float duration, boolean spins){
            this.duration = duration;
            swirls = spins;
        }

        @OnlyIn( Dist.CLIENT )
        public float getDuration(){
            return duration;
        }

        @OnlyIn( Dist.CLIENT )
        public boolean getSwirls(){
            return swirls;
        }

        @Override
        public @NotNull ParticleType<Data> getType(){
            return new Type(false);
        }
    }
}
