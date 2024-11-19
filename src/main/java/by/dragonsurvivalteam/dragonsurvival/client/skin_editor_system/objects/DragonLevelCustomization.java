package by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.loader.DefaultPartLoader;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.loader.DragonPartLoader;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Saved customization per dragon level */
public class DragonLevelCustomization implements INBTSerializable<CompoundTag> {
    public static final String HAS_WINGS = "wings";
    public static final String IS_DEFAULT_SKIN = "defaultSkin";

    public HashMap<EnumSkinLayer, Lazy<LayerSettings>> settings = new HashMap<>();
    public ResourceKey<DragonLevel> dragonLevel;

    public boolean hasWings = true;
    public boolean isDefaultSkin;

    public DragonLevelCustomization(final ResourceKey<DragonLevel> dragonLevel, final AbstractDragonType type) {
        this(dragonLevel);

        for (EnumSkinLayer layer : EnumSkinLayer.values()) {
            // Convert the numbered 'EXTRA' layer to the generic 'EXTRA' layer
            EnumSkinLayer actualLayer = EnumSkinLayer.valueOf(layer.getNameUpperCase());

            Map<EnumSkinLayer, List<DragonPart>> partMap = DragonPartLoader.DRAGON_PARTS.get(type.getTypeNameLowerCase());
            List<DragonPart> parts = partMap.get(actualLayer);

            String partKey = DefaultPartLoader.getDefaultPartKey(type, dragonLevel, layer);

            if (parts != null) {
                for (DragonPart part : parts) {
                    if (part.key().equals(partKey)) {
                        settings.put(layer, Lazy.of(() -> new LayerSettings(partKey, part.averageHue())));
                        break;
                    }
                }
            } else {
                settings.put(layer, Lazy.of(() -> new LayerSettings(partKey, 0.5f)));
            }
        }
    }

    public DragonLevelCustomization(ResourceKey<DragonLevel> dragonLevel) {
        this.dragonLevel = dragonLevel;

        for (EnumSkinLayer layer : EnumSkinLayer.values()) {
            settings.computeIfAbsent(layer, key -> Lazy.of(LayerSettings::new));
        }
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag layerData = new CompoundTag();
        layerData.putBoolean(HAS_WINGS, hasWings);
        layerData.putBoolean(IS_DEFAULT_SKIN, isDefaultSkin);

        for (EnumSkinLayer layer : EnumSkinLayer.values()) {
            layerData.put(layer.name(), settings.getOrDefault(layer, Lazy.of(LayerSettings::new)).get().serializeNBT(provider));
        }

        return layerData;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        hasWings = tag.getBoolean(HAS_WINGS);
        isDefaultSkin = tag.getBoolean(IS_DEFAULT_SKIN);

        for (EnumSkinLayer layer : EnumSkinLayer.values()) {
            settings.put(layer, Lazy.of(() -> {
                LayerSettings group = new LayerSettings();
                CompoundTag layerData = tag.getCompound(layer.name());
                group.deserializeNBT(provider, layerData);
                return group;
            }));
        }
    }
}