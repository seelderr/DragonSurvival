package by.jackraidenph.dragonsurvival.handlers;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.gecko.entity.DragonPartEntity;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.ClientDragonRender;
import by.jackraidenph.dragonsurvival.util.DragonType;
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
public class DragonSizeHandler {
	@SubscribeEvent
    public static void getDragonSize(EntityEvent.Size event) {
    	if (!(event.getEntity() instanceof PlayerEntity) && !(event.getEntity() instanceof DragonPartEntity))
    		return;
    	PlayerEntity player = null;
		
		if(event.getEntity() instanceof PlayerEntity){
			player = (PlayerEntity)event.getEntity();
		}else{
			player = (PlayerEntity)((DragonPartEntity)event.getEntity()).parentMob;
		}
		
		boolean isHitbox = event.getEntity() instanceof DragonPartEntity;
		
		PlayerEntity finalPlayer = player;
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
    		if (!dragonStateHandler.isDragon())
    			return;
    		double size = dragonStateHandler.getSize();
    		// Calculate base values
		    double height = calculateDragonHeight(size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get());
		    double width = calculateDragonWidth(size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get());
		    double eyeHeight = calculateDragonEyeHeight(size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get());
    		// Handle Pose stuff
    		if (ConfigHandler.SERVER.sizeChangesHitbox.get()) {
    		Pose overridePose = overridePose(finalPlayer);
    		height = calculateModifiedHeight(height, overridePose, true);
    		eyeHeight = calculateModifiedEyeHeight(eyeHeight, overridePose);
    		// Apply changes
			event.setNewEyeHeight((float)eyeHeight);
    		event.setNewSize(new EntitySize((float)(isHitbox ? width * 2F : width), (float)height, false));
    		}
        });
    }
	
    
	public static double calculateDragonHeight(double size, boolean growsPastHuman) {
		double height = (size + 4.0F) / 20.0F; // 0.9 -> 2.2
		if (!growsPastHuman)
			height = 9F * (size + 12F) / 260F; // 0.9 -> 1.8
		return height;
	}

	public static double calculateDragonWidth(double size, boolean growsPastHuman) {
		double width = (3.0F * size + 62.0F) / 260.0F; // 0.4 -> 0.7
		if (!growsPastHuman)
			width = (size + 38) / 130F; // 0.4 -> 0.6
		return width;
	}

	public static double calculateDragonEyeHeight(double size, boolean growsPastHuman) {
		double eyeHeight = (11.0F * size + 54.0F) / 260.0F; // 0.8 -> 1.9
		if (!growsPastHuman)
			eyeHeight = (41F * size + 466F) / 1300F; // 14, 0.8 -> 40, 1.62
		return eyeHeight;
	}

	public static double calculateModifiedHeight(double height, Pose pose, boolean sizeChangesHitbox) {
    	if (pose == Pose.CROUCHING) {
    		if (sizeChangesHitbox)
    			height *= 5.0F / 6.0F;
    		else
    			height = 1.5F;
		} else if (pose == Pose.SWIMMING || pose == Pose.FALL_FLYING || pose == Pose.SPIN_ATTACK) {
			if (sizeChangesHitbox)
				height *= 7.0F / 12.0F;
			else
				height = 0.6F;
		}
    	return height;
    }

	public static double calculateModifiedEyeHeight(double eyeHeight, Pose pose) {
    	if (pose == Pose.CROUCHING) {
    		eyeHeight *= 5.0F / 6.0F;
		} else if (pose == Pose.SWIMMING || pose == Pose.FALL_FLYING || pose == Pose.SPIN_ATTACK) {
			eyeHeight *= 7.0F / 12.0F;
		}
    	return eyeHeight;
    }
    
    public static boolean canPoseFit(LivingEntity player, Pose pose) {
    	if (!DragonStateProvider.getCap(player).isPresent())
    		return false;
	    double size = player.getCapability(DragonStateProvider.DRAGON_CAPABILITY).orElse(null).getSize();
	    double height = calculateModifiedHeight(calculateDragonHeight((float)size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get()), pose, ConfigHandler.SERVER.sizeChangesHitbox.get());
	    double width = calculateDragonWidth((float)size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get());
		return (player.level.getBlockCollisions(null, new AxisAlignedBB(
				player.position().subtract(width * 0.5D, 0.0D, width * 0.5D), 
				player.position().add(width * 0.5D, height, width * 0.5D)))
		.count() == 0);
    }
    
    private static Pose overridePose(PlayerEntity player) {
		Pose overridePose = getOverridePose(player);
		if (player.getForcedPose() != overridePose) {
			player.setForcedPose(overridePose);
			if (player.level.isClientSide() && Minecraft.getInstance().cameraEntity != player)
				player.refreshDimensions();
		}


		return overridePose;
	}

	/**
	 * Server Only
	 */
	public static ConcurrentHashMap<Integer, Boolean> wingsStatusServer = new ConcurrentHashMap<>(20);

	public static Pose getOverridePose(LivingEntity player) {
		boolean swimming = (player.isInWaterOrBubble() || (player.isInLava() && ConfigHandler.SERVER.bonuses.get() && ConfigHandler.SERVER.caveLavaSwimming.get() && DragonStateProvider.getCap(player).orElseGet(null).getType() == DragonType.CAVE)) && player.isSprinting() && !player.isPassenger();
		boolean flying = (player.level.isClientSide && ClientDragonRender.dragonsFlying.getOrDefault(player.getId(), false) && !player.isInWater() && !player.isInLava() && !player.isOnGround() && player.getCapability(DragonStateProvider.DRAGON_CAPABILITY).orElse(null).hasWings())
				|| (!player.level.isClientSide && !player.isOnGround() && wingsStatusServer.getOrDefault(player.getId(), false) && !player.isInWater() && !player.isInLava() && player.getCapability(DragonStateProvider.DRAGON_CAPABILITY).orElse(null).hasWings());
		boolean spinning = player.isAutoSpinAttack();
		boolean crouching = player.isShiftKeyDown();
		if (flying && !player.isSleeping())
			return Pose.FALL_FLYING;
		else if (swimming || ((player.isInWaterOrBubble() || player.isInLava()) && !canPoseFit(player, Pose.STANDING) && canPoseFit(player, Pose.SWIMMING)))
			return Pose.SWIMMING;
		else if (spinning)
			return Pose.SPIN_ATTACK;
		else if (crouching || (!canPoseFit(player, Pose.STANDING) && canPoseFit(player, Pose.CROUCHING)))
				return Pose.CROUCHING;
		return Pose.STANDING;
    }
    
    private static ConcurrentHashMap<Integer, Boolean> wasDragon = new ConcurrentHashMap<Integer, Boolean>(20);
    public static ConcurrentHashMap<Integer, Double> lastSize = new ConcurrentHashMap<Integer, Double>(20);
    
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
    	PlayerEntity player = event.player;
    	if (player == null || event.phase == TickEvent.Phase.END || !ConfigHandler.SERVER.sizeChangesHitbox.get())
    		return;
    	DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
    		if (dragonStateHandler.isDragon()) {
    			overridePose(player);
    			if (!wasDragon.getOrDefault(player.getId(), false)) {
    				player.refreshDimensions();
    				wasDragon.put(player.getId(), true);
    			}
    			else if (lastSize.getOrDefault(player.getId(), 20.0) != dragonStateHandler.getSize()) {
    				player.refreshDimensions();
    				lastSize.put(player.getId(), dragonStateHandler.getSize());
    			}
    		} else if (wasDragon.getOrDefault(player.getId(), false)) {
    			player.setForcedPose(null);
    			player.refreshDimensions();
    			wasDragon.put(player.getId(), false);
			}
    	});
    }
}


