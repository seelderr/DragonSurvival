package by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DSEnchantmentTags extends EnchantmentTagsProvider {
    public DSEnchantmentTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, DragonSurvivalMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) { // FIXME :: size_changing is wrongly defined in json, add tag
        // Used in enchantments
        tag(exclusiveSet("anti_dragon"))
                .add(DSEnchantments.DRAGONSBANE)
                .add(DSEnchantments.DRAGONSBONK)
                .add(DSEnchantments.DRAGONSBOON);

        // Used in enchantments
        tag(exclusiveSet("evil_dragon"))
                .add(DSEnchantments.BLOOD_SIPHON)
                .add(DSEnchantments.DRACONIC_SUPERIORITY)
                .add(DSEnchantments.MURDERERS_CUNNING)
                .add(DSEnchantments.OVERWHELMING_MIGHT)
                .add(DSEnchantments.CURSE_OF_OUTLAW);

        // Used in enchantments
        tag(exclusiveSet("good_dragon"))
                .add(DSEnchantments.AERODYNAMIC_MASTERY)
                .add(DSEnchantments.COMBAT_RECOVERY)
                .add(DSEnchantments.SACRED_SCALES)
                .add(DSEnchantments.UNBREAKABLE_SPIRIT)
                .add(DSEnchantments.CURSE_OF_KINDNESS);
    }

    private static TagKey<Enchantment> exclusiveSet(@NotNull final String path) {
        return key("exclusive_set/" + path);
    }

    private static TagKey<Enchantment> key(@NotNull final String path) {
        return TagKey.create(Registries.ENCHANTMENT, DragonSurvivalMod.res(path));
    }
}
