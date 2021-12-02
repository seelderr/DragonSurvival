package by.jackraidenph.dragonsurvival;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.entity.MagicalPredatorEntity;
import by.jackraidenph.dragonsurvival.gecko.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.handlers.Client.ClientDragonRender;
import by.jackraidenph.dragonsurvival.handlers.Client.ClientFlightHandler;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.nest.NestEntity;
import by.jackraidenph.dragonsurvival.network.*;
import by.jackraidenph.dragonsurvival.network.magic.*;
import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Synchronizes client data
 */
public class PacketProxy {
    
    public DistExecutor.SafeRunnable handleAddedEffect(SyncPotionAddedEffect message, Supplier<NetworkEvent.Context> supplier) {
        return () -> {
            NetworkEvent.Context context = supplier.get();
            context.enqueueWork(() -> handleAddedEffect(message, context));
        };
    }
    
    private void handleAddedEffect(SyncPotionAddedEffect message, NetworkEvent.Context context) {
        PlayerEntity thisPlayer = Minecraft.getInstance().player;
        if (thisPlayer != null) {
            World world = thisPlayer.level;
            Entity entity = world.getEntity(message.entityId);
            Effect ef = Effect.byId(message.effectId);
            
            if(ef != null){
                if(entity instanceof LivingEntity){
                    ((LivingEntity)entity).addEffect(new EffectInstance(ef, message.duration, message.amplifier));
                }
            }
        }
        context.setPacketHandled(true);
    }
    
    public DistExecutor.SafeRunnable handleEndedEffect(SyncPotionRemovedEffect message, Supplier<NetworkEvent.Context> supplier) {
        return () -> {
            NetworkEvent.Context context = supplier.get();
            context.enqueueWork(() -> handleEndedEffect(message, context));
        };
    }
    
    private void handleEndedEffect(SyncPotionRemovedEffect message, NetworkEvent.Context context) {
        PlayerEntity thisPlayer = Minecraft.getInstance().player;
        if (thisPlayer != null) {
            World world = thisPlayer.level;
            Entity entity = world.getEntity(message.entityId);
            Effect ef = Effect.byId(message.effectId);
            
            if(ef != null){
                if(entity instanceof LivingEntity){
                    ((LivingEntity)entity).removeEffect(ef);
                }
            }
        }
        context.setPacketHandled(true);
    }
    
    public DistExecutor.SafeRunnable handleClientSideAbility(SyncAbilityActivation abilityActivation, Supplier<NetworkEvent.Context> supplier) {
        return () -> {
            NetworkEvent.Context context = supplier.get();
            context.enqueueWork(() -> handleClientSideAbility(abilityActivation, context));
        };
    }
    
    private void handleClientSideAbility(SyncAbilityActivation abilityActivation, NetworkEvent.Context context) {
        PlayerEntity thisPlayer = Minecraft.getInstance().player;
        if (thisPlayer != null) {
            World world = thisPlayer.level;
            Entity entity = world.getEntity(abilityActivation.playerId);
            if (entity instanceof PlayerEntity) {
                DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                    DragonAbility ability = dragonStateHandler.getAbilityFromSlot(abilityActivation.slot);
                    if(ability.getLevel() > 0) {
                        ability.onKeyPressed((PlayerEntity)entity);
                    }
                });
            }
        }
        context.setPacketHandled(true);
    }
    
    public DistExecutor.SafeRunnable handleSkillAnimation(SyncCurrentAbilityCasting syncCapabilityDebuff, Supplier<NetworkEvent.Context> supplier) {
        return () -> {
            NetworkEvent.Context context = supplier.get();
            context.enqueueWork(() -> handleSkillAnimation(syncCapabilityDebuff, context));
        };
    }
    
    private void handleSkillAnimation(SyncCurrentAbilityCasting abilityCasting, NetworkEvent.Context context) {
        PlayerEntity thisPlayer = Minecraft.getInstance().player;
        if (thisPlayer != null) {
            World world = thisPlayer.level;
            Entity entity = world.getEntity(abilityCasting.playerId);
            if (entity instanceof PlayerEntity) {
                DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                    dragonStateHandler.setCurrentlyCasting((ActiveDragonAbility)abilityCasting.currentAbility);
                });
            }
        }
        context.setPacketHandled(true);
    }
    
    public DistExecutor.SafeRunnable handleMagicAbilities(SyncMagicAbilities magicStatus, Supplier<NetworkEvent.Context> supplier) {
        return () -> {
            NetworkEvent.Context context = supplier.get();
            context.enqueueWork(() -> handleMagicAbilities(magicStatus, context));
        };
    }
    
    private void handleMagicAbilities(SyncMagicAbilities magicStatus, NetworkEvent.Context context) {
        PlayerEntity thisPlayer = Minecraft.getInstance().player;
        if (thisPlayer != null) {
            World world = thisPlayer.level;
            Entity entity = world.getEntity(magicStatus.playerId);
            if (entity instanceof PlayerEntity) {
                DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                    magicStatus.abilities.forEach((ab) -> ab.player = (PlayerEntity)entity);
                    dragonStateHandler.getAbilities().clear();
                    dragonStateHandler.getAbilities().addAll(magicStatus.abilities);
                });
            }
        }
        context.setPacketHandled(true);
    }
    
    public DistExecutor.SafeRunnable handleMagicSync(SyncMagicStats magicStatus, Supplier<NetworkEvent.Context> supplier) {
        return () -> {
            NetworkEvent.Context context = supplier.get();
            context.enqueueWork(() -> handleMagicStats(magicStatus, context));
        };
    }
    
    private void handleMagicStats(SyncMagicStats magicStatus, NetworkEvent.Context context) {
        PlayerEntity thisPlayer = Minecraft.getInstance().player;
        if (thisPlayer != null) {
            World world = thisPlayer.level;
            Entity entity = world.getEntity(magicStatus.playerid);
            if (entity instanceof PlayerEntity) {
                DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                    dragonStateHandler.setCurrentMana(magicStatus.currentMana);
                    dragonStateHandler.setSelectedAbilitySlot(magicStatus.selectedSlot);
                    dragonStateHandler.setRenderAbilities(magicStatus.renderHotbar);
                });
            }
        }
        context.setPacketHandled(true);
    }
    
    public DistExecutor.SafeRunnable handleCapabilityDebuff(SyncCapabilityDebuff syncCapabilityDebuff, Supplier<NetworkEvent.Context> supplier) {
        return () -> {
            NetworkEvent.Context context = supplier.get();
            context.enqueueWork(() -> handleDebuffs(syncCapabilityDebuff, context));
        };
    }
    
	 private void handleDebuffs(SyncCapabilityDebuff syncCapabilityDebuff, NetworkEvent.Context context) {
		 PlayerEntity thisPlayer = Minecraft.getInstance().player;
		 if (thisPlayer != null) {
	 		World world = thisPlayer.level;
            Entity entity = world.getEntity(syncCapabilityDebuff.playerId);
            if (entity instanceof PlayerEntity) {
            	DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                    dragonStateHandler.setDebuffData(syncCapabilityDebuff.timeWithoutWater, syncCapabilityDebuff.timeInDarkness, syncCapabilityDebuff.timeInRain);
                });
            }
		 }
         context.setPacketHandled(true);
	 }
	
	
    public DistExecutor.SafeRunnable handleCapabilityMovement(PacketSyncCapabilityMovement syncCapabilityMovement, Supplier<NetworkEvent.Context> supplier) {
        return () -> {
            NetworkEvent.Context context = supplier.get();
            context.enqueueWork(() -> handleMovement(syncCapabilityMovement, context));
        };
    }

    private void handleMovement(PacketSyncCapabilityMovement syncCapabilityMovement, NetworkEvent.Context context) {
        PlayerEntity thisPlayer = Minecraft.getInstance().player;
        if (thisPlayer != null) {
            World world = thisPlayer.level;
            Entity entity = world.getEntity(syncCapabilityMovement.playerId);
            if (entity instanceof PlayerEntity) {
                DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                	if (entity == thisPlayer)
                        dragonStateHandler.setMovementData(syncCapabilityMovement.bodyYaw, ((PlayerEntity) entity).yHeadRot, entity.xRot, syncCapabilityMovement.bite);
                    else
                        dragonStateHandler.setMovementData(syncCapabilityMovement.bodyYaw, syncCapabilityMovement.headYaw, syncCapabilityMovement.headPitch, syncCapabilityMovement.bite);
                });
            }
        }
        context.setPacketHandled(true);
    }

    public DistExecutor.SafeRunnable updateSize(SyncSize syncSize, Supplier<NetworkEvent.Context> contextSupplier) {
        return () -> {
            Minecraft minecraft = Minecraft.getInstance();
            Entity entity = minecraft.level.getEntity(syncSize.playerId);
            if (entity instanceof PlayerEntity) {
                DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                    dragonStateHandler.setSize(syncSize.size, (PlayerEntity)entity);
                });

            }
            contextSupplier.get().setPacketHandled(true);
        };
    }

    public DistExecutor.SafeRunnable refreshInstances(SynchronizeDragonCap synchronizeDragonCap, Supplier<NetworkEvent.Context> context) {
        return () -> {
            ClientPlayerEntity myPlayer = Minecraft.getInstance().player;
            if (myPlayer != null) {
                World world = myPlayer.level;

                if (ClientDragonRender.dragonEntity != null) {
                    ClientDragonRender.dragonEntity.get().player = myPlayer.getId();
                }
                if (ClientDragonRender.dragonArmor != null) {
                    ClientDragonRender.dragonArmor.player = myPlayer.getId();
                }
                PlayerEntity thatPlayer = (PlayerEntity) world.getEntity(synchronizeDragonCap.playerId);

                if (thatPlayer != null) {
                    DragonStateProvider.getCap(thatPlayer).ifPresent(dragonStateHandler -> {
                        dragonStateHandler.setType(synchronizeDragonCap.dragonType);
                        dragonStateHandler.setIsHiding(synchronizeDragonCap.hiding);
                        dragonStateHandler.setHasWings(synchronizeDragonCap.hasWings);
                        dragonStateHandler.setSize(synchronizeDragonCap.size);
                        dragonStateHandler.setLavaAirSupply(synchronizeDragonCap.lavaAirSupply);
                        dragonStateHandler.setPassengerId(synchronizeDragonCap.passengerId);
                        if (!dragonStateHandler.hasWings() && thatPlayer == myPlayer)
                            ClientFlightHandler.wingsEnabled = false;
                    });
                    //refresh instances
                    if (thatPlayer != myPlayer) {
                        DragonEntity dragonEntity = EntityTypesInit.DRAGON.create(world);
                        dragonEntity.player = thatPlayer.getId();
                        ClientDragonRender.playerDragonHashMap.computeIfAbsent(thatPlayer.getId(), integer -> new AtomicReference<>(dragonEntity)).getAndSet(dragonEntity);
                        DragonEntity dragonArmor = EntityTypesInit.DRAGON_ARMOR.create(world);
                        dragonArmor.player = thatPlayer.getId();
                        ClientDragonRender.playerArmorMap.computeIfAbsent(thatPlayer.getId(), integer -> dragonArmor);
                    }
                    thatPlayer.setForcedPose(null);
                    thatPlayer.refreshDimensions();
                }
            }
            context.get().setPacketHandled(true);
        };
    }

    public void syncXpDevour(PacketSyncXPDevour m, Supplier<NetworkEvent.Context> supplier) {

        World world = Minecraft.getInstance().level;
        if (world != null) {
            ExperienceOrbEntity xpOrb = (ExperienceOrbEntity) (world.getEntity(m.xp));
            MagicalPredatorEntity entity = (MagicalPredatorEntity) world.getEntity(m.entity);
            if (xpOrb != null && entity != null) {
                entity.size += xpOrb.getValue() / 100.0F;
                entity.size = MathHelper.clamp(entity.size, 0.95F, 1.95F);
                world.addParticle(ParticleTypes.SMOKE, xpOrb.getX(), xpOrb.getY(), xpOrb.getZ(), 0, world.getRandom().nextFloat() / 12.5f, 0);
                xpOrb.remove();
                supplier.get().setPacketHandled(true);
            }
        }
    }

    public void syncPredatorStats(PacketSyncPredatorStats m, Supplier<NetworkEvent.Context> supplier) {

        World world = Minecraft.getInstance().level;
        if (world != null) {
            Entity entity = world.getEntity(m.id);
            if (entity != null) {
                ((MagicalPredatorEntity) entity).size = m.size;
                ((MagicalPredatorEntity) entity).type = m.type;
                supplier.get().setPacketHandled(true);
            }
        }
    }

    public void syncNest(SynchronizeNest synchronizeNest, Supplier<NetworkEvent.Context> contextSupplier) {
        PlayerEntity player = Minecraft.getInstance().player;
        ClientWorld world = Minecraft.getInstance().level;
        TileEntity entity = world.getBlockEntity(synchronizeNest.pos);
        if (entity instanceof NestEntity) {
            NestEntity nestEntity = (NestEntity) entity;
            nestEntity.energy = synchronizeNest.health;
            nestEntity.damageCooldown = synchronizeNest.cooldown;
            nestEntity.setChanged();
            if (nestEntity.energy <= 0) {
                world.playSound(player, synchronizeNest.pos, SoundEvents.METAL_BREAK, SoundCategory.BLOCKS, 1, 1);
            } else {
                world.playSound(player, synchronizeNest.pos, SoundEvents.SHIELD_BLOCK, SoundCategory.BLOCKS, 1, 1);
            }
            contextSupplier.get().setPacketHandled(true);
        }
    }
}
