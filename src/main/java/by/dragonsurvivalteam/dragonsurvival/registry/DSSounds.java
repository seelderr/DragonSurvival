package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DSSounds {
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(
			BuiltInRegistries.SOUND_EVENT,
			DragonSurvivalMod.MODID
	);
	public static Supplier<SoundEvent> ACTIVATE_BEACON = SOUNDS.register("activate_beacon", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "activate_beacon")));
	public static Supplier<SoundEvent> DEACTIVATE_BEACON = SOUNDS.register("deactivate_beacon", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "deactivate_beacon")));
	public static Supplier<SoundEvent> UPGRADE_BEACON = SOUNDS.register("upgrade_beacon", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "upgrade_beacon")));
	public static Supplier<SoundEvent> APPLY_EFFECT = SOUNDS.register("apply_effect", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "apply_effect")));

	public static Supplier<SoundEvent> FIRE_BREATH_START = SOUNDS.register("fire_breath_start", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "fire_breath_start")));
	public static Supplier<SoundEvent> FIRE_BREATH_LOOP = SOUNDS.register("fire_breath_loop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "fire_breath_loop")));
	public static Supplier<SoundEvent> FIRE_BREATH_END = SOUNDS.register("fire_breath_end", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "fire_breath_end")));

	public static Supplier<SoundEvent> FOREST_BREATH_START = SOUNDS.register("forest_breath_start", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "forest_breath_start")));
	public static Supplier<SoundEvent> FOREST_BREATH_LOOP = SOUNDS.register("forest_breath_loop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "forest_breath_loop")));
	public static Supplier<SoundEvent> FOREST_BREATH_END = SOUNDS.register("forest_breath_end", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "forest_breath_end")));

	public static Supplier<SoundEvent> STORM_BREATH_START = SOUNDS.register("storm_breath_start", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "storm_breath_start")));
	public static Supplier<SoundEvent> STORM_BREATH_LOOP = SOUNDS.register("storm_breath_loop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "storm_breath_loop")));
	public static Supplier<SoundEvent> STORM_BREATH_END = SOUNDS.register("storm_breath_end", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "storm_breath_end")));

	public static Supplier<SoundEvent>  TREASURE_GEM_BREAK = SOUNDS.register("treasure_gem_break", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "treasure_gem_break")));
	public static Supplier<SoundEvent>  TREASURE_GEM_HIT = SOUNDS.register("treasure_gem_hit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "treasure_gem_hit")));
	public static DeferredSoundType TREASURE_GEM = new DeferredSoundType(1f, 1f, TREASURE_GEM_BREAK, TREASURE_GEM_HIT, TREASURE_GEM_HIT, TREASURE_GEM_HIT, TREASURE_GEM_HIT);
	public static Supplier<SoundEvent> TREASURE_METAL_BREAK = SOUNDS.register("treasure_metal_break", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "treasure_metal_break")));
	public static Supplier<SoundEvent> TREASURE_METAL_HIT = SOUNDS.register("treasure_metal_hit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DragonSurvivalMod.MODID, "treasure_metal_hit")));

	public static DeferredSoundType TREASURE_METAL = new DeferredSoundType(1f, 1f, TREASURE_METAL_BREAK, TREASURE_METAL_HIT, TREASURE_METAL_HIT, TREASURE_METAL_HIT, TREASURE_METAL_HIT);
}