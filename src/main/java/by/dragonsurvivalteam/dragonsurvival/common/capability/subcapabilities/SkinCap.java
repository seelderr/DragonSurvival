package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonLevelCustomization;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SkinCap extends SubCap {
    public static final String RENDER_CUSTOM_SKIN = "render_custom_skin";
    public static final String SKIN_PRESET = "skin_preset";

    public Map<ResourceKey<DragonLevel>, Boolean> recompileSkin = new HashMap<>();
    public Map<ResourceKey<DragonLevel>, Boolean> isCompiled = new HashMap<>();

    public SkinPreset skinPreset = new SkinPreset();

    public boolean renderCustomSkin;
    public boolean blankSkin;

    public SkinCap(DragonStateHandler handler) {
        super(handler);
    }

    public void compileSkin(final Holder<DragonLevel> dragonLevel) {
        recompileSkin.put(dragonLevel.getKey(), true);
    }

    public Lazy<DragonLevelCustomization> get(final ResourceKey<DragonLevel> dragonLevel) {
        return skinPreset.get(dragonLevel);
    }

    @Override
    public CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(RENDER_CUSTOM_SKIN, renderCustomSkin);
        tag.put(SKIN_PRESET, skinPreset.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, CompoundTag tag) {
        renderCustomSkin = tag.getBoolean(RENDER_CUSTOM_SKIN);

        CompoundTag skin = tag.getCompound(SKIN_PRESET);
        skinPreset = new SkinPreset();
        skinPreset.deserializeNBT(provider, skin);
    }
}