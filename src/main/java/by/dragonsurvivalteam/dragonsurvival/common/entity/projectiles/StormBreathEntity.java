package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/entity/projectiles/StormBreathEntity.java
public class StormBreathEntity extends Entity implements IAnimatable
{
	public StormBreathEntity(EntityType<?> p_i48580_1_, Level p_i48580_2_)
	{
=======
public class StormBreathEntity extends Entity implements IAnimatable{
	AnimationFactory animationFactory = new AnimationFactory(this);

	public StormBreathEntity(EntityType<?> p_i48580_1_, World p_i48580_2_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/entity/projectiles/StormBreathEntity.java
		super(p_i48580_1_, p_i48580_2_);
	}

	@Override
	protected void defineSynchedData(){}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/entity/projectiles/StormBreathEntity.java
	protected void readAdditionalSaveData(CompoundTag p_70037_1_) {}
	
	@Override
	protected void addAdditionalSaveData(CompoundTag p_213281_1_) {}
	
	@Override
	public Packet<?> getAddEntityPacket() {
=======
	protected void readAdditionalSaveData(CompoundNBT p_70037_1_){}

	@Override
	protected void addAdditionalSaveData(CompoundNBT p_213281_1_){}

	@Override
	public IPacket<?> getAddEntityPacket(){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/entity/projectiles/StormBreathEntity.java
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void registerControllers(AnimationData data){
		data.addAnimationController(new AnimationController<>(this, "everything", 0, event -> {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", true));
			return PlayState.CONTINUE;
		}));
	}

	@Override
	public AnimationFactory getFactory(){
		return animationFactory;
	}
}