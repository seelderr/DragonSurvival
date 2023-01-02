package by.dragonsurvivalteam.dragonsurvival.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;

public class BeaconParticle extends TextureSheetParticle{
	private double fallSpeed;

	public BeaconParticle(ClientLevel p_i232447_1_, double p_i232447_2_, double p_i232447_4_, double p_i232447_6_){
		super(p_i232447_1_, p_i232447_2_, p_i232447_4_, p_i232447_6_);
	}

	public BeaconParticle(ClientLevel p_i232448_1_, double x, double y, double z, double xd, double yd, double zd){
		super(p_i232448_1_, x, y, z, xd, yd, zd);
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
	public ParticleRenderType getRenderType(){
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
}