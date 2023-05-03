package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncTreasureRestStatus;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.awt.Color;

@Mod.EventBusSubscriber
public class DragonTreasureHandler{
	private static int sleepTimer = 0;

	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
		if(event.phase == Phase.START || event.side == LogicalSide.CLIENT){
			return;
		}
		Player player = event.player;

		if(DragonUtils.isDragon(player)){
			DragonStateHandler handler = DragonUtils.getHandler(player);

			if(handler.treasureResting){
				if(player.isCrouching() || !(player.getFeetBlockState().getBlock() instanceof TreasureBlock) || handler.getMovementData().bite){
					handler.treasureResting = false;
					NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncTreasureRestStatus(player.getId(), false));
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
								BlockState state = player.level.getBlockState(pos);

								if(state.getBlock() instanceof TreasureBlock){
									int layers = state.getValue(TreasureBlock.LAYERS);
									treasureNearby += layers;
								}
							}
						}
					}
					treasureNearby = Mth.clamp(treasureNearby, 0, ServerConfig.maxTreasures);

					int totalTime = ServerConfig.treasureRegenTicks;
					int restTimer = totalTime - ServerConfig.treasureRegenTicksReduce * treasureNearby;

					if(handler.treasureRestTimer >= restTimer){
						handler.treasureRestTimer = 0;

						if(player.getHealth() < player.getMaxHealth()){
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
	public static void playerTick(ClientTickEvent event){
		if(event.phase == Phase.START){
			return;
		}
		Player player = Minecraft.getInstance().player;

		if(DragonUtils.isDragon(player)){
			DragonStateHandler handler = DragonUtils.getHandler(player);

			if(handler.treasureResting){
				Vec3 velocity = player.getDeltaMovement();
				float groundSpeed = Mth.sqrt((float)(velocity.x * velocity.x + velocity.z * velocity.z));
				if(Math.abs(groundSpeed) > 0.05){
					handler.treasureResting = false;
					NetworkHandler.CHANNEL.sendToServer(new SyncTreasureRestStatus(player.getId(), false));
				}
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void sleepScreenRender(RenderGuiOverlayEvent.Post event){
		Player playerEntity = Minecraft.getInstance().player;

		if(playerEntity == null || !DragonUtils.isDragon(playerEntity) || playerEntity.isSpectator()){
			return;
		}

		DragonStateProvider.getCap(playerEntity).ifPresent(cap -> {
			if(event.getOverlay() == VanillaGuiOverlay.AIR_LEVEL.type()){

				Window window = Minecraft.getInstance().getWindow();
				float f = playerEntity.level.getSunAngle(1.0F);

				float f1 = f < (float)Math.PI ? 0.0F : (float)Math.PI * 2F;
				f = f + (f1 - f) * 0.2F;
				double val = Mth.cos(f);
				if(cap.treasureResting && val < 0.25 && sleepTimer < 100){
					sleepTimer++;
				}else if(sleepTimer > 0){
					sleepTimer--;
				}
				if(sleepTimer > 0){
					Color darkening = new Color(0.05f, 0.05f, 0.05f, Mth.lerp(Math.min(sleepTimer, 100) / 100f, 0, 0.5F));
					Gui.fill(event.getPoseStack(), 0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight(), darkening.getRGB());
				}
			}
		});
	}

	@SubscribeEvent
	public static void playerAttacked(LivingHurtEvent event){
		LivingEntity entity = event.getEntity();

		if(entity instanceof Player player){
			
			if(!player.level.isClientSide){
				DragonStateProvider.getCap(player).ifPresent(cap -> {
					if(cap.treasureResting){
						cap.treasureResting = false;
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncTreasureRestStatus(player.getId(), false));
					}
				});
			}
		}
	}
}