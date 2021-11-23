package by.jackraidenph.dragonsurvival.magic.entity;

import by.jackraidenph.dragonsurvival.gecko.AnimationTimer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class DragonBallEntity extends AbstractFireballEntity implements IAnimatable
{
	public static final DataParameter<Integer> SKILL_LEVEL = EntityDataManager.defineId(DragonBallEntity.class, DataSerializers.INT);
	public int skillLevel = 1;
	
	public int getLevel(){
		return this.entityData.get(SKILL_LEVEL);
	}
	
	public DragonBallEntity(EntityType<? extends AbstractFireballEntity> p_i50168_1_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_, World p_i50168_9_)
	{
		super(p_i50168_1_, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
	}
	
	public DragonBallEntity(EntityType<? extends AbstractFireballEntity> p_i50166_1_, World p_i50166_2_) {
		super(p_i50166_1_, p_i50166_2_);
	}
	
	@Override
	protected boolean canHitEntity(Entity p_230298_1_)
	{
		return false;
	}
	
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SKILL_LEVEL, 1);
	}
	
	public void setLevel(int level){
		this.skillLevel = level;
		this.entityData.set(SKILL_LEVEL, level);
	}
	
	@Override
	protected IParticleData getTrailParticle()
	{
		return ParticleTypes.WHITE_ASH;
	}
	
	protected boolean shouldBurn() {
		return false;
	}
	
	
	@Override
	protected void onHitBlock(BlockRayTraceResult p_230299_1_)
	{
		attackMobs();
	}
	
	protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
		attackMobs();
	}
	
	public void attackMobs() {}
	
	AnimationTimer animationTimer = new AnimationTimer();
	
	@Override
	public void registerControllers(AnimationData data)
	{
		data.addAnimationController(new AnimationController<>(this, "everything", 3, event -> {
			AnimationBuilder animationBuilder = new AnimationBuilder();
			AnimationController animationController = event.getController();
			
			animationTimer.trackAnimation("idle");
			
			if (animationTimer.getDuration("idle") <= 0) {
				animationTimer.putAnimation("idle", 88, animationBuilder);
			}
			
			animationController.setAnimation(animationBuilder);
			return PlayState.CONTINUE;
		}));
	}
	
	AnimationFactory animationFactory = new AnimationFactory(this);
	
	@Override
	public AnimationFactory getFactory()
	{
		return animationFactory;
	}
}
