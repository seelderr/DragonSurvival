package by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/ForestDragon/LargePoisonParticle.java
public class LargePoisonParticle extends TextureSheetParticle
{
	private int swirlTick;
	private final float spread;
	boolean swirls;
	private final SpriteSet sprites;

	public LargePoisonParticle(ClientLevel world, double x, double y, double z, double vX, double vY, double vZ, double duration, boolean swirls, SpriteSet sprite) {
=======
public class LargePoisonParticle extends SpriteTexturedParticle{
	private final float spread;
	private final IAnimatedSprite sprites;
	public static IParticleRenderType PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH = new IParticleRenderType(){
		public void begin(BufferBuilder p_217600_1_, TextureManager p_217600_2_){
			RenderSystem.depthMask(true);
			RenderSystem.disableCull();
			p_217600_2_.bind(AtlasTexture.LOCATION_PARTICLES);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.alphaFunc(516, 0.003921569F);
			p_217600_1_.begin(7, DefaultVertexFormats.PARTICLE);
		}

		public void end(Tessellator p_217599_1_){
			p_217599_1_.end();
		}

		public String toString(){
			return "PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH";
		}
	};
	boolean swirls;
	private int swirlTick;

	public LargePoisonParticle(ClientWorld world, double x, double y, double z, double vX, double vY, double vZ, double duration, boolean swirls, IAnimatedSprite sprite){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/ForestDragon/LargePoisonParticle.java
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
	protected float getV1(){
		return super.getV1() - (super.getV1() - super.getV0()) / 8f;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/ForestDragon/LargePoisonParticle.java
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
	

	@Override
	public void tick() {
=======
	public void tick(){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/ForestDragon/LargePoisonParticle.java
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/ForestDragon/LargePoisonParticle.java
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		float var = (age + partialTicks)/(float)lifetime;
		alpha = (float) (1 - Math.exp(10 * (var - 1)) - Math.pow(2000, -var));
		if (alpha < 0.1) alpha = 0.1f;
=======
	public IParticleRenderType getRenderType(){
		return PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH;
	}

	@Override
	public void render(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks){
		float var = (age + partialTicks) / (float)lifetime;
		alpha = (float)(1 - Math.exp(10 * (var - 1)) - Math.pow(2000, -var));
		if(alpha < 0.1){
			alpha = 0.1f;
		}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/ForestDragon/LargePoisonParticle.java

		super.render(buffer, renderInfo, partialTicks);
	}

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/ForestDragon/LargePoisonParticle.java
	@OnlyIn(Dist.CLIENT)
	public static final class ForestFactory implements ParticleProvider<LargePoisonParticleData>
	{
		private final SpriteSet spriteSet;

		public ForestFactory(SpriteSet sprite) {
=======
	@OnlyIn( Dist.CLIENT )
	public static final class ForestFactory implements IParticleFactory<LargePoisonParticleData>{
		private final IAnimatedSprite spriteSet;

		public ForestFactory(IAnimatedSprite sprite){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/ForestDragon/LargePoisonParticle.java
			this.spriteSet = sprite;
		}

		@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/ForestDragon/LargePoisonParticle.java
		public Particle createParticle(LargePoisonParticleData typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
=======
		public Particle createParticle(LargePoisonParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/ForestDragon/LargePoisonParticle.java
			LargePoisonParticle particle = new LargePoisonParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getDuration(), typeIn.getSwirls(), spriteSet);
			particle.setSpriteFromAge(spriteSet);
			return particle;
		}
	}
}