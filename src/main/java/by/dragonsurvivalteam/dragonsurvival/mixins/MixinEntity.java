package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigUtils;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import com.mojang.math.Vector3f;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( Entity.class )
public abstract class MixinEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity>{
	@Shadow
	private EntityDimensions dimensions;

	protected MixinEntity(Class<Entity> baseClass){
		super(baseClass);
	}

	@Inject( at = @At( value = "HEAD" ), method = "Lnet/minecraft/world/entity/Entity;positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", cancellable = true )
	private void positionRider(Entity p_226266_1_, Entity.MoveFunction p_226266_2_, CallbackInfo callbackInfo){
		if(DragonUtils.isDragon((Entity)(net.minecraftforge.common.capabilities.CapabilityProvider<Entity>)this)){
			if(hasPassenger(p_226266_1_)){
				double d0 = this.getY() + this.getPassengersRidingOffset() + p_226266_1_.getMyRidingOffset();
				Vector3f cameraOffset = DragonUtils.getCameraOffset((Entity)(net.minecraftforge.common.capabilities.CapabilityProvider<Entity>)this);
				p_226266_2_.accept(p_226266_1_, this.getX() - cameraOffset.x(), d0, this.getZ() - cameraOffset.z());
				callbackInfo.cancel();
			}
		}
	}

	@Shadow
	private boolean hasPassenger(Entity p_184196_1_){
		throw new IllegalStateException("Mixin failed to shadow hasPassenger()");
	}

	@Shadow
	private double getPassengersRidingOffset(){
		throw new IllegalStateException("Mixin failed to shadow getPassengersRidingOffset()");
	}

	@Shadow
	public double getX(){
		throw new IllegalStateException("Mixin failed to shadow getX()");
	}

	@Shadow
	public double getY(){
		throw new IllegalStateException("Mixin failed to shadow getY()");
	}

	@Shadow
	public double getZ(){
		throw new IllegalStateException("Mixin failed to shadow getZ()");
	}

	@Inject( at = @At( value = "HEAD" ), method = "Lnet/minecraft/world/entity/Entity;displayFireAnimation()Z", cancellable = true )
	private void hideCaveDragonFireAnimation(CallbackInfoReturnable<Boolean> ci){
		DragonStateProvider.getCap((Entity)(Object)this).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.getType() == DragonType.CAVE){
				ci.setReturnValue(false);
			}
		});
	}

	@Inject( at = @At( value = "HEAD" ), method = "Lnet/minecraft/world/entity/Entity;getPassengersRidingOffset()D", cancellable = true )
	public void getDragonPassengersRidingOffset(CallbackInfoReturnable<Double> ci){
		if(DragonUtils.isDragon((Entity)(Object)this)){
			switch(((Entity)(Object)this).getPose()){
				case FALL_FLYING:
				case SWIMMING:
				case SPIN_ATTACK:
					ci.setReturnValue((double)this.dimensions.height * 0.6D);
					break;
				case CROUCHING:
					ci.setReturnValue((double)this.dimensions.height * 0.45D);
					break;
				default:
					ci.setReturnValue((double)this.dimensions.height * 0.5D);
			}
		}
	}

	@Inject( at = @At( value = "HEAD" ), method = "Lnet/minecraft/world/entity/Entity;isVisuallyCrawling()Z", cancellable = true )
	public void isDragonVisuallyCrawling(CallbackInfoReturnable<Boolean> ci){
		if(DragonUtils.isDragon((Entity)(Object)this)){
			ci.setReturnValue(false);
		}
	}

	@Inject( at = @At( value = "RETURN" ), method = "canRide", cancellable = true )
	public void canRide(Entity entity, CallbackInfoReturnable<Boolean> ci){
		if(ci.getReturnValue() && DragonUtils.isDragon((Entity)(Object)this) && !DragonUtils.isDragon(entity)){
			if(ConfigHandler.SERVER.ridingBlacklist.get()){
				ci.setReturnValue(ConfigUtils.containsEntity(ConfigHandler.SERVER.allowedVehicles.get(), entity));
			}
		}
	}

	@Redirect( method = "canEnterPose(Lnet/minecraft/world/entity/Pose;)Z", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBoundingBoxForPose(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/phys/AABB;" ) )
	public AABB dragonPoseBB(Entity entity, Pose pose){
		if(DragonUtils.isDragon(entity) && ConfigHandler.SERVER.sizeChangesHitbox.get()){
			double size = DragonStateProvider.getCap(entity).orElseGet(null).getSize();
			double height = DragonSizeHandler.calculateModifiedHeight(DragonSizeHandler.calculateDragonHeight(size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get()), pose, ConfigHandler.SERVER.sizeChangesHitbox.get());
			double width = DragonSizeHandler.calculateDragonWidth(size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get()) / 2.0D;
			Vec3 vector3d = new Vec3(getX() - width, getY(), getZ() - width);
			Vec3 vector3d1 = new Vec3(getX() + width, getY() + height, getZ() + width);
			return new AABB(vector3d, vector3d1);
		}else{
			return getBoundingBoxForPose(pose);
		}
	}

	@Shadow
	public AABB getBoundingBoxForPose(Pose pose){
		throw new IllegalStateException("Mixin failed to shadow getBoundingBoxForPose()");
	}
}