package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class DragonSizeHandler{
	public static ConcurrentHashMap<Integer, Double> lastSize = new ConcurrentHashMap<Integer, Double>(20);
	private static final ConcurrentHashMap<Integer, Boolean> wasDragon = new ConcurrentHashMap<Integer, Boolean>(20);

	@SubscribeEvent
	public static void getDragonSize(EntityEvent.Size event){
		if(!(event.getEntity() instanceof PlayerEntity)){
			return;
		}
		PlayerEntity player = (PlayerEntity)event.getEntity();

		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(!dragonStateHandler.isDragon()){
				return;
			}
			double size = dragonStateHandler.getSize();
			// Calculate base values
			double height = calculateDragonHeight(size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get());
			double width = calculateDragonWidth(size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get());
			double eyeHeight = calculateDragonEyeHeight(size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get());
			// Handle Pose stuff
			if(ConfigHandler.SERVER.sizeChangesHitbox.get()){
				Pose overridePose = overridePose(player);
				height = calculateModifiedHeight(height, overridePose, true);
				eyeHeight = calculateModifiedEyeHeight(eyeHeight, overridePose);
				// Apply changes
				event.setNewEyeHeight((float)eyeHeight);
				// Rounding solves floating point issues that caused the dragon to get stuck inside a block at times.
				event.setNewSize(new EntitySize((float)(Math.round(width * 100.0D) / 100.0D), (float)(Math.round(height * 100.0D) / 100.0D), false));
			}
		});
	}

	public static double calculateDragonHeight(double size, boolean growsPastHuman){
		double height = (size + 4.0D) / 20.0D; // 0.9 -> Config Dragon Max
		if(!growsPastHuman){
			height = 0.9D + 0.9D * (size - 14.0D) / (ConfigHandler.SERVER.maxGrowthSize.get() - 14.0D); // 0.9 -> 1.8 (Min to Human Max)
		}
		return height;
	}

	public static double calculateDragonWidth(double size, boolean growsPastHuman){
		double width = (3.0D * size + 62.0D) / 260.0D; // 0.4 -> Config Dragon Max
		if(!growsPastHuman){
			width = 0.4D + 0.2D * (size - 14.0D) / (ConfigHandler.SERVER.maxGrowthSize.get() - 14.0D); // 0.4 -> 0.6 (Min to Human Max)
		}
		return width;
	}

	public static double calculateDragonEyeHeight(double size, boolean growsPastHuman){
		double eyeHeight = (11.0D * size + 54.0D) / 260.0D; // 0.8 -> Config Dragon Max
		if(!growsPastHuman){
			eyeHeight = 0.8D + 0.8D * (size - 14.0D) / (ConfigHandler.SERVER.maxGrowthSize.get() - 14.0D); // 0.8 -> 1.6 (Min to Human Max)
		}
		return eyeHeight;
	}

	public static double calculateModifiedEyeHeight(double eyeHeight, Pose pose){
		if(pose == Pose.CROUCHING){
			eyeHeight *= 5.0D / 6.0D;
		}else if(pose == Pose.SWIMMING || pose == Pose.FALL_FLYING || pose == Pose.SPIN_ATTACK){
			eyeHeight *= 7.0D / 12.0D;
		}
		return eyeHeight;
	}

	public static Pose overridePose(PlayerEntity player){
		Pose overridePose = getOverridePose(player);
		if(player.getForcedPose() != overridePose){
			player.setForcedPose(overridePose);
			if(player.level.isClientSide() && Minecraft.getInstance().cameraEntity != player){
				player.refreshDimensions();
			}
		}


		return overridePose;
	}

	public static Pose getOverridePose(LivingEntity player){
		DragonStateHandler handler = DragonUtils.getHandler(player);

		if(player != null){
			boolean swimming = (player.isInWaterOrBubble() || (player.isInLava() && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.caveLavaSwimming.get() && handler.getType() == DragonType.CAVE)) && player.isSprinting() && !player.isPassenger();
			boolean flying = ServerFlightHandler.isFlying(player);
			boolean spinning = player.isAutoSpinAttack();
			boolean crouching = player.isShiftKeyDown();
			if(flying && !player.isSleeping()){
				return Pose.FALL_FLYING;
			}else if(swimming || ((player.isInWaterOrBubble() || player.isInLava()) && !canPoseFit(player, Pose.STANDING) && canPoseFit(player, Pose.SWIMMING))){
				return Pose.SWIMMING;
			}else if(spinning){
				return Pose.SPIN_ATTACK;
			}else if(crouching || (!canPoseFit(player, Pose.STANDING) && canPoseFit(player, Pose.CROUCHING))){
				return Pose.CROUCHING;
			}
		}
		return Pose.STANDING;
	}

	public static boolean canPoseFit(LivingEntity player, Pose pose){
		if(!DragonStateProvider.getCap(player).isPresent()){
			return false;
		}
		double size = player.getCapability(DragonStateProvider.DRAGON_CAPABILITY).orElse(null).getSize();
		double height = calculateModifiedHeight(calculateDragonHeight((float)size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get()), pose, ConfigHandler.SERVER.sizeChangesHitbox.get());
		double width = calculateDragonWidth((float)size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get());
		return (player.level.getBlockCollisions(null, new AxisAlignedBB(player.position().subtract(width * 0.5D, 0.0D, width * 0.5D), player.position().add(width * 0.5D, height, width * 0.5D))).count() == 0);
	}

	public static double calculateModifiedHeight(double height, Pose pose, boolean sizeChangesHitbox){
		if(pose == Pose.CROUCHING){
			if(sizeChangesHitbox){
				height *= 5.0D / 6.0D;
			}else{
				height = 1.5D;
			}
		}else if(pose == Pose.SWIMMING || pose == Pose.FALL_FLYING || pose == Pose.SPIN_ATTACK){
			if(sizeChangesHitbox){
				height *= 7.0D / 12.0D;
			}else{
				height = 0.6D;
			}
		}
		return height;
	}

	@SubscribeEvent
	public static void playerTick(TickEvent.PlayerTickEvent event){
		PlayerEntity player = event.player;
		if(player == null || event.phase == TickEvent.Phase.END || !ConfigHandler.SERVER.sizeChangesHitbox.get()){
			return;
		}
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon()){
				overridePose(player);
				if(!wasDragon.getOrDefault(player.getId(), false)){
					player.refreshDimensions();
					wasDragon.put(player.getId(), true);
				}else if(lastSize.getOrDefault(player.getId(), 20.0) != dragonStateHandler.getSize()){
					player.refreshDimensions();
					lastSize.put(player.getId(), dragonStateHandler.getSize());
				}
			}else if(wasDragon.getOrDefault(player.getId(), false)){
				player.setForcedPose(null);
				player.refreshDimensions();
				wasDragon.put(player.getId(), false);
			}
		});
	}
}