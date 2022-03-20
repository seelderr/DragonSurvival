package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class StormBreathEntity extends Entity implements IAnimatable{
	AnimationFactory animationFactory = new AnimationFactory(this);

	public StormBreathEntity(EntityType<?> p_i48580_1_, World p_i48580_2_){
		super(p_i48580_1_, p_i48580_2_);
	}

	@Override
	protected void defineSynchedData(){}

	@Override
	protected void readAdditionalSaveData(CompoundNBT p_70037_1_){}

	@Override
	protected void addAdditionalSaveData(CompoundNBT p_213281_1_){}

	@Override
	public IPacket<?> getAddEntityPacket(){
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