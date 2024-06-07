package by.dragonsurvivalteam.dragonsurvival.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BeaconParticle extends TextureSheetParticle{
	private double fallSpeed;

	public BeaconParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd){
		super(level, x, y, z, xd, yd, zd);
		gravity = 0.9f;
		fallSpeed = 0.02;
	}

	@Override
	public void tick(){
		xo = x;
		yo = y;
		zo = z;
		if(age++ >= lifetime){
			remove();
		}else{
			//            this.setSpriteFromAge(this.sprites);
			yd += fallSpeed;
			move(0, yd, 0);
			if(y == yo){
				xd *= 1.1D;
				zd *= 1.1D;
			}
			yd *= 0.7F;
			if(onGround){
				xd *= 0.96F;
				zd *= 0.96F;
			}
		}
	}

	@Override
	public @NotNull ParticleRenderType getRenderType(){
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet){
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd){
			BeaconParticle beaconParticle = new BeaconParticle(level, x, y, z, xd, yd, zd);
			beaconParticle.pickSprite(spriteSet);
			return beaconParticle;
		}
	}
}