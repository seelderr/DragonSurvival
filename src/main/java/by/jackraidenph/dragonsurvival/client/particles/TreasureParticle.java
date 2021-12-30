package by.jackraidenph.dragonsurvival.client.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TreasureParticle extends SpriteTexturedParticle
{
	private final IAnimatedSprite sprites;
	
	public TreasureParticle(ClientWorld p_i232378_1_, double p_i232378_2_, double p_i232378_4_, double p_i232378_6_, double p_i232378_8_, double p_i232378_10_, double p_i232378_12_, TreasureParticleData p_i232378_14_, IAnimatedSprite p_i232341_10_)
	{
		super(p_i232378_1_, p_i232378_2_, p_i232378_4_, p_i232378_6_, p_i232378_8_, p_i232378_10_, p_i232378_12_);
		this.sprites = p_i232341_10_;
		this.xd *= (double)0.1F;
		this.yd *= (double)0.1F;
		this.zd *= (double)0.1F;
		float f = (float)Math.random() * 0.4F + 0.6F;
		this.rCol = ((float)(Math.random() * (double)0.2F) + 0.8F) * p_i232378_14_.getR() * f;
		this.gCol = ((float)(Math.random() * (double)0.2F) + 0.8F) * p_i232378_14_.getG() * f;
		this.bCol = ((float)(Math.random() * (double)0.2F) + 0.8F) * p_i232378_14_.getB() * f;
		this.quadSize *= 0.75F * p_i232378_14_.getScale();
		int i = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
		this.lifetime = (int)Math.max((float)i * p_i232378_14_.getScale(), 1.0F);
		this.pickSprite(p_i232341_10_);
	}
	
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
	
	public float getQuadSize(float p_217561_1_) {
		return this.quadSize * MathHelper.clamp(((float)this.age + p_217561_1_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
	}

	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			this.remove();
		}
	}
	
	@OnlyIn( Dist.CLIENT )
	public static class Factory implements IParticleFactory<TreasureParticleData>
	{
		private final IAnimatedSprite sprites;
		
		public Factory(IAnimatedSprite p_i50563_1_)
		{
			this.sprites = p_i50563_1_;
		}
		
		public Particle createParticle(TreasureParticleData p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_)
		{
			return new TreasureParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, p_199234_1_, this.sprites);
		}
	}
}