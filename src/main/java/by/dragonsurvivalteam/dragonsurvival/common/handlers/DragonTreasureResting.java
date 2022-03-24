<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonTreasureResting.java
package by.jackraidenph.dragonsurvival.common.handlers;

import by.jackraidenph.dragonsurvival.common.blocks.TreasureBlock;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.status.SyncTreasureRestStatus;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
import com.mojang.blaze3d.platform.Window;
=======
package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.mixins.MixinServerWorld;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncTreasureRestStatus;
import net.minecraft.block.BlockState;
import net.minecraft.client.MainWindow;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonTreasureResting.java
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonTreasureResting.java
import net.minecraftforge.network.PacketDistributor;

import java.awt.Color;
import java.util.List;
=======
import net.minecraftforge.fml.network.PacketDistributor;

import java.awt.Color;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonTreasureResting.java
import java.util.Optional;

@Mod.EventBusSubscriber
public class DragonTreasureResting{
	private static int sleepTimer = 0;

	@SubscribeEvent
	public static void playerTick(PlayerTickEvent event){
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonTreasureResting.java
		if(event.phase == Phase.START || event.side == LogicalSide.CLIENT) return;
		Player player = event.player;
		
		if(DragonUtils.isDragon(player)){
			DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
			
=======
		if(event.phase == Phase.START || event.side == LogicalSide.CLIENT){
			return;
		}
		PlayerEntity player = event.player;

		if(DragonUtils.isDragon(player)){
			DragonStateHandler handler = DragonUtils.getHandler(player);

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonTreasureResting.java
			if(handler != null){
				if(handler.treasureResting){
					if(player.isCrouching() || !(player.getFeetBlockState().getBlock() instanceof TreasureBlock) || handler.getMovementData().bite){
						handler.treasureResting = false;
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncTreasureRestStatus(player.getId(), false));
						return;
					}

					handler.treasureSleepTimer++;

					if(ConfigHandler.SERVER.treasureHealthRegen.get()){
						int horizontalRange = 16;
						int verticalRange = 9;
						int treasureNearby = 0;

						for(int x = -(horizontalRange / 2); x < (horizontalRange / 2); x++){
							for(int y = -(verticalRange / 2); y < (verticalRange / 2); y++){
								for(int z = -(horizontalRange / 2); z < (horizontalRange / 2); z++){
									BlockPos pos = player.blockPosition().offset(x, y, z);
									BlockState state = player.level.getBlockState(pos);

									if(state.getBlock() instanceof TreasureBlock){
										int layers = state.getValue(TreasureBlock.LAYERS);
										treasureNearby += layers;
									}
								}
							}
						}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonTreasureResting.java
						treasureNearby = Mth.clamp(treasureNearby, 0, ConfigHandler.SERVER.maxTreasures.get());
						
=======
						treasureNearby = MathHelper.clamp(treasureNearby, 0, ConfigHandler.SERVER.maxTreasures.get());

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonTreasureResting.java
						int totalTime = ConfigHandler.SERVER.treasureRegenTicks.get();
						int restTimer = totalTime - (ConfigHandler.SERVER.treasureRegenTicksReduce.get() * treasureNearby);

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
	}

	@SubscribeEvent
	public static void serverTick(WorldTickEvent event){
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonTreasureResting.java
		if(event.phase == Phase.START) return;
		if(event.side == LogicalSide.CLIENT) return;
		
		ServerLevel world = (ServerLevel)event.world;
		List<ServerPlayer> playerList = world.players();
		playerList.removeIf((pl) -> {
			if(DragonUtils.isDragon(pl)) {
				DragonStateHandler handler = DragonStateProvider.getCap(pl).orElse(null);
				
				if (ForgeEventFactory.fireSleepingTimeCheck(pl, Optional.empty())) {
					
					if (handler != null) {
						if (handler.treasureResting) {
							return true;
						}
=======
		if(event.phase == Phase.START){
			return;
		}
		if(event.side == LogicalSide.CLIENT){
			return;
		}

		ServerWorld world = (ServerWorld)event.world;
		MixinServerWorld serverWorld = (MixinServerWorld)world;
		if(!serverWorld.getallPlayersSleeping()){
			if(!serverWorld.getPlayers().isEmpty()){
				int i = 0;
				int j = 0;

				if(world.getGameTime() % 20 == 0){
					for(ServerPlayerEntity serverplayerentity : serverWorld.getPlayers()){
						if(serverplayerentity.isSpectator()){
							++i;
						}else if(serverplayerentity.isSleeping()){
							++j;
						}else if(DragonUtils.isDragon(serverplayerentity)){
							DragonStateHandler handler = DragonStateProvider.getCap(serverplayerentity).orElse(null);

							if(ForgeEventFactory.fireSleepingTimeCheck(serverplayerentity, Optional.empty())){

								if(handler != null){
									if(handler.treasureResting){
										++j;
									}
								}
							}else{
								handler.treasureSleepTimer = 0;
							}
						}
					}

					boolean all = j > 0 && j >= serverWorld.getPlayers().size() - i;

					if(all){
						serverWorld.setallPlayersSleeping(true);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonTreasureResting.java
					}
				}else{
					handler.treasureSleepTimer = 0;
				}
			}
			return false;
		});
		
		world.sleepStatus.update(playerList);
	}

	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void playerTick(ClientTickEvent event){
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonTreasureResting.java
		if(event.phase == Phase.START) return;
		Player player = Minecraft.getInstance().player;
		
		if(DragonUtils.isDragon(player)){
			DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
			
=======
		if(event.phase == Phase.START){
			return;
		}
		PlayerEntity player = Minecraft.getInstance().player;

		if(DragonUtils.isDragon(player)){
			DragonStateHandler handler = DragonUtils.getHandler(player);

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonTreasureResting.java
			if(handler != null){
				if(handler.treasureResting){
					Vec3 velocity = player.getDeltaMovement();
					float groundSpeed = Mth.sqrt((float)((velocity.x * velocity.x) + (velocity.z * velocity.z)));
					if(Math.abs(groundSpeed) > 0.05){
						handler.treasureResting = false;
						NetworkHandler.CHANNEL.sendToServer(new SyncTreasureRestStatus(player.getId(), false));
					}
				}
			}
		}
	}

	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonTreasureResting.java
	public static void sleepScreenRender(RenderGameOverlayEvent.Post event) {
		Player playerEntity = Minecraft.getInstance().player;
		
		if (playerEntity == null || !DragonUtils.isDragon(playerEntity) || playerEntity.isSpectator())
=======
	public static void sleepScreenRender(RenderGameOverlayEvent.Post event){
		PlayerEntity playerEntity = Minecraft.getInstance().player;

		if(playerEntity == null || !DragonUtils.isDragon(playerEntity) || playerEntity.isSpectator()){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonTreasureResting.java
			return;
		}

		DragonStateProvider.getCap(playerEntity).ifPresent(cap -> {
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonTreasureResting.java
			if (event.getType() == ElementType.ALL) {
				
				Window window = Minecraft.getInstance().getWindow();
=======
			if(event.getType() == ElementType.HOTBAR){
				MainWindow window = Minecraft.getInstance().getWindow();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonTreasureResting.java
				float f = playerEntity.level.getSunAngle(1.0F);

				float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
				f = f + (f1 - f) * 0.2F;
				double val = Mth.cos(f);
				if(cap.treasureResting && val < 0.25 && sleepTimer < 100){
					sleepTimer++;
				}else if(sleepTimer > 0){
					sleepTimer--;
				}
				if(sleepTimer > 0){
					Color darkening = new Color(0.05f, 0.05f, 0.05f, Mth.lerp(Math.min(sleepTimer, 100) / 100f, 0, 0.5F));
					Gui.fill(event.getMatrixStack(), 0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight(), darkening.getRGB());
				}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonTreasureResting.java
				
				
=======
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonTreasureResting.java
			}
		});
	}

	@SubscribeEvent
	public static void playerAttacked(LivingHurtEvent event){
		LivingEntity entity = event.getEntityLiving();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/handlers/DragonTreasureResting.java
		
		if(entity instanceof Player){
			Player player = (Player)entity;
			
			if(!player.level.isClientSide) {
=======

		if(entity instanceof PlayerEntity){
			PlayerEntity player = (PlayerEntity)entity;

			if(!player.level.isClientSide){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/handlers/DragonTreasureResting.java
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