package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonStage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SkinPreset implements INBTSerializable<CompoundTag> {
    private final Lazy<HashMap<ResourceLocation, Lazy<DragonLevelCustomization>>> skins = Lazy.of(this::initialize);

    public Lazy<DragonLevelCustomization> get(final ResourceKey<DragonStage> dragonLevel) {
        return skins.get().get(dragonLevel.location());
    }

    public void put(final ResourceKey<DragonStage> dragonLevel, final Lazy<DragonLevelCustomization> customization) {
        skins.get().put(dragonLevel.location(), customization);
    }

    public void initDefaults(final DragonStateHandler handler) {
        initDefaults(handler.getType());
    }

    public void initDefaults(final AbstractDragonType type) {
        if (type == null) {
            return;
        }

        for (ResourceKey<DragonStage> dragonLevel : DragonStage.keys(null)) {
            skins.get().put(dragonLevel.location(), Lazy.of(() -> new DragonLevelCustomization(dragonLevel.location(), type)));
        }
    }

    public HashMap<ResourceLocation, Lazy<DragonLevelCustomization>> initialize() {
        HashMap<ResourceLocation, Lazy<DragonLevelCustomization>> customizations = new HashMap<>();

        for (ResourceKey<DragonStage> dragonLevel : DragonStage.keys(null)) {
            customizations.computeIfAbsent(dragonLevel.location(), location -> Lazy.of(DragonLevelCustomization::new));
        }

        return customizations;
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        for (ResourceKey<DragonStage> dragonLevel : DragonStage.keys(provider)) {
            tag.put(dragonLevel.location().toString(), skins.get().getOrDefault(dragonLevel.location(), Lazy.of(DragonLevelCustomization::new)).get().serializeNBT(provider));
        }

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag base) {
        for (ResourceKey<DragonStage> level : DragonStage.keys(provider)) {
            skins.get().put(level.location(), Lazy.of(() -> {
                        DragonLevelCustomization group = new DragonLevelCustomization();
                        CompoundTag dragonLevelData = base.getCompound(level.location().toString());
                        group.deserializeNBT(provider, dragonLevelData);
                        return group;
                    })
            );
        }
    }
}