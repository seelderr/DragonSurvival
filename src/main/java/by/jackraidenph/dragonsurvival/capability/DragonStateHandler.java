package by.jackraidenph.dragonsurvival.capability;

import by.jackraidenph.dragonsurvival.capability.DragonCapabilities.ClawInventory;
import by.jackraidenph.dragonsurvival.capability.DragonCapabilities.EmoteCap;
import by.jackraidenph.dragonsurvival.capability.DragonCapabilities.MagicCap;
import by.jackraidenph.dragonsurvival.capability.DragonCapabilities.SkinCap;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.util.DragonLevel;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;


public class DragonStateHandler {
	public static final ToolType[] CLAW_TOOL_TYPES = new ToolType[]{null, ToolType.PICKAXE, ToolType.AXE, ToolType.SHOVEL};
	
	private boolean isHiding;
    private DragonType type = DragonType.NONE;
    private final DragonMovementData movementData = new DragonMovementData(0, 0, 0, false);
	
    private boolean hasWings;
	private boolean spreadWings;
	
    private double size = 0;
	public boolean growing = true;
	
	//Saving status of other types incase the config option for saving all is on
	public double caveSize;
	public double seaSize;
	public double forestSize;
	public boolean caveWings;
	public boolean seaWings;
	public boolean forestWings;
	
    private final DragonDebuffData debuffData = new DragonDebuffData(0, 0, 0);
	
	private final ClawInventory clawInventory = new ClawInventory();
	private final EmoteCap emotes = new EmoteCap();
	private final MagicCap magic = new MagicCap(this);
	private final SkinCap skin = new SkinCap();
	
	private int lavaAirSupply;
    private int passengerId;
	
	public static final UUID REACH_MODIFIER_UUID = UUID.fromString("7455d5c7-4e1f-4cca-ab46-d79353764020");
	public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("03574e62-f9e4-4f1b-85ad-fde00915e446");
    public static final UUID DAMAGE_MODIFIER_UUID = UUID.fromString("5bd3cebc-132e-4f9d-88ef-b686c7ad1e2c");
    public static final UUID SWIM_SPEED_MODIFIER_UUID = UUID.fromString("2a9341f3-d19e-446c-924b-7cf2e5259e10");
	
	
	public double getSize() {
        return size;
    }

    /**
     * Sets the size, health and base damage
     */
    public void setSize(double size, PlayerEntity playerEntity) {
        setSize(size);
		if(isDragon()) {
			AttributeModifier healthMod = buildHealthMod(size);
			updateHealthModifier(playerEntity, healthMod);
			AttributeModifier damageMod = buildDamageMod(this, isDragon());
			updateDamageModifier(playerEntity, damageMod);
			AttributeModifier swimSpeedMod = buildSwimSpeedMod(getType());
			updateSwimSpeedModifier(playerEntity, swimSpeedMod);
			AttributeModifier reachMod = buildReachMod(size);
			updateReachModifier(playerEntity, reachMod);
		}else{
			AttributeModifier oldMod = getHealthModifier(playerEntity);
			if (oldMod != null) {
				ModifiableAttributeInstance max = Objects.requireNonNull(playerEntity.getAttribute(Attributes.MAX_HEALTH));
				max.removeModifier(oldMod);
			}
			
			oldMod = getDamageModifier(playerEntity);
			if (oldMod != null) {
				ModifiableAttributeInstance max = Objects.requireNonNull(playerEntity.getAttribute(Attributes.ATTACK_DAMAGE));
				max.removeModifier(oldMod);
			}
			
			oldMod =getSwimSpeedModifier(playerEntity);
			if (oldMod != null) {
				ModifiableAttributeInstance max = Objects.requireNonNull(playerEntity.getAttribute(ForgeMod.SWIM_SPEED.get()));
				max.removeModifier(oldMod);
			}
			
			oldMod = getReachModifier(playerEntity);
			if (oldMod != null) {
				ModifiableAttributeInstance max = Objects.requireNonNull(playerEntity.getAttribute(ForgeMod.REACH_DISTANCE.get()));
				max.removeModifier(oldMod);
			}
		}
    }
	
	public void setSize(double size) {
		if(size != this.size) {
			this.size = size;
			
			switch (type) {
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

    public boolean hasWings() {
        return hasWings;
    }

    public void setHasWings(boolean hasWings) {
		if(hasWings != this.hasWings) {
			this.hasWings = hasWings;
			
			switch (type) {
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
	
	public boolean isWingsSpread()
	{
		return hasWings && spreadWings;
	}
	
	public void setWingsSpread(boolean flying)
	{
		spreadWings = flying;
	}
	
	public boolean isDragon() {
        return this.type != DragonType.NONE;
    }

    public boolean isHiding() {
        return isHiding;
    }

    public void setIsHiding(boolean hiding) {
        isHiding = hiding;
    }

    public DragonLevel getLevel() {
        if (size < 20F)
        	return DragonLevel.BABY;
        else if (size < 30F)
        	return DragonLevel.YOUNG;
        else
        	return DragonLevel.ADULT;
    }

    public boolean canHarvestWithPaw(PlayerEntity player, BlockState state) {
    	int harvestLevel = state.getHarvestLevel();
		int baseHarvestLevel = 0;
		
		for(int i = 1; i < 4; i++) {
			if(state.getHarvestTool() ==  CLAW_TOOL_TYPES[i]){
				ItemStack stack = getClawInventory().getClawsInventory().getItem(i);
				
				if(!stack.isEmpty()){
					int hvLevel = stack.getHarvestLevel(CLAW_TOOL_TYPES[i], player, state);
					if (hvLevel > baseHarvestLevel) {
						baseHarvestLevel = hvLevel;
					}
				}
			}
		}
		
    	switch(getLevel()) {
    		case BABY:
    			if (ConfigHandler.SERVER.bonusUnlockedAt.get() != DragonLevel.BABY){
    			    if (harvestLevel <= ConfigHandler.SERVER.baseHarvestLevel.get() + baseHarvestLevel)
                        return true;
    			    break;
                }
    		case YOUNG:
    		    if (ConfigHandler.SERVER.bonusUnlockedAt.get() == DragonLevel.ADULT && getLevel() != DragonLevel.BABY){
    		        if (harvestLevel <= ConfigHandler.SERVER.baseHarvestLevel.get() + baseHarvestLevel)
                        return true;
    		        break;
                }
            case ADULT:
            	if (harvestLevel <= ConfigHandler.SERVER.bonusHarvestLevel.get() + baseHarvestLevel) {
                    switch (getType()) {
                        case SEA:
                            if (state.getHarvestTool() == ToolType.SHOVEL)
                            	return true;
                            break;
                        case CAVE:
                            if (state.getHarvestTool() == ToolType.PICKAXE)
                            	return true;
                            break;
                        case FOREST:
                            if (state.getHarvestTool() == ToolType.AXE)
                                return true;
                    }
                }
            	if (harvestLevel <= ConfigHandler.SERVER.baseHarvestLevel.get() + baseHarvestLevel)
                    return true;
    	}
    	return false;
    }
	
	@Nullable
	public static AttributeModifier getReachModifier(PlayerEntity player) {
		return Objects.requireNonNull(player.getAttribute(ForgeMod.REACH_DISTANCE.get())).getModifier(REACH_MODIFIER_UUID);
	}
	
	@Nullable
    public static AttributeModifier getHealthModifier(PlayerEntity player) {
    	return Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).getModifier(HEALTH_MODIFIER_UUID);
    }
    
    @Nullable
    public static AttributeModifier getDamageModifier(PlayerEntity player) {
    	return Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE)).getModifier(DAMAGE_MODIFIER_UUID);
    }
    
    @Nullable
    public static AttributeModifier getSwimSpeedModifier(PlayerEntity player) {
    	return Objects.requireNonNull(player.getAttribute(ForgeMod.SWIM_SPEED.get())).getModifier(SWIM_SPEED_MODIFIER_UUID);
    }
    
    
    public static AttributeModifier buildHealthMod(double size) {
		double healthMod = ((float)ConfigHandler.SERVER.minHealth.get() + (((size - 14) / 26F) * ((float)ConfigHandler.SERVER.maxHealth.get() - (float)ConfigHandler.SERVER.minHealth.get()))) - 20;
		healthMod = Math.min(healthMod, ConfigHandler.SERVER.maxHealth.get() - 20);
		
		
		return new AttributeModifier(
    			HEALTH_MODIFIER_UUID,
    			"Dragon Health Adjustment",
			    healthMod,
    			AttributeModifier.Operation.ADDITION
    		);
    }
	
	public static AttributeModifier buildReachMod(double size) {
		double reachMod = 1 + (((size - DragonLevel.BABY.size) / (60.0 - DragonLevel.BABY.size)) * (ConfigHandler.SERVER.reachBonus.get()));
		
		return new AttributeModifier(
				REACH_MODIFIER_UUID,
				"Dragon Reach Adjustment",
				reachMod,
				Operation.MULTIPLY_BASE
		);
	}
    
    public static AttributeModifier buildDamageMod(DragonStateHandler handler, boolean isDragon) {
		double ageBonus = isDragon ? (handler.getLevel() == DragonLevel.ADULT ? ConfigHandler.SERVER.adultBonusDamage.get() : handler.getLevel() == DragonLevel.YOUNG ? ConfigHandler.SERVER.youngBonusDamage.get() : ConfigHandler.SERVER.babyBonusDamage.get()) : 0;

    	return new AttributeModifier(
    			DAMAGE_MODIFIER_UUID,
    			"Dragon Damage Adjustment",
			    ageBonus,
    			AttributeModifier.Operation.ADDITION
    		);
    }
    
    public static AttributeModifier buildSwimSpeedMod(DragonType dragonType) {
    	return new AttributeModifier(
    			SWIM_SPEED_MODIFIER_UUID,
    			"Dragon Swim Speed Adjustment",
    			dragonType == DragonType.SEA && ConfigHandler.SERVER.seaSwimmingBonuses.get() ? 1 : 0,
    			AttributeModifier.Operation.ADDITION
    		);
    }

    public static void updateModifiers(PlayerEntity oldPlayer, PlayerEntity newPlayer) {
		if(!DragonStateProvider.isDragon(newPlayer)) return;
		
    	AttributeModifier oldMod = getHealthModifier(oldPlayer);
        if (oldMod != null)
            updateHealthModifier(newPlayer, oldMod);
        oldMod = getDamageModifier(oldPlayer);
        if (oldMod != null)
            updateDamageModifier(newPlayer, oldMod);
        oldMod =getSwimSpeedModifier(oldPlayer);
        if (oldMod != null)
        	updateSwimSpeedModifier(newPlayer, oldMod);
	    oldMod = getReachModifier(oldPlayer);
	    if (oldMod != null)
		    updateReachModifier(newPlayer, oldMod);
    }
	
	public static void updateReachModifier(PlayerEntity player, AttributeModifier mod) {
		if (!ConfigHandler.SERVER.bonuses.get())
			return;
		ModifiableAttributeInstance max = Objects.requireNonNull(player.getAttribute(ForgeMod.REACH_DISTANCE.get()));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}
	
    public static void updateHealthModifier(PlayerEntity player, AttributeModifier mod) {
    	if (!ConfigHandler.SERVER.healthAdjustments.get())
    		return;
    	float oldMax = player.getMaxHealth();
    	ModifiableAttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH));
    	max.removeModifier(mod);
	    max.addPermanentModifier(mod);
	    float newHealth = player.getHealth() * player.getMaxHealth() / oldMax;
	    player.setHealth(newHealth);
    }
    
    public static void updateDamageModifier(PlayerEntity player, AttributeModifier mod) {
    	if (!ConfigHandler.SERVER.bonuses.get() || !ConfigHandler.SERVER.attackDamage.get())
    		return;
    	ModifiableAttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE));
    	max.removeModifier(mod);
    	max.addPermanentModifier(mod);
    }
    
    public static void updateSwimSpeedModifier(PlayerEntity player, AttributeModifier mod) {
    	if (!ConfigHandler.SERVER.bonuses.get() || !ConfigHandler.SERVER.seaSwimmingBonuses.get())
    		return;
    	ModifiableAttributeInstance max = Objects.requireNonNull(player.getAttribute(ForgeMod.SWIM_SPEED.get()));
    	max.removeModifier(mod);
    	max.addPermanentModifier(mod);
    }
    
    public void setMovementData(double bodyYaw, double headYaw, double headPitch, boolean bite) {
        movementData.bodyYaw = bodyYaw;
        movementData.headYaw = headYaw;
        movementData.headPitch = headPitch;
        movementData.bite = bite;
    }

    public DragonMovementData getMovementData() {
        return this.movementData;
    }
    
    public void setDebuffData(double timeWithoutWater, int timeInDarkness, int timeInRain) {
    	debuffData.timeWithoutWater = timeWithoutWater;
    	debuffData.timeInDarkness = timeInDarkness;
		debuffData.timeInRain = timeInRain;
    }
    
    public DragonDebuffData getDebuffData() {
    	return this.debuffData;
    }

    public DragonType getType() {
        return this.type;
    }
    public void setType(DragonType type) {
		if(this.type != type && this.type != DragonType.NONE){
			growing = true;
			
			getMagic().initAbilities(type);
		}
		
        this.type = type;
	
		if(ConfigHandler.SERVER.saveGrowthStage.get()) {
			switch (type) {
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
    
    public int getLavaAirSupply() {
    	return this.lavaAirSupply;
    }
    
    public void setLavaAirSupply(int lavaAirSupply) {
    	this.lavaAirSupply = lavaAirSupply;
    }

    public int getPassengerId() {
        return this.passengerId;
    }

    public void setPassengerId( int passengerId){
        this.passengerId = passengerId;
    }
	
	public static class DragonMovementData {
        public double bodyYaw;
        public double headYaw;
        public double headPitch;

        public double headYawLastTick;
        public double headPitchLastTick;
        public double bodyYawLastTick;

        public boolean bite;
		public boolean dig;
		
		public boolean spinLearned;
		public int spinCooldown;
		public int spinAttack;
        
        public DragonMovementData(double bodyYaw, double headYaw, double headPitch, boolean bite) {
            this.bodyYaw = bodyYaw;
            this.headYaw = headYaw;
            this.headPitch = headPitch;
            this.headYawLastTick = headYaw;
            this.headPitchLastTick = headPitch;
            this.bodyYawLastTick = bodyYaw;
            this.bite = bite;
        }
    }
    
    public static class DragonDebuffData {
    	public double timeWithoutWater;
		public int timeInRain;
    	public int timeInDarkness;
    	
    	public DragonDebuffData(double timeWithoutWater, int timeInDarkness, int timeInRain) {
    		this.timeWithoutWater = timeWithoutWater;
    		this.timeInDarkness = timeInDarkness;
			this.timeInRain = timeInRain;
    	}
    }
	
	public ClawInventory getClawInventory()
	{
		return clawInventory;
	}
	
	public EmoteCap getEmotes()
	{
		return emotes;
	}
	
	public MagicCap getMagic()
	{
		return magic;
	}
	
	public SkinCap getSkin(){
		return skin;
	}
}
