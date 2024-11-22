package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonStageCustomization;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonStage;
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

    public Map<ResourceKey<DragonStage>, Boolean> recompileSkin = new HashMap<>();
    public Map<ResourceKey<DragonStage>, Boolean> isCompiled = new HashMap<>();

    public SkinPreset skinPreset = new SkinPreset();

    public boolean renderCustomSkin;
    public boolean blankSkin;

    public SkinCap(DragonStateHandler handler) {
        super(handler);
    }

    public void compileSkin(final Holder<DragonStage> dragonStage) {
        recompileSkin.put(dragonStage.getKey(), true);
    }

    public Lazy<DragonStageCustomization> get(final ResourceKey<DragonStage> dragonStage) {
        return skinPreset.get(dragonStage);
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