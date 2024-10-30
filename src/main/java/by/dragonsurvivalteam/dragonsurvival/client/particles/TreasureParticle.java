package by.dragonsurvivalteam.dragonsurvival.client.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class TreasureParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public TreasureParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, TreasureParticle.Data data, SpriteSet spriteSet) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        sprites = spriteSet;
        rCol = data.r();
        gCol = data.g();
        bCol = data.b();
        quadSize *= 0.75F * data.scale();
        int i = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        lifetime = (int) Math.max((float) i * data.scale(), 1.0F);
        pickSprite(spriteSet);
    }

    @Override
    public float getQuadSize(float p_217561_1_) {
        return quadSize * Mth.clamp(((float) age + p_217561_1_) / (float) lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        if (age++ >= lifetime) {
            remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    protected int getLightColor(float p_189214_1_) {
        int i = super.getLightColor(p_189214_1_);
        int k = i >> 16 & 255;
        return 240 | k << 16;
    }

    public static class Factory implements ParticleProvider<TreasureParticle.Data> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(TreasureParticle.Data typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new TreasureParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn, sprites);
        }
    }

    public static class Type extends ParticleType<Data> {
        public Type(boolean pOverrideLimitter) {
            super(pOverrideLimitter);
        }

        @Override
        public @NotNull MapCodec<Data> codec() {
            return Data.CODEC;
        }

        @Override
        public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, Data> streamCodec() {
            return Data.STREAM_CODEC;
        }
    }

    public record Data(float r, float g, float b, float scale) implements ParticleOptions {
        public static MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(codecBuilder -> {
            return codecBuilder.group(
                    Codec.FLOAT.fieldOf("r").forGetter(Data::r),
                    Codec.FLOAT.fieldOf("g").forGetter(Data::g),
                    Codec.FLOAT.fieldOf("b").forGetter(Data::b),
                    Codec.FLOAT.fieldOf("scale").forGetter(Data::scale)).apply(codecBuilder, Data::new);
        });

        public static final StreamCodec<ByteBuf, Data> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public void encode(ByteBuf pBuffer, Data pValue) {
                pBuffer.writeFloat(pValue.r);
                pBuffer.writeFloat(pValue.b);
                pBuffer.writeFloat(pValue.r);
                pBuffer.writeFloat(pValue.scale);
            }

            @Override
            public @NotNull Data decode(ByteBuf pBuffer) {
                return new Data(pBuffer.readFloat(), pBuffer.readFloat(), pBuffer.readFloat(), pBuffer.readFloat());
            }
        };

        public static final Type TYPE = new Type(false);

        public Data(float r, float g, float b, float scale) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.scale = Mth.clamp(scale, 0.01F, 4.0F);
        }

        @Override
        public float r() {
            return r;
        }

        @Override
        public float g() {
            return g;
        }

        @Override
        public float b() {
            return b;
        }

        @Override
        public float scale() {
            return scale;
        }

        @Override
        public @NotNull ParticleType<Data> getType() {
            return TYPE;
        }
    }
}