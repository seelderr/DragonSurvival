package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/entity/projectiles/DragonSpikeEntity.java
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class DragonSpikeEntity extends AbstractArrow
{
	public static final EntityDataAccessor<Integer> ARROW_LEVEL = SynchedEntityData.defineId(DragonSpikeEntity.class, EntityDataSerializers.INT);
	
	public DragonSpikeEntity(Level p_i50172_2_)
	{
		super(DSEntities.DRAGON_SPIKE, p_i50172_2_);
	}
	
	public DragonSpikeEntity(EntityType<? extends AbstractArrow> type, Level  worldIn) {
		super(type, worldIn);
	}
	
	public DragonSpikeEntity(EntityType<? extends AbstractArrow> type, Level world, LivingEntity entity)
	{
		super(type, entity, world);
	}
	
	@Override
	protected ItemStack getPickupItem()
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent()
	{
		return SoundEvents.AXE_STRIP;
	}
	
	@Override
	protected void onHitBlock(BlockHitResult p_230299_1_) {
		super.onHitBlock(p_230299_1_);
	}
	
=======
import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class DragonSpikeEntity extends AbstractArrowEntity{
	public static final DataParameter<Integer> ARROW_LEVEL = EntityDataManager.defineId(DragonSpikeEntity.class, DataSerializers.INT);

	public DragonSpikeEntity(World p_i50172_2_){
		super(DSEntities.DRAGON_SPIKE, p_i50172_2_);
	}

	public DragonSpikeEntity(EntityType<? extends AbstractArrowEntity> type, World worldIn){
		super(type, worldIn);
	}

	public DragonSpikeEntity(EntityType<? extends AbstractArrowEntity> type, World world, LivingEntity entity){
		super(type, entity, world);
	}

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/entity/projectiles/DragonSpikeEntity.java
	@Override
	protected void defineSynchedData(){
		super.defineSynchedData();
		this.entityData.define(ARROW_LEVEL, 1);
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/entity/projectiles/DragonSpikeEntity.java
	
	public void setArrow_level(int arrow_level){
		this.entityData.set(ARROW_LEVEL, arrow_level);
	}
	
	public int getArrow_level(){
		return this.entityData.get(ARROW_LEVEL);
	}
	
	@Override
	public boolean isCritArrow()
	{
		return false;
	}
	
	@Override
	public double getBaseDamage()
	{
		return getArrow_level() * ConfigHandler.SERVER.spikeDamage.get();
	}
	
	protected void onHitEntity(EntityHitResult p_213868_1_) {
=======

	protected void onHitEntity(EntityRayTraceResult p_213868_1_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/entity/projectiles/DragonSpikeEntity.java
		Entity entity = p_213868_1_.getEntity();
		Entity entity1 = this.getOwner();
		DamageSource damagesource;
		if(entity1 == null){
			damagesource = DamageSource.arrow(this, this);
		}else{
			damagesource = DamageSource.arrow(this, entity1);
			if(entity1 instanceof LivingEntity){
				((LivingEntity)entity1).setLastHurtMob(entity);
			}
		}
		float damage = (float)getBaseDamage();

		if(entity.hurt(damagesource, damage)){
			if(entity instanceof LivingEntity){
				LivingEntity livingentity = (LivingEntity)entity;
				if(!this.level.isClientSide){
					livingentity.setArrowCount(livingentity.getArrowCount() + 1);
				}

				if(!this.level.isClientSide && entity1 instanceof LivingEntity){
					EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
					EnchantmentHelper.doPostDamageEffects((LivingEntity)entity1, livingentity);
				}

				this.doPostHurtEffects(livingentity);
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/entity/projectiles/DragonSpikeEntity.java
				if (entity1 != null && livingentity != entity1 && livingentity instanceof Player && entity1 instanceof ServerPlayer && !this.isSilent()) {
					((ServerPlayer)entity1).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
				}
			}
			
			if (this.getPierceLevel() <= 0) {
				this.remove(RemovalReason.DISCARDED);
=======
				if(entity1 != null && livingentity != entity1 && livingentity instanceof PlayerEntity && entity1 instanceof ServerPlayerEntity && !this.isSilent()){
					((ServerPlayerEntity)entity1).connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.ARROW_HIT_PLAYER, 0.0F));
				}
			}

			if(this.getPierceLevel() <= 0){
				this.remove();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/entity/projectiles/DragonSpikeEntity.java
			}
		}else{
			this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
			this.setYRot(getYRot() + 180.0F);
			this.yRotO += 180.0F;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/entity/projectiles/DragonSpikeEntity.java
			if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
				this.remove(RemovalReason.DISCARDED);
=======
			if(!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D){
				this.remove();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/entity/projectiles/DragonSpikeEntity.java
			}
		}
	}

	@Override
	protected void onHitBlock(BlockRayTraceResult p_230299_1_){
		super.onHitBlock(p_230299_1_);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/entity/projectiles/DragonSpikeEntity.java
	public Packet<?> getAddEntityPacket() {
=======
	protected SoundEvent getDefaultHitGroundSoundEvent(){
		return SoundEvents.AXE_STRIP;
	}

	@Override
	protected ItemStack getPickupItem(){
		return ItemStack.EMPTY;
	}

	@Override
	public double getBaseDamage(){
		return getArrow_level() * ConfigHandler.SERVER.spikeDamage.get();
	}

	public int getArrow_level(){
		return this.entityData.get(ARROW_LEVEL);
	}

	@Override
	public boolean isCritArrow(){
		return false;
	}

	@Override
	public IPacket<?> getAddEntityPacket(){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/entity/projectiles/DragonSpikeEntity.java
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public void setArrow_level(int arrow_level){
		this.entityData.set(ARROW_LEVEL, arrow_level);
	}
}