package by.jackraidenph.dragonsurvival.entity.magic;

import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class DragonSpikeEntity extends AbstractArrowEntity
{
	public static final DataParameter<Integer> ARROW_LEVEL = EntityDataManager.defineId(DragonSpikeEntity.class, DataSerializers.INT);
	
	public int level = 1;
	
	public DragonSpikeEntity(World p_i50172_2_)
	{
		super(EntityTypesInit.DRAGON_SPIKE, p_i50172_2_);
	}
	
	public DragonSpikeEntity(EntityType<? extends AbstractArrowEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	public DragonSpikeEntity(EntityType<? extends AbstractArrowEntity> type,World world, LivingEntity entity)
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
	protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
		super.onHitBlock(p_230299_1_);
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ARROW_LEVEL, 1);
	}
	
	public void setLevel(int level){
		this.level = level;
		this.entityData.set(ARROW_LEVEL, level);
	}
	
	public int getLevel(){
		return this.entityData.get(ARROW_LEVEL);
	}
	
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
