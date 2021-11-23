package by.jackraidenph.dragonsurvival.capability;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.magic.Abilities.DragonAbilities;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.util.DragonLevel;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


public class DragonStateHandler {
    private boolean isHiding;
    private DragonType type = DragonType.NONE;
    private final DragonMovementData movementData = new DragonMovementData(0, 0, 0, false);
    private boolean hasWings;
    private float size;
    private final DragonDebuffData debuffData = new DragonDebuffData(0, 0, 0);
    private int lavaAirSupply;
    private int passengerId;
	
	public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("03574e62-f9e4-4f1b-85ad-fde00915e446");
    public static final UUID DAMAGE_MODIFIER_UUID = UUID.fromString("5bd3cebc-132e-4f9d-88ef-b686c7ad1e2c");
    public static final UUID SWIM_SPEED_MODIFIER_UUID = UUID.fromString("2a9341f3-d19e-446c-924b-7cf2e5259e10");
	
    public float getSize() {
        return size;
    }

    /**
     * Sets the size, health and base damage
     */
    public void setSize(float size, PlayerEntity playerEntity) {
        setSize(size);
    	AttributeModifier healthMod = buildHealthMod(size);
        updateHealthModifier(playerEntity, healthMod);
        AttributeModifier damageMod = buildDamageMod(getLevel(), isDragon());
        updateDamageModifier(playerEntity, damageMod);
        AttributeModifier swimSpeedMod = buildSwimSpeedMod(getType());
        updateSwimSpeedModifier(playerEntity, swimSpeedMod);
    }
	
	public void setSize(float size) {
    	this.size = size;
    }

    public boolean hasWings() {
        return hasWings;
    }

    public void setHasWings(boolean hasWings) {
        this.hasWings = hasWings;
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

    public boolean canHarvestWithPaw(BlockState state) {
    	int harvestLevel = state.getHarvestLevel();
    	switch(getLevel()) {
    		case BABY:
    			if (ConfigHandler.SERVER.bonusUnlockedAt.get() != DragonLevel.BABY){
    			    if (harvestLevel <= ConfigHandler.SERVER.baseHarvestLevel.get())
                        return true;
    			    break;
                }
    		case YOUNG:
    		    if (ConfigHandler.SERVER.bonusUnlockedAt.get() == DragonLevel.ADULT && getLevel() != DragonLevel.BABY){
    		        if (harvestLevel <= ConfigHandler.SERVER.baseHarvestLevel.get())
                        return true;
    		        break;
                }
            case ADULT:
            	if (harvestLevel <= ConfigHandler.SERVER.bonusHarvestLevel.get()) {
                    switch (getType()) {
                        case SEA:
                            if (state.isToolEffective(ToolType.SHOVEL))
                            	return true;
                            break;
                        case CAVE:
                            if (state.isToolEffective(ToolType.PICKAXE))
                            	return true;
                            break;
                        case FOREST:
                            if (state.isToolEffective(ToolType.AXE))
                                return true;
                    }
                }
            	if (harvestLevel <= ConfigHandler.SERVER.baseHarvestLevel.get())
                    return true;
    	}
    	return false;
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
    
    
    public static AttributeModifier buildHealthMod(float size) {
    	return new AttributeModifier(
    			HEALTH_MODIFIER_UUID,
    			"Dragon Health Adjustment",
    			((float)ConfigHandler.SERVER.minHealth.get() + (((size - 14) / 26F) * ((float)ConfigHandler.SERVER.maxHealth.get() - (float)ConfigHandler.SERVER.minHealth.get()))) - 20,
    			AttributeModifier.Operation.ADDITION
    		);
    }
    
    public static AttributeModifier buildDamageMod(DragonLevel level, boolean isDragon) {
    	return new AttributeModifier(
    			DAMAGE_MODIFIER_UUID,
    			"Dragon Damage Adjustment",
    			isDragon ? (level == DragonLevel.ADULT ? ConfigHandler.SERVER.adultBonusDamage.get() : level == DragonLevel.YOUNG ? ConfigHandler.SERVER.youngBonusDamage.get() : ConfigHandler.SERVER.babyBonusDamage.get()) : 0,
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
    	AttributeModifier oldMod = getHealthModifier(oldPlayer);
        if (oldMod != null)
            updateHealthModifier(newPlayer, oldMod);
        oldMod = getDamageModifier(oldPlayer);
        if (oldMod != null)
            updateDamageModifier(newPlayer, oldMod);
        oldMod =getSwimSpeedModifier(oldPlayer);
        if (oldMod != null)
        	updateSwimSpeedModifier(newPlayer, oldMod);
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
        this.type = type;
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
	
	private ArrayList<DragonAbility> abilities = new ArrayList<>();
	public static final int MAX_SLOTS = 4;
	
	private ActiveDragonAbility currentlyCasting = null;
	private int selectedAbilitySlot = 0;
	private int currentMana = 0;
	
	private boolean renderAbilities = true;
	
	
	public int getMaxMana(PlayerEntity entity) {
		int mana = 1;
		
		mana += (Math.min(50, entity.experienceLevel) - 5) / 5;
		
		switch(type){
			case SEA:
				mana += getAbilityLevel(DragonAbilities.SEA_MAGIC);
				break;
			
			case CAVE:
				mana += getAbilityLevel(DragonAbilities.CAVE_MAGIC);
				break;
			
			case FOREST:
				mana += getAbilityLevel(DragonAbilities.FOREST_MAGIC);
				break;
		}
		
		return mana;
	}
	
	public int getCurrentMana() {
		return currentMana;
	}
	
	public void setCurrentMana(int currentMana) {
		this.currentMana = currentMana;
	}
	
	public ActiveDragonAbility getCurrentlyCasting()
	{
		return currentlyCasting;
	}
	
	public DragonStateHandler setCurrentlyCasting(ActiveDragonAbility currentlyCasting)
	{
		this.currentlyCasting = currentlyCasting;
		return this;
	}
	
	public DragonAbility getAbilityOrDefault(DragonAbility ability){
		DragonAbility ab = getAbility(ability);
		return ab != null ? ab : ability;
	}
	
	public DragonAbility getAbility(DragonAbility ability){
		if(ability != null && ability.getId() != null) {
			for (DragonAbility ab : getAbilities()) {
				if (ab != null && ab.getId() != null) {
					if (Objects.equals(ab.getId(), ability.getId())) {
						return ab;
					}
				}
			}
		}
		
		return null;
	}
	
	public int getAbilityLevel(DragonAbility ability){
		DragonAbility ab = getAbility(ability);
		return ab == null ? ability.getMinLevel() : ab.getLevel();
	}
	
	public ArrayList<DragonAbility> getAbilities()
	{
		return abilities;
	}
	
	public void addAbility(DragonAbility ability){
		abilities.add(ability);
	}
	
	public ActiveDragonAbility getAbilityFromSlot(int slot) {
		ActiveDragonAbility dragonAbility = type != null && DragonAbilities.ACTIVE_ABILITIES.get(type) != null && DragonAbilities.ACTIVE_ABILITIES.get(type).size() >= slot ? DragonAbilities.ACTIVE_ABILITIES.get(type).get(slot) : null;
		ActiveDragonAbility actual = (ActiveDragonAbility)getAbility(dragonAbility);
		
		return actual == null ? dragonAbility : actual;
	}
	
	
	public int getSelectedAbilitySlot() {
		return this.selectedAbilitySlot;
	}
	
	public void setSelectedAbilitySlot(int newSlot) {
		this.selectedAbilitySlot = newSlot;
	}
	
	public boolean renderAbilityHotbar()
	{
		return renderAbilities;
	}
	
	public DragonStateHandler setRenderAbilities(boolean renderAbilities)
	{
		this.renderAbilities = renderAbilities;
		return this;
	}
	
	public CompoundNBT saveAbilities(){
		CompoundNBT tag = new CompoundNBT();
		
		for(DragonAbility ability : abilities){
			tag.put(ability.getId(), ability.saveNBT());
		}
		
		return tag;
	}
	
	public void loadAbilities(CompoundNBT nbt){
		CompoundNBT tag = nbt.contains("abilitySlots") ? nbt.getCompound("abilitySlots") : null;
		
		if(tag != null){
			for(DragonAbility staticAbility : DragonAbilities.ABILITIES.get(type)){
				if(tag.contains(staticAbility.getId())){
					DragonAbility ability = staticAbility.createInstance();
					ability.loadNBT(tag.getCompound(staticAbility.getId()));
					addAbility(ability);
				}
			}
		}
	}
}
