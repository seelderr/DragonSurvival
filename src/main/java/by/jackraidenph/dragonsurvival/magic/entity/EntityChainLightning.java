package by.jackraidenph.dragonsurvival.magic.entity;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityChainLightning extends ProjectileItemEntity
{
	public static final DataParameter<Float> TARGET_X = EntityDataManager.defineId(EntityChainLightning.class, DataSerializers.FLOAT);
	public static final DataParameter<Float> TARGET_Y = EntityDataManager.defineId(EntityChainLightning.class, DataSerializers.FLOAT);
	public static final DataParameter<Float> TARGET_Z = EntityDataManager.defineId(EntityChainLightning.class, DataSerializers.FLOAT);
	
	public long seed;
	
	public EntityChainLightning(LivingEntity owner, double x, double y, double z, World world)
	{
		super(EntityTypesInit.CHAIN_LIGHTNING, owner, world);
		this.entityData.set(TARGET_X, (float)x);
		this.entityData.set(TARGET_Y, (float)y);
		this.entityData.set(TARGET_Z, (float)z);
		seed = this.random.nextLong();
		this.setNoGravity(true);
	}
	
	public Vector3d getTarget(){
		return new Vector3d(entityData.get(TARGET_X), entityData.get(TARGET_Y), entityData.get(TARGET_Z));
	}
	
	public EntityChainLightning(EntityType<EntityChainLightning> entityChainLightningEntityType, World world) {
		super(entityChainLightningEntityType, world);
		seed = this.random.nextLong();
	}
	
	@Override
	protected Item getDefaultItem()
	{
		return null;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if(this.tickCount >= Functions.secondsToTicks(1)){
			this.remove();
		}
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(TARGET_X, -1f);
		this.entityData.define(TARGET_Y, -1f);
		this.entityData.define(TARGET_Z, -1f);
	}
	
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
