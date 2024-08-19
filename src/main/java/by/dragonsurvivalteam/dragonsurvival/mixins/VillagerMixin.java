package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.HunterOmenHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin {
    @Shadow protected abstract SoundEvent getHurtSound(DamageSource pDamageSource);

    @Shadow protected abstract void setUnhappy();

    @Shadow public abstract void setLastHurtByMob(@Nullable LivingEntity pLivingBase);

    @Unique private static final EntityDataAccessor<Integer> PILLAGED_TIMER = SynchedEntityData.defineId(Villager.class, EntityDataSerializers.INT);

    @Inject(method = "startTrading", at = @At("HEAD"), cancellable = true)
    private void preventTradingWithMarkedPlayers(Player pPlayer, CallbackInfo ci) {
        if(pPlayer.hasEffect(DSEffects.HUNTER_OMEN)) {
            if(dragonSurvival$getPillagedTimer() == 0) {
                Villager villager = (Villager)(Object)this;
                // Add some XP, so that a player that only steals from villagers will eventually get higher level trades.
                villager.setVillagerXp(villager.getVillagerXp() + ServerConfig.pillageXPGain);
                if (villager.shouldIncreaseLevel()) {
                    villager.updateMerchantTimer = 40;
                    villager.increaseProfessionLevelOnUpdate = true;
                }
                // Set last hurt by so that looting the villager will hurt trade prices the same way attacking it does
                this.setLastHurtByMob(pPlayer);
                HunterOmenHandler.getVillagerLoot(villager, pPlayer.level(), null, false).forEach(pPlayer.getInventory()::add);
                villager.makeSound(this.getHurtSound(null));
                // Event 13 is the "villager has been hurt" event so it will make angry particles
                pPlayer.level().broadcastEntityEvent(villager, (byte)13);
                dragonSurvival$setPillagedTimer(Functions.secondsToTicks(600));
            } else {
                this.setUnhappy();
            }

            ci.cancel();
        }
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void addPillagedTimerToSynchedData(CallbackInfo ci, @Local(argsOnly = true) SynchedEntityData.Builder builder) {
        builder.define(PILLAGED_TIMER, 0);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readPillagedTimerFromNBT(CallbackInfo ci, @Local(argsOnly = true) CompoundTag pCompound) {
        this.dragonSurvival$setPillagedTimer(pCompound.getInt("PillagedTimer"));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writePillagedTimerToNBT(CallbackInfo ci, @Local(argsOnly = true) CompoundTag pCompound) {
        pCompound.putInt("PillagedTimer", this.dragonSurvival$getPillagedTimer());
    }

    @Unique private int dragonSurvival$getPillagedTimer() {
        return ((Villager)(Object)this).entityData.get(PILLAGED_TIMER);
    }

    @Unique private void dragonSurvival$setPillagedTimer(int pPillagedTimer) {
        ((Villager)(Object)this).entityData.set(PILLAGED_TIMER, pPillagedTimer);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickPillagedTimer(CallbackInfo ci) {
        if(this.dragonSurvival$getPillagedTimer() > 0) {
            this.dragonSurvival$setPillagedTimer(this.dragonSurvival$getPillagedTimer() - 1);
        }
    }

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void sweatIfNearPlayerWithHunterOmen(CallbackInfo ci) {
        if(HunterOmenHandler.isNearbyPlayerWithHunterOmen(8.0, ((Villager)(Object)this).level(), (Villager)(Object)this)) {
            // Event 42 is "villager is near a raid" event, which triggers the watersplash particles (sweat)
            ((Villager)(Object)this).level().broadcastEntityEvent((Villager)(Object)this, (byte)42);
        }
    }
}
