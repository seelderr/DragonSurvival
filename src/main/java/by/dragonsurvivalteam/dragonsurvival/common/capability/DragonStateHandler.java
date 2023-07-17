package by.dragonsurvivalteam.dragonsurvival.common.capability;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.common.capability.objects.DragonMovementData;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.*;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonModifiers;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


public class DragonStateHandler extends EntityStateHandler {

	//TODO Remove / cleanup the following
	private final DragonMovementData movementData = new DragonMovementData(0, 0, 0, false);

	private final ClawInventory clawToolData = new ClawInventory(this);
	public ItemStack storedMainHand = ItemStack.EMPTY;
	public boolean switchedItems;

	private final EmoteCap emoteData = new EmoteCap(this);
	private final MagicCap magicData = new MagicCap(this);
	private final SkinCap skinData = new SkinCap(this);
	private final VillageRelationShips villageRelationShips = new VillageRelationShips(this);

	public final Supplier<SubCap>[] caps = new Supplier[]{this::getSkinData, this::getMagicData, this::getEmoteData, this::getClawToolData, this::getVillageRelationShips};
	private final Map<String, Double> savedDragonSize = new ConcurrentHashMap<>();

	public boolean hasFlown;
	public boolean growing = true;

	public boolean treasureResting;
	public int treasureRestTimer;
	public int treasureSleepTimer;

	public int altarCooldown;
	public boolean hasUsedAltar;

	private boolean isHiding;

	private AbstractDragonType dragonType;

	private boolean hasWings;
	private boolean spreadWings;
	private double size;
	private int passengerId;

	/**
	 * Sets the size, health and base damage
	 */
	public void setSize(double size, Player player){
		setSize(size);
		updateModifiers(size, player);
	}

	private void updateModifiers(double size, Player player){
		if(isDragon()){
			AttributeModifier healthMod = DragonModifiers.buildHealthMod(size);
			DragonModifiers.updateHealthModifier(player, healthMod);
			AttributeModifier damageMod = DragonModifiers.buildDamageMod(this, isDragon());
			DragonModifiers.updateDamageModifier(player, damageMod);
			AttributeModifier swimSpeedMod = DragonModifiers.buildSwimSpeedMod(getType());
			DragonModifiers.updateSwimSpeedModifier(player, swimSpeedMod);
			AttributeModifier reachMod = DragonModifiers.buildReachMod(size);
			DragonModifiers.updateReachModifier(player, reachMod);
		}else{
			AttributeModifier oldMod = DragonModifiers.getHealthModifier(player);
			if(oldMod != null){
				AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH));
				max.removeModifier(oldMod);
			}

			oldMod = DragonModifiers.getDamageModifier(player);
			if(oldMod != null){
				AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE));
				max.removeModifier(oldMod);
			}

			oldMod = DragonModifiers.getSwimSpeedModifier(player);
			if(oldMod != null){
				AttributeInstance max = Objects.requireNonNull(player.getAttribute(ForgeMod.SWIM_SPEED.get()));
				max.removeModifier(oldMod);
			}

			oldMod = DragonModifiers.getReachModifier(player);
			if(oldMod != null){
				AttributeInstance max = Objects.requireNonNull(player.getAttribute(ForgeMod.REACH_DISTANCE.get()));
				max.removeModifier(oldMod);
			}
		}
	}

	public boolean isDragon(){
		return dragonType != null;
	}

	public int getPassengerId(){
		return passengerId;
	}

	public void setPassengerId(int passengerId){
		this.passengerId = passengerId;
	}

	public EmoteCap getEmoteData(){
		return emoteData;
	}

	public SkinCap getSkinData(){
		return skinData;
	}

	public int lastSync = 0;//Last timestamp the server synced this player

	@Override
	public CompoundTag writeNBT(){
		CompoundTag tag = new CompoundTag();
		//tag.putString("type", getType().name());
		tag.putString("type", dragonType != null ? dragonType.getTypeName() : "none");

		if(isDragon()){
			tag.put("typeData", dragonType.writeNBT());

			//Rendering
			DragonMovementData movementData = getMovementData();
			tag.putDouble("bodyYaw", movementData.bodyYaw);
			tag.putDouble("headYaw", movementData.headYaw);
			tag.putDouble("headPitch", movementData.headPitch);

			tag.putBoolean("bite", movementData.bite);
			tag.putBoolean("dig", movementData.dig);
			tag.putBoolean("isHiding", isHiding());

			//Spin attack
			tag.putInt("spinCooldown", movementData.spinCooldown);
			tag.putInt("spinAttack", movementData.spinAttack);

			tag.putDouble("size", getSize());
			tag.putBoolean("growing", growing);

			tag.putBoolean("isFlying", isWingsSpread());

			tag.putBoolean("resting", treasureResting);
			tag.putInt("restingTimer", treasureRestTimer);
		}

		if (isDragon() || ServerConfig.saveAllAbilities) {
			tag.putBoolean("spinLearned", getMovementData().spinLearned);
			tag.putBoolean("hasWings", hasWings());
		}


		tag.putDouble("seaSize", getSavedDragonSize(DragonTypes.SEA.getTypeName()));
		tag.putDouble("caveSize", getSavedDragonSize(DragonTypes.CAVE.getTypeName()));
		tag.putDouble("forestSize", getSavedDragonSize(DragonTypes.FOREST.getTypeName()));

		for(int i = 0; i < caps.length; i++){
			tag.put("cap_" + i, caps[i].get().writeNBT());
		}

		tag.putInt("altarCooldown", altarCooldown);
		tag.putBoolean("usedAltar", hasUsedAltar);

		if(lastPos != null){
			tag.put("lastPos", Functions.newDoubleList(lastPos.x, lastPos.y, lastPos.z));
		}

		tag.putInt("lastAfflicted", lastAfflicted);

		return tag;
	}

	@Override
	public void readNBT(CompoundTag tag){
		dragonType = DragonTypes.newDragonTypeInstance(tag.getString("type"));

		if(dragonType != null){
			if(tag.contains("typeData")){
				dragonType.readNBT(tag.getCompound("typeData"));
			}
		}

		if(isDragon()){
			setMovementData(tag.getDouble("bodyYaw"), tag.getDouble("headYaw"), tag.getDouble("headPitch"), tag.getBoolean("bite"));
			getMovementData().headYawLastTick = getMovementData().headYaw;
			getMovementData().bodyYawLastTick = getMovementData().bodyYaw;
			getMovementData().headPitchLastTick = getMovementData().headPitch;
			setIsHiding(tag.getBoolean("isHiding"));
			getMovementData().dig = tag.getBoolean("dig");

			setWingsSpread(tag.getBoolean("isFlying"));

			getMovementData().spinCooldown = tag.getInt("spinCooldown");
			getMovementData().spinAttack = tag.getInt("spinAttack");

			setSize(tag.getDouble("size"));
			growing = !tag.contains("growing") || tag.getBoolean("growing");

			treasureResting = tag.getBoolean("resting");
			treasureRestTimer = tag.getInt("restingTimer");

			if(getSize() == 0){
				setSize(DragonLevel.NEWBORN.size);
			}
		}

		if (isDragon() || ServerConfig.saveAllAbilities) {
			getMovementData().spinLearned = tag.getBoolean("spinLearned");
			setHasWings(tag.getBoolean("hasWings"));
		}

		setSavedDragonSize(DragonTypes.SEA.getTypeName(), tag.getDouble("seaSize"));
		setSavedDragonSize(DragonTypes.CAVE.getTypeName(), tag.getDouble("caveSize"));
		setSavedDragonSize(DragonTypes.FOREST.getTypeName(), tag.getDouble("forestSize"));

		for(int i = 0; i < caps.length; i++){
			if(tag.contains("cap_" + i)){
				caps[i].get().readNBT((CompoundTag)tag.get("cap_" + i));
			}
		}

		altarCooldown = tag.getInt("altarCooldown");
		hasUsedAltar = tag.getBoolean("usedAltar");

		if(tag.contains("lastPos")){
			ListTag listnbt = tag.getList("lastPos", 6);
			lastPos = new Vec3(listnbt.getDouble(0), listnbt.getDouble(1), listnbt.getDouble(2));
		}

		lastAfflicted = tag.getInt("lastAfflicted");

		getSkinData().compileSkin();
	}

	public void setHasWings(boolean hasWings){
		if(hasWings != this.hasWings){
			this.hasWings = hasWings;
		}
	}

	public void setIsHiding(boolean hiding){
		isHiding = hiding;
	}

	public void setMovementData(double bodyYaw, double headYaw, double headPitch, boolean bite){
		movementData.headYawLastTick = movementData.headYaw;
		movementData.bodyYawLastTick = movementData.bodyYaw;
		movementData.headPitchLastTick = movementData.headPitch;

		movementData.bodyYaw = bodyYaw;
		movementData.headYaw = headYaw;
		movementData.headPitch = headPitch;
		movementData.bite = bite;
	}

	public double getSize(){
		return size;
	}

	public void setSize(double size){
		if(size != this.size){
			DragonLevel oldLevel = getLevel();
			this.size = size;

			if(oldLevel != getLevel())
				onGrow();

			if (dragonType != null) {
				setSavedDragonSize(dragonType.getTypeName(), size);
			}
		}
	}

	public double getSavedDragonSize(final String type) {
		Double value = savedDragonSize.get(type);
		value = value == null ? 0 : value;

		return value;
	}

	public void setSavedDragonSize(final String type, double size) {
		Double value = savedDragonSize.get(type);

		if (size == 0 || (value != null && value == size)) {
			return;
		}

		savedDragonSize.put(type, size);
	}

	public void onGrow(){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (DistExecutor.SafeRunnable)this::requestSkinUpdate);
	}

	@OnlyIn( Dist.CLIENT )
	public void requestSkinUpdate(){
		if(this == DragonUtils.getHandler(Minecraft.getInstance().player))
			ClientEvents.sendClientData(new RequestClientData(getType(), getLevel()));
	}

	public AbstractDragonType getType(){
		return dragonType;
	}

	public String getTypeName() {
		if (dragonType == null) {
			return "human";
		}

		return dragonType.getTypeName();
	}

	public void setType(AbstractDragonType type){
		if(type != null && !Objects.equals(dragonType, type)){
			growing = true;
			getMagicData().initAbilities(type);
		}

		if(type != null){
			if(Objects.equals(dragonType, type)) return;
			dragonType = DragonTypes.newDragonTypeInstance(type.getTypeName());
		}else{
			dragonType = null;
		}
	}

	public MagicCap getMagicData(){
		return magicData;
	}

	public DragonLevel getLevel(){
		if(size < 20F)
			return DragonLevel.NEWBORN;
		else if(size < 30F)
			return DragonLevel.YOUNG;
		else
			return DragonLevel.ADULT;
	}

	public DragonMovementData getMovementData(){
		return movementData;
	}

	public boolean hasWings(){
		return hasWings;
	}

	public boolean isWingsSpread(){
		return hasWings && spreadWings;
	}

	public void setWingsSpread(boolean flying){
		spreadWings = flying;
	}

	public boolean isHiding(){
		return isHiding;
	}

	public ClawInventory getClawToolData()  {
		return clawToolData;
	}

	public VillageRelationShips getVillageRelationShips() {
		return villageRelationShips;
	}

	/** Determines if the current dragon type can harvest the supplied block (with or without tools) (configured harvest bonuses are taken into account) */
	public boolean canHarvestWithPaw(final BlockState state) {
		for (int i = 1; i < 4; i++) {
			// FIXME :: Why is the sword ignored? It can harvest cobwebs (and due to other mods maybe other things as well)
			ItemStack stack = getClawToolData().getClawsInventory().getItem(i);

			if (stack.isCorrectToolForDrops(state)) {
				return true;
			}
		}

		return canHarvestWithPawNoTools(state);
	}

	/** Determines if the current dragon type can harvest the supplied block without a tool (configured harvest bonuses are taken into account) */
	public boolean canHarvestWithPawNoTools(final BlockState blockState) {
		int harvestLevel = blockState.is(BlockTags.NEEDS_DIAMOND_TOOL) ? 3 : blockState.is(BlockTags.NEEDS_IRON_TOOL) ? 2 : blockState.is(BlockTags.NEEDS_STONE_TOOL) ? 1 : 0;

		if (harvestLevel <= ServerConfig.baseHarvestLevel) {
			return true;
		}

		for (TagKey<Block> tagKey : getType().mineableBlocks()) {
			if (blockState.is(tagKey)) {
				return harvestLevel <= getDragonHarvestLevel(getType().slotForBonus);
			}
		}

		return false;
	}

	/**
	 * @param blockState The block for which the tool is required for
	 * @return The appropriate harvest tool for the supplied block<br>
	 * The tier depends on the dragon and configured harvest bonuses
	 */
	public ItemStack getFakeTool(final BlockState blockState) {
		if (getType() == null) {
			return ItemStack.EMPTY;
		}

		int harvestLevel = 0;

		for (TagKey<Block> tagKey : getType().mineableBlocks()) {
			if (blockState.is(tagKey)) {
				harvestLevel = getDragonHarvestLevel(blockState);

				break;
			}
		}

		if (harvestLevel < 0) {
			return ItemStack.EMPTY;
		} else {
			return getToolOfType(getDragonHarvestTier(blockState), blockState);
		}
	}

	/**
	 * @return Harvest tool of which the dragon type is effective for (tier is based on the current harvest level of the dragon)
	 */
	public ItemStack getInnateFakeTool() {
		if (getType() == null) {
			return ItemStack.EMPTY;
		}

		int harvestLevel = getDragonHarvestLevel(getType().slotForBonus);

		if (harvestLevel < 0) {
			return ItemStack.EMPTY;
		} else {
			return getToolOfType(DragonUtils.levelToVanillaTier(harvestLevel), getType().slotForBonus);
		}
	}

	/** Calls {@link DragonStateHandler#getToolOfType(Tier, int)} with the result of {@link DragonStateHandler#getRelevantToolSlot(BlockState)} */
	public ItemStack getToolOfType(final Tier tier, final BlockState blockState) {
		return getToolOfType(tier, getRelevantToolSlot(blockState));
	}

	/**
	 * @param tier The tier which the returned item is supposed to have
	 * @param toolSlot To determine the type of harvest tool (e.g. `1` for Pickaxe)
	 * @return A default instance of the tool (or {@link ItemStack#EMPTY} if nothing matches / some problem occurs)
	 */
	public ItemStack getToolOfType(final Tier tier, int toolSlot) {
		if (!(tier instanceof Tiers tiers)) {
			// TODO :: Do something with ForgeTier to support custom tools (benefit = ?)
			return ItemStack.EMPTY;
		}

		String tierPath = tiers.name().toLowerCase() + "_";
		tierPath = tierPath.replace("wood", "wooden");

		Item item = switch (toolSlot) {
			case 1 -> ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", tierPath + "pickaxe"));
			case 2 -> ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", tierPath + "axe"));
			case 3 -> ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", tierPath + "shovel"));
			default -> ItemStack.EMPTY.getItem();
		};

		if (item != null) {
			return item.getDefaultInstance();
		}

		return ItemStack.EMPTY;
	}

	/** Calls {@link DragonStateHandler#getDragonHarvestLevel(int)} with the result of {@link DragonStateHandler#getRelevantToolSlot(BlockState)} */
	public int getDragonHarvestLevel(final BlockState blockState) {
		return getDragonHarvestLevel(getRelevantToolSlot(blockState));
	}

	/**
	 * Don't call this when the player is not a dragon, if you do you get a -1
	 * @param slot The dragon tool slot of the harvest tool which needs to be checked
	 * @return The harvest level of the current dragon type for the provided slot
	 */
	public int getDragonHarvestLevel(int slot) {
		if (getType() == null) {
			return -1;
		}

		int harvestLevel = ServerConfig.baseHarvestLevel;
		int bonusLevel = 0;

		if (getLevel() == DragonLevel.NEWBORN && ServerConfig.bonusUnlockedAt == DragonLevel.NEWBORN) {
			bonusLevel = ServerConfig.bonusHarvestLevel;
		} else if (getLevel() == DragonLevel.YOUNG && ServerConfig.bonusUnlockedAt != DragonLevel.ADULT) {
			bonusLevel = ServerConfig.bonusHarvestLevel;
		} else if (getLevel() == DragonLevel.ADULT) {
			bonusLevel = ServerConfig.bonusHarvestLevel;
		}

		if (slot == getType().slotForBonus) {
			return harvestLevel + bonusLevel;
		}

		return harvestLevel;
	}

	/** Calls {@link DragonStateHandler#getDragonHarvestTier(int)} with the result of {@link DragonStateHandler#getRelevantToolSlot(BlockState)} */
	public @Nullable Tier getDragonHarvestTier(final BlockState blockState) {
		return getDragonHarvestTier(getRelevantToolSlot(blockState));
	}

	/**
	 *
	 * @param slot The (dragon) tool slot the item would belong to (e.g. `1` for Pickaxe)
	 * @return the tier of the current dragon harvest level<br>
	 * (Which is a combination of {@link ServerConfig#baseHarvestLevel} and {@link ServerConfig#bonusHarvestLevel} (if enabled))<br>
	 * Will return null if the config somehow set a negative harvest level
	 */
	public @Nullable Tier getDragonHarvestTier(int slot) {
		int harvestLevel = getDragonHarvestLevel(slot);

		if (harvestLevel < 0) {
			return null;
		}

		return switch(harvestLevel) {
			case 0 -> Tiers.WOOD;
			case 1 -> Tiers.STONE;
			case 2 -> Tiers.IRON;
			case 3 -> Tiers.DIAMOND;
			default -> Tiers.NETHERITE;
		};
	}

	/**
	 * @param blockState The block to test against
	 * @return The dragon tool slot which is effective for the supplied block (e.g. `1` (Pickaxe) for the block `Stone`)
	 */
	public int getRelevantToolSlot(final BlockState blockState) {
		if (blockState.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
			return 1;
		} else if (blockState.is(BlockTags.MINEABLE_WITH_AXE)) {
			return 2;
		} else if (blockState.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
			return 3;
		}

		return 0;
	}
}