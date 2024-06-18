package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.client.particles.BeaconParticle;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.SourceOfMagicBlock;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncMagicSourceStatus;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.SourceOfMagicPlaceholder;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.SourceOfMagicTileEntity;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;


@EventBusSubscriber
public class SourceOfMagicHandler{
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent.Pre event){
		if(event.getEntity().level().isClientSide()){
			return;
		}

		Player player = event.getEntity();

		if(DragonStateProvider.isDragon(player)){
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);

			if(handler.getMagicData().onMagicSource){
				if(!(player.getBlockStateOn().getBlock() instanceof SourceOfMagicBlock) || handler.getMovementData().bite || player.isCrouching() && handler.getMagicData().magicSourceTimer > 40){
					handler.getMagicData().onMagicSource = false;
					handler.getMagicData().magicSourceTimer = 0;
					PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncMagicSourceStatus.Data(player.getId(), false, 0));
					return;
				}

				BlockPos pos1 = player.blockPosition();
				BlockEntity blockEntity = player.level().getBlockEntity(pos1);

				if(blockEntity instanceof SourceOfMagicPlaceholder){
					pos1 = ((SourceOfMagicPlaceholder)blockEntity).rootPos;
				}

				BlockEntity sourceOfMagic = player.level().getBlockEntity(pos1);

				if(sourceOfMagic instanceof SourceOfMagicTileEntity tile){
					if(!tile.isEmpty()){
						BlockState pState = sourceOfMagic.getBlockState();
						
						if(!SourceOfMagicBlock.shouldHarmPlayer(pState, player) || player.isCreative()){
								if(handler.getMagicData().magicSourceTimer >= Functions.secondsToTicks(10)){
									handler.getMagicData().magicSourceTimer = 0;
									MobEffectInstance effectInstance = player.getEffect(DSEffects.SOURCE_OF_MAGIC);
									int duration = SourceOfMagicTileEntity.consumables.get(tile.getItem(0).getItem());

									if(effectInstance == null){
										player.addEffect(new MobEffectInstance(DSEffects.SOURCE_OF_MAGIC, duration));
									}else{
										player.addEffect(new MobEffectInstance(DSEffects.SOURCE_OF_MAGIC, effectInstance.getDuration() + duration));
									}

									tile.removeItem(0, 1);
								}
							}
						}else{
							if(ServerConfig.damageWrongSourceOfMagic){
								if(player.tickCount % Functions.secondsToTicks(5) == 0){
									player.hurt(player.damageSources().magic(), 1F);
								}
							}
						}
					}else{
						handler.getMagicData().magicSourceTimer = 0;
						handler.getMagicData().onMagicSource = false;
						PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncMagicSourceStatus.Data(player.getId(), false, 0));
					}
				}
			}
		}

	@SubscribeEvent
	public static void playerParticles(PlayerTickEvent.Post event){
		if(event.getEntity().level().isClientSide()){
			return;
		}

		Player player = event.getEntity();

		if(DragonStateProvider.isDragon(player)){
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);

			if(handler.getMagicData().onMagicSource){
				BlockPos pos1 = player.blockPosition();
				BlockEntity blockEntity = player.level().getBlockEntity(pos1);

				if(blockEntity instanceof SourceOfMagicPlaceholder){
					pos1 = ((SourceOfMagicPlaceholder)blockEntity).rootPos;
				}

				BlockEntity sourceOfMagic = player.level().getBlockEntity(pos1);

				if(sourceOfMagic instanceof SourceOfMagicTileEntity tile){

					if(!tile.isEmpty()){
						BlockState pState = sourceOfMagic.getBlockState();
						
						if(!SourceOfMagicBlock.shouldHarmPlayer(pState, player) || player.isCreative()){
								if(player.level().isClientSide()){
									Minecraft minecraft = Minecraft.getInstance();
									RandomSource random = player.getRandom();
									double x = -1 + random.nextDouble() * 2;
									double z = -1 + random.nextDouble() * 2;

									if(pState.getBlock() == DSBlocks.SEA_SOURCE_OF_MAGIC.get() || pState.getBlock() == DSBlocks.FOREST_SOURCE_OF_MAGIC.get()){
										if(!minecraft.isPaused()){
											player.level().addParticle(new BeaconParticle.MagicData(), player.getX() + x, player.getY() + 0.5, player.getZ() + z, 0, 0, 0);
										}
									}else if(pState.getBlock() == DSBlocks.CAVE_SOURCE_OF_MAGIC.get()){
										if(!minecraft.isPaused()){
											player.level().addParticle(new BeaconParticle.FireData(), player.getX() + x, player.getY() + 0.5, player.getZ() + z, 0, 0, 0);
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
	public static void playerTick(ClientTickEvent.Post event){
		Player player = Minecraft.getInstance().player;

		if(DragonStateProvider.isDragon(player)){
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);

			if(handler.getMagicData().onMagicSource){
				Vec3 velocity = player.getDeltaMovement();
				float groundSpeed = Mth.sqrt((float)(velocity.x * velocity.x + velocity.z * velocity.z));
				if(Math.abs(groundSpeed) > 0.05){
					PacketDistributor.sendToServer(new SyncMagicSourceStatus.Data(player.getId(), false, 0));
				}
			}
		}
	}

	@SubscribeEvent
	public static void playerAttacked(LivingHurtEvent event){
		LivingEntity entity = event.getEntity();

		if(entity instanceof Player player){
			if(!player.level().isClientSide()){
				DragonStateProvider.getCap(player).ifPresent(cap -> {
					if(cap.getMagicData().onMagicSource){
						cap.getMagicData().onMagicSource = false;
						cap.getMagicData().magicSourceTimer = 0;
						PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncMagicSourceStatus.Data(player.getId(), false, 0));
					}
				});
			}
		}
	}
}