package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.innate.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.passive.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class MagicCap extends SubCap{
	public static final Integer activeAbilitySlots = 4;
	public static final Integer passiveAbilitySlots = 4;
	public static final Integer innateAbilitySlots = 4;

	public final HashMap<Integer, String> passiveDragonAbilities = new HashMap<>();
	public final HashMap<Integer, String> activeDragonAbilities = new HashMap<>();
	public final HashMap<Integer, String> innateDragonAbilities = new HashMap<>();

	public final HashMap<String, DragonAbility> abilities = new HashMap<>();
	private int selectedAbilitySlot = 0;

	public boolean isCasting = false;
	private int currentMana = 0;

	public boolean onMagicSource = false;
	public int magicSourceTimer = 0;

	@Getter
	@Setter
	private boolean renderAbilities = true;

	public MagicCap(DragonStateHandler handler){
		super(handler);
		initAbilities(handler.getType());
	}

	public void initAbilities(DragonType type){
		activeDragonAbilities.clear();
		passiveDragonAbilities.clear();
		innateDragonAbilities.clear();


		if(!ServerConfig.saveAllAbilities)
			abilities.clear();

		for(DragonAbility dragonAbility : DragonAbilities.ABILITIES.getOrDefault(type, new ArrayList<>())){
			if(!abilities.containsKey(dragonAbility.getName())){
				try{
					DragonAbility ability = dragonAbility.getClass().newInstance();

					abilities.put(ability.getName(), ability);
				}catch(InstantiationException | IllegalAccessException e){
					e.printStackTrace();
				}

				if(dragonAbility instanceof ActiveDragonAbility && activeDragonAbilities.size() < activeAbilitySlots){
					activeDragonAbilities.put(activeDragonAbilities.size(), dragonAbility.getName());
				}

				if(dragonAbility instanceof PassiveDragonAbility && passiveDragonAbilities.size() < passiveAbilitySlots){
					passiveDragonAbilities.put(passiveDragonAbilities.size(), dragonAbility.getName());
				}

				if(dragonAbility instanceof InnateDragonAbility){
					innateDragonAbilities.put(innateDragonAbilities.size(), dragonAbility.getName());
				}
			}
		}
	}


	public int getCurrentMana(){
		return currentMana;
	}


	public void setCurrentMana(int currentMana){
		this.currentMana = currentMana;
	}

	public void setSelectedAbilitySlot(int newSlot){
		selectedAbilitySlot = newSlot;
	}

	public int getSelectedAbilitySlot(){
		return selectedAbilitySlot;
	}

	public InnateDragonAbility getInnateAbilityFromSlot(int slot){
		if(innateDragonAbilities.containsKey(slot)){
			String key = innateDragonAbilities.get(slot);
			if(abilities.containsKey(key)){
				return (InnateDragonAbility)abilities.get(key);
			}
		}
		return null;
	}

	public PassiveDragonAbility getPassiveAbilityFromSlot(int slot){
		if(passiveDragonAbilities.containsKey(slot)){
			String key = passiveDragonAbilities.get(slot);
			if(abilities.containsKey(key))
				return (PassiveDragonAbility)abilities.get(key);
		}
		return null;
	}

	public ActiveDragonAbility getCurrentlyCasting(){
		return isCasting ? getAbilityFromSlot(getSelectedAbilitySlot()) : null;
	}

	public void setCurrentlyCasting(){
		isCasting = true;
	}

	public void stopCasting(){
		isCasting = false;
		getCurrentlyCasting().onKeyReleased(getCurrentlyCasting().player);
	}

	public List<ActiveDragonAbility> getActiveAbilities(){
		return abilities.values().stream().filter(ActiveDragonAbility.class::isInstance).filter(s->s.getDragonType() == null || s.getDragonType() == handler.getType()).map(ActiveDragonAbility.class::cast).toList();
	}

	public List<PassiveDragonAbility> getPassiveAbilities(){
		return abilities.values().stream().filter(PassiveDragonAbility.class::isInstance).filter(s->s.getDragonType() == null || s.getDragonType() == handler.getType()).map(PassiveDragonAbility.class::cast).toList();
	}

	public ActiveDragonAbility getAbilityFromSlot(int slot){
		if(activeDragonAbilities.containsKey(slot)){
			String key = activeDragonAbilities.get(slot);
			if(abilities.containsKey(key))
				return (ActiveDragonAbility)abilities.get(key);
		}

		return null;
	}


	public CompoundTag saveAbilities(){
		CompoundTag tag = new CompoundTag();

		for(Entry<String, DragonAbility> entry : abilities.entrySet()){
			tag.put(entry.getKey(), entry.getValue().saveNBT());
		}

		for(int i = 0; i < activeAbilitySlots; i++){
			if(activeDragonAbilities.containsKey(i))
				tag.putString("active_" + i, activeDragonAbilities.get(i));
		}

		for(int i = 0; i < passiveAbilitySlots; i++){
			if(passiveDragonAbilities.containsKey(i))
				tag.putString("passive_" + i, passiveDragonAbilities.get(i));
		}

		for(int i = 0; i < innateAbilitySlots; i++){
			if(innateDragonAbilities.containsKey(i))
				tag.putString("innate_" + i, innateDragonAbilities.get(i));
		}

		return tag;
	}

	public void loadAbilities(CompoundTag tag){
		for(Entry<DragonType, ArrayList<DragonAbility>> entry : DragonAbilities.ABILITIES.entrySet()){
			if(!ServerConfig.saveAllAbilities && entry.getKey() != handler.getType()) continue;

			for(DragonAbility ability : entry.getValue()){
				if(tag.contains(ability.getName())){
					try{
						DragonAbility ab =  ability.getClass().newInstance();
						ab.loadNBT(tag.getCompound(ability.getName()));
						abilities.put(ab.getName(), ab);
					}catch(InstantiationException | IllegalAccessException e){
						e.printStackTrace();
					}
				}
			}
		}

		for(int i = 0; i < activeAbilitySlots; i++){
			if(tag.contains("active_" + i)){
				activeDragonAbilities.put(i, tag.getString("active_" + i));
			}
		}

		for(int i = 0; i < passiveAbilitySlots; i++){
			if(tag.contains("passive_" + i)){
				passiveDragonAbilities.put(i, tag.getString("passive_" + i));
			}
		}

		for(int i = 0; i < innateAbilitySlots; i++){
			if(tag.contains("innate_" + i)){
				innateDragonAbilities.put(i, tag.getString("innate_" + i));
			}
		}
	}

	@Override
	public CompoundTag writeNBT(){
		CompoundTag tag = new CompoundTag();

		tag.putBoolean("renderSkills", renderAbilities);
		tag.putInt("selectedAbilitySlot",  selectedAbilitySlot);

		tag.putBoolean("onMagicSource", onMagicSource);
		tag.putInt("magicSourceTimer", magicSourceTimer);

		tag.putInt("mana", getCurrentMana());
		tag.putBoolean("isCasting", isCasting);

		tag.put("abilityData", saveAbilities());
		return tag;
	}

	@Override
	public void readNBT(CompoundTag tag){
		onMagicSource = tag.getBoolean("onMagicSource");
		magicSourceTimer = tag.getInt("magicSourceTimer");

		renderAbilities = tag.getBoolean("renderSkills");

		setSelectedAbilitySlot(tag.getInt("selectedAbilitySlot"));
		setCurrentMana(tag.getInt("mana"));

		isCasting = tag.getBoolean("isCasting");

		if(tag.contains("abilityData"))
			loadAbilities(tag.getCompound("abilityData"));
	}
}