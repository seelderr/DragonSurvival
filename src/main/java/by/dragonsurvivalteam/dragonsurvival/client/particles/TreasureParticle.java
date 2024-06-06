package by.dragonsurvivalteam.dragonsurvival.client.particles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TreasureParticle extends TextureSheetParticle{
	private final SpriteSet sprites;

	public TreasureParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, TreasureParticleData data, SpriteSet spriteSet){
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
	public static class Factory implements DragonParticleProvider<TreasureParticleData> {
		private final SpriteSet sprites;

		public Factory(SpriteSet p_i50563_1_){
			sprites = p_i50563_1_;
		}

		@Override
		public Particle createParticle(TreasureParticleData p_199234_1_, ClientLevel p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_){
			return new TreasureParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, p_199234_1_, sprites);
		}
	}
}