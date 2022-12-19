package by.dragonsurvivalteam.dragonsurvival.magic.common.active;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active.StormBreathAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.AbilityAnimation;
import by.dragonsurvivalteam.dragonsurvival.magic.common.ISecondAnimation;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import by.dragonsurvivalteam.dragonsurvival.util.TargetingFunctions;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class BreathAbility extends ChannelingCastAbility implements ISecondAnimation{

	private static final int ARC = 45;
	public float yaw;
	public float pitch;
	public float speed;
	public float spread;
	public float xComp;
	public float yComp;
	public float zComp;
	public double dx;
	public double dy;
	public double dz;
	private int RANGE = 5;

	public static List<LivingEntity> getEntityLivingBaseNearby(LivingEntity source, double radius){
		return getEntitiesNearby(source, LivingEntity.class, radius);
	}

	public static <T extends Entity> List<T> getEntitiesNearby(LivingEntity source, Class<T> entityClass, double r){
		return source.level.getEntitiesOfClass(entityClass, source.getBoundingBox().inflate(r, r, r), e -> e != source && source.distanceTo(e) <= r + e.getBbWidth() / 2f && e.getY() <= source.getY() + r);
	}

	public boolean requiresStationaryCasting(){
		return false;
	}
	@Override
	public boolean canCastSkill(Player player){
		if(ServerFlightHandler.isGliding(player))
			return false;
		return super.canCastSkill(player);
	}

	@Override
	public void onCharging(Player player, int currentChargeTime){}

	@Override
	public void onChanneling(Player player, int castDuration){
		DragonStateHandler playerStateHandler = DragonUtils.getHandler(player);

		DragonLevel growthLevel = DragonStateProvider.getCap(player).map(cap -> cap.getLevel()).get();
		RANGE = (int)Math.round(4 + (playerStateHandler.getSize() - DragonLevel.NEWBORN.size) / (DragonLevel.ADULT.size - DragonLevel.NEWBORN.size) * 4);
		yaw = (float)Math.toRadians(-player.getYRot());
		pitch = (float)Math.toRadians(-player.getXRot());
		speed = growthLevel == DragonLevel.NEWBORN ? 0.1F : growthLevel == DragonLevel.YOUNG ? 0.2F : 0.3F; //Changes distance
		spread = 0.1f;
		xComp = (float)(Math.sin(yaw) * Math.cos(pitch));
		yComp = (float)Math.sin(pitch);
		zComp = (float)(Math.cos(yaw) * Math.cos(pitch));

		double headRot = playerStateHandler.getMovementData().headYaw;
		double pitch = playerStateHandler.getMovementData().headPitch;
		Vector3f bodyRot = Functions.getDragonCameraOffset(player);

		Point2D result = new Double();
		Point2D result2 = new Double();

		Point2D point = new Point2D.Double(player.position().x() + bodyRot.x(), player.position().y() + player.getEyeHeight() - 0.2);
		AffineTransform transform = new AffineTransform();
		double angleInRadians = Mth.clamp(pitch, -90, 90) * -1 * Math.PI / 180;
		transform.rotate(angleInRadians, player.position().x(), player.position().y() + player.getEyeHeight() - 0.2);
		transform.transform(point, result);

		Point2D point2 = new Point2D.Double(player.position().x() + bodyRot.x(), player.position().z() + bodyRot.z());
		AffineTransform transform2 = new AffineTransform();
		double angleInRadians2 = Mth.clamp(headRot, -180, 180) * -1 * Math.PI / 180;
		transform2.rotate(angleInRadians2, player.position().x(), player.position().z());
		transform2.transform(point2, result2);

		dx = result2.getX();
		dy = result.getY() - Math.abs(headRot) / 180 * .5;// * sizeScale;
		dz = result2.getY();

		Vec3 delta = player.getDeltaMovement();

		if(player.isFallFlying() || player.getAbilities().flying)
			yComp += (float)delta.y * 6;

		xComp += (float)delta.x * 6;
		zComp += (float)delta.z * 6;
	}


	@Override
	public AbilityAnimation getLoopingAnimation(){
		return new AbilityAnimation("breath", false, false);
	}

	public void hitEntities(){
		Vec3 targetVec = TargetingFunctions.fromEntityCenter(player).add(player.getLookAngle().scale(RANGE)).add(0, 0.5, 0);
		List<Entity>  entities = player.level.getEntities(player, TargetingFunctions.boxForRange(targetVec, 3), e -> !e.isSpectator() && e.isAlive() && TargetingFunctions.isValidTarget(getPlayer(), e));

		for(Entity entity : entities){
			if(entity instanceof LivingEntity livingEntity){
				if(!canHitEntity(livingEntity))
					return;

				if(livingEntity.getLastHurtByMob() == player && livingEntity.getLastHurtByMobTimestamp() + Functions.secondsToTicks(1) < livingEntity.tickCount)
					continue;

				onEntityHit(livingEntity);
			}
		}
	}

	public abstract boolean canHitEntity(LivingEntity entity);

	public void onEntityHit(LivingEntity entityHit){
		if(TargetingFunctions.attackTargets(getPlayer(), entity -> entity.hurt(new BreathDamage(player), getDamage()), entityHit)){
			entityHit.setDeltaMovement(entityHit.getDeltaMovement().multiply(0.25, 1, 0.25));
			onDamage(entityHit);
		}
	}

	public abstract void onDamage(LivingEntity entity);

	public abstract float getDamage();

	public List<LivingEntity> getEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius){
		return getEntitiesNearby(LivingEntity.class, distanceX, distanceY, distanceZ, radius);
	}

	public <T extends Entity> List<T> getEntitiesNearby(Class<T> entityClass, double dX, double dY, double dZ, double r){
		return player.level.getEntitiesOfClass(entityClass, player.getBoundingBox().inflate(dX, dY, dZ), e -> e != player && player.distanceTo(e) <= r + e.getBbWidth() / 2f && e.getY() <= player.getY() + dY);
	}

	public void hitBlocks(){
		Vec3 vector3d = player.getEyePosition(1.0F);
		Vec3 vector3d1 = player.getViewVector(1.0F).scale(RANGE);
		Vec3 vector3d2 = vector3d.add(vector3d1);
		BlockHitResult result = player.level.clip(new ClipContext(vector3d, vector3d2, ClipContext.Block.OUTLINE, this instanceof StormBreathAbility ? ClipContext.Fluid.NONE : ClipContext.Fluid.ANY, null));

		BlockPos pos = null;

		if(result.getType() == HitResult.Type.MISS)
			pos = new BlockPos(vector3d2.x, vector3d2.y, vector3d2.z);
		else if(result.getType() == HitResult.Type.BLOCK)
			pos = result.getBlockPos();
		if(pos == null)
			return;

		for(int x = -(RANGE / 2); x < RANGE / 2; x++)
			for(int y = -(RANGE / 2); y < RANGE / 2; y++){
				for(int z = -(RANGE / 2); z < RANGE / 2; z++){
					BlockPos newPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
					if(newPos.distSqr(pos) <= RANGE){
						BlockState state = player.level.getBlockState(newPos);
						if(state.getBlock() != Blocks.AIR){
							if(DragonConfigHandler.DRAGON_BREATH_BLOCKS != null && DragonConfigHandler.DRAGON_BREATH_BLOCKS.containsKey(getDragonType().getTypeName()) && DragonConfigHandler.DRAGON_BREATH_BLOCKS.get(getDragonType().getTypeName()).contains(state.getBlock())){
								if(!player.level.isClientSide){
									if(player.level.random.nextFloat() * 100 <= blockBreakChance()){
										player.level.destroyBlock(newPos, false, player);
										continue;
									}
								}
							}

							if(newPos != null && state != null){
								onBlock(newPos, state, result.getDirection());
							}
						}
					}
				}
			}
	}

	public abstract void onBlock(BlockPos pos, BlockState blockState, Direction direction);

	public int blockBreakChance(){
		return 90;
	}

	@Override
	public int getSortOrder(){
		return 1;
	}

	@Override
	public ArrayList<Component> getInfo(){
		ArrayList<Component> components = new ArrayList<Component>();

		DragonLevel growthLevel = DragonUtils.getDragonLevel(player);
		int RANGE = growthLevel == DragonLevel.NEWBORN ? 4 : growthLevel == DragonLevel.YOUNG ? 7 : 10;

		components.add(new TranslatableComponent("ds.skill.mana_cost", getChargingManaCost()));
		components.add(new TranslatableComponent("ds.skill.channel_cost", getManaCost(), 2));

		components.add(new TranslatableComponent("ds.skill.cast_time", nf.format((double)getSkillChargeTime() / 20)));
		components.add(new TranslatableComponent("ds.skill.cooldown", Functions.ticksToSeconds(getSkillCooldown())));

		components.add(new TranslatableComponent("ds.skill.damage", getDamage()));
		components.add(new TranslatableComponent("ds.skill.range.blocks", RANGE));

		if(!KeyInputHandler.ABILITY1.isUnbound()){
			String key = KeyInputHandler.ABILITY1.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);

			if(key.isEmpty())
				key = KeyInputHandler.ABILITY1.getKey().getDisplayName().getString();
			components.add(new TranslatableComponent("ds.skill.keybind", key));
		}

		return components;
	}

	public static class BreathDamage extends EntityDamageSource{
		public BreathDamage(
			@Nullable
				Entity p_i1567_2_){
			super("player", p_i1567_2_);
		}
	}
}