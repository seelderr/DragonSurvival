package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SkinPreset implements INBTSerializable<CompoundTag> {
    public HashMap<ResourceKey<DragonLevel>, Lazy<DragonLevelCustomization>> skins = new HashMap<>();

    public SkinPreset() {
        for (ResourceKey<DragonLevel> dragonLevel : DragonLevel.keys(null)) {
            skins.computeIfAbsent(dragonLevel, level -> Lazy.of(() -> new DragonLevelCustomization(level)));
        }
    }

    public void initDefaults(final DragonStateHandler handler) {
        initDefaults(handler.getType());
    }

    public void initDefaults(final AbstractDragonType type) {
        if (type == null) {
            return;
        }

        for (ResourceKey<DragonLevel> dragonLevel : DragonLevel.keys(null)) {
            skins.put(dragonLevel, Lazy.of(() -> new DragonLevelCustomization(dragonLevel, type)));
        }
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        for (ResourceKey<DragonLevel> dragonLevel : DragonLevel.keys(provider)) {
            tag.put(DragonLevel.name(dragonLevel), skins.getOrDefault(dragonLevel, Lazy.of(() -> new DragonLevelCustomization(dragonLevel))).get().serializeNBT(provider));
        }

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag base) {
        for (ResourceKey<DragonLevel> level : DragonLevel.keys(provider)) {
            skins.put(level, Lazy.of(() -> {
                        DragonLevelCustomization group = new DragonLevelCustomization(level);
                        CompoundTag dragonLevelData = base.getCompound(level.location().toString());
                        group.deserializeNBT(provider, dragonLevelData);
                        return group;
                    })
            );
        }
    }
}