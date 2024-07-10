package by.dragonsurvivalteam.dragonsurvival.client.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;


public class SeaSweepParticle extends TextureSheetParticle{
	private final SpriteSet sprites;

	public SeaSweepParticle(ClientLevel level, double x, double y, double z, double quadSize, SpriteSet spriteSet){
		super(level, x, y, z, 0.0D, 0.0D, 0.0D);
		sprites = spriteSet;
		lifetime = 4;
		float f = random.nextFloat() * 0.6F + 0.4F;
		rCol = f;
		gCol = f;
		bCol = f;
		this.quadSize = 1.0F - (float)quadSize * 0.5F;
		setSpriteFromAge(spriteSet);
	}

	@Override
	public void tick(){
		xo = x;
		yo = y;
		zo = z;
		if(age++ >= lifetime){
			remove();
		}else{
			setSpriteFromAge(sprites);
		}
	}

	@Override
	public @NotNull ParticleRenderType getRenderType(){
		return ParticleRenderType.PARTICLE_SHEET_LIT;
	}

	@Override
	public int getLightColor(float pPartialTicks){
		return 15728880;
	}

	public static class Type extends ParticleType<Data> {
		public Type(boolean pOverrideLimitter) {
			super(pOverrideLimitter);
		}

		@Override
		public MapCodec<Data> codec() {
			return Data.CODEC;
		}

		@Override
		public StreamCodec<? super RegistryFriendlyByteBuf, Data> streamCodec() {
			return Data.STREAM_CODEC;
		}
	}

	public record Data(double quadSize) implements ParticleOptions {
		public static final Type TYPE = new Type(false);

		public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(codecBuilder -> codecBuilder.group(Codec.DOUBLE.fieldOf("quadSize").forGetter(Data::quadSize)).apply(codecBuilder, Data::new));

		public static final StreamCodec<ByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.DOUBLE,
				Data::quadSize,
				Data::new
		);

		public double quadSize() {
			return quadSize;
		}

		@Override
		public @NotNull ParticleType<?> getType() {
			return TYPE;
		}
	}

	public static class Factory implements ParticleProvider<Data> {
		private final SpriteSet sprites;

		public Factory(SpriteSet spriteSet){
			sprites = spriteSet;
		}

		@Override
		public Particle createParticle(@NotNull Data data, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			return new SeaSweepParticle(level, x, y, z, data.quadSize, sprites);
		}
	}
}