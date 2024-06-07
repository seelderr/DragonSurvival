package by.dragonsurvivalteam.dragonsurvival.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
	public int getLightColor(float p_189214_1_){
		return 15728880;
	}

	@OnlyIn (Dist.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Factory(SpriteSet p_i50563_1_){
			sprites = p_i50563_1_;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double quadSize, double unused, double unused2){
			return new SeaSweepParticle(level, x, y, z, quadSize, sprites);
		}
	}
}