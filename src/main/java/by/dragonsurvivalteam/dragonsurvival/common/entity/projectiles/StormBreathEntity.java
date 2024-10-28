package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class StormBreathEntity extends Entity implements GeoEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public StormBreathEntity(final EntityType<?> type, final Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
	}

	@Override

	protected void readAdditionalSaveData(@NotNull final CompoundTag compoundTag) { /* Nothing to do here */ }

	@Override
	protected void addAdditionalSaveData(@NotNull final CompoundTag compoundTag) { /* Nothing to do here */ }

	@Override
	public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "idle", state -> state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}
}