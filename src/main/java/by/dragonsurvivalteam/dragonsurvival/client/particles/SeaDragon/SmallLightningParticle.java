package by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public class SmallLightningParticle extends TextureSheetParticle{
	private final float spread;
	private final SpriteSet sprites;
	boolean swirls;
	private int swirlTick;

	public SmallLightningParticle(ClientLevel world, double x, double y, double z, double vX, double vY, double vZ, double duration, boolean swirls, SpriteSet sprite){

		super(world, x, y, z);
		setSize(1, 1);
		xd = vX;
		yd = vY;
		zd = vZ;
		lifetime = (int)duration;
		swirlTick = random.nextInt(120);
		spread = random.nextFloat();
		hasPhysics = false;
		this.swirls = swirls;
		setSpriteFromAge(sprite);
		sprites = sprite;
	}

	@Override
	protected float getU1(){
		return super.getU1() - (super.getU1() - super.getU0()) / 8f;
	}

	@Override

	protected float getV1(){
		return super.getV1() - (super.getV1() - super.getV0()) / 8f;
	}

	@Override
	public void tick(){
		super.tick();

		if(swirls){
			Vector3f motionVec = new Vector3f((float)xd, (float)yd, (float)zd);
			motionVec.normalize();
			float yaw = (float)Math.atan2(motionVec.x(), motionVec.z());
			float pitch = (float)Math.atan2(motionVec.y(), 1);
			float swirlRadius = 1f * (age / (float)lifetime) * spread;

			Quaternionf quatSpin = new Quaternionf(new AxisAngle4f(swirlTick * 0.2f, motionVec.x(), motionVec.y(), motionVec.z()));
			Quaternionf quatOrient = new Quaternionf().rotateXYZ(pitch, yaw, 0);

			Vector3f vec = new Vector3f(swirlRadius, 0, 0);
			vec = quatSpin.transform(vec);
			vec = quatOrient.transform(vec);

			x += vec.x();
			y += vec.y();
			z += vec.z();
		}

		if(age >= lifetime){
			remove();
		}
		age++;
		swirlTick++;
		setSpriteFromAge(sprites);
	}
	@Override
	public void remove(){
		level.addParticle(ParticleTypes.WHITE_ASH, x, y, z, 0, 0.01, 0);
		super.remove();
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

	@Override
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks){
		float var = (age + partialTicks) / (float)lifetime;
		alpha = (float)(1 - Math.exp(10 * (var - 1)) - Math.pow(2000, -var));
		if(alpha < 0.1){
			alpha = 0.1f;
		}

		super.render(buffer, renderInfo, partialTicks);
	}

	@OnlyIn( Dist.CLIENT )
	public static final class SeaFactory implements ParticleProvider<SmallLightningParticleData>{
		private final SpriteSet spriteSet;

		public SeaFactory(SpriteSet sprite){

			spriteSet = sprite;
		}

		@Override

		public Particle createParticle(SmallLightningParticleData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){

			SmallLightningParticle particle = new SmallLightningParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getDuration(), typeIn.getSwirls(), spriteSet);
			particle.setSpriteFromAge(spriteSet);
			return particle;
		}
	}
}