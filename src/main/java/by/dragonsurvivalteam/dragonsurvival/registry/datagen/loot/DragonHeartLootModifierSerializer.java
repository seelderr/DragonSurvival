package by.dragonsurvivalteam.dragonsurvival.registry.datagen.loot;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

public class DragonHeartLootModifierSerializer extends GlobalLootModifierProvider {


    public DragonHeartLootModifierSerializer(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid) {
        super(output, registries, modid);
    }

    @Override
        protected void start() {
            add("dragon_heart", new DragonHeartLootModifier(new LootItemCondition[0]));
        }
}
