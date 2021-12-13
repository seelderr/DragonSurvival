package by.jackraidenph.dragonsurvival.particles.SeaDragon;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SeaSweepParticle extends SpriteTexturedParticle {
	private final IAnimatedSprite sprites;

	public SeaSweepParticle(ClientWorld world, double x, double y, double z, double vX, double vY, double vZ, double duration, IAnimatedSprite sprite) {
		super(world, x, y, z);
		setSize(2, 2);
		xd = vX;
		yd = vY;
		zd = vZ;
		lifetime = (int) duration;
		this.hasPhysics = false;
		setSpriteFromAge(sprite);
		this.sprites = sprite;
	}

	@Override
	protected float getU1() {
		return super.getU1() - (super.getU1() - super.getU0())/8f;
	}

	@Override
	protected float getV1() {
		return super.getV1() - (super.getV1() - super.getV0())/8f;
	}
	
	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
	
	@Override
	protected int getLightColor(float p_189214_1_)
	{
		int i = super.getLightColor(p_189214_1_);
		int k = i >> 16 & 255;
		return 240 | k << 16;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (age >= lifetime) {
			remove();
		}
		
		age++;
		this.setSpriteFromAge(this.sprites);
	}

	@Override
	public void render(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
		float var = (age + partialTicks)/(float)lifetime;
		alpha = (float) (1 - Math.exp(10 * (var - 1)) - Math.pow(2000, -var));
		if (alpha < 0.1) alpha = 0.1f;

		super.render(buffer, renderInfo, partialTicks);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static final class SeaFactory implements IParticleFactory<SeaSweepParticleData> {
		private final IAnimatedSprite spriteSet;

		public SeaFactory(IAnimatedSprite sprite) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle createParticle(SeaSweepParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			SeaSweepParticle particle = new SeaSweepParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getDuration(), spriteSet);
			particle.setSpriteFromAge(spriteSet);
			return particle;
		}
	}

}
