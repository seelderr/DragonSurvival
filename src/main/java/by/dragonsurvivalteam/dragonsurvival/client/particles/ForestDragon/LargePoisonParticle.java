package by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

		super.render(buffer, renderInfo, partialTicks);
	}

	@OnlyIn( Dist.CLIENT )
	public static final class ForestFactory implements IParticleFactory<LargePoisonParticleData>{
		private final IAnimatedSprite spriteSet;

		public ForestFactory(IAnimatedSprite sprite){
			this.spriteSet = sprite;
		}

		@Override
		public Particle createParticle(LargePoisonParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed){
			LargePoisonParticle particle = new LargePoisonParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getDuration(), typeIn.getSwirls(), spriteSet);
			particle.setSpriteFromAge(spriteSet);
			return particle;
		}
	}
}