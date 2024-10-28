package by.dragonsurvivalteam.dragonsurvival.client.particles;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BeaconParticle extends TextureSheetParticle {
    private final double fallSpeed;

    public BeaconParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, xd, yd, zd);
        gravity = 0.9f;
        fallSpeed = 0.02;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
        } else {
            //            this.setSpriteFromAge(this.sprites);
            yd += fallSpeed;
            move(0, yd, 0);
            if (y == yo) {
                xd *= 1.1D;
                zd *= 1.1D;
            }
            yd *= 0.7F;
            if (onGround) {
                xd *= 0.96F;
                zd *= 0.96F;
            }
        }
    }

    public static BeaconParticle createParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteSet) {
        BeaconParticle beaconParticle = new BeaconParticle(level, x, y, z, xd, yd, zd);
        beaconParticle.pickSprite(spriteSet);
        return beaconParticle;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class FireType extends ParticleType<FireData> {
        public FireType(boolean pOverrideLimitter) {
            super(pOverrideLimitter);
        }

        @Override
        public MapCodec<FireData> codec() {
            return FireData.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, FireData> streamCodec() {
            return FireData.STREAM_CODEC;
        }
    }

    public static class FireData implements ParticleOptions {
        public static final FireType TYPE = new FireType(false);

        public static final MapCodec<FireData> CODEC = MapCodec.unit(new FireData());

        public static final StreamCodec<ByteBuf, FireData> STREAM_CODEC = StreamCodec.unit(new FireData());

        @Override
        public @NotNull ParticleType<?> getType() {
            return TYPE;
        }
    }

    public static class FireFactory implements ParticleProvider<FireData> {
        private final SpriteSet spriteSet;

        public FireFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(@NotNull FireData type, @NotNull ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return BeaconParticle.createParticle(level, x, y, z, xd, yd, zd, spriteSet);
        }
    }

    public static class MagicType extends ParticleType<MagicData> {
        public MagicType(boolean pOverrideLimitter) {
            super(pOverrideLimitter);
        }

        @Override
        public MapCodec<MagicData> codec() {
            return MagicData.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, MagicData> streamCodec() {
            return MagicData.STREAM_CODEC;
        }
    }

    public static class MagicData implements ParticleOptions {
        public static final MagicType TYPE = new MagicType(false);

        public static final MapCodec<MagicData> CODEC = MapCodec.unit(new MagicData());

        public static final StreamCodec<ByteBuf, MagicData> STREAM_CODEC = StreamCodec.unit(new MagicData());

        @Override
        public @NotNull ParticleType<?> getType() {
            return TYPE;
        }
    }

    public static class MagicFactory implements ParticleProvider<MagicData> {
        private final SpriteSet spriteSet;

        public MagicFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(@NotNull MagicData type, @NotNull ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return BeaconParticle.createParticle(level, x, y, z, xd, yd, zd, spriteSet);
        }
    }

    public static class PeaceType extends ParticleType<PeaceData> {
        public PeaceType(boolean pOverrideLimitter) {
            super(pOverrideLimitter);
        }

        @Override
        public MapCodec<PeaceData> codec() {
            return PeaceData.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, PeaceData> streamCodec() {
            return PeaceData.STREAM_CODEC;
        }
    }

    public static class PeaceData implements ParticleOptions {
        public static final PeaceType TYPE = new PeaceType(false);

        public static final MapCodec<PeaceData> CODEC = MapCodec.unit(new PeaceData());

        public static final StreamCodec<ByteBuf, PeaceData> STREAM_CODEC = StreamCodec.unit(new PeaceData());

        @Override
        public @NotNull ParticleType<?> getType() {
            return TYPE;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class PeaceFactory implements ParticleProvider<PeaceData> {
        private final SpriteSet spriteSet;

        public PeaceFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(@NotNull PeaceData type, @NotNull ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return BeaconParticle.createParticle(level, x, y, z, xd, yd, zd, spriteSet);
        }
    }
}