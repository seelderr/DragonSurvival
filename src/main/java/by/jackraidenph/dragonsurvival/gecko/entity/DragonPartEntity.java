package by.jackraidenph.dragonsurvival.gecko.entity;

import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.DragonHitboxAttacked;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraftforge.entity.PartEntity;

public class DragonPartEntity extends PartEntity<PlayerEntity>
{
	public PlayerEntity parentMob;
	public EntitySize size;
	
	public DragonPartEntity(PlayerEntity parent, float p_i50232_3_, float p_i50232_4_)
	{
		super(parent);
		this.size = EntitySize.scalable(p_i50232_3_, p_i50232_4_);
		this.refreshDimensions();
		this.parentMob = parent;
	}
	
	public boolean hurt(DamageSource source, float amount) {
		if(size.width == 0) return false;
		NetworkHandler.CHANNEL.sendToServer(new DragonHitboxAttacked(parentMob.getId(), amount));
		return parentMob.hurt(source, amount);
	}
	
	public boolean is(Entity p_70028_1_) {
		return this == p_70028_1_ || this.parentMob == p_70028_1_;
	}
	
	public EntitySize getDimensions(Pose p_213305_1_) {
		return this.size;
	}
	
	public boolean isPickable() {
		return true;
	}
	
	@Override
	protected void defineSynchedData() {}
	
	@Override
	protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {}
	
	@Override
	protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {}
}
