package by.dragonsurvivalteam.dragonsurvival.client.sounds;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry{
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DragonSurvivalMod.MODID);

	public static SoundEvent activateBeacon, deactivateBeacon, upgradeBeacon, applyEffect;
	public static SoundEvent fireBreathStart, fireBreathLoop, fireBreathEnd;
	public static SoundEvent forestBreathStart, forestBreathLoop, forestBreathEnd;
	public static SoundEvent stormBreathStart, stormBreathLoop, stormBreathEnd;

	public static RegistryObject<SoundEvent> treasureGemBreak = SOUNDS.register("treasure_gem_break", () -> new SoundEvent(new ResourceLocation(DragonSurvivalMod.MODID, "treasure_gem_break")));
	public static RegistryObject<SoundEvent> treasureGemHit = SOUNDS.register("treasure_gem_hit", () -> new SoundEvent(new ResourceLocation(DragonSurvivalMod.MODID, "treasure_gem_hit")));
	public static ForgeSoundType treasureGem = new ForgeSoundType(1f, 1f, treasureGemBreak, treasureGemHit, treasureGemHit, treasureGemHit, treasureGemHit);
	public static RegistryObject<SoundEvent> treasureMetalBreak = SOUNDS.register("treasure_metal_break", () -> new SoundEvent(new ResourceLocation(DragonSurvivalMod.MODID, "treasure_metal_break")));
	public static RegistryObject<SoundEvent> treasureMetalHit = SOUNDS.register("treasure_metal_hit", () -> new SoundEvent(new ResourceLocation(DragonSurvivalMod.MODID, "treasure_metal_hit")));
	public static ForgeSoundType treasureMetal = new ForgeSoundType(1f, 1f, treasureMetalBreak, treasureMetalHit, treasureMetalHit, treasureMetalHit, treasureMetalHit);

	public static void register()
	{
		activateBeacon = register("activate_beacon");
		deactivateBeacon = register("deactivate_beacon");
		upgradeBeacon = register("upgrade_beacon");
		applyEffect = register("apply_effect");

		fireBreathStart = register("fire_breath_start");
		fireBreathLoop = register("fire_breath_loop");
		fireBreathEnd = register("fire_breath_end");

		forestBreathStart = register("forest_breath_start");
		forestBreathLoop = register("forest_breath_loop");
		forestBreathEnd = register("forest_breath_end");

		stormBreathStart = register("storm_breath_start");
		stormBreathLoop = register("storm_breath_loop");
		stormBreathEnd = register("storm_breath_end");
	}

	private static SoundEvent register(String name){
		SoundEvent soundEvent = new SoundEvent(new ResourceLocation(DragonSurvivalMod.MODID, name));
		SOUNDS.register(name, ()->soundEvent);
		return soundEvent;
	}
}