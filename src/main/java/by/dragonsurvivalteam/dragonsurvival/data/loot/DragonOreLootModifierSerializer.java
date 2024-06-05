package by.dragonsurvivalteam.dragonsurvival.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

// We aren't going to serialize anything here, so this is essentially just a formality.
public class DragonOreLootModifierSerializer extends GlobalLootModifierProvider {

    public DragonOreLootModifierSerializer(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid) {
        super(output, registries, modid);
    }

    @Override
    protected void start() {
        add("dragon_ore", new DragonOreLootModifier(new LootItemCondition[0]));
    }
}
