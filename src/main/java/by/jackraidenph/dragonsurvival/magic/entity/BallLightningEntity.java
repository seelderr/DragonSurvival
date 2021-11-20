package by.jackraidenph.dragonsurvival.magic.entity;

import by.jackraidenph.dragonsurvival.gecko.AnimationTimer;
import by.jackraidenph.dragonsurvival.magic.Abilities.Actives.BallLightningAbility;
import by.jackraidenph.dragonsurvival.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
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

import java.util.List;

public class BallLightningEntity extends AbstractFireballEntity implements IAnimatable
{
	public static final DataParameter<Integer> SKILL_LEVEL = EntityDataManager.defineId(BallLightningEntity.class, DataSerializers.INT);
	public int skillLevel = 1;
	
	public int getLevel(){
		return this.entityData.get(SKILL_LEVEL);
	}
	
	public BallLightningEntity(World p_i50168_9_, LivingEntity p_i50168_2_, double p_i50168_3_, double p_i50168_5_, double p_i50168_7_)
	{
		super(EntityTypesInit.BALL_LIGHTNING, p_i50168_2_, p_i50168_3_, p_i50168_5_, p_i50168_7_, p_i50168_9_);
	}
	
	public BallLightningEntity(EntityType<? extends AbstractFireballEntity> p_i50166_1_, World p_i50166_2_) {
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
	
	private void attackMobs()
	{
		int range = ((BallLightningAbility)DragonAbilities.BALL_LIGHTNING).getRange();
		List<Entity> entities = this.level.getEntities(null, new AxisAlignedBB(position().x - range, position().y - range, position().z - range, position().x + range, position().y + range, position().z + range));
		
		for(Entity ent : entities){
			if(ent == this) continue;
			if(ent.position().distanceTo(position()) > (range / 2f)) continue;
			
			if (!this.level.isClientSide) {
				ent.hurt(DamageSource.LIGHTNING_BOLT, BallLightningAbility.getDamage(getLevel()));
				
				if (getOwner() instanceof LivingEntity) {
					this.doEnchantDamageEffects((LivingEntity)getOwner(), ent);
				}
			}
			
			if(this.level.isClientSide) {
				for (int i = 0; i < 4; i++) {
					double d1 = level.random.nextFloat();
					double d2 = level.random.nextFloat();
					double d3 = level.random.nextFloat();
					level.addParticle(ParticleTypes.LARGE_SMOKE, ent.getX() + d1, ent.getY() + d2, ent.getZ() + d3, 0.0D, 0.0D, 0.0D);
				}
			}
		}
		
		this.remove();
	}
	
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
