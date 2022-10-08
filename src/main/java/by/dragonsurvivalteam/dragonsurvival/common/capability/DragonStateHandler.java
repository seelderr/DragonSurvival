package by.dragonsurvivalteam.dragonsurvival.common.capability;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.common.capability.objects.DragonDebuffData;
import by.dragonsurvivalteam.dragonsurvival.common.capability.objects.DragonMovementData;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.ClawInventory;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.EmoteCap;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.MagicCap;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.SkinCap;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonModifiers;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.DistExecutor;

import java.util.Objects;
import java.util.function.Supplier;


public class DragonStateHandler implements NBTInterface{
	private final DragonMovementData movementData = new DragonMovementData(0, 0, 0, false);
	private final DragonDebuffData debuffData = new DragonDebuffData(0, 0, 0);
	private final ClawInventory clawInventory = new ClawInventory(this);
	private final EmoteCap emotes = new EmoteCap(this);
	private final MagicCap magic = new MagicCap(this);
	private final SkinCap skin = new SkinCap(this);
	public final Supplier<NBTInterface>[] caps = new Supplier[]{this::getSkin, this::getMagic, this::getEmotes, this::getClawInventory};
	public boolean hasFlown;
	public boolean growing = true;



	public boolean treasureResting;
	public int treasureRestTimer;
	public int treasureSleepTimer;

	//Saving status of other types incase the config option for saving all is on
	public double caveSize;
	public double seaSize;
	public double forestSize;
	public boolean caveWings;
	public boolean seaWings;
	public boolean forestWings;
	private boolean isHiding;
	private DragonType type = DragonType.NONE;
	private boolean hasWings;
	private boolean spreadWings;
	private double size;
	private int lavaAirSupply;
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
		return type != DragonType.NONE;
	}

	public int getPassengerId(){
		return passengerId;
	}

	public void setPassengerId(int passengerId){
		this.passengerId = passengerId;
	}

	public EmoteCap getEmotes(){
		return emotes;
	}

	public SkinCap getSkin(){
		return skin;
	}

	public int lastSync = 0;//Last timestamp the server synced this player

	@Override
	public CompoundTag writeNBT(){
		CompoundTag tag = new CompoundTag();
		tag.putString("type", getType().toString());

		if(isDragon()){
			DragonMovementData movementData = getMovementData();
			tag.putDouble("bodyYaw", movementData.bodyYaw);
			tag.putDouble("headYaw", movementData.headYaw);
			tag.putDouble("headPitch", movementData.headPitch);

			tag.putInt("spinCooldown", movementData.spinCooldown);
			tag.putInt("spinAttack", movementData.spinAttack);
			tag.putBoolean("spinLearned", movementData.spinLearned);

			tag.putBoolean("bite", movementData.bite);
			tag.putBoolean("dig", movementData.dig);

			DragonDebuffData debuffData = getDebuffData();
			tag.putDouble("timeWithoutWater", debuffData.timeWithoutWater);
			tag.putInt("timeInDarkness", debuffData.timeInDarkness);
			tag.putInt("timeInRain", debuffData.timeInRain);
			tag.putBoolean("isHiding", isHiding());

			tag.putDouble("size", getSize());
			tag.putBoolean("growing", growing);

			tag.putBoolean("hasWings", hasWings());
			tag.putBoolean("isFlying", isWingsSpread());

			tag.putInt("lavaAirSupply", getLavaAirSupply());

			tag.putBoolean("resting", treasureResting);
			tag.putInt("restingTimer", treasureRestTimer);

			tag.putDouble("caveSize", caveSize);
			tag.putDouble("seaSize", seaSize);
			tag.putDouble("forestSize", forestSize);

			tag.putBoolean("caveWings", caveWings);
			tag.putBoolean("seaWings", seaWings);
			tag.putBoolean("forestWings", forestWings);

			for(int i = 0; i < caps.length; i++)
				tag.put("cap_" + i, caps[i].get().writeNBT());
		}
		return tag;
	}

	@Override
	public void readNBT(CompoundTag tag){
		if(tag.getString("type").equals(""))
			setType(DragonType.NONE);
		else
			setType(DragonType.valueOf(tag.getString("type")));

		if(isDragon()){
			setMovementData(tag.getDouble("bodyYaw"), tag.getDouble("headYaw"), tag.getDouble("headPitch"), tag.getBoolean("bite"));
			getMovementData().headYawLastTick = getMovementData().headYaw;
			getMovementData().bodyYawLastTick = getMovementData().bodyYaw;
			getMovementData().headPitchLastTick = getMovementData().headPitch;

			setHasWings(tag.getBoolean("hasWings"));
			setWingsSpread(tag.getBoolean("isFlying"));

			getMovementData().dig = tag.getBoolean("dig");
			getMovementData().spinCooldown = tag.getInt("spinCooldown");
			getMovementData().spinAttack = tag.getInt("spinAttack");
			getMovementData().spinLearned = tag.getBoolean("spinLearned");

			setDebuffData(tag.getInt("timeWithoutWater"), tag.getInt("timeInDarkness"), tag.getInt("timeInRain"));
			setIsHiding(tag.getBoolean("isHiding"));

			setSize(tag.getDouble("size"));
			growing = !tag.contains("growing") || tag.getBoolean("growing");

			treasureResting = tag.getBoolean("resting");
			treasureRestTimer = tag.getInt("restingTimer");

			caveSize = tag.getDouble("caveSize");
			seaSize = tag.getDouble("seaSize");
			forestSize = tag.getDouble("forestSize");

			caveWings = tag.getBoolean("caveWings");
			seaWings = tag.getBoolean("seaWings");
			forestWings = tag.getBoolean("forestWings");

			for(int i = 0; i < caps.length; i++)
				if(tag.contains("cap_" + i)){
					caps[i].get().readNBT((CompoundTag)tag.get("cap_" + i));
				}

			if(getSize() == 0)
				setSize(DragonLevel.NEWBORN.size);

			setLavaAirSupply(tag.getInt("lavaAirSupply"));
		}

		getSkin().compileSkin();
	}

	public void setHasWings(boolean hasWings){
		if(hasWings != this.hasWings){
			this.hasWings = hasWings;

			switch(type){
				case SEA -> seaWings = hasWings;
				case CAVE -> caveWings = hasWings;
				case FOREST -> forestWings = hasWings;
			}
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

	public void setDebuffData(double timeWithoutWater, int timeInDarkness, int timeInRain){
		debuffData.timeWithoutWater = timeWithoutWater;
		debuffData.timeInDarkness = timeInDarkness;
		debuffData.timeInRain = timeInRain;
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

			switch(type){
				case SEA -> seaSize = size;
				case CAVE -> caveSize = size;
				case FOREST -> forestSize = size;
			}
		}
	}

	public void onGrow(){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (DistExecutor.SafeRunnable)this::requestSkinUpdate);
	}

	@OnlyIn( Dist.CLIENT )
	public void requestSkinUpdate(){
		if(this == DragonUtils.getHandler(Minecraft.getInstance().player))
			ClientEvents.sendClientData(new RequestClientData(getType(), getLevel()));
	}

	public DragonType getType(){
		return type;
	}

	public void setType(DragonType type){
		if(this.type != type && this.type != DragonType.NONE){
			growing = true;

			getMagic().initAbilities(type);
		}

		this.type = type;

		if(ServerConfig.saveGrowthStage)
			switch(type){
				case SEA -> {
					size = seaSize;
					hasWings = seaWings;
				}
				case CAVE -> {
					size = caveSize;
					hasWings = caveWings;
				}
				case FOREST -> {
					size = forestSize;
					hasWings = forestWings;
				}
			}
	}

	public MagicCap getMagic(){
		return magic;
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

	public DragonDebuffData getDebuffData(){
		return debuffData;
	}

	public int getLavaAirSupply(){
		return lavaAirSupply;
	}

	public void setLavaAirSupply(int lavaAirSupply){
		this.lavaAirSupply = lavaAirSupply;
	}

	public boolean canHarvestWithPaw(Player player, BlockState state){
		int harvestLevel = state.is(BlockTags.NEEDS_DIAMOND_TOOL) ? 3 : state.is(BlockTags.NEEDS_IRON_TOOL) ? 2 : state.is(BlockTags.NEEDS_STONE_TOOL) ? 1 : 0;
		int baseHarvestLevel = 0;

		for(int i = 1; i < 4; i++){
			ItemStack stack = getClawInventory().getClawsInventory().getItem(i);
			if(stack.isCorrectToolForDrops(state))
				return true;
		}

		switch(getLevel()){
			case NEWBORN:
				if(ServerConfig.bonusUnlockedAt != DragonLevel.NEWBORN){
					if(harvestLevel <= ServerConfig.baseHarvestLevel + baseHarvestLevel)
						return true;
					break;
				}
			case YOUNG:
				if(ServerConfig.bonusUnlockedAt == DragonLevel.ADULT && getLevel() != DragonLevel.NEWBORN){
					if(harvestLevel <= ServerConfig.baseHarvestLevel + baseHarvestLevel)
						return true;
					break;
				}
			case ADULT:
				if(harvestLevel <= ServerConfig.bonusHarvestLevel + baseHarvestLevel)
					switch(getType()){
						case SEA:
							if(state.is(BlockTags.MINEABLE_WITH_SHOVEL)){
								return true;
							}
							break;
						case CAVE:
							if(state.is(BlockTags.MINEABLE_WITH_PICKAXE)){
								return true;
							}
							break;
						case FOREST:
							if(state.is(BlockTags.MINEABLE_WITH_AXE)){
								return true;
							}
					}
				if(harvestLevel <= ServerConfig.baseHarvestLevel + baseHarvestLevel)
					return true;
		}
		return false;
	}

	public ClawInventory getClawInventory(){
		return clawInventory;
	}
}