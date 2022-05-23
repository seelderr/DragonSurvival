package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import com.google.common.collect.Lists;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class DragonHitBox extends LivingEntity{
	public static final EntityDataAccessor<Integer> PLAYER_ID = SynchedEntityData.defineId(DragonHitBox.class, EntityDataSerializers.INT);

	private final DragonHitboxPart[] subEntities;
	private final DragonHitboxPart head;
	private final DragonHitboxPart body;
	private final DragonHitboxPart tail1;
	private final DragonHitboxPart tail2;
	private final DragonHitboxPart tail3;
	private final DragonHitboxPart tail4;
	private final DragonHitboxPart tail5;
	public EntityDimensions size;
	public Player player;
	private double lastSize;
	private Pose lastPose;

	public DragonHitBox(EntityType<? extends LivingEntity> p_i48577_1_, Level p_i48580_2_){
		super(p_i48577_1_, p_i48580_2_);

		this.head = new DragonHitboxPart(this, "head", 0.5F, 0.5F);
		this.body = new DragonHitboxPart(this, "body", 1.0F, 1.0F);
		this.tail1 = new DragonHitboxPart(this, "tail1", 1.0F, 1.0F);
		this.tail2 = new DragonHitboxPart(this, "tail2", 1.0F, 1.0F);
		this.tail3 = new DragonHitboxPart(this, "tail3", 1.0F, 1.0F);
		this.tail4 = new DragonHitboxPart(this, "tail4", 1.0F, 1.0F);
		this.tail5 = new DragonHitboxPart(this, "tail5", 1.0F, 1.0F);

		this.subEntities = new DragonHitboxPart[]{this.head, this.body, this.tail1, this.tail2, this.tail3, this.tail4, this.tail5};

		this.size = EntityDimensions.scalable(1f, 1f);
		this.refreshDimensions();
	}

	@Override
	protected void defineSynchedData(){
		super.defineSynchedData();
		this.entityData.define(PLAYER_ID, -1);
	}

	@Override
	public void tick(){
		super.tick();
		this.checkInsideBlocks();
	}

	@Override
	public void aiStep(){
		if(level.isClientSide && tickCount > 20){
			if(player == null){
				if(getPlayerId() != -1){
					Entity ent = level.getEntity(getPlayerId());

					if(ent instanceof Player){
						player = (Player)ent;
					}
				}
				return;
			}
		}else{
			if(player == null || player.isDeadOrDying() || !DragonUtils.isDragon(player)){
				if(!level.isClientSide){
					this.remove(RemovalReason.DISCARDED);
				}
			}
		}

		if(player == null){
			return;
		}

		DragonStateHandler handler = DragonUtils.getHandler(player);

		if(handler.getMovementData() == null){
			return;
		}

		Vector3f offset = DragonUtils.getCameraOffset(player);

		double size = handler.getSize();
		double height = DragonSizeHandler.calculateDragonHeight(size, ServerConfig.hitboxGrowsPastHuman);
		double width = DragonSizeHandler.calculateDragonWidth(size, ServerConfig.hitboxGrowsPastHuman);

		Pose overridePose = DragonSizeHandler.overridePose(player);
		height = DragonSizeHandler.calculateModifiedHeight(height, overridePose, true);


		double headRot = handler.getMovementData().headYaw;
		double pitch = handler.getMovementData().headPitch * -1;
		Vector3f bodyRot = DragonUtils.getCameraOffset(player);

		bodyRot = new Vector3f(bodyRot.x() / 2, bodyRot.y() / 2, bodyRot.z() / 2);

		Point2D result = new Point2D.Double();
		Point2D result2 = new Point2D.Double();

		{
			Point2D point = new Double(player.position().x() + bodyRot.x(), player.position().y() + player.getEyeHeight());
			AffineTransform transform = new AffineTransform();
			double angleInRadians = ((Mth.clamp(pitch, -90, 90) * -1) * Math.PI / 360);
			transform.rotate(angleInRadians, player.position().x(), player.position().y() + player.getEyeHeight());
			transform.transform(point, result);
		}

		{
			Point2D point2 = new Double(player.position().x() + bodyRot.x(), player.position().z() + bodyRot.z());
			AffineTransform transform2 = new AffineTransform();
			double angleInRadians2 = ((Mth.clamp(headRot, -180, 180) * -1) * Math.PI / 360);
			transform2.rotate(angleInRadians2, player.position().x(), player.position().z());
			transform2.transform(point2, result2);
		}

		double dx = result2.getX();
		double dy = result.getY() - (Math.abs(headRot) / 180 * .5);
		double dz = result2.getY();

		if(lastSize != size || lastPose != overridePose){
			this.size = EntityDimensions.scalable((float)width * 1.6f, handler.getPassengerId() != 0 ? (float)(height / 2f) : (float)height); //Half hitbox size if there is a rider
			refreshDimensions();

			body.size = EntityDimensions.scalable((float)width * 1.6f, handler.getPassengerId() != 0 ? (float)(height / 2f) : (float)height); //Half hitbox size if there is a rider
			body.refreshDimensions();

			head.size = EntityDimensions.scalable((float)width, (float)width);
			head.refreshDimensions();

			tail1.size = EntityDimensions.scalable((float)width, (float)height / 3);
			tail1.refreshDimensions();

			tail2.size = EntityDimensions.scalable((float)width * 0.8f, (float)height / 3);
			tail2.refreshDimensions();

			tail3.size = EntityDimensions.scalable((float)width * 0.7f, (float)height / 3);
			tail3.refreshDimensions();

			tail4.size = EntityDimensions.scalable((float)width * 0.7f, (float)height / 3);
			tail4.refreshDimensions();

			tail5.size = EntityDimensions.scalable((float)width * 0.7f, (float)height / 3);
			tail5.refreshDimensions();

			lastSize = size;
			lastPose = overridePose;
			player.refreshDimensions();
		}else{
			setPos(player.getX() - offset.x(), player.getY(), player.getZ() - offset.z());
			xRot = (float)handler.getMovementData().headPitch;
			yRot = (float)handler.getMovementData().bodyYaw;

			body.setPos(player.getX() - offset.x(), player.getY(), player.getZ() - offset.z());
			body.xRot = xRot;
			body.yRot = yRot;

			head.setPos(dx, dy - (DragonSizeHandler.calculateDragonWidth(handler.getSize(), ServerConfig.hitboxGrowsPastHuman) / 2), dz);
			tail1.setPos(getX() - offset.x(), getY() + (player.getEyeHeight() / 2) - (height / 9), getZ() - offset.z());
			tail2.setPos(getX() - offset.x() * 1.5, getY() + (player.getEyeHeight() / 2) - (height / 9), getZ() - offset.z() * 1.5);
			tail3.setPos(getX() - offset.x() * 2, getY() + (player.getEyeHeight() / 2) - (height / 9), getZ() - offset.z() * 2);
			tail4.setPos(getX() - offset.x() * 2.5, getY() + (player.getEyeHeight() / 2) - (height / 9), getZ() - offset.z() * 2.5);
			tail5.setPos(getX() - offset.x() * 3, getY() + (player.getEyeHeight() / 2) - (height / 9), getZ() - offset.z() * 3);
		}
	}

	@Override
	public boolean hasCustomName(){
		return true;
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public Component getCustomName(){
		return TextComponent.EMPTY;
	}

	@Override
	public boolean isInvisibleTo(Player pPlayer){
		return pPlayer.getId() == getPlayerId();
	}

	@Override
	public boolean shouldShowName(){
		return false;
	}

	@Override
	public boolean isCustomNameVisible(){
		return false;
	}

	public int getPlayerId(){
		return this.entityData.get(PLAYER_ID);
	}

	public void setPlayerId(int id){
		this.entityData.set(PLAYER_ID, id);
	}

	public boolean hurt(DragonHitboxPart part, DamageSource source, float damage){
		return hurt(source, damage);
	}

	@Override
	public boolean hurt(DamageSource source, float damage){
		if(source.getEntity() == player || source.getDirectEntity() == player) return false;
		return player != null && !this.isInvulnerableTo(source) && player.hurt(source, damage);
	}

	@Override
	public Iterable<ItemStack> getArmorSlots(){
		return player != null ? player.getArmorSlots() : Lists.newArrayList();
	}

	@Override
	public ItemStack getItemBySlot(EquipmentSlot pSlot){
		return player != null ? player.getItemBySlot(pSlot) : ItemStack.EMPTY;
	}

	@Override
	public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack){}

	@Override
	public boolean isPickable(){
		return level.isClientSide && !isOwner();
	}

	@Override
	public HumanoidArm getMainArm(){
		return player != null ? player.getMainArm() : HumanoidArm.LEFT;
	}

	@OnlyIn( Dist.CLIENT )
	public boolean isOwner(){
		return player == Minecraft.getInstance().player;
	}

	@Override
	public Packet<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public EntityDimensions getDimensions(Pose pPose){
		return size;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource pSource){
		return super.isInvulnerableTo(pSource) || pSource == DamageSource.CRAMMING || pSource == DamageSource.IN_WALL || pSource == DamageSource.LIGHTNING_BOLT || player != null && player.isInvulnerableTo(pSource);
	}

	@Override
	public int getAirSupply(){
		return getMaxAirSupply();
	}

	@Override
	public InteractionResult interact(Player pPlayer, InteractionHand pHand){
		return player != null ? player.interact(pPlayer, pHand) : InteractionResult.PASS;
	}

	@Override
	protected void playHurtSound(DamageSource pSource){}

	@Override
	protected SoundEvent getHurtSound(DamageSource pDamageSource){
		return null;
	}

	@Override
	protected SoundEvent getDeathSound(){
		return null;
	}

	@Override
	public Fallsounds getFallSounds(){
		return null;
	}

	@Override
	protected void playBlockFallSound(){}

	@Override
	protected float getSoundVolume(){
		return 0;
	}

	@Override
	protected SoundEvent getDrinkingSound(ItemStack pStack){
		return null;
	}

	@Override
	public SoundEvent getEatingSound(ItemStack pItemStack){
		return null;
	}

	@Override
	protected void playStepSound(BlockPos pPos, BlockState pBlock){}

	@Override
	public void playSound(SoundEvent pSound, float pVolume, float pPitch){}

	@Override
	protected void playEntityOnFireExtinguishedSound(){}

	@Override
	public boolean canCollideWith(Entity pEntity){
		return false;
	}

	@Override
	public boolean canBeCollidedWith(){
		return false;
	}

	@Override
	public boolean isColliding(BlockPos pPos, BlockState pState){
		return false;
	}

	@Override
	protected void playSwimSound(float pVolume){}

	@Override
	public boolean removeAllEffects(){
		return super.removeAllEffects();
	}

	@Override
	public MobEffectInstance removeEffectNoUpdate(@org.jetbrains.annotations.Nullable MobEffect pPotioneffectin){
		return player != null ? player.removeEffectNoUpdate(pPotioneffectin) : super.removeEffectNoUpdate(pPotioneffectin);
	}

	@Override
	public boolean removeEffect(MobEffect pEffect){
		return player != null ? player.removeEffect(pEffect) : super.removeEffect(pEffect);
	}

	@Override
	public MobEffectInstance getEffect(MobEffect pPotion){
		return player != null ? player.getEffect(pPotion) : super.getEffect(pPotion);
	}

	@Override
	public boolean addEffect(MobEffectInstance p_147208_, @org.jetbrains.annotations.Nullable Entity p_147209_){
		return player != null ? player.addEffect(p_147208_, p_147209_) : super.addEffect(p_147208_, p_147209_);
	}

	@Override
	public Vec3 getDeltaMovement(){
		return player != null ? player.getDeltaMovement() : super.getDeltaMovement();
	}

	@Override
	public boolean isMultipartEntity(){
		return true;
	}

	@Nullable
	@Override
	public PartEntity<?>[] getParts(){
		return subEntities;
	}

	@Override
	public boolean fireImmune(){
		return player != null ? player.fireImmune() : super.fireImmune();
	}

	@Override
	public boolean isOnFire(){
		return player != null ? player.isOnFire() : super.isOnFire();
	}

	@Override
	public void setSecondsOnFire(int pSeconds){
		if(player != null){
			player.setSecondsOnFire(pSeconds);
		}
	}

	@Override
	public void setRemainingFireTicks(int pTicks){
		if(player != null){
			player.setRemainingFireTicks(pTicks);
		}
	}

	@Override
	public int getRemainingFireTicks(){
		return player != null ? player.getRemainingFireTicks() : super.getRemainingFireTicks();
	}

	@Override
	public void clearFire(){
		if(player != null){
			player.clearFire();
		}
	}

	public boolean is(Entity entity){
		return this == entity || entity.getId() == getPlayerId() || player != null && entity.getId() == player.getId();
	}
}