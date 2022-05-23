package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.client.particles.DSParticles;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.SourceOfMagicBlock;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncMagicSourceStatus;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.SourceOfMagicPlaceholder;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.SourceOfMagicTileEntity;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Random;


@Mod.EventBusSubscriber
public class SourceOfMagicHandler{
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		if(event.phase == Phase.START || event.side == LogicalSide.CLIENT){
			return;
		}
		Player player = event.player;

		if(DragonUtils.isDragon(player)){
			DragonStateHandler handler = DragonUtils.getHandler(player);

			if(handler.getMagic().onMagicSource){
				if(!(player.getFeetBlockState().getBlock() instanceof SourceOfMagicBlock) || handler.getMovementData().bite || player.isCrouching() && handler.getMagic().magicSourceTimer > 40){
					handler.getMagic().onMagicSource = false;
					handler.getMagic().magicSourceTimer = 0;
					NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicSourceStatus(player.getId(), false, 0));
					return;
				}

				BlockPos pos1 = player.blockPosition();
				BlockEntity blockEntity = player.level.getBlockEntity(pos1);

				if(blockEntity instanceof SourceOfMagicPlaceholder){
					pos1 = ((SourceOfMagicPlaceholder)blockEntity).rootPos;
				}

				BlockEntity sourceOfMagic = player.level.getBlockEntity(pos1);

				if(sourceOfMagic instanceof SourceOfMagicTileEntity){
					SourceOfMagicTileEntity tile = (SourceOfMagicTileEntity)sourceOfMagic;

					if(!tile.isEmpty()){
						BlockState pState = sourceOfMagic.getBlockState();
						boolean harm = false;
						DragonType type = DragonUtils.getDragonType(player);

						if(type != DragonType.CAVE && pState.getBlock() == DSBlocks.caveSourceOfMagic){
							harm = true;
						}
						if(type != DragonType.SEA && pState.getBlock() == DSBlocks.seaSourceOfMagic){
							harm = true;
						}
						if(type != DragonType.FOREST && pState.getBlock() == DSBlocks.forestSourceOfMagic){
							harm = true;
						}

						if(!harm || player.isCreative() || ServerConfig.canUseAllSourcesOfMagic){
							if(ServerConfig.sourceOfMagicInfiniteMagic){
								if(handler.getMagic().magicSourceTimer >= Functions.secondsToTicks(10)){
									handler.getMagic().magicSourceTimer = 0;
									MobEffect effect = DragonEffects.SOURCE_OF_MAGIC;
									MobEffectInstance effectInstance = player.getEffect(effect);
									int duration = SourceOfMagicTileEntity.consumables.get(tile.getItem(0).getItem());

									if(effectInstance == null){
										player.addEffect(new MobEffectInstance(effect, duration));
									}else{
										player.addEffect(new MobEffectInstance(effect, effectInstance.getDuration() + duration));
									}

									tile.removeItem(0, 1);
								}else{
									handler.getMagic().magicSourceTimer++;
								}
							}
						}else{
							if(ServerConfig.damageWrongSourceOfMagic){
								if(player.tickCount % Functions.secondsToTicks(5) == 0){
									player.hurt(DamageSource.MAGIC, 1F);
								}
							}
						}
					}else{
						handler.getMagic().magicSourceTimer = 0;
						handler.getMagic().onMagicSource = false;
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicSourceStatus(player.getId(), false, 0));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void playerParticles(PlayerTickEvent event){
		if(event.phase == Phase.START || event.side == LogicalSide.SERVER){
			return;
		}
		Player player = event.player;

		if(DragonUtils.isDragon(player)){
			DragonStateHandler handler = DragonUtils.getHandler(player);

			if(handler.getMagic().onMagicSource){
				BlockPos pos1 = player.blockPosition();
				BlockEntity blockEntity = player.level.getBlockEntity(pos1);

				if(blockEntity instanceof SourceOfMagicPlaceholder){
					pos1 = ((SourceOfMagicPlaceholder)blockEntity).rootPos;
				}

				BlockEntity sourceOfMagic = player.level.getBlockEntity(pos1);

				if(sourceOfMagic instanceof SourceOfMagicTileEntity){
					SourceOfMagicTileEntity tile = (SourceOfMagicTileEntity)sourceOfMagic;

					if(!tile.isEmpty()){
						BlockState pState = sourceOfMagic.getBlockState();
						boolean harm = false;
						DragonType type = DragonUtils.getDragonType(player);

						if(type != DragonType.CAVE && pState.getBlock() == DSBlocks.caveSourceOfMagic){
							harm = true;
						}
						if(type != DragonType.SEA && pState.getBlock() == DSBlocks.seaSourceOfMagic){
							harm = true;
						}
						if(type != DragonType.FOREST && pState.getBlock() == DSBlocks.forestSourceOfMagic){
							harm = true;
						}


						if(!harm || player.isCreative() || ServerConfig.canUseAllSourcesOfMagic){
							if(ServerConfig.sourceOfMagicInfiniteMagic){
								if(player.level.isClientSide){
									Minecraft minecraft = Minecraft.getInstance();
									Random random = player.level.random;
									double x = -1 + random.nextDouble() * 2;
									double z = -1 + random.nextDouble() * 2;

									if(pState.getBlock() == DSBlocks.seaSourceOfMagic || pState.getBlock() == DSBlocks.forestSourceOfMagic){
										if(!minecraft.isPaused()){
											player.level.addParticle(DSParticles.magicBeaconParticle, player.getX() + x, player.getY() + 0.5, player.getZ() + z, 0, 0, 0);
										}
									}else if(pState.getBlock() == DSBlocks.caveSourceOfMagic){
										if(!minecraft.isPaused()){
											player.level.addParticle(DSParticles.fireBeaconParticle, player.getX() + x, player.getY() + 0.5, player.getZ() + z, 0, 0, 0);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void playerTick(ClientTickEvent event){
		if(event.phase == Phase.START){
			return;
		}
		Player player = Minecraft.getInstance().player;

		if(DragonUtils.isDragon(player)){
			DragonStateHandler handler = DragonUtils.getHandler(player);

			if(handler.getMagic().onMagicSource){
				Vec3 velocity = player.getDeltaMovement();
				float groundSpeed = Mth.sqrt((float)((velocity.x * velocity.x) + (velocity.z * velocity.z)));
				if(Math.abs(groundSpeed) > 0.05){
					NetworkHandler.CHANNEL.sendToServer(new SyncMagicSourceStatus(player.getId(), false, 0));
				}
			}
		}
	}

	@SubscribeEvent
	public static void playerAttacked(LivingHurtEvent event){
		LivingEntity entity = event.getEntityLiving();

		if(entity instanceof Player){
			Player player = (Player)entity;

			if(!player.level.isClientSide){
				DragonStateProvider.getCap(player).ifPresent(cap -> {
					if(cap.getMagic().onMagicSource){
						cap.getMagic().onMagicSource = false;
						cap.getMagic().magicSourceTimer = 0;
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicSourceStatus(player.getId(), false, 0));
					}
				});
			}
		}
	}
}