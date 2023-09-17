package by.dragonsurvivalteam.dragonsurvival.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TreasureParticle extends TextureSheetParticle{
	private final SpriteSet sprites;

	public TreasureParticle(ClientLevel p_i232378_1_, double p_i232378_2_, double p_i232378_4_, double p_i232378_6_, double p_i232378_8_, double p_i232378_10_, double p_i232378_12_, TreasureParticleData p_i232378_14_, SpriteSet p_i232341_10_){
		super(p_i232378_1_, p_i232378_2_, p_i232378_4_, p_i232378_6_, p_i232378_8_, p_i232378_10_, p_i232378_12_);
		sprites = p_i232341_10_;
		rCol = p_i232378_14_.getR();
		gCol = p_i232378_14_.getG();
		bCol = p_i232378_14_.getB();
		quadSize *= 0.75F * p_i232378_14_.getScale();
		int i = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
		lifetime = (int)Math.max((float)i * p_i232378_14_.getScale(), 1.0F);
		pickSprite(p_i232341_10_);
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
	public static class Factory implements ParticleProvider<TreasureParticleData>{
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