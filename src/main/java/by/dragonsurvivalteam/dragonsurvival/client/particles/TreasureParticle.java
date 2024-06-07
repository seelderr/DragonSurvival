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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class TreasureParticle extends TextureSheetParticle{
	private final SpriteSet sprites;

	public TreasureParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, TreasureParticle.Data data, SpriteSet spriteSet){
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
		sprites = spriteSet;
		rCol = data.getR();
		gCol = data.getG();
		bCol = data.getB();
		quadSize *= 0.75F * data.getScale();
		int i = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
		lifetime = (int)Math.max((float)i * data.getScale(), 1.0F);
		pickSprite(spriteSet);
	}

	@Override
	public float getQuadSize(float p_217561_1_){
		return quadSize * Mth.clamp(((float)age + p_217561_1_) / (float)lifetime * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void tick(){
		if(age++ >= lifetime){
			remove();
		}
	}

	@Override
	public ParticleRenderType getRenderType(){
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	protected int getLightColor(float p_189214_1_){
		int i = super.getLightColor(p_189214_1_);
		int k = i >> 16 & 255;
		return 240 | k << 16;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory implements ParticleProvider<TreasureParticle.Data> {
		private final SpriteSet sprites;

		public Factory(SpriteSet sprites){
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(TreasureParticle.Data typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			return new TreasureParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn, sprites);
		}
	}

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
		private final float r;
		private final float g;
		private final float b;
		private final float scale;

		public static MapCodec<Data> CODEC(ParticleType<Data> particleType) {
			return RecordCodecBuilder.mapCodec(codecBuilder -> {
				return codecBuilder.group(
						Codec.FLOAT.fieldOf("r").forGetter(Data::getR),
						Codec.FLOAT.fieldOf("g").forGetter(Data::getG),
						Codec.FLOAT.fieldOf("b").forGetter(Data::getB),
						Codec.FLOAT.fieldOf("scale").forGetter(Data::getScale)).apply(codecBuilder, Data::new);
			});
		}

		public static final StreamCodec<ByteBuf, Data> STREAM_CODEC = new StreamCodec<>(){
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

		public Data(float r, float g, float b, float scale){
			this.r = r;
			this.g = g;
			this.b = b;
			this.scale = Mth.clamp(scale, 0.01F, 4.0F);
		}

		@OnlyIn( Dist.CLIENT )
		public float getR(){
			return r;
		}

		@OnlyIn( Dist.CLIENT )
		public float getG(){
			return g;
		}

		@OnlyIn( Dist.CLIENT )
		public float getB(){
			return b;
		}

		@OnlyIn( Dist.CLIENT )
		public float getScale(){
			return scale;
		}

		@Override
		public @NotNull ParticleType<Data> getType(){
			return new Type(false);
		}
	}
}