package by.dragonsurvivalteam.dragonsurvival.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class DSSounds {
    public static final DeferredRegister<SoundEvent> DS_SOUNDS = DeferredRegister.create(
            BuiltInRegistries.SOUND_EVENT,
            MODID
    );
    public static Supplier<SoundEvent> BONK = DS_SOUNDS.register("bonk", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "bonk")));

    public static Supplier<SoundEvent> ACTIVATE_BEACON = DS_SOUNDS.register("activate_beacon", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "activate_beacon")));
    public static Supplier<SoundEvent> DEACTIVATE_BEACON = DS_SOUNDS.register("deactivate_beacon", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "deactivate_beacon")));
    public static Supplier<SoundEvent> UPGRADE_BEACON = DS_SOUNDS.register("upgrade_beacon", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "upgrade_beacon")));
    public static Supplier<SoundEvent> APPLY_EFFECT = DS_SOUNDS.register("apply_effect", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "apply_effect")));

    public static Supplier<SoundEvent> FIRE_BREATH_START = DS_SOUNDS.register("fire_breath_start", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "fire_breath_start")));
    public static Supplier<SoundEvent> FIRE_BREATH_LOOP = DS_SOUNDS.register("fire_breath_loop", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "fire_breath_loop")));
    public static Supplier<SoundEvent> FIRE_BREATH_END = DS_SOUNDS.register("fire_breath_end", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "fire_breath_end")));

    public static Supplier<SoundEvent> FOREST_BREATH_START = DS_SOUNDS.register("forest_breath_start", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "forest_breath_start")));
    public static Supplier<SoundEvent> FOREST_BREATH_LOOP = DS_SOUNDS.register("forest_breath_loop", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "forest_breath_loop")));
    public static Supplier<SoundEvent> FOREST_BREATH_END = DS_SOUNDS.register("forest_breath_end", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "forest_breath_end")));

    public static Supplier<SoundEvent> STORM_BREATH_START = DS_SOUNDS.register("storm_breath_start", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "storm_breath_start")));
    public static Supplier<SoundEvent> STORM_BREATH_LOOP = DS_SOUNDS.register("storm_breath_loop", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "storm_breath_loop")));
    public static Supplier<SoundEvent> STORM_BREATH_END = DS_SOUNDS.register("storm_breath_end", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "storm_breath_end")));

    public static Supplier<SoundEvent> TREASURE_GEM_BREAK = DS_SOUNDS.register("treasure_gem_break", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "treasure_gem_break")));
    public static Supplier<SoundEvent> TREASURE_GEM_HIT = DS_SOUNDS.register("treasure_gem_hit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "treasure_gem_hit")));
    public static DeferredSoundType TREASURE_GEM = new DeferredSoundType(1f, 1f, TREASURE_GEM_BREAK, TREASURE_GEM_HIT, TREASURE_GEM_HIT, TREASURE_GEM_HIT, TREASURE_GEM_HIT);
    public static Supplier<SoundEvent> TREASURE_METAL_BREAK = DS_SOUNDS.register("treasure_metal_break", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "treasure_metal_break")));
    public static Supplier<SoundEvent> TREASURE_METAL_HIT = DS_SOUNDS.register("treasure_metal_hit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "treasure_metal_hit")));

    public static DeferredSoundType TREASURE_METAL = new DeferredSoundType(1f, 1f, TREASURE_METAL_BREAK, TREASURE_METAL_HIT, TREASURE_METAL_HIT, TREASURE_METAL_HIT, TREASURE_METAL_HIT);
}