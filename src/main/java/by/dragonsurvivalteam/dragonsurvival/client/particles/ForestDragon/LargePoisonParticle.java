package by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon;

import by.dragonsurvivalteam.dragonsurvival.client.particles.DragonParticle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public class LargePoisonParticle extends TextureSheetParticle{
	private final float spread;
	private final SpriteSet sprites;
	boolean swirls;
	private int swirlTick;

	public LargePoisonParticle(ClientLevel world, double x, double y, double z, double vX, double vY, double vZ, double duration, boolean swirls, SpriteSet sprite){

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
	public ParticleRenderType getRenderType(){
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
	public static final class Factory implements ParticleProvider<DragonParticle.Data>{
		private final SpriteSet spriteSet;

		public Factory(SpriteSet sprite){
			spriteSet = sprite;
		}

		@Override
		public @NotNull Particle createParticle(DragonParticle.Data typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			LargePoisonParticle particle = new LargePoisonParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getDuration(), typeIn.getSwirls(), spriteSet);
			particle.setSpriteFromAge(spriteSet);
			return particle;
		}
	}
}