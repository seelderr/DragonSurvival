package by.dragonsurvivalteam.dragonsurvival.client.particles.SeaDragon;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/SeaDragon/LargeLightningParticle.java
public class LargeLightningParticle extends TextureSheetParticle
{
	private int swirlTick;
	private final float spread;
	boolean swirls;
	private final SpriteSet sprites;

	public LargeLightningParticle(ClientLevel world, double x, double y, double z, double vX, double vY, double vZ, double duration, boolean swirls, SpriteSet sprite) {
=======
public class LargeLightningParticle extends SpriteTexturedParticle{
	private final float spread;
	private final IAnimatedSprite sprites;
	boolean swirls;
	private int swirlTick;

	public LargeLightningParticle(ClientWorld world, double x, double y, double z, double vX, double vY, double vZ, double duration, boolean swirls, IAnimatedSprite sprite){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/SeaDragon/LargeLightningParticle.java
		super(world, x, y, z);
		setSize(1, 1);
		xd = vX;
		yd = vY;
		zd = vZ;
		lifetime = (int)duration;
		swirlTick = random.nextInt(120);
		spread = random.nextFloat();
		this.hasPhysics = false;
		this.swirls = swirls;
		setSpriteFromAge(sprite);
		this.sprites = sprite;
	}

	@Override
	protected float getU1(){
		return super.getU1() - (super.getU1() - super.getU0()) / 8f;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/SeaDragon/LargeLightningParticle.java
	protected float getV1() {
		return super.getV1() - (super.getV1() - super.getV0())/8f;
	}
	
	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
=======
	protected float getV1(){
		return super.getV1() - (super.getV1() - super.getV0()) / 8f;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/SeaDragon/LargeLightningParticle.java
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
			Quaternion quatSpin = motionVec.rotation(swirlTick * 0.2f);
			Quaternion quatOrient = new Quaternion(pitch, yaw, 0, false);
			Vector3f vec = new Vector3f(swirlRadius, 0, 0);
			vec.transform(quatOrient);
			vec.transform(quatSpin);
			x += vec.x();
			y += vec.y();
			z += vec.z();
		}

		if(age >= lifetime){
			remove();
		}
		age++;
		swirlTick++;
		this.setSpriteFromAge(this.sprites);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/SeaDragon/LargeLightningParticle.java
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		float var = (age + partialTicks)/(float)lifetime;
		alpha = (float) (1 - Math.exp(10 * (var - 1)) - Math.pow(2000, -var));
		if (alpha < 0.1) alpha = 0.1f;
=======
	public IParticleRenderType getRenderType(){
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	protected int getLightColor(float p_189214_1_){
		int i = super.getLightColor(p_189214_1_);
		int k = i >> 16 & 255;
		return 240 | k << 16;
	}

	@Override
	public void render(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks){
		float var = (age + partialTicks) / (float)lifetime;
		alpha = (float)(1 - Math.exp(10 * (var - 1)) - Math.pow(2000, -var));
		if(alpha < 0.1){
			alpha = 0.1f;
		}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/SeaDragon/LargeLightningParticle.java

		super.render(buffer, renderInfo, partialTicks);
	}

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/SeaDragon/LargeLightningParticle.java
	@OnlyIn(Dist.CLIENT)
	public static final class SeaFactory implements ParticleProvider<LargeLightningParticleData>
	{
		private final SpriteSet spriteSet;

		public SeaFactory(SpriteSet sprite) {
=======
	@OnlyIn( Dist.CLIENT )
	public static final class SeaFactory implements IParticleFactory<LargeLightningParticleData>{
		private final IAnimatedSprite spriteSet;

		public SeaFactory(IAnimatedSprite sprite){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/SeaDragon/LargeLightningParticle.java
			this.spriteSet = sprite;
		}

		@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/SeaDragon/LargeLightningParticle.java
		public Particle createParticle(LargeLightningParticleData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
=======
		public Particle createParticle(LargeLightningParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/SeaDragon/LargeLightningParticle.java
			LargeLightningParticle particle = new LargeLightningParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getDuration(), typeIn.getSwirls(), spriteSet);
			particle.setSpriteFromAge(spriteSet);
			return particle;
		}
	}
}