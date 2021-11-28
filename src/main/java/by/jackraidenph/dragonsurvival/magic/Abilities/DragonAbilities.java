package by.jackraidenph.dragonsurvival.magic.Abilities;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.magic.Abilities.Actives.*;
import by.jackraidenph.dragonsurvival.magic.Abilities.Innate.DragonClawsAbility;
import by.jackraidenph.dragonsurvival.magic.Abilities.Innate.DragonWingAbility;
import by.jackraidenph.dragonsurvival.magic.Abilities.Innate.FearOfDarkAbility;
import by.jackraidenph.dragonsurvival.magic.Abilities.Innate.HotBloodAbility;
import by.jackraidenph.dragonsurvival.magic.Abilities.Passives.*;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.InnateDragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.PassiveDragonAbility;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.registration.ParticleRegistry;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import java.util.ArrayList;
import java.util.HashMap;

public class DragonAbilities
{
	//Forest dragon
	public static ActiveDragonAbility POISONOUS_BREATH;
	public static ActiveDragonAbility SPIKE;
	public static ActiveDragonAbility INSPIRATION;
	public static ActiveDragonAbility HUNTER;
	
	public static PassiveDragonAbility FOREST_MAGIC;
	public static PassiveDragonAbility FOREST_ATHLETICS;
	public static PassiveDragonAbility LIGHT_IN_DARKNESS;
	public static PassiveDragonAbility CLIFFHANGER;
	
	public static InnateDragonAbility FOREST_CLAWS_AND_TEETH;
	public static InnateDragonAbility FOREST_WINGS;
	public static InnateDragonAbility FOREST_DRAGON;
	public static InnateDragonAbility FEAR_OF_DARK;
	
	//Sea dragon
	public static ActiveDragonAbility STORM_BREATH;
	public static ActiveDragonAbility BALL_LIGHTNING;
	public static ActiveDragonAbility REVEALING_THE_SOUL;
	public static ActiveDragonAbility SEA_EYES;
	
	public static PassiveDragonAbility SEA_MAGIC;
	public static PassiveDragonAbility SEA_ATHLETICS;
	public static PassiveDragonAbility WATER;
	public static PassiveDragonAbility SPECTRAL_IMPACT;
	
	public static InnateDragonAbility SEA_CLAWS_AND_TEETH;
	public static InnateDragonAbility SEA_WINGS;
	public static InnateDragonAbility SEA_DRAGON;
	public static InnateDragonAbility AMPHIBIAN;
	
	//Cave dragon
	public static ActiveDragonAbility NETHER_BREATH;
	public static ActiveDragonAbility FIREBALL;
	public static ActiveDragonAbility STRONG_LEATHER;
	public static ActiveDragonAbility LAVA_VISION;
	
	public static PassiveDragonAbility CAVE_MAGIC;
	public static PassiveDragonAbility CAVE_ATHLETICS;
	public static PassiveDragonAbility CONTRAST_SHOWER;
	public static PassiveDragonAbility BURN;
	
	public static InnateDragonAbility CAVE_CLAWS_AND_TEETH;
	public static InnateDragonAbility CAVE_WINGS;
	public static InnateDragonAbility CAVE_DRAGON;
	public static InnateDragonAbility HOT_BLOOD;
	
	public static void initAbilities(){
		//Forest dragon
		POISONOUS_BREATH = register(DragonType.FOREST, new PoisonBreathAbility("poisonous_breath", "forest/poisonous_breath", 1, 4, 2, 10, Functions.secondsToTicks(5), new Integer[]{0, 10, 30, 50}));
		SPIKE = register(DragonType.FOREST, new SpikeAbility("spike", "forest/spike", 0, 4, 1, 0, Functions.secondsToTicks(1), new Integer[]{0, 20, 30, 40}));
		INSPIRATION = register(DragonType.FOREST, new AoeBuffAbility(new EffectInstance(Effects.DIG_SPEED, 0, 2), 5, ParticleRegistry.fireBeaconParticle, "inspiration", "forest/inspiration", 0, 3, 5, Functions.secondsToTicks(5), Functions.secondsToTicks(30), new Integer[]{0, 15, 35}));
		HUNTER = register(DragonType.FOREST, new HunterAbility("hunter", "forest/hunter", 0, 2, 3, Functions.secondsToTicks(3), Functions.secondsToTicks(30), new Integer[]{0, 25}));
		
		FOREST_MAGIC = register(DragonType.FOREST, new MagicAbility("forest_magic", "forest/forest_magic", 0, 10));
		FOREST_ATHLETICS = register(DragonType.FOREST, new AthleticsAbility("forest_athletics", "forest/forest_athletics", 0, 5));
		LIGHT_IN_DARKNESS = register(DragonType.FOREST, new LightInDarknessAbility("light_in_darkness", "forest/light_in_darkness", 0, 6));
		CLIFFHANGER = register(DragonType.FOREST, new CliffhangerAbility("cliffhanger", "forest/cliffhanger", 0, 5));
		
		FOREST_CLAWS_AND_TEETH = register(DragonType.FOREST, new DragonClawsAbility("forest_claws_and_teeth", "forest/forest_claws_and_teeth", 1, 1));
		FOREST_WINGS = register(DragonType.FOREST, new DragonWingAbility("forest_wings", "forest/forest_wings", 1, 1));
		FOREST_DRAGON = register(DragonType.FOREST, new InnateDragonAbility("forest_dragon", "forest/forest_dragon", 1, 1));
		FEAR_OF_DARK = register(DragonType.FOREST, new FearOfDarkAbility("fear_of_dark", "forest/fear_of_dark", 1, 1));
		
		//Sea dragon
		STORM_BREATH = register(DragonType.SEA, new LightningBreathAbility("storm_breath", "sea/storm_breath", 1, 4, 2, 10, Functions.secondsToTicks(5), new Integer[]{0, 10, 30, 50}));
		BALL_LIGHTNING = register(DragonType.SEA, new BallLightningAbility(4, "ball_lightning", "sea/ball_lightning", 0, 4, 6, Functions.secondsToTicks(2), Functions.secondsToTicks(60), new Integer[]{0, 20, 45, 50}));
		REVEALING_THE_SOUL = register(DragonType.SEA, new AoeBuffAbility(new EffectInstance(DragonEffects.REVEALING_THE_SOUL, 60), 5, ParticleRegistry.magicBeaconParticle, "revealing_the_soul", "sea/revealing_the_soul", 0, 3, 5, Functions.secondsToTicks(5), Functions.secondsToTicks(30), new Integer[]{0, 25, 40}));
		SEA_EYES = register(DragonType.SEA, new VisionAbility(DragonEffects.WATER_VISION, "sea_eyes", "sea/sea_eyes", 0, 2, 2, Functions.secondsToTicks(2), Functions.secondsToTicks(30), new Integer[]{0, 15}));
		
		SEA_MAGIC = register(DragonType.SEA, new MagicAbility("sea_magic", "sea/sea_magic", 0, 10));
		SEA_ATHLETICS = register(DragonType.SEA, new AthleticsAbility("sea_athletics", "sea/sea_athletics", 0, 5));
		WATER = register(DragonType.SEA, new WaterAbility("water", "sea/water", 0, 5));
		SPECTRAL_IMPACT = register(DragonType.SEA, new SpectralImpactAbility("spectral_impact", "sea/spectral_impact", 0, 3));
		
		SEA_CLAWS_AND_TEETH = register(DragonType.SEA, new DragonClawsAbility("sea_claws_and_teeth", "sea/sea_claws_and_teeth", 1, 1));
		SEA_WINGS = register(DragonType.SEA, new DragonWingAbility("sea_wings", "sea/sea_wings", 1, 1));
		SEA_DRAGON = register(DragonType.SEA, new InnateDragonAbility("sea_dragon", "sea/sea_dragon", 1, 1));
		AMPHIBIAN = register(DragonType.SEA, new InnateDragonAbility("amphibian", "sea/amphibian", 1, 1));
		
		//Cave dragon
		NETHER_BREATH = register(DragonType.CAVE, new FireBreathAbility("nether_breath", "cave/nether_breath", 1, 4, 2, 10, Functions.secondsToTicks(5), new Integer[]{0, 10, 30, 50}));
		FIREBALL = register(DragonType.CAVE, new FireBallAbility("fireball", "cave/fireball", 0, 4,6, Functions.secondsToTicks(4), Functions.secondsToTicks(60),  new Integer[]{0, 20, 40, 45}));
		STRONG_LEATHER = register(DragonType.CAVE, new StrongLeatherAbility(new EffectInstance(DragonEffects.STRONG_LEATHER), 5, ParticleRegistry.peaceBeaconParticle, "strong_leather", "cave/strong_leather", 0, 3, 5, Functions.secondsToTicks(5), Functions.secondsToTicks(30),  new Integer[]{0, 15, 35}));
		LAVA_VISION = register(DragonType.CAVE, new VisionAbility(DragonEffects.LAVA_VISION, "lava_vision", "cave/lava_vision", 0, 2, 2, Functions.secondsToTicks(2), Functions.secondsToTicks(30),  new Integer[]{0, 25}));
		
		CAVE_MAGIC = register(DragonType.CAVE, new MagicAbility("cave_magic", "cave/cave_magic", 0, 10));
		CAVE_ATHLETICS = register(DragonType.CAVE, new AthleticsAbility("cave_athletics", "cave/cave_athletics", 0, 5));
		CONTRAST_SHOWER = register(DragonType.CAVE, new ContrastShowerAbility("contrast_shower", "cave/contrast_shower", 0, 5));
		BURN = register(DragonType.CAVE, new BurnAbility("burn", "cave/burn", 0, 3));
		
		CAVE_CLAWS_AND_TEETH = register(DragonType.CAVE, new DragonClawsAbility("cave_claws_and_teeth", "cave/cave_claws_and_teeth", 1, 1));
		CAVE_WINGS = register(DragonType.CAVE, new DragonWingAbility("cave_wings", "cave/cave_wings", 1, 1));
		CAVE_DRAGON = register(DragonType.CAVE, new InnateDragonAbility("cave_dragon", "cave/cave_dragon", 1, 1));
		HOT_BLOOD = register(DragonType.CAVE, new HotBloodAbility("hot_bloode", "cave/hot_bloode", 1, 1));
	}
	
	
	public static HashMap<DragonType, ArrayList<DragonAbility>> ABILITIES = new HashMap<>();
	public static HashMap<DragonType, ArrayList<ActiveDragonAbility>> ACTIVE_ABILITIES = new HashMap<>();
	public static HashMap<DragonType, ArrayList<PassiveDragonAbility>> PASSIVE_ABILITIES = new HashMap<>();
	public static HashMap<DragonType, ArrayList<InnateDragonAbility>> INFORMATION_ABILITIES = new HashMap<>();
	
	public static HashMap<String, DragonAbility> ABILITY_LOOKUP = new HashMap<>();
	
	public static ActiveDragonAbility register(DragonType type, ActiveDragonAbility activeDragonAbility){
		if(!ABILITIES.containsKey(type)){
			ABILITIES.put(type, new ArrayList<>());
		}
		
		if(!ACTIVE_ABILITIES.containsKey(type)){
			ACTIVE_ABILITIES.put(type, new ArrayList<>());
		}
		
		ABILITIES.get(type).add(activeDragonAbility);
		ACTIVE_ABILITIES.get(type).add(activeDragonAbility);
		
		ABILITY_LOOKUP.put(activeDragonAbility.getId(), activeDragonAbility);
		
		return activeDragonAbility;
	}
	
	public static PassiveDragonAbility register(DragonType type, PassiveDragonAbility passiveDragonAbility){
		if(!ABILITIES.containsKey(type)){
			ABILITIES.put(type, new ArrayList<>());
		}
		
		if(!PASSIVE_ABILITIES.containsKey(type)){
			PASSIVE_ABILITIES.put(type, new ArrayList<>());
		}
		
		ABILITIES.get(type).add(passiveDragonAbility);
		PASSIVE_ABILITIES.get(type).add(passiveDragonAbility);
		ABILITY_LOOKUP.put(passiveDragonAbility.getId(), passiveDragonAbility);
		
		return passiveDragonAbility;
	}
	
	public static InnateDragonAbility register(DragonType type, InnateDragonAbility informationDragonAbility){
		if(!ABILITIES.containsKey(type)){
			ABILITIES.put(type, new ArrayList<>());
		}
		
		if(!INFORMATION_ABILITIES.containsKey(type)){
			INFORMATION_ABILITIES.put(type, new ArrayList<>());
		}
		
		ABILITIES.get(type).add(informationDragonAbility);
		INFORMATION_ABILITIES.get(type).add(informationDragonAbility);
		ABILITY_LOOKUP.put(informationDragonAbility.getId(), informationDragonAbility);
		
		return informationDragonAbility;
	}
}
