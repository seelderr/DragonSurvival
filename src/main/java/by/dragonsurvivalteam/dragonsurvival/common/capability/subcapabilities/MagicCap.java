package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.Objects;

public class MagicCap extends SubCap{

	public boolean onMagicSource = false;
	public int magicSourceTimer = 0;
	private final ArrayList<DragonAbility> abilities = new ArrayList<>();
	private ActiveDragonAbility currentlyCasting = null;
	private int selectedAbilitySlot = 0;
	private int currentMana = 0;
	private boolean renderAbilities = true;

	public MagicCap(DragonStateHandler handler){
		super(handler);
	}

	public void setCurrentlyCasting(ActiveDragonAbility currentlyCasting){
		this.currentlyCasting = currentlyCasting;
	}

	public DragonAbility getAbilityOrDefault(DragonAbility ability){
		DragonAbility ab = getAbility(ability);
		return ab != null ? ab : ability;
	}

	public DragonAbility getAbility(DragonAbility ability){
		if(ability != null && ability.getId() != null){
			for(DragonAbility ab : getAbilities()){
				if(ab != null && ab.getId() != null){
					if(Objects.equals(ab.getId(), ability.getId())){
						return ab;
					}
				}
			}
		}

		return null;
	}

	public ArrayList<DragonAbility> getAbilities(){
		if(abilities.size() <= 0 && this.handler.getType() != null && this.handler.getType() != DragonType.NONE){
			initAbilities(this.handler.getType());
		}

		return abilities;
	}

	public void initAbilities(DragonType type){
		if(DragonAbilities.ACTIVE_ABILITIES.containsKey(type)){
			if(handler.getType() != null && handler.getType() != DragonType.NONE){
				if(!ConfigHandler.SERVER.saveAllAbilities.get()){
					abilities.clear();
				}
			}
			top:
			for(DragonAbility ability : DragonAbilities.ABILITIES.get(type)){
				if(ability.getMinLevel() <= 0 && ability instanceof PassiveDragonAbility){
					continue;
				}

				for(DragonAbility ab : abilities){
					if(Objects.equals(ab.getId(), ability.getId())){
						continue top;
					}
				}


				DragonAbility newAbility = ability.createInstance();
				newAbility.setLevel(ability.getMinLevel());
				abilities.add(newAbility);
			}
		}
	}

	public int getAbilityLevel(DragonAbility ability){
		DragonAbility ab = getAbility(ability);
		return ab == null ? ability.getMinLevel() : ab.getLevel();
	}

	@Override
	public CompoundNBT writeNBT(){
		CompoundNBT tag = new CompoundNBT();

		tag.putBoolean("renderSkills", renderAbilityHotbar());

		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("mana", getCurrentMana());
		nbt.putInt("selectedAbilitySlot", getSelectedAbilitySlot());
		nbt.put("abilitySlots", saveAbilities());
		tag.put("abilityData", nbt);
		tag.putBoolean("onMagicSource", onMagicSource);
		tag.putInt("magicSourceTimer", magicSourceTimer);

		return tag;
	}

	public int getCurrentMana(){
		return currentMana;
	}

	public int getSelectedAbilitySlot(){
		return this.selectedAbilitySlot;
	}

	public boolean renderAbilityHotbar(){
		return renderAbilities;
	}

	public CompoundNBT saveAbilities(){
		CompoundNBT tag = new CompoundNBT();

		for(DragonAbility ability : abilities){
			tag.put(ability.getId(), ability.saveNBT());
		}

		return tag;
	}

	@Override
	public void readNBT(CompoundNBT tag){
		onMagicSource = tag.getBoolean("onMagicSource");
		magicSourceTimer = tag.getInt("magicSourceTimer");

		setRenderAbilities(tag.getBoolean("renderSkills"));

		if(tag.contains("abilityData")){
			CompoundNBT ability = tag.getCompound("abilityData");

			if(ability != null){
				setSelectedAbilitySlot(ability.getInt("selectedAbilitySlot"));
				setCurrentMana(ability.getInt("mana"));
				loadAbilities(ability);
			}
		}
	}

	public void setCurrentMana(int currentMana){
		this.currentMana = currentMana;
	}

	public void setSelectedAbilitySlot(int newSlot){
		this.selectedAbilitySlot = newSlot;
		resetCasting();
	}

	public void resetCasting(){
		for(int i = 0; i < 4; i++){
			if(getAbilityFromSlot(i) != null){
				getAbilityFromSlot(i).setCastTime(0);
			}
		}
		if(getCurrentlyCasting() != null){
			getCurrentlyCasting().setCastTime(0);
		}
	}

	public ActiveDragonAbility getCurrentlyCasting(){
		return currentlyCasting;
	}

	public ActiveDragonAbility getAbilityFromSlot(int slot){
		ActiveDragonAbility dragonAbility = handler.getType() != null && DragonAbilities.ACTIVE_ABILITIES.get(handler.getType()) != null && DragonAbilities.ACTIVE_ABILITIES.get(handler.getType()).size() >= slot ? DragonAbilities.ACTIVE_ABILITIES.get(handler.getType()).get(slot) : null;
		ActiveDragonAbility actual = (ActiveDragonAbility)getAbility(dragonAbility);

		return actual == null ? dragonAbility : actual;
	}

	public void setRenderAbilities(boolean renderAbilities){
		this.renderAbilities = renderAbilities;
	}

	public void loadAbilities(CompoundNBT nbt){
		CompoundNBT tag = nbt.contains("abilitySlots") ? nbt.getCompound("abilitySlots") : null;

		if(tag != null){
			for(DragonAbility staticAbility : DragonAbilities.ABILITY_LOOKUP.values()){
				if(tag.contains(staticAbility.getId())){
					DragonAbility ability = staticAbility.createInstance();
					ability.loadNBT(tag.getCompound(staticAbility.getId()));
					addAbility(ability);
				}
			}
		}
	}

	public void addAbility(DragonAbility ability){
		abilities.removeIf((c) -> c.getId() == ability.getId());
		abilities.add(ability);
	}
}