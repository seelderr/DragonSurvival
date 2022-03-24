package by.dragonsurvivalteam.dragonsurvival.common.capability;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.common.capability.objects.DragonDebuffData;
import by.dragonsurvivalteam.dragonsurvival.common.capability.objects.DragonMovementData;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.ClawInventory;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.EmoteCap;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.MagicCap;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.SkinCap;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonModifiers;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

import java.util.Objects;
import java.util.function.Supplier;


public class DragonStateHandler implements NBTInterface{
	public static final ToolType[] CLAW_TOOL_TYPES = new ToolType[]{null, ToolType.PICKAXE, ToolType.AXE, ToolType.SHOVEL};
	private final DragonMovementData movementData = new DragonMovementData(0, 0, 0, false);
	private final DragonDebuffData debuffData = new DragonDebuffData(0, 0, 0);
	private final ClawInventory clawInventory = new ClawInventory(this);
	private final EmoteCap emotes = new EmoteCap(this);
	private final MagicCap magic = new MagicCap(this);
	private final SkinCap skin = new SkinCap(this);
	public final Supplier<NBTInterface>[] caps = new Supplier[]{this::getSkin, this::getMagic, this::getEmotes, this::getClawInventory};
	public boolean hasFlown;
	public boolean growing = true;

	public int altarCooldown;
	public boolean hasUsedAltar;

	public boolean treasureResting = false;
	public int treasureRestTimer = 0;
	public int treasureSleepTimer = 0;

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
	private double size = 0;
	private int lavaAirSupply;
	private int passengerId;

	/**
	 * Sets the size, health and base damage
	 */
	public void setSize(double size, PlayerEntity playerEntity){
		setSize(size);
		updateModifiers(size, playerEntity);
	}

	private void updateModifiers(double size, PlayerEntity playerEntity){
		if(isDragon()){
			AttributeModifier healthMod = DragonModifiers.buildHealthMod(size);
			DragonModifiers.updateHealthModifier(playerEntity, healthMod);
			AttributeModifier damageMod = DragonModifiers.buildDamageMod(this, isDragon());
			DragonModifiers.updateDamageModifier(playerEntity, damageMod);
			AttributeModifier swimSpeedMod = DragonModifiers.buildSwimSpeedMod(getType());
			DragonModifiers.updateSwimSpeedModifier(playerEntity, swimSpeedMod);
			AttributeModifier reachMod = DragonModifiers.buildReachMod(size);
			DragonModifiers.updateReachModifier(playerEntity, reachMod);
		}else{
			AttributeModifier oldMod = DragonModifiers.getHealthModifier(playerEntity);
			if(oldMod != null){
				ModifiableAttributeInstance max = Objects.requireNonNull(playerEntity.getAttribute(Attributes.MAX_HEALTH));
				max.removeModifier(oldMod);
			}

			oldMod = DragonModifiers.getDamageModifier(playerEntity);
			if(oldMod != null){
				ModifiableAttributeInstance max = Objects.requireNonNull(playerEntity.getAttribute(Attributes.ATTACK_DAMAGE));
				max.removeModifier(oldMod);
			}

			oldMod = DragonModifiers.getSwimSpeedModifier(playerEntity);
			if(oldMod != null){
				ModifiableAttributeInstance max = Objects.requireNonNull(playerEntity.getAttribute(ForgeMod.SWIM_SPEED.get()));
				max.removeModifier(oldMod);
			}

			oldMod = DragonModifiers.getReachModifier(playerEntity);
			if(oldMod != null){
				ModifiableAttributeInstance max = Objects.requireNonNull(playerEntity.getAttribute(ForgeMod.REACH_DISTANCE.get()));
				max.removeModifier(oldMod);
			}
		}
	}

	public boolean isDragon(){
		return this.type != DragonType.NONE;
	}

	public void setSize(double size){
		if(size != this.size){
			DragonLevel oldLevel = getLevel();
			this.size = size;

			if(oldLevel != getLevel()){
				onGrow();
			}

			switch(type){
				case SEA:
					seaSize = size;
					break;

				case CAVE:
					caveSize = size;
					break;

				case FOREST:
					forestSize = size;
					break;
			}
		}
	}

	public void onGrow(){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)this::requestSkinUpdate);
	}

	@OnlyIn( Dist.CLIENT )
	public void requestSkinUpdate(){
		if(this == DragonUtils.getHandler(Minecraft.getInstance().player)){
			ClientEvents.sendClientData(new RequestClientData(getType(), getLevel()));
		}
	}

	public DragonType getType(){
		return this.type;
	}

	public DragonLevel getLevel(){
		if(size < 20F){
			return DragonLevel.BABY;
		}else if(size < 30F){
			return DragonLevel.YOUNG;
		}else{
			return DragonLevel.ADULT;
		}
	}

	public boolean canHarvestWithPaw(PlayerEntity player, BlockState state){
		int harvestLevel = state.getHarvestLevel();
		int baseHarvestLevel = 0;

		for(int i = 1; i < 4; i++){
			if(state.getHarvestTool() == CLAW_TOOL_TYPES[i]){
				ItemStack stack = getClawInventory().getClawsInventory().getItem(i);

				if(!stack.isEmpty()){
					int hvLevel = stack.getHarvestLevel(CLAW_TOOL_TYPES[i], player, state);
					if(hvLevel > baseHarvestLevel){
						baseHarvestLevel = hvLevel;
					}
				}
			}
		}

		switch(getLevel()){
			case BABY:
				if(ConfigHandler.SERVER.bonusUnlockedAt.get() != DragonLevel.BABY){
					if(harvestLevel <= ConfigHandler.SERVER.baseHarvestLevel.get() + baseHarvestLevel){
						return true;
					}
					break;
				}
			case YOUNG:
				if(ConfigHandler.SERVER.bonusUnlockedAt.get() == DragonLevel.ADULT && getLevel() != DragonLevel.BABY){
					if(harvestLevel <= ConfigHandler.SERVER.baseHarvestLevel.get() + baseHarvestLevel){
						return true;
					}
					break;
				}
			case ADULT:
				if(harvestLevel <= ConfigHandler.SERVER.bonusHarvestLevel.get() + baseHarvestLevel){
					switch(getType()){
						case SEA:
							if(state.getHarvestTool() == ToolType.SHOVEL){
								return true;
							}
							break;
						case CAVE:
							if(state.getHarvestTool() == ToolType.PICKAXE){
								return true;
							}
							break;
						case FOREST:
							if(state.getHarvestTool() == ToolType.AXE){
								return true;
							}
					}
				}
				if(harvestLevel <= ConfigHandler.SERVER.baseHarvestLevel.get() + baseHarvestLevel){
					return true;
				}
		}
		return false;
	}

	public ClawInventory getClawInventory(){
		return clawInventory;
	}

	public int getPassengerId(){
		return this.passengerId;
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

	public void readNBT(CompoundNBT tag){
		if(tag.getString("type").equals("")){
			setType(DragonType.NONE);
		}else{
			setType(DragonType.valueOf(tag.getString("type")));
		}

		if(isDragon()){
			setMovementData(tag.getDouble("bodyYaw"), tag.getDouble("headYaw"), tag.getDouble("headPitch"), tag.getBoolean("bite"));
			getMovementData().headYawLastTick = getMovementData().headYaw;
			getMovementData().bodyYawLastTick = getMovementData().bodyYaw;
			getMovementData().headPitchLastTick = getMovementData().headPitch;

			altarCooldown = tag.getInt("altarCooldown");
			hasUsedAltar = tag.getBoolean("usedAltar");

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

			for(int i = 0; i < caps.length; i++){
				if(tag.contains("cap_" + i)){
					caps[i].get().readNBT((CompoundNBT)tag.get("cap_" + i));
				}
			}

			if(getSize() == 0){
				setSize(DragonLevel.BABY.size);
			}

			setLavaAirSupply(tag.getInt("lavaAirSupply"));
		}
	}

	public double getSize(){
		return size;
	}

	public void setHasWings(boolean hasWings){
		if(hasWings != this.hasWings){
			this.hasWings = hasWings;

			switch(type){
				case SEA:
					seaWings = hasWings;
					break;

				case CAVE:
					caveWings = hasWings;
					break;

				case FOREST:
					forestWings = hasWings;
					break;
			}
		}
	}

	public void setWingsSpread(boolean flying){
		spreadWings = flying;
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

	public DragonMovementData getMovementData(){
		return this.movementData;
	}

	public void setDebuffData(double timeWithoutWater, int timeInDarkness, int timeInRain){
		debuffData.timeWithoutWater = timeWithoutWater;
		debuffData.timeInDarkness = timeInDarkness;
		debuffData.timeInRain = timeInRain;
	}

	public void setType(DragonType type){
		if(this.type != type && this.type != DragonType.NONE){
			growing = true;

			getMagic().initAbilities(type);
		}

		this.type = type;

		if(ConfigHandler.SERVER.saveGrowthStage.get()){
			switch(type){
				case SEA:
					size = seaSize;
					hasWings = seaWings;
					break;

				case CAVE:
					size = caveSize;
					hasWings = caveWings;
					break;

				case FOREST:
					size = forestSize;
					hasWings = forestWings;
					break;
			}
		}
	}

	public MagicCap getMagic(){
		return magic;
	}

	public void setLavaAirSupply(int lavaAirSupply){
		this.lavaAirSupply = lavaAirSupply;
	}

	public CompoundNBT writeNBT(){
		CompoundNBT tag = new CompoundNBT();
		tag.putString("type", getType().toString());

		if(isDragon()){
			DragonMovementData movementData = getMovementData();
			tag.putDouble("bodyYaw", movementData.bodyYaw);
			tag.putDouble("headYaw", movementData.headYaw);
			tag.putDouble("headPitch", movementData.headPitch);

			tag.putInt("altarCooldown", altarCooldown);
			tag.putBoolean("usedAltar", hasUsedAltar);

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

			for(int i = 0; i < caps.length; i++){
				tag.put("cap_" + i, caps[i].get().writeNBT());
			}
		}
		return tag;
	}

	public boolean hasWings(){
		return hasWings;
	}

	public boolean isWingsSpread(){
		return hasWings && spreadWings;
	}

	public boolean isHiding(){
		return isHiding;
	}

	public DragonDebuffData getDebuffData(){
		return this.debuffData;
	}

	public int getLavaAirSupply(){
		return this.lavaAirSupply;
	}
}