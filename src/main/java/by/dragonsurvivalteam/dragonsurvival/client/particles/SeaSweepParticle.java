package by.dragonsurvivalteam.dragonsurvival.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/SeaSweepParticle.java
public class SeaSweepParticle extends TextureSheetParticle
{
	private final SpriteSet sprites;
	
	public SeaSweepParticle(ClientLevel p_i232341_1_, double p_i232341_2_, double p_i232341_4_, double p_i232341_6_, double p_i232341_8_, SpriteSet p_i232341_10_) {
=======
public class SeaSweepParticle extends SpriteTexturedParticle{
	private final IAnimatedSprite sprites;

	public SeaSweepParticle(ClientWorld p_i232341_1_, double p_i232341_2_, double p_i232341_4_, double p_i232341_6_, double p_i232341_8_, IAnimatedSprite p_i232341_10_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/SeaSweepParticle.java
		super(p_i232341_1_, p_i232341_2_, p_i232341_4_, p_i232341_6_, 0.0D, 0.0D, 0.0D);
		this.sprites = p_i232341_10_;
		this.lifetime = 4;
		float f = this.random.nextFloat() * 0.6F + 0.4F;
		this.rCol = f;
		this.gCol = f;
		this.bCol = f;
		this.quadSize = 1.0F - (float)p_i232341_8_ * 0.5F;
		this.setSpriteFromAge(p_i232341_10_);
	}

	public void tick(){
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if(this.age++ >= this.lifetime){
			this.remove();
		}else{
			this.setSpriteFromAge(this.sprites);
		}
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/SeaSweepParticle.java
	
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_LIT;
	}
	
	@OnlyIn( Dist.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType>
	{
		private final SpriteSet sprites;
		
		public Factory(SpriteSet p_i50563_1_) {
			this.sprites = p_i50563_1_;
		}
		
		public Particle createParticle(SimpleParticleType p_199234_1_, ClientLevel p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
=======

	public IParticleRenderType getRenderType(){
		return IParticleRenderType.PARTICLE_SHEET_LIT;
	}

	public int getLightColor(float p_189214_1_){
		return 15728880;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory implements IParticleFactory<BasicParticleType>{
		private final IAnimatedSprite sprites;

		public Factory(IAnimatedSprite p_i50563_1_){
			this.sprites = p_i50563_1_;
		}

		public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/SeaSweepParticle.java
			return new SeaSweepParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, this.sprites);
		}
	}
}