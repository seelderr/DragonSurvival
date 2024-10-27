package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import static by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers.SLEEP_ON_TREASURE;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncTreasureRestStatus;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.util.Color;

@EventBusSubscriber
public class DragonTreasureHandler{
	private static int sleepTimer = 0;

	@SubscribeEvent
	public static void playerTick(PlayerTickEvent.Post event){
		if(event.getEntity().level().isClientSide()) {
			return;
		}

		Player player = event.getEntity();

		if(DragonStateProvider.isDragon(player)){
			DragonStateHandler handler = DragonStateProvider.getData(player);

			if(handler.treasureResting){
				if(player.isCrouching() || !(player.getBlockStateOn().getBlock() instanceof TreasureBlock) || handler.getMovementData().bite){
					handler.treasureResting = false;
					PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncTreasureRestStatus.Data(player.getId(), false));
					return;
				}

				handler.treasureSleepTimer++;

				if(ServerConfig.treasureHealthRegen){
					int horizontalRange = 16;
					int verticalRange = 9;
					int treasureNearby = 0;

					for(int x = -(horizontalRange / 2); x < horizontalRange / 2; x++){
						for(int y = -(verticalRange / 2); y < verticalRange / 2; y++){
							for(int z = -(horizontalRange / 2); z < horizontalRange / 2; z++){
								BlockPos pos = player.blockPosition().offset(x, y, z);
								BlockState state = player.level().getBlockState(pos);

								if(state.getBlock() instanceof TreasureBlock){
									int layers = state.getValue(TreasureBlock.LAYERS);
									treasureNearby += layers;
								}
							}
						}
					}
					treasureNearby = Mth.clamp(treasureNearby, 0, ServerConfig.maxTreasures);
					SLEEP_ON_TREASURE.get().trigger((ServerPlayer) event.getEntity(), treasureNearby);

					int totalTime = ServerConfig.treasureRegenTicks;
					int restTimer = totalTime - ServerConfig.treasureRegenTicksReduce * treasureNearby;

					if(handler.treasureRestTimer >= restTimer){
						handler.treasureRestTimer = 0;

						if(player.getHealth() < player.getMaxHealth() + 1){
							player.heal(1);
						}
					}else{
						handler.treasureRestTimer++;
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
			DragonStateHandler handler = DragonStateProvider.getData(player);

			if(handler.treasureResting){
				Vec3 velocity = player.getDeltaMovement();
				float groundSpeed = Mth.sqrt((float)(velocity.x * velocity.x + velocity.z * velocity.z));
				if(Math.abs(groundSpeed) > 0.05 || handler.getMovementData().dig){
					handler.treasureResting = false;
					PacketDistributor.sendToServer(new SyncTreasureRestStatus.Data(player.getId(), false));
				}
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void sleepScreenRender(RenderGuiLayerEvent.Post event){
		Player playerEntity = Minecraft.getInstance().player;

		if(!DragonStateProvider.isDragon(playerEntity) || playerEntity.isSpectator()){
			return;
		}

		DragonStateProvider.getOptional(playerEntity).ifPresent(cap -> {
			if(event.getName() == VanillaGuiLayers.AIR_LEVEL){

				Window window = Minecraft.getInstance().getWindow();
				float f = playerEntity.level().getSunAngle(1.0F);

				float f1 = f < (float)Math.PI ? 0.0F : (float)Math.PI * 2F;
				f = f + (f1 - f) * 0.2F;
				double val = Mth.cos(f);
				if(cap.treasureResting && val < 0.25 && sleepTimer < 100){
					sleepTimer++;
				}else if(sleepTimer > 0){
					sleepTimer--;
				}
				if(sleepTimer > 0){
					Color darkening = Color.ofRGBA(0.05f, 0.05f, 0.05f, Mth.lerp(Math.min(sleepTimer, 100) / 100f, 0, 0.5F));
					event.getGuiGraphics().fill(0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight(), darkening.getColor());
				}
			}
		});
	}


	@SubscribeEvent
	public static void playerAttacked(LivingIncomingDamageEvent event){
		LivingEntity entity = event.getEntity();

		if(entity instanceof Player player){

			if(!player.level().isClientSide()){
				DragonStateProvider.getOptional(player).ifPresent(cap -> {
					if(cap.treasureResting){
						cap.treasureResting = false;
						PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncTreasureRestStatus.Data(player.getId(), false));
					}
				});
			}
		}
	}

	// There is a third case for mining as well. See MiningTickHandler.java
}